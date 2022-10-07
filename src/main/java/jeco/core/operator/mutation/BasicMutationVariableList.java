package jeco.core.operator.mutation;

import java.util.ArrayList;
import java.util.List;

import jeco.core.algorithm.sge.AbstractProblemDSGE;
import jeco.core.algorithm.sge.VariableList;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * Mutation for AbstractProblemDSGE that chooses an internal list element and interchanges it by another value in the accepted range, it only 
 * performs one mutation per non-terminal list only if a probability is accepted. Equivalent to
 * IntegerFlipMutationList in SSGE
 *
 * @param <T> extends Variable, the type of the individuals genotype elements that will be mutated, they must extends an ArrayList
 * of integers to perform the mutation
 */
public class BasicMutationVariableList <T extends Variable<ArrayList<Integer>>> extends MutationOperator<T>  {

	private AbstractProblemDSGE problem;
	
	/**Constructor for BasicMutationVariableList
	 * @param probability probability that determines whether to perform a mutation or not
	 * @param problem problem that contains all the variables of the solutions
	 */
	public BasicMutationVariableList(double probability, AbstractProblemDSGE problem) {
		super(probability);
		this.problem = problem;
		// TODO Auto-generated constructor stub
	}

	/**Executes mutation on a solution
	 * 
	 */
	@Override
	public Solution<T> execute(Solution<T> solution) {
		ArrayList<Integer> options = new ArrayList<>();
		
		//Check that the mutation will affect the gene because it  has more than one derivation, and that we are using the gene mutated
		//(Since a list might not be in use in the current solution)
		for(int i = 0; i < problem.getNumberOfVariables(); i++) {
			if((problem.getLowerBound(i)+1) < problem.getUpperBound(i) && (solution.getVariable(i).getValue().size() > 0)) {
				options.add(i);
			}
		}
	
		
		for(int i = 0;i<options.size(); i++) {
			if (RandomGenerator.nextDouble() < probability) {
			
				int randAlele = RandomGenerator.nextInt(solution.getVariable(options.get(i)).getValue().size());
				
				int randValue;
				
				do {
					randValue = RandomGenerator.nextInt((int) Math.round(problem.getLowerBound(options.get(i))),(int) Math.round(problem.getUpperBound(options.get(i))));
				}while(randValue == solution.getVariable(options.get(i)).getValue().get(randAlele));
		
				solution.getVariable(options.get(i)).getValue().remove(randAlele);
				solution.getVariable(options.get(i)).getValue().add(randAlele, randValue);
			
			}
		
		}
		
		return solution;
	}

}
