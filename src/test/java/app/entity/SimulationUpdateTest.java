package app.entity;

import app.utils.Matrix;
import app.phys.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimulationUpdateTest {

    @Test
    public void testPositionUpdateReturnsValidMatrix() {
        PlanetarySystem system = new PlanetarySystem();

        Matrix result = system.step();

        // Assert: The result should not be null
        assertNotNull(result, "The result matrix should not be null");
        assertEquals(24, result.rows(), "Matrix should have 24 rows: 12 for position and 12 for velocity");
        assertEquals(3, result.cols(), "Matrix should have 3 columns representing x, y, and z");



        for (int i = 0; i < result.rows(); i++) {

            for (int j = 0; j < result.cols(); j++){
                assertFalse(Double.isNaN(result.get(i,  j)),
                        "Value at [" + i + "," + j + "] should not be NaN");
            }
        }
    }
}
