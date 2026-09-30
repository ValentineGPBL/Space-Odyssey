package app.phys.optimizer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import app.utils.Vector;

public class GeneticAlgorithmOptimizerStrategy implements OptimizerStrategy {

    // Inner class to represent an individual in the population
    private static class Individual {
        Vector genome;  // The solution vector
        double fitness; // Cost of the solution (lower is better)

        public Individual(Vector genome) {
            this.genome = genome;
            this.fitness = Double.MAX_VALUE; // Initialize with worst fitness
        }

        public Individual(Vector genome, double fitness) {
            this.genome = genome;
            this.fitness = fitness;
        }

        public Vector getGenome() {
            return genome;
        }

        public double getFitness() {
            return fitness;
        }

        public void setFitness(double fitness) {
            this.fitness = fitness;
        }
        
        // Creates a copy of the individual
        public Individual copy() {
            // Assuming Vector.clone() performs a deep copy of its internal data
            return new Individual(this.genome.clone(), this.fitness);
        }
    }

    private final Function<Vector, Double> costFunction;
    private final GeneticAlgorithmParams params;
    private final int genomeLength;
    private final Vector initial;
    private List<Individual> population;
    private final Random random = new Random();
    private final Vector lowerBound;
    private final Vector upperBound;

    public GeneticAlgorithmOptimizerStrategy(Function<Vector, Double> costFunction, Vector initialState, GeneticAlgorithmParams params) {
        if (costFunction == null) throw new IllegalArgumentException("Cost function cannot be null.");
        if (initialState == null) throw new IllegalArgumentException("Initial state cannot be null.");
        if (params == null) throw new IllegalArgumentException("GeneticAlgorithmParams cannot be null.");
        
        this.costFunction = costFunction;
        this.params = params;
        this.initial = initialState;
        this.genomeLength = initialState.size(); // Genome length is determined by the initialState's dimension

        if (this.genomeLength <= 0) {
            throw new IllegalArgumentException("Genome length (derived from initial state size) must be positive.");
        }

        // Parameter bounds are validated for null and matching dimensions in GeneticAlgorithmParams.
        // Here, check if their dimension matches the genomeLength derived from initialState.
        if (params.getLowerBound().size() != genomeLength || params.getUpperBound().size() != genomeLength) {
            throw new IllegalArgumentException("Bounds dimensions in params must match initial state (genome) dimensions.");
        }
        this.lowerBound = params.getLowerBound();
        this.upperBound = params.getUpperBound();

        this.population = new ArrayList<>(params.getPopulationSize());
    }

    @Override
    public Vector solve() {
        initializePopulation();
        evaluatePopulation();

        for (int generation = 0; generation < params.getMaxGenerations(); generation++) {
            List<Individual> newPopulation = new ArrayList<>(params.getPopulationSize());

            // Sort current population by fitness (ascending, lower fitness is better)
            population.sort(Comparator.comparingDouble(Individual::getFitness));
            
            // Elitism: carry over the best individuals to the new population
            for (int i = 0; i < params.getElitismCount() && i < population.size(); i++) {
                newPopulation.add(population.get(i).copy()); // Add a copy
            }

            // Fill the rest of the new population through selection, crossover, and mutation
            while (newPopulation.size() < params.getPopulationSize()) {
                Individual parent1 = tournamentSelection();
                Individual parent2 = tournamentSelection();

                // Create offspring as copies of parents initially
                Individual offspring1 = parent1.copy(); 
                Individual offspring2 = parent2.copy();

                // Apply crossover
                if (random.nextDouble() < params.getCrossoverRate()) {
                    arithmeticCrossover(parent1, parent2, offspring1, offspring2);
                }
                
                // Apply mutation
                mutate(offspring1);
                mutate(offspring2);
                
                // Ensure offspring genes are within bounds
                clampToBounds(offspring1.genome);
                clampToBounds(offspring2.genome);

                // Add offspring to new population if there's space
                if (newPopulation.size() < params.getPopulationSize()) {
                    newPopulation.add(offspring1);
                }
                if (newPopulation.size() < params.getPopulationSize()) {
                    newPopulation.add(offspring2);
                }
            }
            population = newPopulation; // Replace old population with the new one
            evaluatePopulation(); // Evaluate fitness of the new population

            if (generation % 1 == 0) { // Example logging
               System.out.println("Generation: " + generation + ", Best Fitness: " + population.get(0).getFitness());
            }
        }

        // Sort final population to find the best individual
        population.sort(Comparator.comparingDouble(Individual::getFitness));
        
        if (population.isEmpty()) {
            // Fallback for an unlikely scenario (e.g., if population size was 0, though params should prevent)
            return new Vector(genomeLength); // Return a default or empty vector
        }
        return population.get(0).getGenome(); // Return the genome of the best individual
    }

