package app.phys.optimizer;

import app.utils.Vector;

public class GeneticAlgorithmParams {
    private final int populationSize;
    private final double mutationRate;
    private final double crossoverRate;
    private final int elitismCount;
    private final int maxGenerations;
    private final int tournamentSize;
    private final Vector lowerBound; // Lower bound for gene values
    private final Vector upperBound; // Upper bound for gene values

    private GeneticAlgorithmParams(Builder builder) {
        this.populationSize = builder.populationSize;
        this.mutationRate = builder.mutationRate;
        this.crossoverRate = builder.crossoverRate;
        this.elitismCount = builder.elitismCount;
        this.maxGenerations = builder.maxGenerations;
        this.tournamentSize = builder.tournamentSize;
        this.lowerBound = builder.lowerBound;
        this.upperBound = builder.upperBound;

        // Validations
        if (populationSize <= 0) {
            throw new IllegalArgumentException("Population size must be positive.");
        }
        if (lowerBound == null) {
            throw new IllegalArgumentException("Lower bound must be set.");
        }
        if (upperBound == null) {
            throw new IllegalArgumentException("Upper bound must be set.");
        }
        if (lowerBound.size() != upperBound.size()) {
            throw new IllegalArgumentException("Lower and upper bounds must have the same dimension.");
        }
        if (elitismCount < 0) {
            throw new IllegalArgumentException("Elitism count cannot be negative.");
        }
        if (elitismCount >= populationSize) {
            throw new IllegalArgumentException("Elitism count must be less than population size.");
        }
        if (tournamentSize <= 0) {
            throw new IllegalArgumentException("Tournament size must be positive.");
        }
        if (tournamentSize > populationSize) {
            throw new IllegalArgumentException("Tournament size cannot exceed population size.");
        }
        if (mutationRate < 0.0 || mutationRate > 1.0) {
            throw new IllegalArgumentException("Mutation rate must be between 0.0 and 1.0.");
        }
        if (crossoverRate < 0.0 || crossoverRate > 1.0) {
            throw new IllegalArgumentException("Crossover rate must be between 0.0 and 1.0.");
        }
    }

    // Getters
    public int getPopulationSize() { return populationSize; }
    public double getMutationRate() { return mutationRate; }
    public double getCrossoverRate() { return crossoverRate; }
    public int getElitismCount() { return elitismCount; }
    public int getMaxGenerations() { return maxGenerations; }
    public int getTournamentSize() { return tournamentSize; }
    public Vector getLowerBound() { return lowerBound; }
    public Vector getUpperBound() { return upperBound; }

    public static class Builder {
        private int populationSize = 100;
        private double mutationRate = 0.015; // Typical small value
        private double crossoverRate = 0.7;  // Typical high value
        private int elitismCount = 2;       // Carry over a few best
        private int maxGenerations = 1000;
        private int tournamentSize = 5;     // Common tournament size
        private Vector lowerBound = null;   // Must be set by user
        private Vector upperBound = null;   // Must be set by user

        public Builder populationSize(int populationSize) {
            this.populationSize = populationSize;
            return this;
        }

        public Builder mutationRate(double mutationRate) {
            this.mutationRate = mutationRate;
            return this;
        }

        public Builder crossoverRate(double crossoverRate) {
            this.crossoverRate = crossoverRate;
            return this;
        }

        public Builder elitismCount(int elitismCount) {
            this.elitismCount = elitismCount;
            return this;
        }

        public Builder maxGenerations(int maxGenerations) {
            this.maxGenerations = maxGenerations;
            return this;
        }

        public Builder tournamentSize(int tournamentSize) {
            this.tournamentSize = tournamentSize;
            return this;
        }

        public Builder lowerBound(Vector lowerBound) {
            this.lowerBound = lowerBound;
            return this;
        }

        public Builder upperBound(Vector upperBound) {
            this.upperBound = upperBound;
            return this;
        }
        
        /**
         * Convenience method to set uniform bounds for all dimensions.
         * @param min The minimum value for each gene.
         * @param max The maximum value for each gene.
         * @param dimensions The number of dimensions (genome length).
         */
        public Builder bounds(double min, double max, int dimensions) {
            if (dimensions <= 0) {
                throw new IllegalArgumentException("Dimensions for bounds must be positive.");
            }
            double[] minVals = new double[dimensions];
            double[] maxVals = new double[dimensions];
            for(int i=0; i<dimensions; i++) {
                minVals[i] = min;
                maxVals[i] = max;
            }
            this.lowerBound = new Vector(minVals);
            this.upperBound = new Vector(maxVals);
            return this;
        }

        public GeneticAlgorithmParams build() {
            return new GeneticAlgorithmParams(this);
        }
    }
}