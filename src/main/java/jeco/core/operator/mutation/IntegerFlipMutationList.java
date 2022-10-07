package jeco.core.operator.mutation;

import jeco.core.algorithm.sge.AbstractProblemSSGE;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * 
 * Mutation for AbstractProblemSSGE that chooses an internal list element and interchanges it by another value in the accepted range, it only 
 * performs one mutation per non-terminal list only if a probability is accepted. Equivalent to
 * BasicMutationVariableList in DSGE
 *
 *
 * @param <T> extends Variable, the type of the individuals genotype elements that will be mutated, they must extends an array
 * of Integers to perform the mutation
 */
public class IntegerFlipMutationList <T extends Variable<Integer[]>> extends MutationOperator<T> {

	protected AbstractProblemSSGE problem;
	public IntegerFlipMutationList(AbstractProblemSSGE problem, double probability) {
		super(probability);
		this.problem = problem;
	}
	@Override
	public Solution<T> execute(Solution<T> solution) {
		for (int i = 0; i < solution.getVariables().size(); i++) {
			int lowerBound = (int)Math.round(problem.getLowerBound(i));
			int upperBound = (int)Math.round(problem.getUpperBound(i));
			
			//If the mutation affects a gene and the probability is less than the probability of mutation
			if (((lowerBound+1) < upperBound) && (RandomGenerator.nextDouble() < probability)) {
				
				int selected_gene = RandomGenerator.nextInt(solution.getVariable(i).getValue().length);
							
				
				
				int newValue;
				//We always try to change it to a different value than the original
				do {
					newValue =  RandomGenerator.nextInteger(lowerBound, upperBound);
				}while(newValue == solution.getVariable(i).getValue()[selected_gene]);
				
				solution.getVariable(i).getValue()[selected_gene] = newValue;
				
			}
		}
		return solution;
	}

}
