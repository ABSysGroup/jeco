package jeco.core.operator.crossover;

import java.util.ArrayList;
import java.util.List;

import jeco.core.algorithm.moge.CFG_GP;
import jeco.core.algorithm.sge.NodeTree;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;

public class NodeSubTreeCrossover <T extends NodeTree> extends CrossoverOperator<T> {
	
	private CFG_GP problem;
	private double probability;

	
	public NodeSubTreeCrossover(double probability,CFG_GP problem) {
		super();
		this.problem = problem;
		this.probability= probability;

		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Solutions<T> execute(Solution<T> parent1, Solution<T> parent2) {
		Solutions<T> solutions = new Solutions<>();
		
		Solution<T> child1 = parent1.clone();
		Solution<T> child2 = parent2.clone();
		solutions.add(child1);
		solutions.add(child2);
		
		if(RandomGenerator.nextDouble() <= probability) {
			Symbol s = null;
			//First we choose a rule to interchange
			do {
				int rule = RandomGenerator.nextInt(problem.getReader().getRules().size());
				s = problem.getReader().getRules().get(rule).getLHS();

			}while((!child1.getVariable(0).containsSymbol(s) || !child2.getVariable(0).containsSymbol(s)));

			
			//We choose an occurence of the rule
			int numSym1 = child1.getVariable(0).countSymbols(s);
			int pos1 = RandomGenerator.nextInt(numSym1);
			int numSym2 = child2.getVariable(0).countSymbols(s);
			int pos2 = RandomGenerator.nextInt(numSym2);
			
			//We get the occurence by searching through the tree
			current = 0;
			NodeTree sol1 = null;
			
			sol1 = getOcurrence(child1.getVariable(0), pos1, s);
			
			if(sol1 == null) {
				throw new RuntimeException("Error crossover");
			}
			
			current = 0;
			NodeTree sol2 = null;
			
			sol2 = getOcurrence(child2.getVariable(0), pos2, s);

			if(sol2 == null) {
				throw new RuntimeException("Error crossover");
			}
			
			//We interchange them
			if(!sol1.getValue().toString().equals(sol2.getValue().toString())) {
				throw new RuntimeException("Error crossover");
			}
			

			List<NodeTree> list1 = sol2.getChildren();
			
			sol2.setChildren(sol1.getChildren());
				
			sol1.setChildren(list1);
				
		
		}
		
		return solutions;
	}
	
	private Integer current;
	
	public NodeTree getOcurrence(NodeTree sol, int pos, Symbol s){
		NodeTree occurence = null;
		if(sol.getValue().toString().equals(s.toString()) && (pos == current)) {
			return sol;
		}else if(sol.getValue().toString().equals(s.toString())) {
			current++;
		}
		
		for(int i = 0; i< sol.getChildren().size(); i++) {

			occurence = getOcurrence(sol.getChildren().get(i), pos, s);
			if(occurence != null) {
				return occurence;
			}
		}
		
		return occurence;
	}

}
