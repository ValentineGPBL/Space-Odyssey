package app.utils;

import java.io.Serializable;

public class Vector extends RawVector<Vector> {

    /**
     * Constructs a new vector with the specified coordinates.
     *
     * @param coordinates an array containing the coordinates of the vector
     */
    public Vector(double[] coordinates) {
        super(coordinates);
    }

    /**
     * Constructs a new vector with the specified number of dimensions, initialized to zero.
     *
     * @param dimensions the number of dimensions for the vector
     */
    public Vector(int dimensions) {
        super(dimensions);
    }

    public double norm() {
        double sum = 0.0;
        for (int i = 0; i < this.size(); i++) {
            sum += Math.pow(this.get(i), 2);
        }
        return Math.sqrt(sum);
    }

    /**
     * Clone the vector.
     * 
     * @return a new Vector object with the same coordinates as this vector
     */
    @Override
    public Vector clone() {
        return new Vector(getComponents().clone());
    }


    
}
