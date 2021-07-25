package jeco.core.algorithm.sge;

import java.util.Comparator;


import java.util.logging.Logger;

import jeco.core.algorithm.Algorithm;
import jeco.core.algorithm.ga.SimpleGeneticAlgorithm;
import jeco.core.algorithm.ga.StaticSimpleGeneticAlgorithm;
import jeco.core.algorithm.moga.NSGAII;
import jeco.core.operator.assigner.CrowdingDistance;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.crossover.UniformCrossover;
import jeco.core.operator.mutation.IntegerFlipMutation;
import jeco.core.operator.mutation.IntegerFlipMutationList;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.operator.selection.BinaryTournamentNSGAII;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;



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
