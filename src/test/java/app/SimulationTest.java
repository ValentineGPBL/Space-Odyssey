package app;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import app.phys.*;
import app.ui.SolarSystemInitializer;
import app.utils.*;

class SimulationTest {

    private void createTest(int a, int b, double from, double to) {
        Entity[] entities = SolarSystemInitializer.parseBodyList(getClass().getResourceAsStream("/IC.csv")).toArray(new Entity[0]);
        Simulation sim = new Simulation(entities, 1);

        for(int i = 0; i < 100000; i++) {
            Matrix positions = sim.step();
            double dist = positions.getRow(a).sub(positions.getRow(b)).length();
            Assertions.assertTrue(dist > from && dist < to, "Distance between bodies " + a + " and " + b + " is out of bounds: " + dist + " not in [" + from + ", " + to + "]");
        }
    }

    @Test
    void earthSunTest() {
        createTest(0, 3, 146e6, 153e6);
    }

    @Test
    void earthMoonTest() {
        createTest(3, 4, 0.35e6, 0.41e6);
    }

    @Test
    void saturnTitanTest() {
        createTest(7, 8, 1.1e6, 1.3e6);
    }

}