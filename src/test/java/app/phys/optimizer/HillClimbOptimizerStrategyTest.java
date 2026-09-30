package app.phys.optimizer;

import app.utils.RawVector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import app.utils.Vector;


public class HillClimbOptimizerStrategyTest {

    @Test
    void solveQuadraticFunction() {
        // Create a simple quadratic function to minimize: f(x,y) = x^2 + y^2
        Vector initialState = new Vector(new double[] {5.0, 5.0});
        HillClimbParams params = new HillClimbParams.Builder()
                .stepSize(0.5)
                .lookAroundSize(2)
                .maxIterations(100)
                .build();

        var optimizer = new HillClimbOptimizerStrategy(
                RawVector::lengthSquared, // x^2 + y^2
                initialState,
                params
        );

        Vector solution = optimizer.solve();

        Assertions.assertEquals(0.0, solution.get(0), 0.5, "X coordinate should be close to 0");
        Assertions.assertEquals(0.0, solution.get(1), 0.5, "Y coordinate should be close to 0");
    }

    @Test
    void solveBowlFunction() {
        // f(x,y) = (x-3)^2 + (y-4)^2
        Vector initialState = new Vector(new double[] {0.0, 0.0});
        HillClimbParams params = new HillClimbParams.Builder()
                .stepSize(0.5)
                .lookAroundSize(2)
                .maxIterations(100)
                .build();

        var optimizer = new HillClimbOptimizerStrategy(
                v -> Math.pow(v.get(0) - 3, 2) + Math.pow(v.get(1) - 4, 2),
                initialState,
                params
        );

        Vector solution = optimizer.solve();

        Assertions.assertEquals(3.0, solution.get(0), 0.5, "X coordinate should be close to 3");
        Assertions.assertEquals(4.0, solution.get(1), 0.5, "Y coordinate should be close to 4");
    }

    @Test
    void testLookAroundSizeEffect() {
        Vector initialState = new Vector(new double[] {1.0, 1.0});

        HillClimbParams smallLookParams = new HillClimbParams.Builder()
                .stepSize(0.2)
                .lookAroundSize(1)
                .maxIterations(50)
                .build();

        var smallLookOptimizer = new HillClimbOptimizerStrategy(
                v -> Math.sin(v.get(0)) * Math.cos(v.get(1)),
                initialState.clone(),
                smallLookParams
        );

        HillClimbParams largeLookParams = new HillClimbParams.Builder()
                .stepSize(0.2)
                .lookAroundSize(3)
                .maxIterations(50)
                .build();

        var largeLookOptimizer = new HillClimbOptimizerStrategy(
                v -> Math.sin(v.get(0)) * Math.cos(v.get(1)),
                initialState.clone(),
                largeLookParams
        );

        Vector smallLookSolution = smallLookOptimizer.solve();
        Vector largeLookSolution = largeLookOptimizer.solve();


        Assertions.assertNotEquals(
                smallLookSolution.get(0), largeLookSolution.get(0),
                "Different look around sizes should produce different solutions"
        );
    }
}
