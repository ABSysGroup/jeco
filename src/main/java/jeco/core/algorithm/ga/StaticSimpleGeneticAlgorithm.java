package jeco.core.algorithm.ga;

import java.util.Collections;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Simple GA with a different offspring replacement that is more static because
 * it does not replaces all the population but only the 2 worst individuals.
 * 
 * @author J. Manuel Colmenar
 */
public class StaticSimpleGeneticAlgorithm<V extends Variable<?>> extends SimpleGeneticAlgorithm<V> {
	private int num_chang;
    
	public StaticSimpleGeneticAlgorithm(Problem problem, Integer maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved, MutationOperator mutationOperator, CrossoverOperator crossoverOperator, SelectionOperator selectionOperator) {
        super(problem, maxPopulationSize, maxGenerations, stopWhenSolved, mutationOperator, crossoverOperator, selectionOperator);
        num_chang = 2;

    }
    
    public StaticSimpleGeneticAlgorithm(Problem problem, Integer maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved, MutationOperator mutationOperator, CrossoverOperator crossoverOperator, SelectionOperator selectionOperator, int num) {
        super(problem, maxPopulationSize, maxGenerations, stopWhenSolved, mutationOperator, crossoverOperator, selectionOperator);
        num_chang = num;
    }


    /**
     * Merges the population with the offspring removing the worst 2 individuals
     * of the population and including the 2 better offspring.
     * 
     * @param population
     * @param offspring
     * @return 
     */
    protected Solutions<V> replacement(Solutions<V> population, Solutions<V> offspring) {
        Collections.sort(offspring, dominance);
        for(int i = 0; i < this.num_chang; i++) {
            population.add(offspring.get(i));

        }
        //population.add(offspring.get(0));
        //population.add(offspring.get(1));
        Collections.sort(population, dominance);
        
        for(int i = 0; i < this.num_chang; i++) {
            population.remove(population.size() - 1);

        }
        //population.remove(population.size() - 1);
        //population.remove(population.size() - 1);

        return population;
    }

}
