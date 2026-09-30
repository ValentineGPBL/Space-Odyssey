package app.entity;

import app.utils.Vector3;
import app.phys.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EntityTest {

    @Test
    public void testConstructorAndGetters() {
        String name = "TestEntity";
        Vector3 position = new Vector3(1.0, 2.0, 3.0);
        Vector3 velocity = new Vector3(4.0, 5.0, 6.0);
        double mass = 10.0;
        double radius = 2.5;

        Entity entity = new TestEntity(name, position, velocity, mass, radius);

        assertEquals(name, entity.getName());
        assertEquals(position, entity.getPosition());
        assertEquals(velocity, entity.getVelocity());
        assertEquals(mass, entity.getMass(), 1e-9);
        assertEquals(radius, entity.getRadius(), 1e-9);
    }

    @Test
    public void testSetters() {
        Entity entity = new TestEntity("Initial", new Vector3(), new Vector3(), 1.0, 1.0);

        entity.setName("UpdatedEntity");
        entity.setPosition(new Vector3(7.0, 8.0, 9.0));
        entity.setVelocity(new Vector3(-1.0, -2.0, -3.0));
        entity.setMass(50.0);
        entity.setRadius(5.5);

        assertEquals("UpdatedEntity", entity.getName());
        assertEquals(7.0, entity.getPosition().getX(), 1e-9);
        assertEquals(8.0, entity.getPosition().getY(), 1e-9);
        assertEquals(9.0, entity.getPosition().getZ(), 1e-9);

        assertEquals(-1.0, entity.getVelocity().getX(), 1e-9);
        assertEquals(-2.0, entity.getVelocity().getY(), 1e-9);
        assertEquals(-3.0, entity.getVelocity().getZ(), 1e-9);

        assertEquals(50.0, entity.getMass(), 1e-9);
        assertEquals(5.5, entity.getRadius(), 1e-9);
    }
}
