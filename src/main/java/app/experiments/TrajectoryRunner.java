package app.experiments;

import java.util.List;

import app.phys.PlanetarySystem;
import app.phys.optimizer.GeneticAlgorithmOptimizerStrategy;
import app.phys.optimizer.GeneticAlgorithmParams;
import app.phys.optimizer.HillClimbOptimizerStrategy;
import app.phys.optimizer.HillClimbParams;
import app.phys.optimizer.OptimizerStrategy;
import app.phys.trajectory.FlybyState;
import app.phys.trajectory.InterplanetaryTrajectoryStrategy;
import app.utils.PhysUtils;
import app.utils.Vector;

public class TrajectoryRunner {
    public static void main(String[] args) {
        PhysUtils.DEBUG = true;
        PlanetarySystem system = new PlanetarySystem();
        system.setStepSize(10);

        /*List<FlybyState> path = List.of(
                new FlybyState("Earth", system.get("Earth").getRadius()),
                new FlybyState("Earth"),
                new FlybyState("Saturn"),
                new FlybyState("Titan"),
                new FlybyState("Titan", system.get("Titan").getRadius() + 200));*/

        List<FlybyState> path = List.of(
                new FlybyState("Earth", system.get("Earth").getRadius() + 200),
                new FlybyState("Earth"),
                new FlybyState("Saturn"),
                new FlybyState("Titan"),
                new FlybyState("Titan", system.get("Titan").getRadius() + 200),
                new FlybyState("Titan"),
                new FlybyState("Saturn"),
                new FlybyState("Earth"),
                new FlybyState("Earth", system.get("Earth").getRadius() + 200)
        );


        InterplanetaryTrajectoryStrategy strategy = new InterplanetaryTrajectoryStrategy(system, path);
        Vector state = strategy.computeInitialGuess();
        for (double i = 100000000; i >= 0.00001; i *= 0.1) {
            System.out.println(i);

            OptimizerStrategy optimizer;
            if (true) {
                double range = i;
                Vector lowerBound = state.clone();
                Vector upperBound = state.clone();
                for (int j = 0; j < state.size(); j++) {
                    lowerBound.set(j, state.get(j) - range);
                    upperBound.set(j, state.get(j) + range);
                }

                GeneticAlgorithmParams params = new GeneticAlgorithmParams.Builder()
                        .populationSize(2500)
                        .crossoverRate(0.9)
                        .mutationRate(0.1)
                        .maxGenerations(500)
                        .elitismCount(75)
                        .lowerBound(lowerBound)
                        .upperBound(upperBound)
                        .build();
                optimizer = new GeneticAlgorithmOptimizerStrategy(strategy::costFunction, state, params);
            } else {
                HillClimbParams params = new HillClimbParams.Builder()
                        .lookAroundSize(1)
                        .stepSize(i)
                        .maxIterations(9999999)
                        .build();
                optimizer = new HillClimbOptimizerStrategy(strategy::costFunction, state, params);
            }

            state = optimizer.solve();
            // Normalize the state
            for (int j = 0; j < state.size(); j++) {
                if (j % 3 == 2) { // Time multiplier
                    // state.set(j, Math.clamp(state.get(j), 0.1, 500));
                } else { // Angle
                    // state.set(j, Math.clamp(state.get(j), -1, 1));
                }
            }
            System.out.println("Current state: " + state);
            System.out.println("Cost: " + strategy.costFunction(state));
        }

        var solution = strategy.getSolution(state);
        try {
            java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(
                new java.io.FileOutputStream("mission2.dat"));
            oos.writeObject(solution);
            oos.close();
        } catch (java.io.IOException e) {
            System.err.println("Error saving solution: " + e.getMessage());
        }
        
        // System.exit(0);

        /*
         * InterplanterySimulationTrajectoryStrategy simulationStrategy = new
         * InterplanterySimulationTrajectoryStrategy(system, path);
         * HillClimbParams params = new HillClimbParams.Builder()
         * .lookAroundSize(2)
         * .stepSize(0.0001)
         * .maxIterations(9999999)
         * .build();
         * HillClimbOptimizerStrategy optimizer = new
         * HillClimbOptimizerStrategy(simulationStrategy::costFunction, state, params);
         * simulationStrategy.costFunction(state);
         */
        // state = optimizer.solve();
    }
}
