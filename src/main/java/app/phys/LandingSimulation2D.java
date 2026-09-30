package app.phys;

import app.phys.wind.AltitudeScalingWindModel;
import app.phys.wind.CompositeWindModel;
import app.phys.wind.GaussianWindModel;
import app.phys.wind.RandomWalkWindModel;
import app.utils.Matrix;
import app.utils.Vector3;

public class LandingSimulation2D extends Simulation {
    private final Probe probe;
    private final CombinedController combinedController;
    private final Vector3 gravity = new Vector3(0, -1.352, 0);
    private double angularVelocity = 0.0;
    private boolean landed = false;

    private final double deltaX = 0.1;
    private final double deltaTheta = 0.02;
    private final double epsilonX = 0.1;
    private final double epsilonY = 0.1;
    private final double epsilonTheta = 0.01;

    private final app.phys.wind.WindModel windModel;

    public LandingSimulation2D() {
        super(new Entity[]{});
        Vector3 initialPosition = new Vector3(0, 200, 0);
        Vector3 initialVelocity = new Vector3(0, -3.0, 0);
        double mass = 1000;
        double radius = 1;
        this.probe = new Probe(initialPosition, initialVelocity, mass, radius);
        probe.setOrientation(new Vector3(0, 0, 0));
        this.combinedController = new CombinedController(probe);

        Vector3 titanCenter = new Vector3(0, 0, 0);
        double titanRadius = 20;
        // Increase wind strength: Use higher sigma values for more visible lateral effects.
        this.windModel = new AltitudeScalingWindModel(
                new CompositeWindModel(
                        new RandomWalkWindModel(0.015, 0.95),
                        new GaussianWindModel(0.015)
                ),
                titanCenter, titanRadius, 2000.0
        );
    }

    public void update(double deltaTime) {
        if (landed) {
            return;
        }
        Vector3 position = probe.getPosition();
        Vector3 velocity = probe.getVelocity();
        double currentTheta = probe.getOrientation().getZ();

        if (Math.abs(position.getY()) <= 0.1 &&
                Math.abs(position.getX()) <= deltaX &&
                Math.abs(velocity.getX()) <= epsilonX &&
                Math.abs(velocity.getY()) <= epsilonY &&
                Math.abs((currentTheta % (2 * Math.PI))) <= deltaTheta &&
                Math.abs(angularVelocity) <= epsilonTheta) {
            probe.setPosition(new Vector3(0, 0, 0));
            probe.setVelocity(new Vector3(0, 0, 0));
            angularVelocity = 0.0;
            probe.setOrientation(new Vector3(0, 0, 0));
            landed = true;
            return;
        }

        Vector3 windAccel = windModel.getWindAcceleration(0, position, velocity);
        Vector3 gravityAccel = gravity;
        Vector3 thrustForce = combinedController.getCombinedThrust(deltaTime, this);
        Vector3 totalAccel = gravityAccel.add(windAccel).add(thrustForce);
        Vector3 newVelocity = velocity.add(totalAccel.mul(deltaTime));
        Vector3 newPosition = position.add(newVelocity.mul(deltaTime));

        if (newPosition.getY() <= 0) {
            newPosition = new Vector3(newPosition.getX(), 0, newPosition.getZ());
            newVelocity = new Vector3(newVelocity.getX(), 0, newVelocity.getZ());
            probe.setPosition(newPosition);
            probe.setVelocity(newVelocity);
            angularVelocity = 0.0;
            probe.setOrientation(new Vector3(0, 0, 0));
            landed = true;
            return;
        }

        probe.setPosition(newPosition);
        probe.setVelocity(newVelocity);

        double netTorque = combinedController.getTorque(this);
        angularVelocity += netTorque * deltaTime;
        double newTheta = currentTheta + angularVelocity * deltaTime;
        probe.setOrientation(new Vector3(0, 0, newTheta));

        combinedController.updateFeedback(deltaTime, newPosition, newVelocity);
    }

    public boolean isLanded() {
        return landed;
    }

    public Probe getProbe() {
        return probe;
    }

    @Override
    public Matrix getState() {
        Matrix state = new Matrix(5, 3);
        state.setRow(0, probe.getPosition());
        state.setRow(1, probe.getVelocity());
        state.setRow(2, new Vector3(0, 0, probe.getOrientation().getZ()));
        state.setRow(3, new Vector3(0, 0, 0));
        state.setRow(4, new Vector3(0, 0, 0));
        return state;
    }

    public Vector3 getWindAccelerationAtCurrentState() {
        return windModel.getWindAcceleration(0, probe.getPosition(), probe.getVelocity());
    }
}