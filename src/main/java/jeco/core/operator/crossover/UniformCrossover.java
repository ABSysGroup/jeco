package jeco.core.operator.crossover;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

/**
 *General use UniformCrossover, will interchange the alleles of type T of the
 *individuals if they accept a certain probability
 *
 * @param <T> extends Variable, the type of the individuals genotype elements to perform the crossover.
 */
public class UniformCrossover<T extends Variable<?>> extends CrossoverOperator<T> {

	private double DEFAULT_PROBABILITY;
	private double probability;
	public UniformCrossover(double probability, double probOfChange) {
		this.probability = probability;
		this.DEFAULT_PROBABILITY = probOfChange;
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
				if(RandomGenerator.nextDouble() <= DEFAULT_PROBABILITY) {
					T variable = child1.getVariable(i);
					child1.getVariables().set(i, child2.getVariable(i));
					child2.getVariables().set(i, variable);
					
					
				}
			}
		
		 }
		
		return solutions;
	}

}
