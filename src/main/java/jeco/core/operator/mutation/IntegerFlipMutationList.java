package jeco.core.operator.mutation;

import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class IntegerFlipMutationList <T extends Variable<Integer[]>> extends MutationOperator<T> {

	protected Problem<T> problem;
	public IntegerFlipMutationList(Problem<T> problem, double probability) {
		super(probability);
		this.problem = problem;
		// TODO Auto-generated constructor stub
	}
	@Override
	public Solution<T> execute(Solution<T> solution) {
		for (int i = 0; i < solution.getVariables().size(); i++) {
			if (RandomGenerator.nextDouble() < probability) {
				
				int selected_gene = RandomGenerator.nextInt(solution.getVariable(i).getValue().length);
				
				int lowerBound = (int)Math.round(problem.getLowerBound(i));
				int upperBound = (int)Math.round(problem.getUpperBound(i));
				//The arrays inside the genes are not cloned properly, to fix this issue, since the only operator
				//that touches the inside of the list is the mutation I will create a new one and add all the values plus
				//the changed one instead of chaging it inside
				Integer[] value = new Integer[solution.getVariables().get(i).getValue().length];
				for(int j = 0; j < solution.getVariables().get(i).getValue().length; j++) {
					value[j] = solution.getVariables().get(i).getValue()[j];
				}
				
				
				value[selected_gene] = RandomGenerator.nextInteger(lowerBound, upperBound);
				solution.getVariables().get(i).setValue(value);
			}
		}
		return solution;
	}

}
