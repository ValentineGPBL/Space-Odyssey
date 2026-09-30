package app.phys.wind;

import app.utils.Vector3;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class AltitudeScalingWindModelTest {

    @Test
    void accelerationDecreasesWithHeight(){
        var base = new GaussianWindModel(0.1);
        Vector3 center = new Vector3(0, 0, 0);
        double R = 1000, H = 100;
        var scaled = new AltitudeScalingWindModel(base, center, R, H);

        Vector3 low = scaled. getWindAcceleration(0, new Vector3(R + 10, 0, 0), null);
        Vector3 high = scaled. getWindAcceleration(0, new Vector3(R + 200, 0, 0), null);

        assertTrue(high.length() < low.length(), "Expected high-altitude < low-altitude");
    }
}
