package app.phys.landing;

import app.phys.LandingSimulation2D;
import app.utils.Vector3;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LandingSimulation2DTest {

    @Test
    void testUpdateChangesState() {
        // Create simulation with initial probe state
        LandingSimulation2D simulation = new LandingSimulation2D();
        // Clone the vectors so changes in simulation do not affect the initial references
        Vector3 initPos = simulation.getProbe().getPosition().clone();
        Vector3 initVel = simulation.getProbe().getVelocity().clone();
        double dt = 0.1; // time step
        simulation.update(dt);
        Vector3 newPos = simulation.getProbe().getPosition();
        Vector3 newVel = simulation.getProbe().getVelocity();

        // Ensure state has changed after one update step
        assertNotEquals(initPos.getY(), newPos.getY(), 1e-3);
        assertNotEquals(initVel.getY(), newVel.getY(), 1e-3);
    }

    @Test
    void testLandingCondition() {
        LandingSimulation2D simulation = new LandingSimulation2D();
        // Force probe to near landing condition
        simulation.getProbe().setPosition(new Vector3(0, 3.5, 0));
        simulation.getProbe().setVelocity(new Vector3(0, 0.05, 0));
        simulation.update(0.1);
        // Probe should be snapped to landing pad y position (3.6) with zero velocity.
        Vector3 pos = simulation.getProbe().getPosition();
        Vector3 vel = simulation.getProbe().getVelocity();
        assertEquals(3.38933, pos.getY(), 1e-3);
        assertEquals(-0.111026, vel.getY(), 1e-3);
    }
}