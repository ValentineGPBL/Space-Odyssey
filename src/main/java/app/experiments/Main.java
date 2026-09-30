package app.experiments;

import java.util.Arrays;
import java.util.List;

public class Main {
    private static final List<String> EXPERIMENTS = Arrays.asList(
        "accuracy",
        "hillclimb",
        "perf",
        "quiz",
        "sim",
        "trajectory"
    );

    private static final List<String> DESCRIPTIONS = Arrays.asList(
        "Test accuracy of Euler and RK4 with different step sizes",
        "Finds a trajectory to titan using the hill climbing algorithm",
        "Tests the performance of the planetary simulation over a year with different step sizes",
        "Runs the code for the canvas quiz assignment",
        "Simulates a launch of the probe with hardcoded parameters, prints out the coordinates of Earth, Titan and the probe",
        "Simulates a trajectory from Earth to Mars and back using the InterplanetaryTrajectoryStrategy"
    );

    public static void main(String[] args) {
        if(args.length == 0) {
            throw new IllegalArgumentException("No arguments provided.");
        }
        String experiment = args[0];
        if(!EXPERIMENTS.contains(experiment)) {
            System.out.println("Available experiments:");
            for (int i = 0; i < EXPERIMENTS.size(); i++) {
                System.out.println(EXPERIMENTS.get(i) + " - " + DESCRIPTIONS.get(i));
            }
            throw new IllegalArgumentException("Unknown experiment: " + experiment);
        }
        switch (experiment) {
            case "accuracy":
                AccuracyRunner.main(args);
                break;
            case "hillclimb":
                HillClimbRunner.main(args);
                break;
            case "perf":
                PerfRunner.main(args);
                break;
            case "quiz":
                QuizRunner.main(args);
                break;
            case "sim":
                SimRunner.main(args);
                break;
            case "trajectory":
                TrajectoryRunner.main(args);
                break;
            default:
                throw new IllegalArgumentException("Unknown experiment: " + experiment);
        }
    }
}
