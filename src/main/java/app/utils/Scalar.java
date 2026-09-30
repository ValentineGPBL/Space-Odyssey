package app.utils;

public class Scalar extends RawVector<Scalar> {

    /**
     * Constructs a new scalar with the value 0.
     */
    public Scalar() {
        this(0);
    }

    /**
     * Constructs a new scalar with the specified value.
     *
     * @param value the value of the scalar
     */
    public Scalar(double value) {
        super(new double[]{value});
    }

    /**
     * Returns the value of the scalar.
     *
     * @return the value of the scalar
     */
    public double getValue() {
        return get(0);
    }

    /**
     * Sets the value of the scalar.
     *
     * @param value the new value of the scalar
     */
    public void setValue(double value) {
        set(0, value);
    }

    /**
     * Returns the string representation of the scalar.
     *
     * @return the string representation of the scalar
     */
    @Override
    public String toString() {
        return String.valueOf(getValue());
    }

    /**
     * Creates a copy of the scalar.
     * 
     * @return a new Scalar object with the same value as this scalar
     */
    @Override
    public Scalar clone() {
        return new Scalar(getValue());
    }

    /**
     * To generic Vector conversion.
     */
    public Vector toVector() {
        return new Vector(getComponents());
    }
    
}
