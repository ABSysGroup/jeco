package jeco.core.algorithm.ma;

import java.util.Comparator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

/**
 * All the local searches must implement this interface.
 *
 * @param <V> extends Variable, the type of the individuals genotype elements.
 * @author J. M. Colmenar
 */
public interface LocalSearch<V extends Variable<?>> {

    /**
     * Performs a local search over the given solution.
     * @param problem Problem to be used for evalution.
     * @param dominance Dominance comparator.
     * @param sol Input solution for local search.
     * @return New solution result of the local search.
     */
    public Solution<V> doLocalSearch(Problem<V> problem, Comparator<Solution<V>> dominance, Solution<V> sol);
}
