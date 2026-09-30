package app.phys.trajectory;

import app.phys.Entity;
import app.phys.PlanetarySystem;
import app.utils.PhysUtils;
import app.utils.Vector;
import app.utils.Vector3;

public class CrashTrajectoryStrategy implements TrajectoryStrategy {

    private PlanetarySystem system;
    private int startIndex;
    private int targetIndex;
    private int probeIndex;

    public CrashTrajectoryStrategy(PlanetarySystem system, Entity start, Entity target, Entity probe) {
        this.system = system;
        this.startIndex = system.indexOf(start);
        this.targetIndex = system.indexOf(target);
        this.probeIndex = system.indexOf(probe);


        if (startIndex < 0 || targetIndex < 0 || probeIndex < 0) {
            throw new IllegalArgumentException("Start, target, or probe entity not found in the planetary system.");
        }
    }

    public void setupProbe(PlanetarySystem system, Vector x) {
        Vector3 angle = PhysUtils.fromAzimuthElevation(x);
        system.get(probeIndex).setPosition(system.get(startIndex).getPosition().clone().add(angle.clone().mul(system.get(startIndex).getRadius())));
        system.get(probeIndex).setVelocity(angle.clone().mul(PhysUtils.PROBE_SPEED)); // 600 km/s
        system.updateState();
    }

    @Override
    public double costFunction(Vector x) {
        PlanetarySystem sim = system.clone();
        Entity targetObj = sim.get(targetIndex);
        Entity probeObj = sim.get(probeIndex);

        setupProbe(sim, x);

        double estimatedTime = targetObj.getDistance(probeObj) / PhysUtils.PROBE_SPEED;
        double minDist = Double.MAX_VALUE;
        int itersLeft = 250;

        sim.advance(estimatedTime * 0.9);

        for(int y = 0; y < 1000000; y++) {
            sim.step();
            double distance = targetObj.getDistance(probeObj);

            /*if(distance < targetDist) {
                return 0;
            }*/
            if (distance > minDist) {
                if(--itersLeft <= 0) {
                    break; // Stop if we are not getting closer to the target object
                }
            } else {
                itersLeft = 250; // Reset the counter if we are getting closer
                minDist = distance; // Update the minimum distance
            }
        }

        return Math.log10(minDist); // Return the best distance to the target object
    }

    @Override
    public Vector computeInitialGuess() {
        return PhysUtils.computeInitialGuess(system, startIndex, targetIndex);
    }
    
}
