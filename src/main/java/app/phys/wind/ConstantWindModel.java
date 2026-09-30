package app.phys.wind;

import app.utils.Vector3;

/**
 * Always returns the same fixed acceleration vector.
 */
public class ConstantWindModel implements WindModel {
    private final Vector3 constantAcceleration;

    /**
     * @param constantAcceleration the constant wind acceleration (km/s²)
     */
    public ConstantWindModel(Vector3 constantAcceleration) {
        this.constantAcceleration = constantAcceleration;
    }


    @Override
    public Vector3 getWindAcceleration(double time, Vector3 position, Vector3 velocity) {
        // wind acceleration is constant and doesn't depend on time, position, or velocity
        return constantAcceleration.clone();
    }
}
