package app.experiments;

import app.phys.*;
import app.phys.optimizer.HillClimbOptimizerStrategy;
import app.phys.optimizer.HillClimbParams;
import app.phys.trajectory.CrashTrajectoryStrategy;
import app.utils.*;

class HillClimbRunner {

    private static void makeTest() {
        PlanetarySystem system = new PlanetarySystem();
        CrashTrajectoryStrategy strategy = new CrashTrajectoryStrategy(system, system.get("Earth"), system.get("Titan"), system.getProbe());
        Vector state = strategy.computeInitialGuess();
        //Vector state = new Vector(new double[] {2.5744068308951356,-2.8869355073145933});
        double timeStep = 30720;
        double stepSize = 0.59049;
        while(true) {
            system.setStepSize(timeStep);
            System.out.println("Clear");
            HillClimbParams params = new HillClimbParams.Builder()
                .stepSize(stepSize)
                .maxIterations(10000)
                .tolerance(1e-6)
                .lookAroundSize(3)
                .build();
            HillClimbOptimizerStrategy optimizer = new HillClimbOptimizerStrategy(strategy::costFunction, state, params);

            state = optimizer.solve();
            stepSize /= 3;
            timeStep /= 2;

            /*if(stepSize < 1e-3) {
                timeStep = 60;
            }*/

            try { Thread.sleep(5000) ; } catch (InterruptedException e) { break; }
        }
    }

    public static void main(String[] args) {
        PhysUtils.DEBUG = true;
        makeTest();
    }

}