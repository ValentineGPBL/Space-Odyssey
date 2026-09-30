package app.phys.trajectory;

import java.util.List;

import app.phys.OrbitalParams;
import app.phys.Planet;
import app.phys.PlanetarySystem;
import app.phys.Simulation;
import app.phys.lambert.BattinsLambertSolver;
import app.phys.lambert.LambertSolver;
import app.phys.optimizer.GradientDescentOptimizerStrategy;
import app.utils.PhysUtils;
import app.utils.Vector;
import app.utils.Vector3;
import javafx.util.Pair;

public class InterplanterySimulationTrajectoryStrategy implements TrajectoryStrategy {

    /*
     * The vector represents flight parameters
     * For say a trajectory from Earth to the sun and then to Mars,
     * the vector could represent the following:
     * [
     *  0.1, // Azimuth angle from Earth
     *  0.1, // Elevation angle from Earth
     * 
     *  500, // Time of flight from Earth to Mars
     *  
     *  0.2, // Azimuth angle from Mars
     *  0.2, // Elevation angle from Mars
     *  
     * ]
     */

    private static double minCost = Double.MAX_VALUE;
    private int i = 0;

    private PlanetarySystem system = null;
    private List<Planet> path;
    private LambertSolver solver = new BattinsLambertSolver();

