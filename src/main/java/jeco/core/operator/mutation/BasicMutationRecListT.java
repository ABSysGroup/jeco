package jeco.core.operator.mutation;

import java.util.ArrayList;

import jeco.core.algorithm.sge.AbstractProblemDSGE;
import jeco.core.algorithm.sge.AbstractProblemTreeGE;
import jeco.core.algorithm.sge.RecListT;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;

public class BasicMutationRecListT <T extends RecListT<Integer>> extends MutationOperator<T>   {
	private AbstractProblemTreeGE problem;
	/**Decides whether to keep mutating once the parent node has been mutated, if set to true we can potentially mutate the children
	 * If false, once the parent node has mutated we do not change the children
	 * */
	private boolean keepMut; 
	
	private boolean mutateAllChildren;
							
	private double probType= 0.5;
	/**Constructor for BasicMutationVariableList
	 * @param probability probability that determines whether to perform a mutation or not
	 * @param problem problem that contains all the variables of the solutions
	 */
	public BasicMutationRecListT(double probability, AbstractProblemTreeGE problem, boolean keepMut, boolean mutateAllChildren) {
		super(probability);
		this.problem = problem;
		this.keepMut = keepMut;
		this.mutateAllChildren = mutateAllChildren;
		// TODO Auto-generated constructor stub
	}
	
	/*@Override
	public Solution<T> execute(Solution<T> solution) {

		RecListT<Integer> list = solution.getVariable(0);
		
		executeMutation(list);
		
		return solution;
	}
	
	public void executeMutation(RecListT<Integer> l) {
		boolean mutated = false;
		if (RandomGenerator.nextDouble() <= probability) {
			if(this.problem.getReader().findRule(l.getS()).size() > 1) {
				int randAlele= 0;
				mutated = true;
				do {
					randAlele = RandomGenerator.nextInt(this.problem.getReader().findRule(l.getS()).size());
				}while(randAlele == l.getValue());
				l.setValue(randAlele);
			}
			
			
		}
		
		if(this.keepMut || !mutated) {
		
			
			if(this.mutateAllChildren) {
				for(RecListT<Integer> IntList: l.getInteriorList()) {
					executeMutation(IntList);
				
				}
			}else {
				Production p = problem.getReader().findRule(l.getS()).get(l.getValue());
				
				l.resetIndex();
	
				
				for(Symbol sym: p) {
					RecListT<Integer> next = l.getnextSymbol(sym);
					if(next != null) {
						executeMutation(next);
					}
				}
				
				l.resetIndex();
			}
		}
		
	}*/
	
	@Override
	public Solution<T> execute(Solution<T> solution) {

		RecListT<Integer> list = solution.getVariable(0);
		
		executeMutation(list, true, 0);
		
		return solution;
	}
	
	public void executeMutation(RecListT<Integer> l, boolean mutate, int depth) {
		//Change number
		boolean mutated = false;
		
		boolean randomAction= RandomGenerator.nextBoolean();
		
		if (mutate && (RandomGenerator.nextDouble() <= probability)) {
			if(this.problem.getReader().findRule(l.getS()).size() > 1) {
				int randAlele= 0;
				mutated = true;
				do {
					randAlele = RandomGenerator.nextInt(this.problem.getReader().findRule(l.getS()).size());
				}while(randAlele == l.getValue());
				l.setValue(randAlele);
			}
		
		}
		
		Rule nextRule = this.problem.getReader().findRule(l.getS());
		Production nextProduction = nextRule.get(l.getValue());
		
		if(nextRule.getRecursive() && (depth >= problem.getMaxDepth())) {
		//if((depth >= problem.getMaxDepth())) {
			if(problem.getReader().sameRecursion(nextRule, nextProduction)) {

				problem.transformToTerminalExpansion(l.getS(), l);
				//problem.deleteExtraChildren(l.getS(), l);
				nextProduction = nextRule.get(l.getValue());
				
				if(problem.getReader().sameRecursion(nextRule, nextProduction)) {
					throw new RuntimeException("Error in limiting mutation depth");
				}
			}
		}
		
		if(!mutate) {
			/*Production p = problem.getReader().findRule(l.getS()).get(l.getValue());

			l.resetIndex();
			for(Symbol sym: p) {
				RecListT<Integer> next = l.getnextSymbol(sym);
				if(next != null) {
					executeMutation(next, false, depth+1);

				}else if(!sym.isTerminal()) {
					next = l.getnewEmpty();
					l.addnewSymbol(sym);
					next.setS(sym);
					next.setValue(problem.generateExpansion(sym));
					
					executeMutation(next, false, depth+1);
				}
			}
			
			l.resetIndex();*/
		}else if(this.mutateAllChildren && randomAction) {
			for(RecListT<Integer> IntList: l.getInteriorList()) {
				
				
				if(this.keepMut || !mutated) {
					executeMutation(IntList, mutate, depth+1);
				}else {
					executeMutation(IntList, false, depth+1);
				}
			
			}
		}else {
			
			Production p = problem.getReader().findRule(l.getS()).get(l.getValue());
			l.resetIndex();
			for(Symbol sym: p) {
				RecListT<Integer> next = l.getnextSymbol(sym);
				if(next != null) {
					if(this.keepMut || !mutated) {
						executeMutation(next, mutate, depth+1);
					}else {
						executeMutation(next, false, depth+1);
					}
				}else if(!sym.isTerminal()) {
					/*next = l.getnewEmpty();
					l.addnewSymbol(sym);
					next.setS(sym);
					next.setValue(problem.generateExpansion(sym));
					
					executeMutation(next, false, depth+1);*/
				}
			}
			
			l.resetIndex();
		}
		
	}
}

