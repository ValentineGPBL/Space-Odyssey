package app.experiments;

import java.util.Locale;
import java.util.function.BiFunction;

import app.solver.Euler;
import app.solver.RK4;
import app.utils.*;

public class AccuracyRunner {
    public static void main(String[] args) {
        double k = 0.5;
        double t0 = 0.0;
        double tf = 5.0;
        var y0 = new Scalar(100.0);

        BiFunction<Double, Scalar, Scalar> decayFn = (t, y) -> (Scalar) y.clone().mul(-k);
        double exact = y0.getValue() * Math.exp(-k * tf);

        double[] stepSizes = {1.0, 0.5, 0.25, 0.1, 0.05, 0.01, 0.0001};

        System.out.println("StepSize\tEulerError\tEulerTime(ms)\tRK4Error\tRK4Time(ms)");

        for (double dt : stepSizes) {
            // Euler
            Euler<Scalar> euler = new Euler<>(decayFn, dt, t0, y0.clone());
            long eulerStart = System.nanoTime();
            double eulerResult = euler.solve(tf).getValue();
            long eulerEnd = System.nanoTime();
            double eulerError = Math.abs(eulerResult - exact);
            double eulerTime = (eulerEnd - eulerStart) / 1_000_000.0;

            // RK4
            RK4<Scalar> rk4 = new RK4<>(decayFn, dt, t0, y0.clone());
            long rk4Start = System.nanoTime();
            double rk4Result = rk4.solve(tf).getValue();
            long rk4End = System.nanoTime();
            double actualTf = rk4.getX();
            double rk4Exact = y0.getValue() * Math.exp(-k * actualTf);
            double rk4Error = Math.abs(rk4Result - rk4Exact);
            double rk4Time = (rk4End - rk4Start) / 1_000_000.0;

            System.out.printf(Locale.US, "%.4f\t|\t%.6f\t%.3f\t\t|\t%.6f\t\t%.3f%n",
                    dt, eulerError, eulerTime, rk4Error, rk4Time);
        }
    }
}