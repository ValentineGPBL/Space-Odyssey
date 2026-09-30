package app.phys.trajectory;

import app.phys.Entity;
import app.phys.PlanetarySystem;
import app.utils.Vector;
import app.utils.Vector3;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;


public class CrashTrajectoryStrategyTest {

    private PlanetarySystem system;
    private Entity start;
    private Entity target;
    private Entity probe;
    private CrashTrajectoryStrategy strategy;

    @BeforeEach
    void setUp() {
        system = new PlanetarySystem();

        start = system.get("Earth");

        target = system.get("Mars");

        probe = system.getProbe();

        strategy = new CrashTrajectoryStrategy(system, start, target, probe);
    }

    @Test
    void testSetupProbe() {

        Vector params = new Vector(new double[] {0, 0}); // straight along x-axis

        strategy.setupProbe(system, params);

        Vector3 probePos = system.get(system.indexOf(probe)).getPosition();
        Vector3 startPos = system.get(system.indexOf(start)).getPosition();

        double distance = probePos.distanceTo(startPos);
        assertEquals(start.getRadius(), distance, 0.1, "Probe should be positioned at the surface of the start entity");

        Vector3 probeVel = system.get(system.indexOf(probe)).getVelocity();
        assertTrue(probeVel.getX() > 0, "Probe should be moving in the positive x direction");
        assertEquals(0, probeVel.getY(), 0.1, "Probe should not be moving in the y direction");
        assertEquals(0, probeVel.getZ(), 0.1, "Probe should not be moving in the z direction");
    }

    @Test
    void testCostFunction() {

        Vector params = strategy.computeInitialGuess();

        double cost = strategy.costFunction(params);

        assertFalse(Double.isNaN(cost), "Cost should not be NaN");
        assertFalse(Double.isInfinite(cost), "Cost should not be infinite");

        assertTrue(cost < 10, "Cost for direct shot should be low");

        Vector missParams = new Vector(new double[] {Math.PI/2, 0}); // 90 degrees, straight up
        double missCost = strategy.costFunction(missParams);

        assertTrue(missCost > cost, "Cost for missing should be higher than cost for hitting");
    }

    @Test
    void testComputeInitialGuess() {

        Vector guess = strategy.computeInitialGuess();

        assertNotNull(guess, "Initial guess should not be null");

        assertEquals(2, guess.size(), "Initial guess should have 2 elements");

        assertFalse(Double.isNaN(guess.get(0)), "First element should not be NaN");
        assertFalse(Double.isNaN(guess.get(1)), "Second element should not be NaN");
        assertFalse(Double.isInfinite(guess.get(0)), "First element should not be infinite");
        assertFalse(Double.isInfinite(guess.get(1)), "Second element should not be infinite");
    }
}
