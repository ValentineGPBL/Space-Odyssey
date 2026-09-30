package app.phys;

import app.utils.Vector3;

public class OuterLoopController {
    private final Probe probe;

    public OuterLoopController(Probe probe) {
        this.probe = probe;
    }

    /**
     * Since the PD controller in the feedback controller now computes both
     * the main thrust and the desired orientation, the outer loop is set to zero.
     */
    public Vector3 controller(Simulation simulation) {
        return new Vector3(0, 0, 0);
    }

    /**
     * No open-loop torque is applied.
     */
    public double getTorque(Simulation simulation) {
        return 0.0;
    }
}