package app.experiments;

import app.phys.LandingSimulation2D;
import app.utils.Vector3;

public class LandingExperiment {
    public static void main(String[] args) {
        int numExperiments = 10;
        double dt = 0.1;
        for (int i = 0; i < numExperiments; i++) {
            LandingSimulation2D simulation = new LandingSimulation2D();
            double elapsedTime = 0;
            while (!simulation.isLanded() && elapsedTime < 800) {
                simulation.update(dt);
                elapsedTime += dt;
            }
            Vector3 finalPosition = simulation.getProbe().getPosition();
            Vector3 finalVelocity = simulation.getProbe().getVelocity();
            Vector3 finalWindAccel = simulation.getWindAccelerationAtCurrentState();
            System.out.println("Experiment " + (i + 1) + ":");
            System.out.println("Final Position: " + finalPosition);
            System.out.println("Final Velocity: " + finalVelocity);
            System.out.println("Wind Acceleration: " + finalWindAccel);
            System.out.println("Elapsed Time: " + elapsedTime + " seconds");
            System.out.println("---------------------------------------");
        }
    }
}