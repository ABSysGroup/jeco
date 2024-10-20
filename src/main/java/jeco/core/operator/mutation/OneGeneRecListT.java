package jeco.core.operator.mutation;

import java.util.Objects;

import jeco.core.algorithm.sge.AbstractProblemTreeGE;
import jeco.core.algorithm.sge.RecListT;
import jeco.core.problem.Solution;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;

public class OneGeneRecListT <T extends RecListT<Integer>> extends MutationOperator<T>{

	private AbstractProblemTreeGE problem;
	private boolean mutateAllChildren;						
	private double probability;
	
	
	/**Constructor for OneGeneRecListT
	 * @param probability probability that determines whether to perform a mutation over all nodes or only codifying nodes. Mutation always happens
	 * @param problem problem that contains all the variables of the solutions
	 * @param mutateAllChildren if set to true mutation afects always all the nodes
	 */
	public OneGeneRecListT(double probability, AbstractProblemTreeGE problem, boolean mutateAllChildren) {
		super(probability);
		this.problem = problem;
		this.mutateAllChildren = mutateAllChildren;
		this.probability = probability;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Solution<T> execute(Solution<T> solution) {

		RecListT<Integer> list = solution.getVariable(0);
		
		executeMutation(list);
		
		return solution;
	}
	
	/*public void executeMutation(RecListT<Integer> l) {
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
	private Integer current;
	
	public void executeMutation(RecListT<Integer> l) {
		current = 0;
		int pos = 0;
		RecListT<Integer> occ = null;
		
		//boolean used = RandomGenerator.nextBoolean();
		double mutateProb = RandomGenerator.nextDouble();
		if((mutateProb > this.probability) || this.mutateAllChildren) {
			pos=RandomGenerator.nextInt(l.countSymbols(null));
			
			occ = getOcurrence(l,pos);
		}else {
			pos=RandomGenerator.nextInt(l.countUsedSymbols(problem.getReader(), null));
		
			occ = getUsedOcurrence(l,pos);
		}
		
		

		while(this.problem.getReader().findRule(occ.getS()).size() <= 1) {
			current = 0;
			if((mutateProb > this.probability) || this.mutateAllChildren) {
				pos=RandomGenerator.nextInt(l.countSymbols(null));
				occ = getOcurrence(l,pos);
			}else {
				pos=RandomGenerator.nextInt(l.countUsedSymbols(problem.getReader(), null));
				occ = getUsedOcurrence(l,pos);
			}
			
		}
		
		int randAlele= 0;
		do {
			randAlele = RandomGenerator.nextInt(this.problem.getReader().findRule(occ.getS()).size());
		}while(randAlele == occ.getValue());
		occ.setValue(randAlele);
		
		
	}
	
	
	public RecListT<Integer> getUsedOcurrence(RecListT<Integer> sol, int pos){
		RecListT<Integer> occurence = null;
		if(pos == current) {
			return sol;
		}else {
			current++;
		}
		
		//Symbol and rule asociated
		Symbol sRule = sol.getS();
		Rule r = this.problem.getReader().findRule(sRule);
		Production p = r.get(sol.getValue());
		
		sol.resetIndex();
		for(Symbol sym: p) {
			
			RecListT<Integer> next = sol.getnextSymbol(sym);
			if(next != null) {
				occurence = getUsedOcurrence(next, pos);
				if(occurence != null) {
					sol.resetIndex();
					
					return occurence;
				}
			}
		}
		sol.resetIndex();
		
		return occurence;
	}
	
	
	public RecListT<Integer> getOcurrence(RecListT<Integer> sol, int pos){
		RecListT<Integer> occurence = null;
		if(pos == current) {
			return sol;
		}else {
			current++;
		}
		
		for(int i = 0; i< sol.getInteriorList().size(); i++) {

			occurence = getOcurrence(sol.getInteriorList().get(i), pos);
			if(occurence != null) {
				return occurence;
			}
		}
		
		return occurence;
	}
	
}

