package jeco.core.operator.crossover;

import jeco.core.algorithm.sge.AbstractProblemSGE;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 * Uniform Crosssover for AbstractProblemSGE<T> that interchanges only the non-terminal lists
 * that contain terminal symbols. Functions the same as Uniform Crossover
 *
 * @param <T>
 */
public class UniformTerminalCrossover<T extends Variable<?>> extends CrossoverOperator<T> {

	private double DEFAULT_PROBABILITY;
	private double probability;
	AbstractProblemSGE<T> problem; 
	public UniformTerminalCrossover(AbstractProblemSGE<T> problem, double probability, double probOfChange) {
		this.probability = probability;
		this.DEFAULT_PROBABILITY = probOfChange;
		this.problem = problem;
	}
	
	@Override
	public Solutions<T> execute(Solution<T> parent1, Solution<T> parent2) {
		Solutions<T> solutions = new Solutions<>();
		
		Solution<T> child1 = parent1.clone();
		Solution<T> child2 = parent2.clone();
		solutions.add(child1);
		solutions.add(child2);
		
		if (RandomGenerator.nextDouble() <= probability) {
			int size = Math.min(parent1.getVariables().size(), parent2.getVariables().size());
			for(int i = 0; i< size; i++) {
				if((problem.getIndexesTerminals().contains((Integer) i)) && RandomGenerator.nextDouble() <= DEFAULT_PROBABILITY) {
					T variable = child1.getVariable(i);
					child1.getVariables().set(i, child2.getVariable(i));
					child2.getVariables().set(i, variable);
					
					
				}
			}
		
		 }
		
		return solutions;
	}

}