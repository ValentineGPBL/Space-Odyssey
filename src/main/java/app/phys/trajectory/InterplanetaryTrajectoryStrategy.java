package app.phys.trajectory;

import java.util.ArrayList;
import java.util.List;

import app.phys.Entity;
import app.phys.Planet;
import app.phys.PlanetarySystem;
import app.phys.lambert.BattinsLambertSolver;
import app.phys.lambert.LambertSolver;
import app.utils.PhysUtils;
import app.utils.Vector;
import app.utils.Vector3;
import javafx.util.Pair;

public class InterplanetaryTrajectoryStrategy implements TrajectoryStrategy {

    /*
     * The vector represents flight parameters
     * For say a trajectory from Earth to the sun and then to Mars,
     * the vector could represent the following:
     * [
     * 0.1, // Azimuth angle from Earth
     * 0.1, // Elevation angle from Earth
     * 
     * 500, // Time of flight from Earth to Mars
     * 
     * 0.2, // Azimuth angle from Mars
     * 0.2, // Elevation angle from Mars
     * 
     * ]
     */

    private PlanetarySystem system;
    private List<FlybyState> path;
    private LambertSolver solver = new BattinsLambertSolver();

    public InterplanetaryTrajectoryStrategy(PlanetarySystem system, List<FlybyState> path) {
        this.system = system;
        this.path = path;
    }

    private Vector3 toDirection(Vector x, int index) {
        double azimuth = x.get(index);
        double elevation = x.get(index + 1);
        return PhysUtils.fromAzimuthElevation(new Vector(new double[] { azimuth, elevation }));
    }

    @Override
    public double costFunction(Vector x) {
        double totalTof = 0;
        double cost = 0;
        Vector3 prevVel = new Vector3();
        for (int i = 0; i < path.size() - 1; i++) {
            // Where do we launch
            FlybyState from = path.get(i);
            Planet fromPlanet = (Planet) system.get(from.target);
            // Where do we go
            FlybyState to = path.get(i + 1);
            Planet toPlanet = (Planet) system.get(to.target);
            // What do we fly around
            Planet center = fromPlanet.getName().equals(toPlanet.getName()) ? toPlanet : toPlanet.getParentBody();

            // How long do we fly
            double tof = Math.clamp(x.get(i * 3 + 2), 0, 365 * 24 * 60 * 60);
            double startTime = totalTof;
            double endTime = totalTof + tof;

            // Heliocentric target positions
            Pair<Vector3, Vector3> fromState = fromPlanet.getState(startTime);
            Pair<Vector3, Vector3> toState = toPlanet.getState(endTime);

            // Extract values from states
            Vector3 fromPos = fromState.getKey();
            Vector3 toPos = toState.getKey();
            Vector3 fromVel = fromState.getValue();
            Vector3 toVel = toState.getValue();

            // Add SOI to the positions
            if (from.altitude == 0)
                fromPos.add(toDirection(x, i * 3).mul(fromPlanet.getSOI()));
            else
                fromPos.add(toDirection(x, i * 3).mul(fromPlanet.getRadius() + from.altitude));

            if (to.altitude == 0)
                toPos.add(toDirection(x, (i + 1) * 3).mul(toPlanet.getSOI()));
            else
                toPos.add(toDirection(x, (i + 1) * 3).mul(toPlanet.getRadius() + to.altitude));

            // Where is the center at those points in time?
            Pair<Vector3, Vector3> centerStartState = center.getState(startTime);
            Pair<Vector3, Vector3> centerEndState = center.getState(endTime);
            Vector3 centerStartPos = centerStartState.getKey();
            Vector3 centerEndPos = centerEndState.getKey();
            Vector3 centerStartVel = centerStartState.getValue();
            Vector3 centerEndVel = centerEndState.getValue();

            // Make positions relative to the center (required for lambert solver)
            fromPos.sub(centerStartPos);
            toPos.sub(centerEndPos);

            // Calculate the Lambert solution
            Pair<Vector3, Vector3> lambertSolution = solver.solve(center, fromPos, toPos, tof);
            Vector3 launchVel = lambertSolution.getKey();
            Vector3 arrivalVel = lambertSolution.getValue();

            // Make velocities heliocentric
            launchVel.add(centerStartVel);
            arrivalVel.add(centerEndVel);

            // Calculate the cost as the difference in velocities (required thrust)
            if (i > 0)
                cost += prevVel.clone().sub(launchVel).length();

            // Cost for takeoff
            if (i == 0)
                cost += Math.abs(launchVel.clone().sub(fromVel).length() - 60) * 100;

            // Cost for landing
            if (i == path.size() - 2)
                cost += arrivalVel.clone().sub(toVel).length();

            prevVel = arrivalVel;
            totalTof += tof;
        }

        if (totalTof > 365 * 24 * 60 * 60 * 2)
            cost += (totalTof - 365 * 24 * 60 * 60 * 2); // Penalize long flights

        return cost;
    }

