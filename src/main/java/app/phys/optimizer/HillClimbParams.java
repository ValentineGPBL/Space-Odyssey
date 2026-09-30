package app.phys.optimizer;

public class HillClimbParams {
    private double stepSize;
    private int maxIterations;
    private double tolerance;
    private int lookAroundSize = 1;

    private HillClimbParams(Builder b) {
        this.stepSize = b.stepSize;
        this.maxIterations = b.maxIterations;
        this.tolerance = b.tolerance;
        this.lookAroundSize = b.lookAroundSize;
    }

    public double getStepSize() {
        return stepSize;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public double getTolerance() {
        return tolerance;
    }

    public int getLookAroundSize() {
        return lookAroundSize;
    }

    public static class Builder {
        private double stepSize = 0.01;
        private int maxIterations = 10000;
        private double tolerance = 1e-6;
        private int lookAroundSize = 1;

        public Builder stepSize(double stepSize) {
            this.stepSize = stepSize;
            return this;
        }

        public Builder maxIterations(int maxIterations) {
            this.maxIterations = maxIterations;
            return this;
        }

        public Builder tolerance(double tolerance) {
            this.tolerance = tolerance;
            return this;
        }

        public Builder lookAroundSize(int lookAroundSize) {
            this.lookAroundSize = lookAroundSize;
            return this;
        }

        public HillClimbParams build() {
            return new HillClimbParams(this);
        }
    }
}
