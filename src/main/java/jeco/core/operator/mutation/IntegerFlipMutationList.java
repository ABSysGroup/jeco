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
				
				solution.getVariable(i).getValue()[selected_gene] = RandomGenerator.nextInteger(lowerBound, upperBound);
				
			}
		}
		return solution;
	}

}
