package app;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.Assertions;

import app.phys.optimizer.GradientDescentOptimizerStrategy;
import app.phys.optimizer.GradientDescentParams;
import app.utils.*;

class OptimizerTest {

    @Test
    void solveQuadraticTest() {
        Vector initialState = new Vector(new double[] {1.0, 1.0});
        GradientDescentParams params = new GradientDescentParams.Builder().build();
        var optimizer = new GradientDescentOptimizerStrategy(v -> v.lengthSquared(), initialState, params);
        Vector solution = optimizer.solve();

        Assertions.assertEquals(0.0, solution.get(0), 1e-3);
        Assertions.assertEquals(0.0, solution.get(1), 1e-3);
    }

    @Test
    void solveRosenbrockTest() {
        Vector initialState = new Vector(new double[] {-2, 2});
        GradientDescentParams params = new GradientDescentParams.Builder().learningRate(0.001).build();
        var optimizer = new GradientDescentOptimizerStrategy(
            v -> Math.pow(1 - v.get(0), 2) + 100 * Math.pow(v.get(1) - Math.pow(v.get(0), 2), 2),
            initialState,
            params
        );
        Vector solution = optimizer.solve();

        Assertions.assertEquals(1.0, solution.get(0), 0.1);
        Assertions.assertEquals(1.0, solution.get(1), 0.1);
    }
    
}
