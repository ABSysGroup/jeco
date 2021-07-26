package jeco.core.algorithm.sge;

import jeco.core.algorithm.ga.StaticSimpleGeneticAlgorithm;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.UniformCrossover;

import jeco.core.operator.mutation.IntegerFlipMutationList;

import jeco.core.operator.selection.BinaryTournament;

import jeco.core.problem.Problem;




public class StructuredGramaticalEvolution extends StaticSimpleGeneticAlgorithm<VariableArray<Integer>> {

    public StructuredGramaticalEvolution(Problem<VariableArray<Integer>> problem, int maxPopulationSize, int maxGenerations, double probMutation, double probCrossover) {
        super(problem,
                maxPopulationSize,
                maxGenerations, true,
                new IntegerFlipMutationList<VariableArray<Integer>>(problem, probMutation),
                new UniformCrossover<VariableArray<Integer>>( probCrossover),
                new BinaryTournament<VariableArray<Integer>>(new SimpleDominance<>()));
    }

}
