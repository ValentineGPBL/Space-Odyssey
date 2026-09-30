package app.phys.wind;

import app.utils.Vector3;
import java.util.Random;

/**
 * A simple stochastic wind model producing white-noise acceleration in 3D.
 * Each component is drawn from N(0, σ²), where σ is the provided standard deviation.
 */
public class GaussianWindModel implements WindModel {
    private final Random randomGenerator;
    private final double sigma; // std deviation of the wind-acceleration (km/s²)

    /**
     * @param sigma standard deviation of acceleration noise (km/s²)
     */
    public GaussianWindModel(double sigma) {
        this.sigma = sigma;
        this.randomGenerator = new Random();
    }

    /**
     * Generate a wind-induced acceleration vector.
     *
     * @param time      current simulation time in seconds (unused)
     * @param position  current position in km (unused)
     * @param velocity  current velocity in km/s (unused)
     * @return          a 3D acceleration vector (km/s²) due to white-noise wind
     */
    @Override
    public Vector3 getWindAcceleration(double time, Vector3 position, Vector3 velocity) {
        double ax = sigma * randomGenerator.nextGaussian();
        double ay = sigma * randomGenerator.nextGaussian();
        double az = sigma * randomGenerator.nextGaussian();

        return new Vector3(ax, ay, az);
    }

}
