package jeco.core.operator.selection;

import java.util.Comparator;

import jeco.core.operator.comparator.SolutionDominance;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/** Binary tournament selection
 */
public class BinaryTournament<T extends Variable<?>> extends SelectionOperator<T> {

	/**Comparator of a list of solutions*/
    protected Comparator<Solution<T>> comparator;

    /**Constructor for Binary tournament with comparator 
     * @param comparator comparator for selection
     */
    public BinaryTournament(Comparator<Solution<T>> comparator) {
        this.comparator = comparator;
    } // BinaryTournament

    /**Constructor for Binary Tournament
     * 
     */
    public BinaryTournament() {
        this(new SolutionDominance<T>());
    } // Constructor

    public Solutions<T> execute(Solutions<T> solutions) {
        Solutions<T> result = new Solutions<T>();
        Solution<T> s1, s2;
        s1 = solutions.get(RandomGenerator.nextInt(0, solutions.size()));
        s2 = solutions.get(RandomGenerator.nextInt(0, solutions.size()));

        int flag = comparator.compare(s1, s2);
        if (flag == -1) {
            result.add(s1);
        } else if (flag == 1) {
            result.add(s2);
        } else if (RandomGenerator.nextDouble() < 0.5) {
            result.add(s1);
        } else {
            result.add(s2);
        }
        return result;
    } // execute
} // BinaryTournament

