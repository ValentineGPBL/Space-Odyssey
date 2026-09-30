package app.phys.optimizer;

import java.math.BigDecimal;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import app.utils.PhysUtils;
import app.utils.Vector;

public class HillClimbOptimizerStrategy implements OptimizerStrategy {

    private boolean multithreaded = true;
    private Function<Vector, Double> costFunction;
    private Vector state; // Current state of the optimizer
    private int dimSize;

    public HillClimbParams params;

    public HillClimbOptimizerStrategy(Function<Vector, Double> costFunction, Vector initialState) {
        this(costFunction, initialState, new HillClimbParams.Builder().build());
    }

    public HillClimbOptimizerStrategy(Function<Vector, Double> costFunction, Vector initialState,
            HillClimbParams params) {
        this.costFunction = costFunction;
        this.state = initialState;
        this.params = params;
        this.dimSize = params.getLookAroundSize() * 2 + 1;
    }

    private Vector shiftFromIndex(int index) {
        Vector shiftV = new Vector(this.state.size());
        for (int i = 0; i < shiftV.size(); i++) {
            int shift = (index / (int) Math.pow(dimSize, i)) % dimSize;
            shiftV.set(i, shift - params.getLookAroundSize());
        }
        return shiftV;
    }

    private int shiftToIndex(Vector shift) {
        int index = 0;
        for (int i = 0; i < shift.size(); i++) {
            int shiftValue = (int) shift.get(i) + params.getLookAroundSize();
            index += shiftValue * Math.pow(dimSize, i);
        }
        return index;
    }

    private double[] shiftCosts(double[] costs, Vector shift) {
        double[] newCosts = new double[costs.length];
        for (int i = 0; i < costs.length; i++) {
            newCosts[i] = Double.NaN;
        }
        for (int i = 0; i < costs.length; i++) {
            int index = shiftToIndex(shiftFromIndex(i).sub(shift));
            if (index >= 0 && index < costs.length) {
                newCosts[index] = costs[i];
            }
        }
        return newCosts;
    }

    @Override
    public Vector solve() {
        double prevCost = Double.MAX_VALUE;
        final double[] costs = new double[(int) Math.pow(dimSize, state.size())];
        for (int i = 0; i < costs.length; i++) {
            costs[i] = Double.NaN;
        }

        for (int i = 0; i < params.getMaxIterations(); i++) {
            int minIndex = 0;
            double minCost = Double.MAX_VALUE;
            double minShiftLength = Double.MAX_VALUE;

            if (!multithreaded) {
                for (int j = 0; j < costs.length; j++) {
                    Vector shift = shiftFromIndex(j);
                    double shiftLength = shift.lengthSquared();
                    if (Double.isNaN(costs[j])) {
                        Vector newPos = shift.mul(params.getStepSize()).add(state);
                        costs[j] = costFunction.apply(newPos);
                        if (PhysUtils.DEBUG) {
                            System.out.println(newPos.get(0) + "," + newPos.get(1) + "," + costs[j]);
                            // System.out.println(new BigDecimal(newPos.get(0)).toPlainString() + "," + 0 +
                            // "," + costs[j]);
                        }
                    }
                    // Try to aim for the centermost cost
                    if (costs[j] < minCost || (costs[j] == minCost && shiftLength < minShiftLength)) {
                        if(PhysUtils.DEBUG) {
                            System.out.println("Cost: " + costs[j] + " Shift: " + shift + " Length: " + shiftLength);
                        }
                        minCost = costs[j];
                        minIndex = j;
                        minShiftLength = shiftLength;
                    }
                }
            } else {
                ExecutorService executors = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
                for (int j = 0; j < costs.length; j++) {
                    final int index = j;
                    if (Double.isNaN(costs[index])) {
                        executors.submit(() -> {
                            Vector newPos = shiftFromIndex(index).mul(params.getStepSize()).add(state);
                            costs[index] = costFunction.apply(newPos);
                            if (PhysUtils.DEBUG) {
                                System.out.println(newPos.get(0) + "," + newPos.get(1) + "," + costs[index]);
                                // System.out.println(new BigDecimal(newPos.get(0)).toPlainString() + "," + 0 +
                                // "," + costs[index]);
                            }
                        });
                    }
                }
                executors.shutdown();
                try {
                    executors.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                for (int j = 0; j < costs.length; j++) {
                    if (costs[j] < minCost) {
                        if(PhysUtils.DEBUG) {
                            System.out.println("Cost: " + costs[j]);
                        }
                        minCost = costs[j];
                        minIndex = j;
                    }
                }
            }

            Vector shift = shiftFromIndex(minIndex);
            /*System.out.println(shift);
            System.out.println(minCost);
            System.out.println(minIndex);
            System.out.println(costs[minIndex]);*/
            if (shift.equals(new Vector(state.size())) || Math.abs(prevCost - minCost) < params.getTolerance()) {
                break;
            }
            prevCost = minCost;
            state.add(shift.clone().mul(params.getStepSize()));
            double[] newCosts = shiftCosts(costs, shift);
            System.arraycopy(newCosts, 0, costs, 0, costs.length);
        }

        return state;
    }

}
