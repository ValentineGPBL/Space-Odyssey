package app.entity;

import app.utils.Vector3;
import app.phys.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlanetTest {

    @Test
    public void testConstructorAndGetters(){
        Planet earth = new Planet("Earth",
                new Vector3(1.0, 2.0, 3.0),
                new Vector3(4.0, 5.0, 6.0),
                5.972e24,
                6371);

        assertEquals("Earth", earth.getName());
         assertEquals(1.0, earth.getPosition().getX(), 1e-9);
        assertEquals(2.0, earth.getPosition().getY(), 1e-9);
        assertEquals(3.0, earth.getPosition().getZ(), 1e-9);
        assertEquals(4.0, earth.getVelocity().getX(), 1e-9);
        assertEquals(5.0, earth.getVelocity().getY(), 1e-9);
        assertEquals(6.0, earth.getVelocity().getZ(), 1e-9);
        assertEquals(5.972e24, earth.getMass(), 1e-9);
        assertEquals(6371, earth.getRadius(), 1e-9);
    }

    @Test
    public void testSetters(){
          Planet planet = new Planet("Test", new Vector3(), new Vector3(), 1.0, 1.0);


         planet.setName("UpdatedPlanet");
        planet.setPosition(new Vector3(10.0, 20.0, 30.0));
        planet.setVelocity(new Vector3(-5.0, -10.0, -15.0));
        planet.setMass(6.0e24);
        planet.setRadius(5000);

        assertEquals("UpdatedPlanet", planet.getName());

        assertEquals(10.0, planet.getPosition().getX(), 1e-9);
         assertEquals(-15.0, planet.getVelocity().getZ(), 1e-9);
        assertEquals(6.0e24, planet.getMass(), 1e-9);
        assertEquals(5000, planet.getRadius(), 1e-9);
    }

    /*@Test
    public void testGetGravitationalForce() {
        Planet earth = new Planet("Earth",
                new Vector3(0.0, 0.0, 0.0),
                new Vector3(0.0, 0.0, 0.0),
                5.972e24,
                6371);

        Planet moon =  new Planet("Moon",
                new Vector3(384400000.0, 0.0, 0.0),
                new Vector3(0.0, 0.0, 0.0),
                7.34767309e22,
                1737);

        Vector3 force = earth.getGravitationalForce(moon);

        assertNotNull(force, "The gravitational force should not be null.");

        assertTrue(force.getX() > 0, "Force X should be positive.");

        assertEquals(0.0, force.getY(), 1e-6);
        assertEquals(0.0, force.getZ(), 1e-6);
    }*/
    
}
