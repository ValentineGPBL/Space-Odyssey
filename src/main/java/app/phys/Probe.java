package app.phys;

import app.utils.Vector3;

public class Probe extends Entity {

    private final FeedbackController feedbackController;

    private Vector3 orientation;
    private double fuelConsumed = 0;

    private double angle = 0;
    private double angularVelocity = 0;

    /**
     * Constructs a Probe with specified position, velocity, mass, and radius.
     * @param position An initial position for the probe
     * @param velocity An initial velocity for the probe
     * @param mass The mass of the probe in kilograms
     * @param radius The (approximate) radius of the probe in kilometers
     */
    public Probe(Vector3 position, Vector3 velocity, double mass, double radius) {
        super("Probe", position, velocity, mass, radius);
        this.feedbackController = new FeedbackController(this);
    }

    /**
     * Gets the orientation of the probe.
     * @return The orientation vector of the probe.
     */
    public Vector3 getOrientation() {
        return orientation;
    }

    /**
     * Sets the orientation of the probe.
     * @param orientation The new orientation vector.
     */
    public void setOrientation(Vector3 orientation) {
        this.orientation = orientation;
    }

    /**
     * Applies thrust given an acceleration vector.
     * @param impulse The acceleration vector to apply as thrust.
     */
    public void applyThrust(Vector3 impulse) {
        getVelocity().add(impulse);
        fuelConsumed += impulse.length() * getMass(); // Assuming fuel consumption is proportional to the impulse applied
    }

    /**
     * Applies thrust given a delta v. Thrusts in its current direction.
     * @param impulse The given acceleration (or delta v)
     */
    public void applyThrust(double impulse) {
        getVelocity().add(orientation.clone().mul(impulse));
        fuelConsumed += impulse * getMass();
    }

    public void applyThrust2D(double impulse) {
        double x = Math.cos(angle);
        double y = Math.sin(angle);
        Vector3 thrust = new Vector3(x, 0, y).normalize().mul(impulse);
        getVelocity().add(thrust);
        fuelConsumed += impulse * getMass();
    }

    public void applyRotation(double amount) {
        if (amount > 1) {
            amount = 1;
        }
        else if (amount < -1) {
            amount = -1;
        }
        angularVelocity += amount;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double value) {
        angle = value;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    /**
     * Gets the amount of fuel consumed by the probe.
     * @return The total fuel consumed.
     */
    public double getFuelConsumed() {
        return fuelConsumed;
    }

    public Vector3 getControllerThrust(double dt) {
        return feedbackController.getCounterThrust(dt);
    }

    public FeedbackController getFeedbackController() {
        return feedbackController;
    }

    public void updateFeedbackController(Vector3 position, Vector3 velocity) {
        feedbackController.setOldProbePosition(position);
        feedbackController.setOldProbeVelocity(velocity);
    }

    @Override
    public Probe clone() {
        return new Probe(getPosition().clone(), getVelocity().clone(), getMass(), getRadius());
    }

}