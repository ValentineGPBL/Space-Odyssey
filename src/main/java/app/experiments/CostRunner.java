package app.experiments;

import java.util.ArrayList;

import app.phys.Entity;
import app.phys.PlanetarySystem;
import app.utils.PhysUtils;
import app.utils.Vector;
import app.utils.Vector3;

public class CostRunner {
    
    private static PlanetarySystem system = new PlanetarySystem();
    private static int startIndex = system.indexOf(system.get("Earth"));
    private static int targetIndex = system.indexOf(system.get("Mars"));
    private static int probeIndex = system.indexOf(system.getProbe());

    public static void main(String[] args) {
        ArrayList<Vector> queue = new ArrayList<>();
        double range = 0.0025;
        double offsetX = 2.348;
        double offsetY = 6.335;
        for(double i = -range / 2; i < range / 2; i += range / 100) {
            for(double j = -range / 2; j < range / 2; j += range / 100) {
                queue.add(new Vector(new double[] {i + offsetX, j + offsetY}));
            }
        }
        java.util.Collections.shuffle(queue);

        int numThreads = Runtime.getRuntime().availableProcessors();
        System.out.println("Using " + numThreads + " threads.");
        Thread[] threads = new Thread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            final int threadIndex = i;
            threads[i] = new Thread(() -> {
                for (int j = threadIndex; j < queue.size(); j += numThreads) {
                    Vector v = queue.get(j);
                    double cost = costFunction(v);
                    System.out.println(v.get(0) + "," + v.get(1) + "," + cost);
                }
            });
            threads[i].start();
        }
    }

    private static void setProbe(PlanetarySystem system, Vector x) {
        Vector3 angle = PhysUtils.fromAzimuthElevation(x);
        system.get(probeIndex).setPosition(system.get(startIndex).getPosition().clone().add(angle.clone().mul(system.get(startIndex).getRadius())));
        system.get(probeIndex).setVelocity(angle.clone().mul(60)); // 600 km/s
        system.updateState();
    }

    private static double costFunction(Vector x) {
        PlanetarySystem sim = system.clone();
        Entity targetObj = sim.get(targetIndex);
        Entity probeObj = sim.get(probeIndex);

        setProbe(sim, x);

        double estimatedTime = targetObj.getDistance(probeObj) / 60;
        double targetDist = targetObj.getRadius() * 0.5;
        double minDist = Double.MAX_VALUE;
        int itersLeft = 25;

        for(int y = 0; y < 1000000; y++) {
            sim.step();
            double distance = targetObj.getDistance(probeObj);

            if(distance < targetDist) {
                return 0;
            }
            if (distance > minDist && sim.getTime() > estimatedTime * 0.9) {
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

}
