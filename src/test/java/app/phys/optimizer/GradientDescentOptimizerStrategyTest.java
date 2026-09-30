package app.phys.optimizer;

import app.utils.RawVector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import app.utils.Vector;

/**
 * Tests for the GradientDescentOptimizerStrategy class.
 */
public class GradientDescentOptimizerStrategyTest {

    @Test
    void solveQuadraticFunction() {
        // Create a simple quadratic function to minimize: f(x,y) = x^2 + y^2
        Vector initialState = new Vector(new double[] {5.0, 5.0});
        GradientDescentParams params = new GradientDescentParams.Builder()
                .learningRate(0.1)
                .maxIterations(1000)
                .build();

        var optimizer = new GradientDescentOptimizerStrategy(
                RawVector::lengthSquared, // x^2 + y^2
                initialState,
                params
        );

        Vector solution = optimizer.solve();

        // The minimum of x^2 + y^2 is at (0,0)
        Assertions.assertEquals(0.0, solution.get(0), 1e-2, "X coordinate should be close to 0");
        Assertions.assertEquals(0.0, solution.get(1), 1e-2, "Y coordinate should be close to 0");
    }


    @Test
    void testParameterEffects() {
        // Test that different parameter settings affect convergence
        Vector initialState = new Vector(new double[] {10.0, 10.0});

        GradientDescentParams fastParams = new GradientDescentParams.Builder()
                .learningRate(0.1)
                .maxIterations(10)
                .build();

        var fastOptimizer = new GradientDescentOptimizerStrategy(
                RawVector::lengthSquared,
                initialState.clone(),
                fastParams
        );


        GradientDescentParams slowParams = new GradientDescentParams.Builder()
                .learningRate(0.01)
                .maxIterations(5)
                .build();

        var slowOptimizer = new GradientDescentOptimizerStrategy(
                RawVector::lengthSquared,
                initialState.clone(),
                slowParams
        );

        Vector fastSolution = fastOptimizer.solve();
        Vector slowSolution = slowOptimizer.solve();

        Assertions.assertTrue(
                fastSolution.lengthSquared() < slowSolution.lengthSquared(),
                "Optimizer with higher learning rate and more iterations should converge better"
        );
    }
}
