package app.experiments;

import app.phys.optimizer.*;
import app.utils.Vector;

import java.util.function.Function;

public class OptimizerExperiment {
    public static void main(String[] args) {
        //f(x, y) = (x - 3)^2 + (y + 2)^2
        Function<Vector, Double> costFunction = v -> {
            double x = v.get(0);
            double y = v.get(1);
            return Math.pow(x - 3, 2) + Math.pow(y + 2, 2); // min at (3, -2)
        };

        Vector initialState = new Vector(new double[]{0.0, 0.0});

        GradientDescentParams params = GradientDescentParams.builder()
                .learningRate(0.1)
                .momentum(0.9)
                .maxIterations(1000)
                .epsilon(1e-6)
                .build();

        GradientDescentOptimizerStrategy optimizer = new GradientDescentOptimizerStrategy(costFunction, initialState, params);
         Vector solution = optimizer.solve();

        System.out.println("Found solution: " + solution);
        double finalCost = costFunction.apply(solution);
        System.out.println("Final cost: " + finalCost);

        Vector trueMinimum = new Vector(new double[]{3.0, -2.0});
        double error = solution.sub(trueMinimum).length();
        System.out.println("Error: " + error);
    }
}
