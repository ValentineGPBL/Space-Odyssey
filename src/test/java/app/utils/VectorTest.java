package app.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VectorTest {

    @Test
    public void testConstructorAndGetComponents() {
        double[] coordinates = {1.0, 2.0, 3.0, 4.0};
        Vector vector = new Vector(coordinates);

        // Check that each component matches
        double[] components = vector.getComponents();
        assertArrayEquals(coordinates, components, 1e-9);
    }

    @Test
    public void testCloneCreatesEqualButSeparateObject() {
        double[] coordinates = {5.0, 6.0, 7.0};
        Vector original = new Vector(coordinates);
        Vector clone = original.clone();

        // The components should be equal
        assertArrayEquals(original.getComponents(), clone.getComponents(), 1e-9);

        // But they should not be the same object in memory
        assertNotSame(original, clone);
        assertNotSame(original.getComponents(), clone.getComponents());
    }
}
