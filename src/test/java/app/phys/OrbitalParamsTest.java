package app.phys;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import app.utils.Vector3;
import javafx.util.Pair;

public class OrbitalParamsTest {

    private static final double EPSILON = 1e-6;

    @Test
    void testConstructorWithOrbitalElements() {
        double semiMajorAxis = 1.5e11; 
        double eccentricity = 0.1;
        double inclination = Math.PI / 6;
        double longitudeOfAscendingNode = Math.PI / 4; 
        double argumentOfPeriapsis = Math.PI / 3; 
        double meanAnomaly = Math.PI / 2; 
        double mu = 1.327e20; 

        OrbitalParams params = new OrbitalParams(
            semiMajorAxis,
            eccentricity,
            inclination,
            longitudeOfAscendingNode,
            argumentOfPeriapsis,
            meanAnomaly,
            mu
        );

        Assertions.assertEquals(semiMajorAxis, params.getSemiMajorAxis(), EPSILON);
        Assertions.assertEquals(eccentricity, params.getEccentricity(), EPSILON);
        Assertions.assertEquals(inclination, params.getInclination(), EPSILON);
        Assertions.assertEquals(longitudeOfAscendingNode, params.getLongitudeOfAscendingNode(), EPSILON);
        Assertions.assertEquals(argumentOfPeriapsis, params.getArgumentOfPeriapsis(), EPSILON);
        Assertions.assertEquals(meanAnomaly, params.getMeanAnomaly(), EPSILON);
        Assertions.assertEquals(mu, params.getMu(), EPSILON);
    }

    @Test
    void testConstructorWithStateVectors() {

        double sunMass = 1.989e30; 
        Entity sun = new Entity("Sun", new Vector3(0, 0, 0), new Vector3(0, 0, 0), sunMass, 6.96e8) {};

        double earthOrbitRadius = 1.496e11; 

        double mu = Simulation.G * sunMass;
        double earthOrbitalSpeed = Math.sqrt(mu / earthOrbitRadius);

        Vector3 position = new Vector3(earthOrbitRadius, 0, 0);
        Vector3 velocity = new Vector3(0, earthOrbitalSpeed, 0);

        OrbitalParams params = new OrbitalParams(sun, position, velocity);

        Assertions.assertEquals(earthOrbitRadius, params.getSemiMajorAxis(), earthOrbitRadius * 0.1);

        double expectedPeriod = 2 * Math.PI * Math.sqrt(Math.pow(earthOrbitRadius, 3) / mu);
        double actualPeriod = params.getPeriod();
        Assertions.assertEquals(expectedPeriod, actualPeriod, expectedPeriod * 0.1);

        Assertions.assertTrue(params.getInclination() < 0.1 || 
                             Math.abs(params.getInclination() - Math.PI) < 0.1,
                             "Inclination should be close to 0 or PI for an orbit in the xy plane");
    }

    @Test
    void testGetStateAtTime() {

        double radius = 1.0e8; 
        double mu = 3.986e14; 

        OrbitalParams params = new OrbitalParams(
            radius,
            0.0,
            0.0,
            0.0,
            0.0,
            0.0,
            mu
        );

        Pair<Vector3, Vector3> initialState = params.getState(0);
        Vector3 initialPosition = initialState.getKey();
        Vector3 initialVelocity = initialState.getValue();

        Assertions.assertEquals(radius, initialPosition.getX(), radius * EPSILON);
        Assertions.assertEquals(0, initialPosition.getY(), radius * EPSILON);
        Assertions.assertEquals(0, initialPosition.getZ(), radius * EPSILON);

        double expectedVelocity = Math.sqrt(mu / radius);
        Assertions.assertEquals(0, initialVelocity.getX(), expectedVelocity * EPSILON);
        Assertions.assertEquals(expectedVelocity, initialVelocity.getY(), expectedVelocity * EPSILON);
        Assertions.assertEquals(0, initialVelocity.getZ(), expectedVelocity * EPSILON);

        double period = params.getPeriod();
        Pair<Vector3, Vector3> quarterState = params.getState(period / 4);
        Vector3 quarterPosition = quarterState.getKey();
        Vector3 quarterVelocity = quarterState.getValue();

        Assertions.assertEquals(0, quarterPosition.getX(), radius * EPSILON);
        Assertions.assertEquals(radius, quarterPosition.getY(), radius * EPSILON);
        Assertions.assertEquals(0, quarterPosition.getZ(), radius * EPSILON);

        Assertions.assertEquals(-expectedVelocity, quarterVelocity.getX(), expectedVelocity * EPSILON);
        Assertions.assertEquals(0, quarterVelocity.getY(), expectedVelocity * EPSILON);
        Assertions.assertEquals(0, quarterVelocity.getZ(), expectedVelocity * EPSILON);
    }

    @Test
    void testEllipticalOrbit() {
        double semiMajorAxis = 1.5e11;
        double eccentricity = 0.5;
        double mu = 1.327e20;

        OrbitalParams params = new OrbitalParams(
            semiMajorAxis,
            eccentricity,
            0.0,
            0.0,
            0.0,
            0.0,
            mu
        );

        Pair<Vector3, Vector3> periapsisState = params.getState(0);
        Vector3 periapsisPosition = periapsisState.getKey();

        double expectedPeriapsisDistance = semiMajorAxis * (1 - eccentricity);
        Assertions.assertEquals(expectedPeriapsisDistance, periapsisPosition.length(), expectedPeriapsisDistance * EPSILON);

        double period = params.getPeriod();
        Pair<Vector3, Vector3> apoapsisState = params.getState(period / 2);
        Vector3 apoapsisPosition = apoapsisState.getKey();

        double expectedApoapsisDistance = semiMajorAxis * (1 + eccentricity);
        Assertions.assertEquals(expectedApoapsisDistance, apoapsisPosition.length(), expectedApoapsisDistance * EPSILON);
    }

    @Test
    void testHyperbolicOrbit() {
        double semiMajorAxis = -1.0e11;
        double eccentricity = 1.5;
        double mu = 1.327e20;

        OrbitalParams params = new OrbitalParams(
            semiMajorAxis,
            eccentricity,
            0.0,
            0.0,
            0.0,
            0.0,
            mu
        );

        Assertions.assertEquals(Double.POSITIVE_INFINITY, params.getPeriod());

        Pair<Vector3, Vector3> periapsisState = params.getState(0);
        Vector3 periapsisPosition = periapsisState.getKey();
        Vector3 periapsisVelocity = periapsisState.getValue();

        double expectedPeriapsisDistance = Math.abs(semiMajorAxis) * (eccentricity - 1);
        Assertions.assertEquals(expectedPeriapsisDistance, periapsisPosition.length(), expectedPeriapsisDistance * EPSILON);

        double escapeVelocity = Math.sqrt(2 * mu / periapsisPosition.length());
        Assertions.assertTrue(periapsisVelocity.length() > escapeVelocity, 
            "Velocity at periapsis should be greater than escape velocity for a hyperbolic orbit");
    }
}
