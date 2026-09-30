package app.phys.optimizer;

import java.util.function.Function;

import app.utils.PhysUtils;
import app.utils.Vector;

// God I hate java naming conventions
public class GradientDescentOptimizerStrategy {
    private Function<Vector, Double> costFunction;
    private Vector state; // Current state of the optimizer
    private Vector velocity;
    private double cost; // Previous cost value

    public GradientDescentParams params;

    public GradientDescentOptimizerStrategy(Function<Vector, Double> costFunction, Vector initialState) {
        this(costFunction, initialState, new GradientDescentParams.Builder().build());
    }

    public GradientDescentOptimizerStrategy(Function<Vector, Double> costFunction, Vector initialState, GradientDescentParams params) {
        this.costFunction = costFunction;
        this.state = initialState;
        this.params = params;
        this.cost = costFunction.apply(state); // Initial cost
        this.velocity = new Vector(initialState.size());
    }

    private Vector computeGradient(Vector x) {
        // Central numerical differentiation
        double epsilon = 1e-8;
        Vector grad = new Vector(x.size());

        for (int i = 0; i < x.size(); i++) {
            Vector xForward = x.clone();
            Vector xBackward = x.clone();
            xForward.set(i, x.get(i) + epsilon);
            xBackward.set(i, x.get(i) - epsilon);
            grad.set(i, (costFunction.apply(xForward) - costFunction.apply(xBackward)) / (2 * epsilon));
        }

        return grad;
    }

    private Vector step() {
        // Calculate the gradient of the cost function at the current state
        Vector stateGradient = computeGradient(state);

        velocity.mul(params.getMomentum())
            .add(stateGradient.mul(params.getLearningRate()));

        state.sub(velocity);

        params.setLearningRate(params.getLearningRate() * (1 - params.getDecay()));
        
        return state;
    }

    public Vector solve() {
        for (int i = 0; i < params.getMaxIterations(); i++) {
            Vector newState = step();
            double newCost = costFunction.apply(newState);

            if(PhysUtils.DEBUG)
                System.out.println(newState.get(0) + "," + newState.get(1) + "," + newCost);

            if (cost < params.getEpsilon()) {
                break; // Converged
            }

            cost = newCost;
        }

        return state;
    }
}
