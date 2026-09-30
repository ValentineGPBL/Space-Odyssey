package app.solver;

import java.util.function.BiFunction;

import app.utils.*;

/**
 * Interface for an ODE solver
 */
public abstract class Solver<T extends RawVector<T>> {

    protected static final double DEFAULT_STEP_SIZE = 0.01;

    protected BiFunction<Double, T, T> fn;
    private double x; // The independent variable (often time)
    private long stepCount = 0; // The number of steps taken
    protected T y; // The current state of the system
    protected double stepSize;

    /**
     * Constructs a new solver with the default step size and the specified initial values.
     *
     * @param fn the function to solve
     * @param x0 the initial x value
     * @param y0 the initial state of the system
    */
    public Solver(BiFunction<Double, T, T> fn, double x0, T y0) {
        this(fn, DEFAULT_STEP_SIZE, x0, y0);
    }

    /**
     * Constructs a new solver with the specified step size and initial values.
     *
     * @param fn the function to solve
     * @param stepSize the step size
     * @param x0 the initial x value
     * @param y0 the initial state of the system
    */
    public Solver(BiFunction<Double, T, T> fn, double stepSize, double x0, T y0) {
        if(stepSize <= 0) {
            throw new IllegalArgumentException("Step size must be positive");
        }

        this.fn = fn;
        this.x = x0;
        this.y = y0;
        this.stepSize = stepSize;
    }

    /**
     * Perform a single step
     * 
     * @return the new y value
     */
    public abstract T step();

    /**
     * Step until a certain x value is reached
     * 
     * @param xn the final x value
     * @return the final y value
     */
    public T solve(double xn) {      
        while (getX() < xn)
            step();
        
        return this.y;
    }

    /**
     * Get the current x value.
     */
    public double getX() {
        return x + stepSize * stepCount;
    }

    /**
     * Set the current x value.
     */
    public void setX(double x) {
        this.x = x;
        this.stepCount = 0;
    }

    /**
     * Increases the x value by one step
     * 
     * @return The new x value
     */
    protected double stepX() {
        return x + stepSize * (++this.stepCount);
    }

    /**
     * Get the current state of the system.
     */
    public T getY() {
        return y;
    }

    /**
     * Set the current state of the system.
     */
    public void setY(T y) {
        this.y = y;
    }

    /**
     * Get the step size.
     */
    public double getStepSize() {
        return stepSize;
    }

    /**
     * Set the step size.
     */
    public void setStepSize(double stepSize) {
        if(stepSize <= 0) {
            throw new IllegalArgumentException("Step size must be positive");
        }
        this.setX(this.getX()); // Reset the step counter
        this.stepSize = stepSize;
    }
    
}
