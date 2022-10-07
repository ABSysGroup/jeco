package jeco.core.operator.mutation;

import jeco.core.algorithm.sge.AbstractProblemSSGE;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * 
 * Mutation of AbstractProblemSSGE that performs the interchange of the value of an allele by another of the possible values,
 * it is performed on any allele on any of the non-terminal lists as long as a certain probability is accepted. Equivalent to
 * BasicMutationVariableListAll in DSGE
 *
 * @param <T> extends Variable, the type of the individuals genotype elements that will be mutated, they must extends an array
 * of Integers to perform the mutation
 */
public class IntegerFlipMutationListAll <T extends Variable<Integer[]>> extends MutationOperator<T> {

	protected AbstractProblemSSGE problem;
	public IntegerFlipMutationListAll(AbstractProblemSSGE problem, double probability) {
		super(probability);
		this.problem = problem;
		// TODO Auto-generated constructor stub
	}
	@Override
	public Solution<T> execute(Solution<T> solution) {
		for (int i = 0; i < solution.getVariables().size(); i++) {
			
			int lowerBound = (int)Math.round(problem.getLowerBound(i));
			int upperBound = (int)Math.round(problem.getUpperBound(i));
			
			//If the mutation affects a gene and the probability is less than the probability of mutation
			if ((lowerBound +1) < upperBound) {
				
				for(int j = 0; j < solution.getVariable(i).getValue().length; j++) {
							
					if (RandomGenerator.nextDouble() < probability) {
						
						int newValue;
						//We always try to change it to a different value than the original
						do {
							newValue =  RandomGenerator.nextInteger(lowerBound, upperBound);
						}while(newValue == solution.getVariable(i).getValue()[j]);
						
						solution.getVariable(i).getValue()[j] = newValue;

					}
				}
			}
		}
		return solution;
	}

}
