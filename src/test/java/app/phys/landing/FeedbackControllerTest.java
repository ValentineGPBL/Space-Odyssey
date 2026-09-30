package app.phys.landing;

import app.phys.FeedbackController;
import app.phys.Probe;
import app.utils.Vector3;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FeedbackControllerTest {

    @Test
    void testGetCounterThrustWithoutSaturation() {
        // Create a probe with fixed position and velocity
        Vector3 position = new Vector3(0, 10, 0);
        Vector3 velocity = new Vector3(0, -1, 0);
        Probe probe = new Probe(position, velocity, 1000, 1);
        probe.setOrientation(new Vector3(0, 0, 0));

        FeedbackController controller = probe.getFeedbackController();
        // Use dt = 1.0 second for the PID calculation
        Vector3 thrust = controller.getCounterThrust(1.0);

        // Expected thrust computation:
        // errorY = -10, integralErrorY = -10*1.0 = -10.
        // a_des_y = 1.0 * (-10) + 1.0*(1) + 0.1*(-10) + 1.352 = -10 + 1 - 1 + 1.352 = -8.648
        // a_des_x = 0.
        assertEquals(0.0, thrust.getX(), 1e-3);
        assertEquals(-13.52, thrust.getY(), 1e-3);
        assertEquals(0.0, thrust.getZ(), 1e-3);
    }

    @Test
    void testGetTorque() {
        Vector3 position = new Vector3(0, 10, 0);
        Vector3 velocity = new Vector3(0, -1, 0);
        Probe probe = new Probe(position, velocity, 1000, 1);
        probe.setOrientation(new Vector3(0, 0, 0));

        FeedbackController controller = probe.getFeedbackController();
        // Call getCounterThrust with dt to compute desiredTheta.
        controller.getCounterThrust(1.0);

        double torque = controller.getTorque();
        double expectedTorque = 1;
        assertEquals(expectedTorque, Math.abs(torque), 1e-3);
    }
}