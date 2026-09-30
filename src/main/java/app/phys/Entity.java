package app.phys;

import app.utils.*;
import javafx.scene.Node;

/**
 * Abstract base class representing a physical entity in a simulation.
 * This class provides the basic properties for any entity that can be simulated
 * with physics, such as position, velocity, acceleration, and mass.
 */
public abstract class Entity {

    /**
     * The current position of the entity in 3D space.
     */
    private Vector3 position;
    
    /**
     * The current velocity of the entity in 3D space.
     */
    private Vector3 velocity;
    
    /**
     * The mass of the entity in kilograms.
     */
    private double mass;
    
    /**
     * The name of the entity
     */
    private String name;


    /**
     * The radius (maximum distance from the center) of the entity.
     */
    private double radius;

    /**
     * Constructs an Entity with specified position, velocity, and mass.
     * 
     * @param position The initial position of the entity in 3D space.
     * @param velocity The initial velocity of the entity in 3D space.
     * @param mass The mass of the entity in kilograms.
     */
    public Entity(String name, Vector3 position, Vector3 velocity, double mass, double radius) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
        this.radius = radius;
    }

    /**
     * Gets the position of the entity.
     * @return The position vector of the entity.
     */
    public Vector3 getPosition() {
        return position;
    }

    /**
     * Sets the position of the entity.
     * @param position The new position vector.
     */
    public void setPosition(Vector3 position) {
        this.position = position;
    }

    /**
     * Gets the velocity of the entity.
     * @return The velocity vector of the entity.
     */
    public Vector3 getVelocity() {
        return velocity;
    }

    /**
     * Sets the velocity of the entity.
     * @param velocity The new velocity vector.
     */
    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    /**
     * Gets the mass of the entity in kilograms.
     * @return The mass of the entity in kilograms.
     */
    public double getMass() {
        return mass;
    }

    /**
     * Sets the mass of the entity in kilograms.
     * @param mass The new mass of the entity in kilograms.
     */
    public void setMass(double mass) {
        this.mass = mass;
    }

    /**
     * Gets the name of the entity.
     * @return The name of the entity.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the entity.
     * @param name The new name of the entity.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the radius of the entity.
     * @return The radius of the entity.
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the entity.
     * @param radius The new radius of the entity.
     */
    public void setRadius(double radius) {
        this.radius = radius;
    }

    /**
     * Gets the distance from the center of the entity to another point in space.
     * @param point The point in space to measure the distance to.
     */
    public double getDistance(Vector3 point) {
        return position.clone().sub(point).length();
    }

    /**
     * Gets the distance from the center of the entity to another entity.
     * @param entity The other entity to measure the distance to.
     */
    public double getDistance(Entity entity) {
        return getDistance(entity.getPosition());
    }

    /**
     * Applies the entity's properties to a JavaFX Node.
     * @param node The JavaFX Node to apply the entity's properties to.
     */
    public void apply(Node node) {
        node.setTranslateX(position.getX() / 1e6);
        node.setTranslateY(position.getZ() / 1e6);
        node.setTranslateZ(position.getY() / 1e6);
    }

    @Override
    public Entity clone() {
        return new Entity(name, position.clone(), velocity.clone(), mass, radius) {};
    }

}