    private void initializePopulation() {
        population.clear();
        for (int i = 0; i < params.getPopulationSize(); i++) {
            Vector genome = new Vector(genomeLength);
            for (int j = 0; j < genomeLength; j++) {
                // Initialize gene value randomly within its specific bounds
                double geneValue = lowerBound.get(j) + (upperBound.get(j) - lowerBound.get(j)) * random.nextDouble();
                geneValue = Math.clamp(geneValue + (initial.get(j) - lowerBound.get(j)) * 0.1, lowerBound.get(j), upperBound.get(j));
                //double geneValue = initial.get(j);
                genome.set(j, geneValue);
            }
            population.add(new Individual(genome));
        }
    }

    private void evaluatePopulation() {
        // Calculate fitness for each individual in the population
        // This could be parallelized for performance improvement on large populations
        for (Individual individual : population) {
            // Fitness is reset for new/modified individuals, or re-evaluated if needed.
            // Current simple approach: always re-evaluate all.
            individual.setFitness(costFunction.apply(individual.getGenome()));
        }
    }

    private Individual tournamentSelection() {
        int currentTournamentSize = params.getTournamentSize();
        // Ensure tournament isn't larger than population (should be caught by param validation)
        // but defensive check for very small populations at intermediate stages (not an issue here)
        if (population.isEmpty()) {
            throw new IllegalStateException("Population is empty, cannot perform selection.");
        }

        Individual bestInTournament = null;
        for (int i = 0; i < currentTournamentSize; i++) {
            // Select a random individual from the population
            Individual contender = population.get(random.nextInt(population.size()));
            if (bestInTournament == null || contender.getFitness() < bestInTournament.getFitness()) {
                bestInTournament = contender;
            }
        }
        return bestInTournament; // The winner of the tournament (best fitness)
    }

    // Arithmetic Crossover (a form of blend crossover)
    private void arithmeticCrossover(Individual parent1, Individual parent2, Individual offspring1, Individual offspring2) {
        Vector genome1 = offspring1.getGenome(); // Offspring1's genome (initially copy of parent1)
        Vector genome2 = offspring2.getGenome(); // Offspring2's genome (initially copy of parent2)
        
        Vector p1Genome = parent1.getGenome();
        Vector p2Genome = parent2.getGenome();

        // Alpha for blending - can be random per crossover or fixed
        double alpha = random.nextDouble(); // A random blend factor between 0.0 and 1.0

        for (int i = 0; i < genomeLength; i++) {
            double geneP1 = p1Genome.get(i);
            double geneP2 = p2Genome.get(i);
            
            // Create new genes by blending parent genes
            double geneOffspring1 = alpha * geneP1 + (1.0 - alpha) * geneP2;
            double geneOffspring2 = (1.0 - alpha) * geneP1 + alpha * geneP2;

            genome1.set(i, geneOffspring1);
            genome2.set(i, geneOffspring2);
        }
        // Mark offspring fitness as needing re-evaluation
        offspring1.setFitness(Double.MAX_VALUE);
        offspring2.setFitness(Double.MAX_VALUE);
    }

    private void mutate(Individual individual) {
        Vector genome = individual.getGenome();
        boolean mutated = false;
        for (int i = 0; i < genomeLength; i++) {
            if (random.nextDouble() < params.getMutationRate()) {
                mutated = true;
                double currentValue = genome.get(i);
                double geneLowerBound = lowerBound.get(i);
                double geneUpperBound = upperBound.get(i);
                double geneRange = geneUpperBound - geneLowerBound;

                double mutationAmount;
                // Mutate by a small perturbation, e.g., up to 10% of the gene's range or current value
                double mutationStrengthFactor = 0.1; 

                if (geneRange > 1e-9) { // If the gene has a defined range
                    mutationAmount = (random.nextDouble() - 0.5) * geneRange * mutationStrengthFactor;
                } else if (Math.abs(currentValue) > 1e-9) { // If no range, but current value is non-zero
                    mutationAmount = (random.nextDouble() - 0.5) * Math.abs(currentValue) * mutationStrengthFactor;
                } else { // Both range and current value are (near) zero
                    mutationAmount = (random.nextDouble() - 0.5) * 0.01; // Apply a very small absolute perturbation
                }
                
                double newValue = currentValue + mutationAmount;
                genome.set(i, newValue);
            }
        }
        if (mutated) {
            // Mark fitness as needing re-evaluation if any gene was mutated
            individual.setFitness(Double.MAX_VALUE);
        }
    }
    
    // Ensures all genes in a genome are within their predefined lower and upper bounds
    private void clampToBounds(Vector genome) {
        for (int i = 0; i < genomeLength; i++) {
            double val = genome.get(i);
            if (val < lowerBound.get(i)) {
                genome.set(i, lowerBound.get(i));
            } else if (val > upperBound.get(i)) {
                genome.set(i, upperBound.get(i));
            }
        }
    }
}