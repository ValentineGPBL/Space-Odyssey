package app;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import app.solver.*;
import app.utils.*;

// Run tests using mvnw test

class EulerTest {

    @Test
    void eulerStep() {
        Euler<Scalar> solver = new Euler<>((x, y) -> new Scalar(1), 0.01, 0, new Scalar(0));
        double result = solver.step().getValue();
        Assertions.assertEquals(0.01, result, 1e-6);
    }

    @Test
    void eulerLinearEq() {
        // y = 3x - 1, y' = 3
        // Step size = 0.01
        // 0 -> 4, -1 -> expected 11
        Euler<Scalar> solver = new Euler<>((x, y) -> new Scalar(3), 0, new Scalar(-1));
        double result = solver.solve(4).getValue();
        Assertions.assertEquals(11, result, 0.05);
    }

    @Test
    void eulerQuadraticEq() {
        // y = x^2, y' = 2x
        // Step size = 0.01
        // 0 -> 2, 0 -> expected 4
        Euler<Scalar> solver = new Euler<>((x, y) -> new Scalar(2*x), 0, new Scalar(0));
        double result = solver.solve(2).getValue();
        Assertions.assertEquals(4, result, 0.05);
    }

}

class RK4Test {

    @Test
    void rk4Step() {
        RK4<Scalar> solver = new RK4<>((x, y) -> new Scalar(1), 0.01, 0, new Scalar(0));
        double result = solver.step().getValue();
        Assertions.assertEquals(0.01, result, 1e-6);
    }

    @Test
    void rk4LinearEq() {
        // y = 3x - 1, y' = 3
        // Step size = 0.01
        // 0 -> 4, -1 -> expected 11
        RK4<Scalar> solver = new RK4<>((x, y) -> new Scalar(3), 0, new Scalar(-1));
        double result = solver.solve(4).getValue();
        Assertions.assertEquals(11, result, 0.05);
    }

    @Test
    void rk4QuadraticEq() {
        // y = x^2, y' = 2x
        // Step size = 0.01
        // 0 -> 2, 0 -> expected 4
        RK4<Scalar> solver = new RK4<>((x, y) -> new Scalar(2*x), 0, new Scalar(0));
        double result = solver.solve(2).getValue();
        Assertions.assertEquals(4, result, 0.05);
    }

}

class MatrixTest {

    @Test
    void matrixConstructor() {
        double[][] data = {{1, 2}, {3, 4}};
        Matrix matrix = new Matrix(data);
        Assertions.assertEquals(2, matrix.rows());
        Assertions.assertEquals(2, matrix.cols());
        Assertions.assertEquals(1, matrix.get(0, 0));
        Assertions.assertEquals(2, matrix.get(0, 1));
        Assertions.assertEquals(3, matrix.get(1, 0));
        Assertions.assertEquals(4, matrix.get(1, 1));
    }

    @Test
    void matrixSetGet() {
        double[][] data = {{1, 2}, {3, 4}};
        Matrix matrix = new Matrix(data);
        matrix.set(0, 0, 5);
        Assertions.assertEquals(5, matrix.get(0, 0));
    }

    @Test
    void matrixMmul() {
        Matrix matrix1 = new Matrix(new double[][] {{1, 2}, {3, 4}});
        Matrix matrix2 = new Matrix(new double[][] {{5, 6}, {7, 8}});
        Matrix result = matrix1.mmul(matrix2);
        Matrix expected = new Matrix(new double[][] {{19, 22}, {43, 50}});
        Assertions.assertEquals(expected, result);
    }

    @Test
    void matrixMath() {
        Matrix matrix1 = new Matrix(new double[][] {{1, 2}, {3, 4}});
        Matrix matrix2 = new Matrix(new double[][] {{4, 8}, {12, 8}});
        // Addition
        Assertions.assertEquals(new Matrix(new double[][] {{5, 10}, {15, 12}}), matrix1.clone().add(matrix2));
        // Subtraction
        Assertions.assertEquals(new Matrix(new double[][] {{-3, -6}, {-9, -4}}), matrix1.clone().sub(matrix2));
        // Multiplication
        Assertions.assertEquals(new Matrix(new double[][] {{4, 16}, {36, 32}}), matrix1.clone().mul(matrix2));
        // Division
        Assertions.assertEquals(new Matrix(new double[][] {{0.25, 0.25}, {0.25, 0.5}}), matrix1.clone().div(matrix2));
        // Scalar multiplication
        Assertions.assertEquals(new Matrix(new double[][] {{2, 4}, {6, 8}}), matrix1.clone().mul(2));
        // Scalar division
        Assertions.assertEquals(new Matrix(new double[][] {{0.5, 1}, {1.5, 2}}), matrix1.clone().div(2));
    }

}