package app.phys;

import app.utils.Vector3;

public class FeedbackController {
    private final Probe probe;
    // Further reduced horizontal gains to allow wind-induced lateral drift.
    private final double kpx = 0.01;
    private final double kdx = 0.01;
    // PD gains for vertical control (default)
    private final double kpy = 2.5;
    private final double kdy = 2.5;
    // PID integral gains
    private final double kix = 0.1;
    private final double kiy = 0.1;
    // Rotational gain
    private final double kpt = 1.5;
    // Titan gravity (m/s^2)
    private final double gTitan = 1.352;
    // Maximum thruster acceleration
    private final double umax = 10 * gTitan;
    // Maximum torque (rad/s^2)
    private final double vmax = 1.0;

    private double desiredTheta = 0.0;

    // Integral error accumulators
    private double integralErrorX = 0.0;
    private double integralErrorY = 0.0;

    public FeedbackController(Probe probe) {
        this.probe = probe;
    }

    /**
     * Computes the thrust vector command using a PID controller.
     * The dt parameter is used to accumulate the integral error.
     */
    public Vector3 getCounterThrust(double dt) {
        Vector3 pos = probe.getPosition();
        Vector3 vel = probe.getVelocity();

        double x = pos.getX();
        double y = pos.getY();
        double vx = vel.getX();
        double vy = vel.getY();

        // Compute errors based on target: x -> 0, y -> 0.
        double errorX = -x;
        double errorY = 0 - y;

        // Accumulate the integral error
        integralErrorX += errorX * dt;
        integralErrorY += errorY * dt;

        // Adjust vertical gains depending on altitude.
        double effectiveKpy = kpy;
        double effectiveKdy = kdy;
        if (y <= 0) {
            effectiveKpy = 50.0;
            effectiveKdy = 50.0;
        } else if (y < 2) {
            effectiveKpy = 20.0;
            effectiveKdy = 20.0;
        }

        // Compute desired accelerations and subtract gravity compensation.
        double a_des_x = kpx * errorX + kdx * (-vx) + kix * integralErrorX;
        double a_des_y = effectiveKpy * errorY + effectiveKdy * (-vy) + kiy * integralErrorY - gTitan;

        // Desired orientation corresponds to the thrust vector direction.
        desiredTheta = Math.atan2(a_des_x, a_des_y);
        double u = Math.sqrt(a_des_x * a_des_x + a_des_y * a_des_y);
        u = Math.min(u, umax);

        // Return thrust vector with u decomposed along the desired direction.
        return new Vector3(u * Math.sin(desiredTheta), u * Math.cos(desiredTheta), 0);
    }

    /**
     * Overloaded method for backward compatibility (dt set to 0).
     */
    public Vector3 getCounterThrust() {
        return getCounterThrust(0);
    }

    /**
     * Computes the torque command based on the error between the desired orientation and current orientation.
     * The torque is limited to vmax.
     */
    public double getTorque() {
        double currentTheta = probe.getOrientation().getZ();
        double error = desiredTheta - currentTheta;
        double rawTorque = kpt * error;
        return Math.max(Math.min(rawTorque, vmax), -vmax);
    }

    public void setOldProbePosition(Vector3 value) { }

    public void setOldProbeVelocity(Vector3 value) { }
}