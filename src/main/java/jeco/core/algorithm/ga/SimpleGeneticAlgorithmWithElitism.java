package jeco.core.algorithm.ga;

import java.util.Collections;

import jeco.core.algorithm.Algorithm;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public class SimpleGeneticAlgorithmWithElitism <V extends Variable<?>> extends SimpleGeneticAlgorithm<V> {
    protected Integer elitism = null;

    public SimpleGeneticAlgorithmWithElitism(Problem<V> problem, Integer maxPopulationSize, Integer maxGenerations, Boolean stopWhenSolved, MutationOperator<V> mutationOperator, CrossoverOperator<V> crossoverOperator, SelectionOperator<V> selectionOperator, double elit) {
        super(problem,maxPopulationSize, maxGenerations, stopWhenSolved, mutationOperator, crossoverOperator, selectionOperator);
        
        this.elitism =(int) Math.ceil(maxPopulationSize*elit);
        if(elitism % 2==1) {
            elitism-=1;
            }
        if(elitism < 2) {
            elitism = 2;
            
        }
    }
    
    @Override
    public void step() {
        currentGeneration++;
        // Create the offSpring solutionSet        
        Solutions<V> childPop = new Solutions<V>();
        Solution<V> parent1, parent2;
        
        for (int i = 0; i < ((maxPopulationSize - elitism) / 2); i++) {
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
    @Override
    protected Solutions<V> replacement(Solutions<V> population, Solutions<V> offspring) {
        
        Collections.sort(population, dominance);
        for (int i = 0; i < elitism; i++) {
            offspring.add(population.get(i));
        }
        //Collections.sort(offspring, dominance);
        return offspring;
    }
    
}