    @Override
    public Vector computeInitialGuess() {
        Vector initialGuess = new Vector(path.size() * 2 + path.size() - 1);
        for (int i = 0; i < path.size() - 1; i++) {
            Entity from = system.get(path.get(i).target);
            Entity to = system.get(path.get(i + 1).target);
            double dist = from.getDistance(to);
            initialGuess.set(i * 3 + 2, dist / 60); // Suggest 60 km/s
        }
        return initialGuess;
    }

    public List<TrajectorySegment> getSolution(Vector x) {
        double totalTof = 0;
        List<TrajectorySegment> solution = new ArrayList<>();

        for(int i = 0; i < path.size(); i++) {
            FlybyState from = path.get(i);
            Planet fromPlanet = (Planet) system.get(from.target);

            if (i < path.size() - 1) {
                FlybyState to = path.get(i + 1);
                Planet toPlanet = (Planet) system.get(to.target);
                Planet center = fromPlanet.getName().equals(toPlanet.getName()) ? toPlanet : toPlanet.getParentBody();

                double tof = Math.clamp(x.get(i * 3 + 2), 0, 365 * 24 * 60 * 60);
                double startTime = totalTof;
                double endTime = totalTof + tof;

                Pair<Vector3, Vector3> fromState = fromPlanet.getState(startTime);
                Pair<Vector3, Vector3> toState = toPlanet.getState(endTime);
                
                Vector3 fromPos = fromState.getKey().clone();
                Vector3 toPos = toState.getKey().clone();

                if (from.altitude == 0)
                    fromPos.add(toDirection(x, i * 3).mul(fromPlanet.getSOI()));
                else
                    fromPos.add(toDirection(x, i * 3).mul(fromPlanet.getRadius() + from.altitude));

                if (to.altitude == 0)
                    toPos.add(toDirection(x, (i + 1) * 3).mul(toPlanet.getSOI()));
                else
                    toPos.add(toDirection(x, (i + 1) * 3).mul(toPlanet.getRadius() + to.altitude));

                Pair<Vector3, Vector3> centerStartState = center.getState(startTime);
                Pair<Vector3, Vector3> centerEndState = center.getState(endTime);
                Vector3 centerStartPos = centerStartState.getKey();
                Vector3 centerEndPos = centerEndState.getKey();
                Vector3 centerStartVel = centerStartState.getValue();
                Vector3 centerEndVel = centerEndState.getValue();

                Vector3 startPosRel = fromPos.clone().sub(centerStartPos);
                Vector3 endPosRel = toPos.clone().sub(centerEndPos);

                Pair<Vector3, Vector3> lambertSolution = solver.solve(center, startPosRel, endPosRel, tof);
                Vector3 startVel = lambertSolution.getKey().clone().add(centerStartVel);
                Vector3 endVel = lambertSolution.getValue().clone().add(centerEndVel);

                System.out.println("---");
                System.out.println(startVel);
                System.out.println(endVel);
                System.out.println(startPosRel);
                System.out.println(endPosRel);
                System.out.println(startTime);
                System.out.println(endTime);
                System.out.println(center.getName());

                TrajectorySegment segment = new TrajectorySegment(
                    startVel, endVel, startPosRel, endPosRel, 
                    startTime, endTime, center.getName()
                );
                solution.add(segment);

                totalTof += tof;
            }
        }
        
        return solution;
    }
}
