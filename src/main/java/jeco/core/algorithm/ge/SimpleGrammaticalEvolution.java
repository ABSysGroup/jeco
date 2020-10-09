package jeco.core.algorithm.ge;

import jeco.core.algorithm.Algorithm;
import jeco.core.algorithm.ga.SimpleGeneticAlgorithm;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.mutation.IntegerFlipMutation;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.problem.Problem;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Grammatical evolution using just one objective.
 * 
 * @author J. M. Colmenar
 */
public class SimpleGrammaticalEvolution extends SimpleGeneticAlgorithm<Variable<Integer>> {

    public SimpleGrammaticalEvolution(Problem<Variable<Integer>> problem, int maxPopulationSize, int maxGenerations, double probMutation, double probCrossover) {
        super(problem,
                maxPopulationSize,
                maxGenerations, true,
                new IntegerFlipMutation<Variable<Integer>>(problem, probMutation),
                new SinglePointCrossover<Variable<Integer>>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, probCrossover, SinglePointCrossover.ALLOW_REPETITION),
                new BinaryTournament<Variable<Integer>>(new SimpleDominance<>()));
    }

}
