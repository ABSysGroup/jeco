package jeco.core.operator.mutation;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class IntegerFlipMutationListAll <T extends Variable<Integer[]>> extends MutationOperator<T> {

	protected Problem<T> problem;
	public IntegerFlipMutationListAll(Problem<T> problem, double probability) {
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
						//solution.getVariable(i).getValue()[j] = RandomGenerator.nextInteger(lowerBound, upperBound);
					}
				}
			}
		}
		return solution;
	}

}
