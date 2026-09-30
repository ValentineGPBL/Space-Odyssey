package app.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Vector3Test {

    @Test
    public void testConstructorAndGetters() {
        Vector3 vector = new Vector3(1.0, 2.0, 3.0);

        assertEquals(1.0, vector.getX(), 1e-9);
        assertEquals(2.0, vector.getY(), 1e-9);
        assertEquals(3.0, vector.getZ(), 1e-9);
    }

    @Test
    public void testArrayConstructor() {
        double[] coords = {4.0, 5.0, 6.0};
        Vector3 vector = new Vector3(coords);

        assertEquals(4.0, vector.getX(), 1e-9);
        assertEquals(5.0, vector.getY(), 1e-9);
        assertEquals(6.0, vector.getZ(), 1e-9);
    }

    @Test
    public void testArrayConstructorInvalidLength() {
        double[] invalidCoords = {1.0, 2.0}; // Less than 3 elements
        assertThrows(IllegalArgumentException.class, () -> new Vector3(invalidCoords));
    }

    @Test
    public void testSetters() {
        Vector3 vector = new Vector3();
        vector.setX(7.0).setY(8.0).setZ(9.0);

        assertEquals(7.0, vector.getX(), 1e-9);
        assertEquals(8.0, vector.getY(), 1e-9);
        assertEquals(9.0, vector.getZ(), 1e-9);
    }

    @Test
    public void testCloneAndCopy() {
        Vector3 original = new Vector3(1.0, 2.0, 3.0);
        Vector3 clone = original.clone();
        Vector3 copy = original.copy();

        assertNotSame(original, clone);
        assertNotSame(original, copy);

        assertEquals(original.getX(), clone.getX(), 1e-9);
        assertEquals(original.getY(), clone.getY(), 1e-9);
        assertEquals(original.getZ(), clone.getZ(), 1e-9);

        assertEquals(original.getX(), copy.getX(), 1e-9);
        assertEquals(original.getY(), copy.getY(), 1e-9);
        assertEquals(original.getZ(), copy.getZ(), 1e-9);
    }

    @Test
    public void testMultiply() {
        Vector3 vector = new Vector3(1.0, -2.0, 3.0);
        Vector3 result = vector.multiply(2.5);

        assertEquals(2.5, result.getX(), 1e-9);
        assertEquals(-5.0, result.getY(), 1e-9);
        assertEquals(7.5, result.getZ(), 1e-9);
    }

    @Test
    public void testDivide() {
        Vector3 vector = new Vector3(10.0, -20.0, 30.0);
        Vector3 result = vector.divide(10.0);

        assertEquals(1.0, result.getX(), 1e-9);
        assertEquals(-2.0, result.getY(), 1e-9);
        assertEquals(3.0, result.getZ(), 1e-9);
    }

    @Test
    public void testDivideByZeroThrows() {
        Vector3 vector = new Vector3(1.0, 2.0, 3.0);
        assertThrows(IllegalArgumentException.class, () -> vector.divide(0));
    }

    @Test
    public void testSubtract() {
        Vector3 v1 = new Vector3(5.0, 7.0, 9.0);
        Vector3 v2 = new Vector3(2.0, 3.0, 4.0);
        Vector3 result = v1.subtract(v2);

        assertEquals(3.0, result.getX(), 1e-9);
        assertEquals(4.0, result.getY(), 1e-9);
        assertEquals(5.0, result.getZ(), 1e-9);
    }

    @Test
    public void testMagnitude() {
        Vector3 vector = new Vector3(3.0, 4.0, 0.0);
        assertEquals(5.0, vector.magnitude(), 1e-9);
    }

    @Test
    public void testDistanceTo() {
        Vector3 v1 = new Vector3(1.0, 2.0, 3.0);
        Vector3 v2 = new Vector3(4.0, 6.0, 3.0);
        assertEquals(5.0, v1.distanceTo(v2), 1e-9);
    }

    @Test
    public void testToVector() {
        Vector3 v3 = new Vector3(1.0, 2.0, 3.0);
        Vector v = v3.toVector();

        double[] components = v.getComponents();
        assertEquals(3, components.length);
        assertEquals(1.0, components[0], 1e-9);
        assertEquals(2.0, components[1], 1e-9);
        assertEquals(3.0, components[2], 1e-9);
    }
}
