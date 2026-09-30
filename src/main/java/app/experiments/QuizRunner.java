package app.experiments;

import app.solver.Euler;
import app.solver.RK4;
import app.utils.*;

import java.util.function.BiFunction;

public class QuizRunner {
    public static void main(String[] args) {
        BiFunction<Double, Vector3, Vector3> fn = (time, state) -> {
            double sigma = 10;
            double rho = 28;
            double beta = 2.7;
            return new Vector3(
                sigma * (state.getY() - state.getX()),
                state.getX() * (rho - state.getZ()) - state.getY(),
                state.getX() * state.getY() - beta * state.getZ()
            );
        };

        var solver = new Euler<>(fn, 0.001, 0, new Vector3(-0.1, 0.5, 0.2));
        var result = solver.solve(10);

        System.out.println("Euler:");
        System.out.println(result);
        System.out.println(solver.getX());


        BiFunction<Double, Vector3, Vector3> fn2 = (time, state) -> {
            double sigma = 9;
            double rho = 28;
            double beta = 3;
            return new Vector3(
                sigma * (state.getY() - state.getX()),
                state.getX() * (rho - state.getZ()) - state.getY(),
                state.getX() * state.getY() - beta * state.getZ()
            );
        };

        var solver2 = new RK4<>(fn2, 0.001, 0, new Vector3(0.1, -0.5, -0.2));
        var result2 = solver2.solve(30);

        System.out.println("RK4:");
        System.out.println(result2);
        System.out.println(solver2.getX());
    }
}