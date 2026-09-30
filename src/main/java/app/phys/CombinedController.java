package app.phys;

import app.utils.Vector3;

public class CombinedController {
    private final Probe probe;
    private final FeedbackController feedbackController;
    private final OuterLoopController outerLoopController;

    public CombinedController(Probe probe) {
        this.probe = probe;
        this.feedbackController = probe.getFeedbackController();
        this.outerLoopController = new OuterLoopController(probe);
    }

    /**
     * Returns the net thrust vector by combining the PID feedback controller output and the open-loop controller.
     */
    public Vector3 getCombinedThrust(double dt, Simulation simulation) {
        Vector3 feedbackThrust = feedbackController.getCounterThrust(dt);
        Vector3 openLoopThrust = outerLoopController.controller(simulation);
        return feedbackThrust.add(openLoopThrust);
    }

    /**
     * Overloaded method for backward compatibility.
     */
    public Vector3 getCombinedThrust(Simulation simulation) {
        return getCombinedThrust(0, simulation);
    }

    /**
     * Returns the net torque command.
     */
    public double getTorque(Simulation simulation) {
        double fbTorque = feedbackController.getTorque();
        double olTorque = outerLoopController.getTorque(simulation);
        return fbTorque + olTorque;
    }

    /**
     * Updates feedback controller state with new position and velocity.
     */
    public void updateFeedback(double dt, Vector3 position, Vector3 velocity) {
        feedbackController.setOldProbePosition(position);
        feedbackController.setOldProbeVelocity(velocity);
    }

    /**
     * Overloaded update method for backward compatibility.
     */
    public void updateFeedback(Vector3 position, Vector3 velocity) {
        updateFeedback(0, position, velocity);
    }
}