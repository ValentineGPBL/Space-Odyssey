package app.entity;

import app.phys.Entity;
import app.utils.Vector3;

/**
 * Dummy concrete class extending Entity just for testing purposes.
 */
public class TestEntity extends Entity {

    public TestEntity(String name, Vector3 position, Vector3 velocity, double mass, double radius) {
        super(name, position, velocity, mass, radius);
    }
}
