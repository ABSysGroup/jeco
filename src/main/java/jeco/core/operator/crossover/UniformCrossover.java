package jeco.core.operator.crossover;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class UniformCrossover<T extends Variable<Integer[]>> extends CrossoverOperator<T> {

	public static final double DEFAULT_PROBABILITY = 0.4;
	private double probability;
	public UniformCrossover(double probability) {
		this.probability = probability;
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
					T variable_1 = child1.getVariable(i);
					child1.getVariables().set(i, child2.getVariable(i));
					child2.getVariables().set(i, variable_1);
					
					
				}
			}
		
		 }
		
		return solutions;
	}

}
