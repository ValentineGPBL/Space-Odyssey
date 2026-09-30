package app.solver;

import java.util.function.BiFunction;

import app.utils.RawVector;

/**
 * Runge-Kutta 4th order solver
 */
public class RK4<T extends RawVector<T>> extends Solver<T> {

    /**
     * Preallocated temporary vector for intermediate calculations
     */
    private T yTemp;

    /**
     * Constructs a new RK4 solver with the default step size and the specified initial values.
     * 
     * @param fn the function to solve
     * @param x0 the initial x value
     * @param y0 the initial y value
     */
    public RK4(BiFunction<Double, T, T> fn, double x0, T y0) {
        super(fn, x0, y0);
        yTemp = y0.clone();
    }

    /**
     * Constructs a new RK4 solver with the specified step size and initial values.
     * 
     * @param fn the function to solve
     * @param stepSize the step size
     * @param x0 the initial x value
     * @param y0 the initial y value
     */
    public RK4(BiFunction<Double, T, T> fn, double stepSize, double x0, T y0) {
        super(fn, stepSize, x0, y0);
        yTemp = y0.clone();
    }

    @Override
    public T step() {
        double x = this.getX();
        T k1 = fn.apply(x, y);
        T k2 = fn.apply(x + stepSize / 2, y.copyTo(yTemp).axpy(stepSize / 2, k1));
        T k3 = fn.apply(x + stepSize / 2, y.copyTo(yTemp).axpy(stepSize / 2, k2));        
        T k4 = fn.apply(x + stepSize, y.copyTo(yTemp).axpy(stepSize, k3));

        y.axpy(stepSize / 6, k1);
        y.axpy(stepSize / 3, k2);
        y.axpy(stepSize / 3, k3);
        y.axpy(stepSize / 6, k4);

        stepX();
        return y;
    }

}
