package app.utils;

import java.io.Serializable;

/**
 * A three-dimensional vector class that represents a point or vector in 3D space.
 */
public class Vector3 extends RawVector<Vector3> {

    /**
     * Constructs a new vector with coordinates (0,0,0).
     */
    public Vector3() {
        this(0, 0, 0);
    }

    /**
     * Constructs a new vector with the specified coordinates.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param z the z-coordinate
     */
    public Vector3(double x, double y, double z) {
        super(new double[]{x, y, z});
    }

    /**
     * Constructs a new vector with the specified coordinates.
     *
     * @param coordinates an array containing the x, y, and z coordinates
     */
    public Vector3(double[] coordinates) {
        super(coordinates);
        if (coordinates.length != 3) {
            throw new IllegalArgumentException("Coordinates array must have exactly 3 elements.");
        }
    }

    /**
     * Constructs a new vector from a RawVector.
     * 
     * @param vector the RawVector to convert
     */
    public Vector3(RawVector<?> vector) {
        super(vector.getComponents());
    }

    /**
     * Performs a cross product with another vector.
     * 
     * @param other the other vector to cross with
     * @return a new Vector3 representing the cross product
     */
    public Vector3 cross(Vector3 other) {
        double x = getY() * other.getZ() - getZ() * other.getY();
        double y = getZ() * other.getX() - getX() * other.getZ();
        double z = getX() * other.getY() - getY() * other.getX();
        return new Vector3(x, y, z);
    }

    /**
     * Returns the x-coordinate of the vector.
     *
     * @return the x-coordinate
     */
    public double getX() {
        return get(0);
    }

    /**
     * Returns the y-coordinate of the vector.
     *
     * @return the y-coordinate
     */
    public double getY() {
        return get(1);
    }

    /**
     * Returns the z-coordinate of the vector.
     *
     * @return the z-coordinate
     */
    public double getZ() {
        return get(2);
    }

    /**
     * Sets the x-coordinate of the vector.
     *
     * @param x the new x-coordinate
     * @return the updated vector
     */
    public Vector3 setX(double x) {
        set(0, x);
        return this;
    }

    /**
     * Sets the y-coordinate of the vector.
     *
     * @param y the new y-coordinate
     * @return the updated vector
     */
    public Vector3 setY(double y) {
        set(1, y);
        return this;
    }

    /**
     * Sets the z-coordinate of the vector.
     *
     * @param z the new z-coordinate
     * @return the updated vector
     */
    public Vector3 setZ(double z) {
        set(2, z);
        return this;
    }

    /**
     * Create a copy (clone) of this vector.
     *
     * @return a new Vector3 object with the same coordinates as this vector
     */
    @Override
    public Vector3 clone() {
        return new Vector3(getComponents().clone());
    }

    /**
     * To generic Vector conversion.
     */
    public Vector toVector() {
        return new Vector(getComponents());
    }

    // TODO: Remove all below, redundant
    public Vector3 multiply(double scalar) {
        return new Vector3(getX() * scalar, getY() * scalar, getZ() * scalar);
    }

    // Subtract another vector from this vector
    public Vector3 subtract(Vector3 other) {
        return new Vector3(getX() - other.getX(), getY() - other.getY(), getZ() - other.getZ());
    }

    // Calculate the magnitude (length) of the vector
    public double magnitude() {
        return Math.sqrt(getX() * getX() + getY() * getY() + getZ() * getZ());
    }

    public Vector3 copy() {
        return this.clone();
    }

    public Vector3 divide(double scalar) {
        if (scalar == 0) {
            throw new IllegalArgumentException("Cannot divide by zero.");
        }
        return new Vector3(getX() / scalar, getY() / scalar, getZ() / scalar);
    }

    public double distanceTo(Vector3 other) {
        return this.subtract(other).magnitude();
    }


}