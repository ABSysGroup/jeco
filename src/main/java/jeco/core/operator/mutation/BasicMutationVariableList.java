package jeco.core.operator.mutation;

import java.util.ArrayList;
import java.util.List;

import jeco.core.algorithm.sge.AbstractProblemDSGE;
import jeco.core.algorithm.sge.VariableList;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class BasicMutationVariableList <T extends Variable<ArrayList<Integer>>> extends MutationOperator<T>  {

	private AbstractProblemDSGE problem;
	
	public BasicMutationVariableList(double probability, AbstractProblemDSGE problem) {
		super(probability);
		this.problem = problem;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Solution<T> execute(Solution<T> solution) {
		ArrayList<Integer> options = new ArrayList<>();
		
		//Check that the mutation will affect the gene because it  has more than one derivation, and that we are using the gene mutated
		//(Since a gene might not be in use in the current solution)
		for(int i = 0; i < problem.getNumberOfVariables(); i++) {
			if((problem.getLowerBound(i)+1) < problem.getUpperBound(i) && (solution.getVariable(i).getValue().size() > 0)) {
				options.add(i);
			}
		}
		
		//int randGene = RandomGenerator.nextInt(options.size());
		
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
