package app.phys.lambert;

import app.phys.Entity;
import app.utils.Vector3;
import javafx.util.Pair;

public interface LambertSolver {
    
    /**
     * Solves the Lambert problem for the given parameters.
     *
     * @param center the central body around which the trajectory is computed
     * @param r1 the initial position vector
     * @param r2 the final position vector
     * @param tof the time of flight
     * @return an array containing the velocity vectors at the initial and final positions
     */
    Pair<Vector3, Vector3> solve(Entity center, Vector3 r1, Vector3 r2, double tof);

}
