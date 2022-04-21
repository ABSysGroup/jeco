package jeco.core.operator.crossover;

import java.util.ArrayList;

import jeco.core.algorithm.sge.AbstractProblemSGE;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.random.RandomGenerator;

public class SubTreeCrossover<T extends Variable<?>> extends CrossoverOperator<T> {

	private double DEFAULT_PROBABILITY;
	private double probability;
	AbstractProblemSGE<T> problem; 
	public SubTreeCrossover(AbstractProblemSGE<T> problem, double probability , double probOfChange) {
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
			
			int gene = RandomGenerator.nextInt(problem.getNextProd().size());
			//ArrayList<Integer> nextProd = problem.getNextProd().get(gene);
			ArrayList<Integer> changed = new ArrayList<Integer>();
			
			//Raiz del árbol
			T variableR = child1.getVariable(gene);
			child1.getVariables().set(gene, child2.getVariable(gene));
			child2.getVariables().set(gene, variableR);
			changed.add(gene);
			
			/*for(Integer i: nextProd) {
				if((i != gene) && RandomGenerator.nextDouble() <= DEFAULT_PROBABILITY) {
					T variable = child1.getVariable(i);
					child1.getVariables().set(i, child2.getVariable(i));
					child2.getVariables().set(i, variable);
				}
			}*/
			
			rec_Change(child1, child2,gene,changed);
			
			
		
		 }
		
		return solutions;
	}
	
	void rec_Change(Solution<T> child1, Solution<T> child2, int gene, ArrayList<Integer> changed) {
		ArrayList<Integer> nextProd = problem.getNextProd().get(gene);
		for(Integer i: nextProd) {
			if(!(changed.contains(i)) && (RandomGenerator.nextDouble() <= DEFAULT_PROBABILITY)) {
				T variable = child1.getVariable(i);
				child1.getVariables().set(i, child2.getVariable(i));
				child2.getVariables().set(i, variable);
				changed.add(i);
				rec_Change(child1, child2,i,changed);
			}
		}
	}

}