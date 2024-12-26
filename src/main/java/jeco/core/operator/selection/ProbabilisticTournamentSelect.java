package jeco.core.operator.selection;

import java.util.Collections;
import java.util.Comparator;

import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class ProbabilisticTournamentSelect<T extends Variable<?>> extends SelectionOperator<T> {
    public static final int DEFAULT_TOURNAMENT_SIZE = 2;
    public static final float DEFAULT_PROB = (float) 0.4;

    protected Comparator<Solution<T>> comparator;
    protected int tournamentSize;
    private float prob;

    public ProbabilisticTournamentSelect(int tournamentSize, float prob, Comparator<Solution<T>> comparator) {
        this.tournamentSize = tournamentSize;
        this.comparator = comparator;
        this.prob = prob;
    } // TournamentSelect
    

    public ProbabilisticTournamentSelect(Comparator<Solution<T>> comparator) {
        this(TournamentSelect.DEFAULT_TOURNAMENT_SIZE, TournamentSelect.DEFAULT_TOURNAMENT_SIZE, comparator);
    } // TournamentSelect

    public ProbabilisticTournamentSelect() {
        this(TournamentSelect.DEFAULT_TOURNAMENT_SIZE, TournamentSelect.DEFAULT_TOURNAMENT_SIZE, new SolutionDominance<T>());
    } // TournamentSelect

    public Solutions<T> execute(Solutions<T> solutions) {
    	float random = (float) RandomGenerator.nextDouble();
        Solutions<T> result = new Solutions<T>();
        Solutions<T> tournamentSet = new Solutions<T>();
        for (int i = 0; i < tournamentSize; ++i) {
            tournamentSet.add(solutions.get(RandomGenerator.nextInteger(solutions.size())));
        }
        Collections.sort(tournamentSet, comparator);
    	if (random < prob) {
	        result.add(tournamentSet.get(tournamentSet.size() -1));

    	}else {

	        result.add(tournamentSet.get(0));
    	}
        return result;
    } // execute
}
