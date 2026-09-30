package app.phys.wind;

import app.utils.Vector3;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RandomWalkWindModelTest {

    @Test
    void averageAutocorrelationApproxAlpha() {
        double sigma = 0.01;
        double alpha = 0.9;
        RandomWalkWindModel model = new RandomWalkWindModel(sigma, alpha);

        int samples = 5000;
        double sumCorr = 0.0;
        Vector3 prev = model.getWindAcceleration(0, null, null);

        for (int i = 1; i <= samples; i++) {
            Vector3 next = model.getWindAcceleration(i, null, null);
            double dot = prev.dot(next);
            double norm2 = prev.dot(prev);
            double corr = (norm2 > 0) ? dot / norm2 : 0.0;
            sumCorr += corr;
            prev = next;
        }

        double avgCorr = sumCorr / samples;
        // Allow a small tolerance around alpha
        assertEquals(alpha, avgCorr, 0.05, "Average autocorrelation off: " + avgCorr);
    }
}
