package app.phys.wind;

import app.utils.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests that CompositeWindModel correctly sums sub-model outputs.
 */
public class CompositeWindModelTest {

    @Test
    void sumsSubmodelsCorrectly() {
        // Use deterministic constant models for reliable testing
        WindModel m1 = new ConstantWindModel(new Vector3(0.1, 0.0, 0.0));
        WindModel m2 = new ConstantWindModel(new Vector3(0.0, 0.2, 0.0));
        CompositeWindModel combo = new CompositeWindModel(m1, m2);

        // Expected sum is (0.1, 0.2, 0.0)
        Vector3 expected = new Vector3(0.1, 0.2, 0.0);
        Vector3 result   = combo.getWindAcceleration(0.0, null, null);

        assertEquals(expected.getX(), result.getX(), 1e-6, "X component sum incorrect");
        assertEquals(expected.getY(), result.getY(), 1e-6, "Y component sum incorrect");
        assertEquals(expected.getZ(), result.getZ(), 1e-6, "Z component sum incorrect");
    }
}
