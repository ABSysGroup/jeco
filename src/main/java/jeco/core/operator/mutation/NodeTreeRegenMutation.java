package jeco.core.operator.mutation;

import jeco.core.algorithm.moge.CFG_GP;
import jeco.core.algorithm.sge.AbstractProblemTreeGE;
import jeco.core.algorithm.sge.NodeTree;
import jeco.core.algorithm.sge.RecListT;
import jeco.core.problem.Solution;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;

public class NodeTreeRegenMutation <T extends NodeTree> extends MutationOperator<T>   {
	private CFG_GP problem;
	private boolean normalize;
	/**Constructor for BasicMutationVariableList
	 * DOES NOT ALLOW CONCURRENCY
	 * @param probability probability that determines whether to perform a mutation or not
	 * @param problem problem that contains all the variables of the solutions
	 * @param normalize whether to consider that each rule type has the same probability (true), or at random from all the nodes of the tree (false)
	 */
	public NodeTreeRegenMutation(double probability, CFG_GP problem, boolean normalize) {
		super(probability);
		this.problem = problem;
		this.normalize = normalize;
		// TODO Auto-generated constructor stub
	}

	@Override
	public Solution<T> execute(Solution<T> solution) {

		if(RandomGenerator.nextDouble() <= probability) {
			NodeTree list = solution.getVariable(0);
			
			if(normalize) {
				Symbol s;
				int rule;
				//First we choose a rule to interchange
				do {
					rule = RandomGenerator.nextInt(problem.getReader().getRules().size());
					s = problem.getReader().getRules().get(rule).getLHS();
		
				}while(!list.containsSymbol(s));
				
				//We choose an occurence of the rule
				int numSym1 = list.countSymbols(s);
				int pos1 = RandomGenerator.nextInt(numSym1);
				
				//We get the occurence by searching through the tree
				current = 0;
				NodeTree sol1 = null;
				
				sol1 = getOcurrence(list, pos1, s, 0);
				
				if(sol1 == null) {
					throw new RuntimeException("Error crossover");
				}
				
				executeMutation(sol1);
			}else {
				
				int numSymbols = list.countNTSymbols();
				int pos1 = RandomGenerator.nextInt(numSymbols);
				
				//We get the occurence by searching through the tree
				current = 0;
				NodeTree sol1 = null;
				sol1 = getOcurrence(list, pos1, 0);
				
				if(sol1 == null) {
					throw new RuntimeException("Error crossover");
				}
				
				executeMutation(sol1);
				
			}
		}
		
		return solution;
	}
	
	public void executeMutation(NodeTree l) {
		
		NodeTree newSubTree = new NodeTree();
		problem.createIndividual(l.getDepth(), l.getDepth(), newSubTree, l.getValue(), false);
		
		if(!l.getValue().equals(newSubTree.getValue())) {
			new RuntimeException("error in mutation");
		}
		
		l.setChildren(newSubTree.getChildren());
		
		
	}
	
	private Integer current;
	
	public NodeTree getOcurrence(NodeTree sol, int pos, Symbol s, int depth){
		NodeTree occurence = null;
		if(sol.getValue().toString().equals(s.toString()) && (pos == current)) {
			sol.setDepth(depth);
			return sol;
			
		}else if(sol.getValue().toString().equals(s.toString())) {
			current++;
		}
		
		for(int i = 0; i< sol.getChildren().size(); i++) {

			occurence = getOcurrence(sol.getChildren().get(i), pos, s, depth+1);
			if(occurence != null) {
				return occurence;
			}
		}
		
		return occurence;
	}
	
	public NodeTree getOcurrence(NodeTree sol, int pos, int depth){
		NodeTree occurence = null;
		if(!sol.getValue().isTerminal() && (pos == current)) {
			sol.setDepth(depth);
			return sol;
			
		}else if(!sol.getValue().isTerminal()) {
			current++;
		}
		
		for(int i = 0; i< sol.getChildren().size(); i++) {

			occurence = getOcurrence(sol.getChildren().get(i), pos, depth+1);
			if(occurence != null) {
				return occurence;
			}
		}
		
		return occurence;
	}
}