    public InterplanterySimulationTrajectoryStrategy(PlanetarySystem system, List<Planet> path) {
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
        PlanetarySystem sim = system.clone();
        double deltaV = 0;
        double totalTof = 0;
        double cost = 0;
        Vector3 prevVel = new Vector3();
        for(int i = 0; i < path.size() - 1; i++) {
            double minDist = Double.MAX_VALUE;

            // Where do we launch
            Planet from = (Planet) sim.get(path.get(i).getName());
            // Where do we go
            Planet to = (Planet) sim.get(path.get(i + 1).getName());
            // What do we fly around
            //Planet center = from.equals(to) ? to : to.getParentBody();
            Planet center = from.equals(to) ? to : (Planet)sim.get(from.getParentBody().getName());
            
            // How long do we fly
            //double tof = Math.max(0, from.getDistance(to) - from.getSOI() - to.getSOI()) / Math.clamp(x.get(i * 3 + 2), MIN_TRAVEL_MULT, MAX_TRAVEL_MULT);
            double tof = Math.clamp(x.get(i * 3 + 2), 0, 365 * 24 * 60 * 60);
            double startTime = totalTof;
            double endTime = totalTof + tof;

            // Heliocentric target positions
            Pair<Vector3, Vector3> fromState = new Pair<Vector3,Vector3>(from.getPosition(), from.getVelocity());//from.getState(startTime);
            Pair<Vector3, Vector3> centerStartState = new Pair<Vector3,Vector3>(center.getPosition(), center.getVelocity());//center.getState(startTime);
            PlanetarySystem stateSim = sim.clone();
            stateSim.advance(tof);
            Pair<Vector3, Vector3> centerEndState = new Pair<Vector3,Vector3>(stateSim.get(center.getName()).getPosition(), stateSim.get(center.getName()).getVelocity());
            Pair<Vector3, Vector3> toState = new Pair<Vector3,Vector3>(stateSim.get(to.getName()).getPosition(), stateSim.get(to.getName()).getVelocity());//to.getState(endTime);

            // Extract values from states
            Vector3 fromPos = fromState.getKey().clone();
            Vector3 toPos = toState.getKey().clone();
            Vector3 fromVel = fromState.getValue();
            Vector3 toVel = toState.getValue();

            // Add SOI to the positions
            fromPos.add(toDirection(x, i * 3).mul(from.getSOI()));
            if(from.getName().equals(to.getName())) // Landing mode
                toPos.add(toDirection(x, (i + 1) * 3).mul(to.getRadius()));
            else
                toPos.add(toDirection(x, (i + 1) * 3).mul(to.getSOI()));

            // Where is the center at those points in time?
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

            sim.getProbe().setPosition(fromPos.clone().add(centerStartPos));
            //sim.getProbe().setVelocity(launchVel);
            double speed = launchVel.length();
            //sim.getProbe().setVelocity(launchVel.normalize().mul(PhysUtils.escapeVelWithExcess(from, sim.getProbe().getDistance(from.getState(startTime).getKey()), speed)));
            //deltaV += launchVel.clone().sub(prevVel).length();
            sim.getProbe().setVelocity(launchVel);
            sim.updateState();

            /*System.out.println(Simulation.G * center.getMass());
            System.out.println(fromPos);
            System.out.println(toPos);
            System.out.println("mu: " + Simulation.G * center.getMass());
            System.out.println("From position: " + fromPos);
            System.out.println("launch velocity: " + launchVel.clone().sub(centerStartVel));*/
            OrbitalParams params = new OrbitalParams(Simulation.G * center.getMass(), fromPos, launchVel.clone().sub(centerStartVel));
            /*System.out.println(params);
            System.out.println(params.getState(0));
            System.out.println(fromPos.clone());
            System.out.println(params.getState(tof));
            System.out.println(toPos);*/
            
            double paramsStartTime = 0;
            for(int j = 0; j < tof;) {
                sim.step();
                j += sim.getStepSize();

                double error = sim.getProbe().getDistance(params.getState(j - paramsStartTime).getKey().add(center.getPosition()));
                //System.out.println(params.getState(j - paramsStartTime).getKey());
                if(Double.isNaN(error)) {
                    System.out.println(params);
                    System.out.println(params.getState(0));
                    System.out.println(params.getState(j - paramsStartTime).getKey());
                    System.out.println(params.getState(j - paramsStartTime).getKey().add(center.getPosition()));
                    System.exit(0);
                }
                double dist = sim.getProbe().getDistance(to.getState(startTime + j).getKey());

                if(error > 1) {
                    Pair<Vector3, Vector3> newSol = solver.solve(center, sim.getProbe().getPosition().clone().sub(center.getPosition()), toPos, tof - j);
                    //System.out.println("Mid-flight correction at " + j + "s: " + newSol.getKey() + " " + newSol.getValue());
                    Vector3 vel = newSol.getKey().add(center.getVelocity());
                    deltaV += vel.clone().sub(sim.getProbe().getVelocity()).length();
                    sim.getProbe().setVelocity(vel);
                    //System.out.println("Dot: " + vel.dot(toPos.clone().add(centerEndPos).sub(sim.getProbe().getPosition())));
                    sim.updateState();
                    params = new OrbitalParams(center, sim.getProbe().getPosition(), sim.getProbe().getVelocity());
                    paramsStartTime = j;
                }

                //System.out.println("Time left: " + (tof - j) + "s Distance to target: " + dist + " (" + error + ")");
                if(dist < minDist) {
                    minDist = dist;
                }
            }
            //sim.advance(tof);

            if(i == path.size() - 2) {
                System.out.println("Expected error: " + to.getDistance(toPos.clone().add(centerEndPos)));
                System.out.println(toPos.length());
            }

            prevVel = arrivalVel;
            totalTof += tof;
        }

        System.out.println(sim.getProbe().getDistance(sim.get("Titan")));
        System.out.println(deltaV);

        /*if(totalTof > 365 * 24 * 60 * 60) {
            cost += (totalTof - 365 * 24 * 60 * 60); // Penalize long flights
            if(PhysUtils.DEBUG) {
                System.out.printf("Cost for long flight: %.3f (totalTof: %.3f)\n", totalTof - 365 * 24 * 60 * 60, totalTof);
            }
        }*/

        int itersLeft = 250;
        double minDist = Double.MAX_VALUE;
        while(itersLeft > 0) {
            itersLeft--;
            sim.step();
            double dist = sim.getProbe().getDistance(path.getLast());
            if(dist < minDist) {
                //System.out.println(dist);
                minDist = dist;
                itersLeft = 250;
            }
        }
        cost = minDist;

        if(cost < minCost) {
            if(i++ % 1 == 0)
                System.out.println("New minimum cost found: " + cost + " " + x);
            minCost = cost;
        }

        System.exit(0);

        return cost;
    }

    @Override
    public Vector computeInitialGuess() {
        Vector initialGuess = new Vector(path.size() * 2 + path.size() - 1);
        for(int i = 0; i < path.size() - 1; i++) {
            Planet from = path.get(i);
            Planet to = path.get(i + 1);
            double dist = from.getDistance(to);
            initialGuess.set(i * 3 + 2, dist / 60); // Suggest 60 km/s 
        }
        return initialGuess;
    }
}
