package jeco.core.algorithm.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;
import jeco.core.algorithm.Algorithm;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import org.apache.commons.math3.stat.StatUtils;

public class SimpleGeneticAlgorithm<V extends Variable<?>> extends Algorithm<V> {

    protected static final Logger logger = Logger.getLogger(SimpleGeneticAlgorithm.class.getName());

    /////////////////////////////////////////////////////////////////////////
    protected Boolean stopWhenSolved = null;
    protected Integer maxGenerations = null;
    protected Integer maxPopulationSize = null;
    protected Integer currentGeneration = null;
    /////////////////////////////////////////////////////////////////////////
    protected SimpleDominance<V> dominance = new SimpleDominance<V>();
    protected Solutions<V> population;
    protected MutationOperator<V> mutationOperator;
    protected CrossoverOperator<V> crossoverOperator;
    protected SelectionOperator<V> selectionOperator;
    /////////////////////////////////////////////////////////////////////////

    public SimpleGeneticAlgorithm(Problem<V> problem, Integer maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved, MutationOperator<V> mutationOperator, CrossoverOperator<V> crossoverOperator, SelectionOperator<V> selectionOperator) {
        super(problem);
        this.maxGenerations = maxGenerations;
        this.maxPopulationSize = maxPopulationSize;
        this.stopWhenSolved = stopWhenSolved;
        this.mutationOperator = mutationOperator;
        this.crossoverOperator = crossoverOperator;
        this.selectionOperator = selectionOperator;
    }

    @Override
    public void initialize() {
        population = problem.newRandomSetOfSolutions(maxPopulationSize);
        problem.evaluate(population);
        currentGeneration = 0;
    }

    @Override
    public Solutions<V> execute() {     
        logger.fine("@ # Gen.;Min Fit.;Max Fit.;Med Fit.");

        int nextPercentageReport = 10;
        HashMap<String,String> obsData = new HashMap<>();
        // For observers:
        obsData.put("MaxGenerations", String.valueOf(maxGenerations));
        stop = false;
        while ((currentGeneration < maxGenerations) && !stop){
            step();
            int percentage = Math.round((currentGeneration * 100) / (float)maxGenerations);
            Double bestObj = population.get(0).getObjectives().get(0);
            
            // For observers:
            obsData.put("CurrentGeneration", String.valueOf(currentGeneration));
            obsData.put("BestObjective", String.valueOf(bestObj));
            this.setChanged();
            this.notifyObservers(obsData);
            
            if (percentage == nextPercentageReport) {
                // Compute more stats:
                int infinityElems = 0;
                ArrayList<Double> fitValsList = new ArrayList<>(population.size());
                for (int i = 0; i < population.size(); i++) {
                    if (Double.isInfinite(population.get(i).getObjective(0)) || Double.isNaN(population.get(i).getObjective(0))) {
                        infinityElems++;
                    } else {
                        fitValsList.add(population.get(i).getObjective(0));
                    }
                }
                // Put into array
                double[] fitnessValues = new double[fitValsList.size()];
                for (int i = 0; i < fitValsList.size(); i++) {
                    fitnessValues[i] = fitValsList.get(i);
                }
                double avg = StatUtils.mean(fitnessValues);
                double stdDev = Math.sqrt(StatUtils.variance(fitnessValues));
                String msg = percentage + "% performed ..." + " -- Fitness info -->> Best: " + bestObj + " -->> Avg.: " + avg + " -->> Std. Dev.: " + stdDev;
                if (infinityElems > 0) {
                    msg += " -->> Infinity or NaN elems.: " + (infinityElems*100/population.size()) + " % ";
                }
                logger.info(msg);
                nextPercentageReport += 10;
            }
            if (stopWhenSolved) {
                if (bestObj <= 0) {
                    logger.info("Optimal solution found in " + currentGeneration + " generations.");
                    break;
                }
            }
        }
        if (stop) {
            logger.info("Execution stopped at generation "+ currentGeneration);
            logger.info("Best objective value: "+population.get(0).getObjectives().get(0));
        }
        
        return population;
    }

    @Override
    public void step() {
        currentGeneration++;
        // Create the offSpring solutionSet        
        Solutions<V> childPop = new Solutions<V>();
        Solution<V> parent1, parent2;
        for (int i = 0; i < (maxPopulationSize / 2); i++) {
            //obtain parents
            parent1 = selectionOperator.execute(population).get(0);
            parent2 = selectionOperator.execute(population).get(0);
            Solutions<V> offSpring = crossoverOperator.execute(parent1, parent2);
            for (Solution<V> solution : offSpring) {
                mutationOperator.execute(solution);
                childPop.add(solution);
            }
        } // for
        problem.evaluate(childPop);
        population = replacement(population,childPop);

        StringBuilder buffer = new StringBuilder();
        buffer.append("@ ").append(currentGeneration).append(";").append(population.get(0).getObjective(0));
        buffer.append(";").append(population.get(population.size() - 1).getObjective(0)).append(";").append(population.get(population.size() / 2).getObjective(0));
        logger.fine(buffer.toString());

    }
/*
    public void reduceLeaders() {
        Collections.sort(leaders, dominance);
        // Remove repetitions:
        int compare;
        Solution<V> solI;
        Solution<V> solJ;
        for (int i = 0; i < leaders.size() - 1; i++) {
            solI = leaders.get(i);
            for (int j = i + 1; j < leaders.size(); j++) {
                solJ = leaders.get(j);
                compare = dominance.compare(solI, solJ);
                if (compare == 0) { // i == j, just one copy
                    leaders.remove(j--);
                }
            }
        }
        if (leaders.size() <= maxPopulationSize) {
            return;
        }
        while (leaders.size() > maxPopulationSize) {
            leaders.remove(leaders.size() - 1);
        }
    }
  */

    @Override
    public Solutions<V> getPopulation() {
        return population;
    }

    /**
     * Replacement of population by the offspring; by default this is a generational GA where all the population is
     * replaced by the offspring.
     *
     * @param population
     * @param offspring
     * @return New population already sorted
     */
    protected Solutions<V> replacement(Solutions<V> population, Solutions<V> offspring) {
        /* Generational implementarion. Only maintains the best individual from the previous population removing
           the worst from the offspring. */
        Collections.sort(population, dominance);
        offspring.add(population.get(0));
        Collections.sort(offspring, dominance);
        offspring.remove(population.size() - 1);

        return offspring;
    }

}
