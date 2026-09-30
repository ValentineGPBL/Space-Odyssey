package app.solver;

import java.util.function.BiFunction;

import app.utils.RawVector;

/**
 * Euler's method for solving ODEs
 */
public class Euler<T extends RawVector<T>> extends Solver<T> {

    /**
     * Constructs a new Euler solver with the default step size and the specified initial values.
     * 
     * @param fn the function to solve
     * @param x0 the initial x value
     * @param y0 the initial y value
     */
    public Euler(BiFunction<Double, T, T> fn, double x0, T y0) {
        super(fn, x0, y0);
    }

    /**
     * Constructs a new Euler solver with the specified step size and initial values.
     * 
     * @param fn the function to solve
     * @param stepSize the step size
     * @param x0 the initial x value
     * @param y0 the initial y value
     */
    public Euler(BiFunction<Double, T, T> fn, double stepSize, double x0, T y0) {
        super(fn, stepSize, x0, y0);
    }
    
    @Override
    public T step() {
        double x = this.getX();
        y.add(fn.apply(x, y).mul(stepSize));
        stepX();
        return y;
    }

}
