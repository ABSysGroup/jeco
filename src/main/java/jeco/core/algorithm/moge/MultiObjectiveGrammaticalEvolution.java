package jeco.core.algorithm.moge;

import jeco.core.algorithm.moga.NSGAII;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.mutation.IntegerFlipMutation;
import jeco.core.operator.selection.TournamentSelect;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Multi-objective Grammatical Evolution algorithm based on NSGAII, which is modified to provide the observer
 * the current population.
 *
 * @author J. Manuel Colmenar
 */
public class MultiObjectiveGrammaticalEvolution extends NSGAII<Variable<Integer>> {

    private int hypervolumeReportGenerationsGap;
    private int returnedSolutions;

    public MultiObjectiveGrammaticalEvolution(Problem<Variable<Integer>> problem, int maxPopulationSize, int maxGenerations, double mutationProb, double crossOverProb, int tournamentSize) {
        super(problem, maxPopulationSize, maxGenerations,
                new IntegerFlipMutation<>(problem, mutationProb),
                new SinglePointCrossover<>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, crossOverProb, SinglePointCrossover.AVOID_REPETITION_IN_FRONT),
                new TournamentSelect<>(tournamentSize, new SimpleDominance<>()));

        // Reports Hypervolume at the end by default.
        hypervolumeReportGenerationsGap = maxGenerations;

        // Default number of final solutions returned by the algorithm is 10
        returnedSolutions = 10;
    }

    public int getReturnedSolutions() {
        return returnedSolutions;
    }

    public void setReturnedSolutions(int returnedSolutions) {
        this.returnedSolutions = returnedSolutions;
    }

    public int getHypervolumeReportGenerationsGap() {
        return hypervolumeReportGenerationsGap;
    }

    public void setHypervolumeReportGenerationsGap(int hypervolumeReportGenerationsGap) {
        this.hypervolumeReportGenerationsGap = hypervolumeReportGenerationsGap;
    }

    @Override
    public Solutions<Variable<Integer>> execute() {

        final int DEFAULT_PCT_REPORT = 10;
        int nextPercentageReport = DEFAULT_PCT_REPORT;

        HashMap<String, Object> obsData = new HashMap<>();
        // For observers:
        obsData.put("MaxGenerations", String.valueOf(maxGenerations));
        stop = false;
        while ((currentGeneration < maxGenerations) && !stop) {
            step();

            if ((this.countObservers() > 0) && ((currentGeneration % hypervolumeReportGenerationsGap) == 0)) {
                double[][] objs = generateObjectivesMatrix(population);
                obsData.put("CurrentGeneration", String.valueOf(currentGeneration));
                obsData.put("Objectives", objs);
                this.setChanged();
                this.notifyObservers(obsData);
            }

            int percentage = Math.round((currentGeneration * 100) / maxGenerations);
            if (percentage == nextPercentageReport) {
                logger.info(percentage + "% performed ... ");
                nextPercentageReport += DEFAULT_PCT_REPORT;
            }

        }

        // Return a reduced number of solutions considering the crowding distance:
        logger.info("Removing repetitions and returning the best "+returnedSolutions+" non-dominated solutions according to the crowding distance ...");

        Solutions<Variable<Integer>> sols = removeRepetitions(this.getCurrentSolution());
        Solutions<Variable<Integer>> finalSols = this.reduce(sols, returnedSolutions);

        // Report final front to plot:
        if (this.countObservers() > 0) {
            double[][] objs = generateObjectivesMatrix(finalSols);
            obsData.put("CurrentGeneration", String.valueOf(currentGeneration));
            obsData.put("Objectives", objs);
            this.setChanged();
            this.notifyObservers(obsData);
        }

        return finalSols;
    }

    private Solutions<Variable<Integer>> removeRepetitions(Solutions<Variable<Integer>> sols) {
        Solutions<Variable<Integer>> filteredSols = new Solutions<>();
        HashSet<String> hash = new HashSet<>(sols.size());

        for (Solution<Variable<Integer>> s : sols) {
            String phen = problem.phenotypeToString(s);
            if (!hash.contains(phen)) {
                filteredSols.add(s);
                hash.add(phen);
            }
        }

        return filteredSols;
    }


    private double[][] generateObjectivesMatrix(Solutions<Variable<Integer>> sols) {
        double[][] objs = new double[sols.size()][problem.getNumberOfObjectives()];
        for (int i = 0; i < sols.size(); i++) {
            for (int j = 0; j < problem.getNumberOfObjectives(); j++) {
                objs[i][j] = sols.get(i).getObjective(j);
            }
        }
        return objs;
    }

}
