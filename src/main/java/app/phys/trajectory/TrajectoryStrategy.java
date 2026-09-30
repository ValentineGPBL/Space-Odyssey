package app.phys.trajectory;

import app.utils.Vector;

public interface TrajectoryStrategy {
    public abstract double costFunction(Vector x);
    public abstract Vector computeInitialGuess();
}
