package jeco.core.operator.mutation;

import java.util.ArrayList;

import jeco.core.algorithm.sge.AbstractProblemDSGE;
import jeco.core.algorithm.sge.VariableList;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * Mutation of AbstractProblemDSGE that performs the interchange of the value of an allele by another of the possible values,
 * it is performed on any allele on any of the non-terminal lists as long as a certain probability is accepted. Equivalent to
 * IntegerFlipMutationListAll in SSGE
 *
 * @param <T>
 */
public class BasicMutationVariableListAll <T extends Variable<ArrayList<Integer>>> extends MutationOperator<T>  {

	private AbstractProblemDSGE problem;
	
	public BasicMutationVariableListAll(double probability, AbstractProblemDSGE problem) {
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
			
			int size = solution.getVariable(options.get(i)).getValue().size();
			for(int randAlele= 0; randAlele < size; randAlele++) {
				
				if (RandomGenerator.nextDouble() < probability) {
					
					//int randValue = RandomGenerator.nextInt((int) problem.getLowerBound(options.get(i)),(int) problem.getUpperBound(options.get(i)));
					int randValue;
					do {
						randValue = RandomGenerator.nextInt((int) Math.round(problem.getLowerBound(options.get(i))),(int) Math.round(problem.getUpperBound(options.get(i))));
					}while(randValue == solution.getVariable(options.get(i)).getValue().get(randAlele));
					
			
					solution.getVariable(options.get(i)).getValue().remove(randAlele);
					solution.getVariable(options.get(i)).getValue().add(randAlele, randValue);
					
				}
			}
			
		
		}
		
		return solution;
	}

}
