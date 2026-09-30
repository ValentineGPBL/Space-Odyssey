package app.phys.wind;

import app.utils.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConstantWindModelTest {

    @Test
    void alwaysSameOutput() {
        Vector3 acc = new Vector3(0.1, 0.0, -0.05);
        var c = new ConstantWindModel(acc);
        Vector3 first = c.getWindAcceleration(0, null, null);
        for (int i = 1; i < 10; i++) {
            assertEquals(first, c.getWindAcceleration(i, null, null));
        }
    }
}
