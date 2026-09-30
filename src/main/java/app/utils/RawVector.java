package app.utils;

import java.io.Serializable;

@SuppressWarnings("unchecked")
public abstract class RawVector<T extends RawVector<T>> implements Serializable {

    protected final double[] components;

    /**
     * Constructs a new vector of a given length.
     *
     * @param length the length of the vector
     */
    public RawVector(int length) {
        components = new double[length];
    }

    /**
     * Constructs a new vector with the given components.
     *
     * @param components the components of the vector
     */
    public RawVector(double[] components) {
        this.components = components;
    }

    /**
     * Get the components of the vector.
     *
     * @return the components of the vector
     */
    public double[] getComponents() {
        return components;
    }

    /**
     * Get the size (dimension) of the vector.
     *
     * @return the size of the vector
     */
    public int size() {
        return components.length;
    }

    /**
     * Get the component at the given index.
     *
     * @param index the index of the component to get
     * @return the value of the component at the given index
     */
    public double get(int index) {
        if (index < 0 || index >= components.length)
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        return components[index];
    }

    /**
     * Set the component at the given index.
     *
     * @param index the index of the component to set
     * @param value the value to set the component to
     */
    public void set(int index, double value) {
        if (index < 0 || index >= components.length)
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        components[index] = value;
    }

    /**
     * Add two vectors together.
     *
     * @param other the vector to add to this vector
     * @return this vector after addition (of type T)
     */
    public T add(T other) {
        if (components.length != other.components.length)
            throw new IllegalArgumentException("Vectors must have the same length");
        for (int i = 0; i < components.length; i++)
            components[i] += other.components[i];
        return (T) this;
    }

    /**
     * Subtract one vector from another.
     *
     * @param other the vector to subtract from this vector
     * @return this vector after subtraction (of type T)
     */
    public T sub(T other) {
        if (components.length != other.components.length)
            throw new IllegalArgumentException("Vectors must have the same length");
        for (int i = 0; i < components.length; i++)
            components[i] -= other.components[i];
        return (T) this;
    }

    /**
     * Element-wise multiplication of two vectors.
     *
     * @param other the vector to multiply with this vector
     * @return this vector after multiplication (of type T)
     */
    public T mul(T other) {
        if (components.length != other.components.length)
            throw new IllegalArgumentException("Vectors must have the same length");
        for (int i = 0; i < components.length; i++)
            components[i] *= other.components[i];
        return (T) this;
    }

    /**
     * Element-wise division of two vectors.
     *
     * @param other the vector to divide this vector by
     * @return this vector after division (of type T)
     */
    public T div(T other) {
        if (components.length != other.components.length)
            throw new IllegalArgumentException("Vectors must have the same length");
        for (int i = 0; i < components.length; i++) {
            if (other.components[i] == 0)
                throw new ArithmeticException("Division by zero at index " + i);
            components[i] /= other.components[i];
        }
        return (T) this;
    }

    /**
     * Scalar multiplication of this vector.
     *
     * @param scalar the scalar to multiply with this vector
     * @return this vector after multiplication (of type T)
     */
    public T mul(double scalar) {
        for (int i = 0; i < components.length; i++)
            components[i] *= scalar;
        return (T) this;
    }

    /**
     * Scalar division of this vector.
     *
     * @param scalar the scalar to divide this vector by
     * @return this vector after division (of type T)
     */
    public T div(double scalar) {
        if (scalar == 0)
            throw new ArithmeticException("Division by zero");
        for (int i = 0; i < components.length; i++)
            components[i] /= scalar;
        return (T) this;
    }

    /**
     * Get the dot product of two vectors.
     *
     * @param other the vector to calculate the dot product with
     * @return the dot product
     */
    public double dot(T other) {
        if (components.length != other.components.length)
            throw new IllegalArgumentException("Vectors must have the same length");
        double result = 0;
        for (int i = 0; i < components.length; i++)
            result += components[i] * other.components[i];
        return result;
    }

    /**
     * Get the length (magnitude) of the vector.
     *
     * @return the length of the vector
     */
    public double length() {
        double sum = 0;
        for (int i = 0; i < components.length; i++)
            sum += components[i] * components[i];
        return Math.sqrt(sum);
    }

    /**
     * Get the squared length (magnitude) of the vector.
     * 
     * @return the squared length of the vector
     */
    public double lengthSquared() {
        double sum = 0;
        for (int i = 0; i < components.length; i++)
            sum += components[i] * components[i];
        return sum;
    }


    /**
     * Normalize the vector to unit length.
     *
     * @return this vector normalized (of type T)
     */
    public T normalize() {
        double len = length();
        if (len == 0)
            throw new ArithmeticException("Cannot normalize a zero vector");
        for (int i = 0; i < components.length; i++)
            components[i] /= len;
        return (T) this;
    }

    /**
     * Computes the AXPY operation: this = a * x + this.
     *
     * @param a the scalar multiplier
     * @param x the vector x
     * @return this vector after the operation (of type T)
     */
    public T axpy(double a, T x) {
        if (components.length != x.components.length)
            throw new IllegalArgumentException("Vectors must have the same length");
        for (int i = 0; i < components.length; i++)
            components[i] += a * x.components[i];
        return (T) this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RawVector<?> other = (RawVector<?>) obj;
        if (components.length != other.components.length)
            return false;
        for (int i = 0; i < components.length; i++) {
            if (components[i] != other.components[i])
                return false;
        }
        return true;
    }

    public boolean equals(Object obj, double delta) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RawVector<?> other = (RawVector<?>) obj;
        if (components.length != other.components.length)
            return false;
        for (int i = 0; i < components.length; i++) {
            if (Math.abs(components[i] - other.components[i]) > delta)
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < components.length; i++) {
            sb.append(components[i]);
            if (i < components.length - 1)
                sb.append(", ");
        }
        sb.append(")");
        return sb.toString();
    }

    /**
     * Clone the vector.
     *
     * @return a new vector that is a copy of this vector (of type T)
     */
    abstract public T clone();

    /**
     * Copy the contents of this vector to another vector.
     *
     * @param other the vector to copy to
     * @return this vector (of type T)
     */
    public T copyTo(T other) {
        if (components.length != other.components.length)
            throw new IllegalArgumentException("Vectors must have the same length");
        System.arraycopy(components, 0, other.components, 0, components.length);
        return other;
    }
}
