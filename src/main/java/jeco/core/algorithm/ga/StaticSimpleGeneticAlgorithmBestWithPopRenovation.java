package jeco.core.algorithm.ga;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.stat.StatUtils;

import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Genetic Algorithms that always keeps the best individuals from the parents and the children as the next population
 * It regenerates a percentage in case the population fitness converges too soon.
 *
 * @param <V> extends Variable, the type of the individuals genotype elements.
 */
public class StaticSimpleGeneticAlgorithmBestWithPopRenovation<V extends Variable<?>> extends SimpleGeneticAlgorithm<V> {
	
	double percentaje;
    public StaticSimpleGeneticAlgorithmBestWithPopRenovation(Problem problem, Integer maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved, MutationOperator mutationOperator, CrossoverOperator crossoverOperator, SelectionOperator selectionOperator, double per) {
        super(problem, maxPopulationSize, maxGenerations, stopWhenSolved, mutationOperator, crossoverOperator, selectionOperator);
        this.percentaje = per;
    }

    @Override
    public void step() {
        currentGeneration++;
        
        // Compute stdev:
        int infinityElems = 0;
        ArrayList<Double> fitValsList = new ArrayList<>(population.size());
        for (int i = 0; i < population.size(); i++) {
            if (Double.isInfinite(population.get(i).getObjective(0)) || Double.isNaN(population.get(i).getObjective(0))) {
                infinityElems++;
            } else {
                fitValsList.add(population.get(i).getObjective(0));
            }
        }
        
        double[] fitnessValues = new double[fitValsList.size()];
        for (int i = 0; i < fitValsList.size(); i++) {
            fitnessValues[i] = fitValsList.get(i);
        }
        double stdDev = Math.sqrt(StatUtils.variance(fitnessValues));
        
        //If we end in a state where there is no different individuals due to always taking the best ones we regenerate a percentage
        //Of the population with new individuals
        if(stdDev == 0.0) {
        	regeneratePop(this.percentaje);
        }
        
        if(population.size() != maxPopulationSize) {
        	throw new RuntimeException("Error population not max size");
        	
        }
        
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
        } // we evaluate the children  before replacement
        problem.evaluate(childPop);
        
        // Replacement
        population = replacement(population,childPop);
        
        if(population.size() != maxPopulationSize) {
        	throw new RuntimeException("Error population not max size");
        	
        }

        //Actualize the archive
        for (Solution<V> solution : population) {
            Solution<V> clone = solution.clone();
            leaders.add(clone);
        }
        reduceLeaders();
        StringBuilder buffer = new StringBuilder();
        buffer.append("@ ").append(currentGeneration).append(";").append(leaders.get(0).getObjective(0));
        buffer.append(";").append(leaders.get(leaders.size() - 1).getObjective(0)).append(";").append(leaders.get(leaders.size() / 2).getObjective(0));
        logger.fine(buffer.toString());

    }
    
    
    private void regeneratePop(double percentaje) {
    	int max = maxPopulationSize-1;
    	 for (int i = 0; i < (int)Math.round(maxPopulationSize*percentaje); i++) {
    		 population.remove(max);
    		 max--;
    	 }
    	 
    	 Solutions<V> newSols = problem.newRandomSetOfSolutions((int)Math.round(maxPopulationSize*percentaje));
    	 problem.evaluate(newSols);
    	 for (int i = 0; i < (int)Math.round(maxPopulationSize*percentaje); i++) {
    		 population.add(newSols.get(i));
    	 }
    }

    
    /**
     * Merges the population with the offspring maintaining the best of 
     * both lists of individuals
     * @param population population to merge
     * @param offspring offspring to merge
     * @return 
     */
    protected Solutions<V> replacement(Solutions<V> population, Solutions<V> offspring) {
        //Collections.sort(offspring, dominance);
       
        for(int i = 0; i < offspring.size(); i++) {
        	 population.add(offspring.get(i));
        }
        
        Collections.sort(population, dominance);
        
        for(int i = 0; i < offspring.size(); i++) {
        	population.remove(population.size() - 1);
        }
        
        
        
        return population;
    }

}
