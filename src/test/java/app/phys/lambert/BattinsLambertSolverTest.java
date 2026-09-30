package app.phys.lambert;

import app.phys.Entity;
import app.utils.Vector3;
import javafx.util.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class BattinsLambertSolverTest {

    @Test
    void test() {

        double centralMass = 1.0e24;
        Entity centralBody = new Entity("CentralBody", new Vector3(0, 0, 0), new Vector3(0, 0, 0), centralMass, 6371e3) {
        };


        Vector3 r1 = new Vector3(1.0e8, 0, 0);
        Vector3 r2 = new Vector3(0, 1.0e8, 0);


        double tof = 3600.0;

        BattinsLambertSolver solver = new BattinsLambertSolver();

        Pair<Vector3, Vector3> result = solver.solve(centralBody, r1, r2, tof);

        Vector3 v1 = result.getKey();
        Vector3 v2 = result.getValue();

        Assertions.assertNotNull(v1, "Initial velocity should not be null");
        Assertions.assertNotNull(v2, "Final velocity should not be null");


        Assertions.assertTrue(
                v1.length() > 0 && v1.length() < 1.0e6,
                "Initial velocity magnitude should be reasonable (between 0 and 1,000,000 m/s)"
        );

        Assertions.assertTrue(
                v2.length() > 0 && v2.length() < 1.0e6,
                "Final velocity magnitude should be reasonable (between 0 and 1,000,000 m/s)"
        );
    }


}
