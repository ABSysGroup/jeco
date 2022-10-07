package jeco.core.operator.selection;

import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public abstract class SelectionOperator<T extends Variable<?>> {
	
	/**Executes a selection operator over a set of solutions
	 * 
	 * @param solutions list of solutions to perform selection
	 * @return Another list of solution after the selection is performed
	 */
    abstract public Solutions<T> execute(Solutions<T> solutions);
}
