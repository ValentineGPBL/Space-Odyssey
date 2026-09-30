package app.phys.wind;

import app.utils.Vector3;
import java.util.Random;

/**
 * A stochastic wind model with temporal correlation (random walk).
 * Acceleration changes as: aₜ = α·aₜ₋₁ + σ·√(1−α²)·N(0,I).
 */
public class RandomWalkWindModel implements WindModel {
    private final double alpha;
    private final double sigma;
    private final Random randomGenerator;
    private Vector3 prevAcceleration = new Vector3(0, 0, 0);

    /**
     * @param sigma standard deviation of the noise (km/s²)
     * @param alpha correlation factor (0≤α<1)
     */
    public RandomWalkWindModel(double sigma, double alpha) {
        this.alpha = alpha;
        this.sigma = sigma;
        this.randomGenerator = new Random();
    }

    @Override
    public Vector3 getWindAcceleration(double time, Vector3 position, Vector3 velocity) {
        prevAcceleration.mul(alpha);    // decay previous value
        double noiseScale = sigma * Math.sqrt(1 - alpha*alpha);     // fresh noise amplitude

        Vector3 noise = new Vector3(
                randomGenerator.nextGaussian() * noiseScale,
                randomGenerator.nextGaussian() * noiseScale,
                randomGenerator.nextGaussian() * noiseScale
        );

        prevAcceleration.add(noise);

        return prevAcceleration.clone();
    }

}
