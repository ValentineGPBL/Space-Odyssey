package app.phys.optimizer;

public final class GradientDescentParams {

    private double learningRate;
    private final double epsilon;
    private final int maxIterations;
    private final double momentum;
    private final double decay;

    private GradientDescentParams(Builder b) {
        this.learningRate = b.learningRate;
        this.epsilon = b.epsilon;
        this.maxIterations = b.maxIterations;
        this.momentum = b.momentum;
        this.decay = b.decay;
    }

    public double getLearningRate() {
        return learningRate;
    }
    public double getEpsilon() {
        return epsilon;
    }
    public int getMaxIterations() {
        return maxIterations;
    }
    public double getMomentum() {
        return momentum;
    }
    public double getDecay() {
        return decay;
    }
    public void setLearningRate(double lr) {
        this.learningRate = lr;
    }

    public static class Builder {
        private double learningRate = 0.01;
        private double epsilon = 1e-6;
        private int maxIterations= 10000;
        private double momentum = 0.9;
        private double decay = 0.0;

        public Builder learningRate(double lr) {
            this.learningRate = lr;
            return this;
        }
        public Builder epsilon(double eps) {
            this.epsilon = eps;
            return this;
        }
        public Builder maxIterations(int max) {
            this.maxIterations = max;
            return this;
        }
        public Builder momentum(double m) {
            this.momentum = m;
            return this;
        }
        public Builder decay(double d) {
            this.decay = d;
            return this;
        }

        public GradientDescentParams build() {
            return new GradientDescentParams(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
