package app.phys.landing;

import app.phys.CombinedController;
import app.phys.Probe;
import app.utils.Vector3;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CombinedControllerTest {

    @Test
    void testGetCombinedThrust() {
        Vector3 position = new Vector3(0, 10, 0);
        Vector3 velocity = new Vector3(0, -1, 0);
        Probe probe = new Probe(position, velocity, 1000, 1);
        probe.setOrientation(new Vector3(0, 0, 0));

        CombinedController combined = new CombinedController(probe);
        // Call the feedback controller once with dt = 1.0 to accumulate error.
        Vector3 expectedThrust = probe.getFeedbackController().getCounterThrust(1.0);
        // Now, call getCombinedThrust with dt = 0 so no additional accumulation occurs.
        Vector3 combinedThrust = combined.getCombinedThrust(0, null);

        assertEquals(expectedThrust.getX(), combinedThrust.getX(), 1e-3);
        assertEquals(expectedThrust.getY(), combinedThrust.getY(), 1e-3);
        assertEquals(expectedThrust.getZ(), combinedThrust.getZ(), 1e-3);
    }

    @Test
    void testGetTorque() {
        Vector3 position = new Vector3(0, 10, 0);
        Vector3 velocity = new Vector3(0, -1, 0);
        Probe probe = new Probe(position, velocity, 1000, 1);
        probe.setOrientation(new Vector3(0, 0, 0));

        CombinedController combined = new CombinedController(probe);
        double torque = combined.getTorque(null);
        double expectedTorque = probe.getFeedbackController().getTorque();
        assertEquals(expectedTorque, torque, 1e-3);
    }
}