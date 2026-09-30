package app.phys.wind;

import app.utils.Vector3;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GaussianWindModelTest {

    @Test
    void magnitudesAreReasonable(){
        double sigma = 0.01;
        var model = new GaussianWindModel(sigma);
        for (int i=0; i<1000; i++){
            Vector3 a = model.getWindAcceleration(i, null, null);
            assertTrue(a.length()<5*sigma, "too large: "+a.length());
        }
    }
}
