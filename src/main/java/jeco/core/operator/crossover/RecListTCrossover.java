package jeco.core.operator.crossover;

import java.util.ArrayList;
import java.util.List;

import jeco.core.algorithm.sge.AbstractProblemTreeGE;
import jeco.core.algorithm.sge.RecListT;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.Pair;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;

public class RecListTCrossover <T extends RecListT<Integer>> extends CrossoverOperator<T> {

	
	private AbstractProblemTreeGE problem;
	private double probability;
	private boolean crossAllList;
	private boolean crossOnlyUsedGenes;
	private boolean duplicate;
	
	public RecListTCrossover(double probability,AbstractProblemTreeGE problem, boolean crossAllList, boolean crossOnlyUsedGenes, boolean duplicate) {
		super();
		this.problem = problem;
		this.probability= probability;
		this.crossAllList = crossAllList;
		this.crossOnlyUsedGenes = crossOnlyUsedGenes;
		this.duplicate = duplicate;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public Solutions<T> execute(Solution<T> parent1, Solution<T> parent2) {
		Solutions<T> solutions = new Solutions<>();
		
		Solution<T> child1 = parent1.clone();
		Solution<T> child2 = parent2.clone();
		solutions.add(child1);
		solutions.add(child2);
		
		boolean used1 = false;
		boolean used2 = false;
		used2 = RandomGenerator.nextBoolean();
		used1 = RandomGenerator.nextBoolean();
		

		if(RandomGenerator.nextDouble() <= probability) {
			Symbol s = null;
			
			if(crossOnlyUsedGenes || (used2 && used1) /*|| (!used2 && !used1)*/) {
				//First we choose a rule to interchange
				do {
					int rule = RandomGenerator.nextInt(problem.getReader().getRules().size());
					s = problem.getReader().getRules().get(rule).getLHS();
	
				}while((!child1.getVariable(0).containsUsedSymbol(problem.getReader(), s) || !child2.getVariable(0).containsUsedSymbol(problem.getReader(), s)));
				
			}else if(!used2 && used1){
				
				do {
					int rule = RandomGenerator.nextInt(problem.getReader().getRules().size());
					s = problem.getReader().getRules().get(rule).getLHS();
	
				}while((!child1.getVariable(0).containsUsedSymbol(problem.getReader(), s)  || !child2.getVariable(0).containsSymbol(s)));
				
			}else if(used2 && !used1){
				
				do {
					int rule = RandomGenerator.nextInt(problem.getReader().getRules().size());
					s = problem.getReader().getRules().get(rule).getLHS();
	
				}while((!child1.getVariable(0).containsSymbol(s)  || !child2.getVariable(0).containsUsedSymbol(problem.getReader(), s)));
				
			}else {
				
				//First we choose a rule to interchange
				
				do {
					int rule = RandomGenerator.nextInt(problem.getReader().getRules().size());
					s = problem.getReader().getRules().get(rule).getLHS();
	
				}while((!child1.getVariable(0).containsSymbol(s) || !child2.getVariable(0).containsSymbol(s)));
			}

			int numSym1;
			int pos1;
			int numSym2;
			int pos2;
			
			if(crossOnlyUsedGenes || (used2 && used1)) {
				
				//We choose an occurence of the rule
				numSym1 = child1.getVariable(0).countUsedSymbols(problem.getReader(), s);
				numSym2 = child2.getVariable(0).countUsedSymbols(problem.getReader(), s);
				
			}else if (!used2 && used1) {
				numSym1 = child1.getVariable(0).countUsedSymbols(problem.getReader(), s);
				numSym2 = child2.getVariable(0).countSymbols(s);	
				
			}else if (used2 && !used1) {
				numSym1 = child1.getVariable(0).countSymbols(s);
				numSym2 = child2.getVariable(0).countUsedSymbols(problem.getReader(), s);
				
			}else {
				
				//We choose an occurence of the rule
				numSym1 = child1.getVariable(0).countSymbols(s);
				numSym2 = child2.getVariable(0).countSymbols(s);		
			}
			
			pos1 = RandomGenerator.nextInt(numSym1);
			pos2 = RandomGenerator.nextInt(numSym2);
			
			//We get the occurence by searching through the tree
			current = 0;
			RecListT<Integer> sol1 = null;
			
			if(crossOnlyUsedGenes || used1) {
				sol1 = getUsedOcurrence(child1.getVariable(0), pos1, s);
			}else {
				sol1 = getOcurrence(child1.getVariable(0), pos1, s);
			}
			
			if(sol1 == null) {
				throw new RuntimeException("Error crossover");
			}
			
			current = 0;
			RecListT<Integer> sol2 = null;
			
			if(crossOnlyUsedGenes || used2) {
				sol2 = getUsedOcurrence(child2.getVariable(0), pos2, s);
			}else {
				sol2 = getOcurrence(child2.getVariable(0), pos2, s);
			}
			
			if(sol2 == null) {
				throw new RuntimeException("Error crossover");
			}
			
			//We interchange them
			if(!sol1.getS().toString().equals(sol2.getS().toString())) {
				throw new RuntimeException("Error crossover");
			}
			
			//We change the internal children lists
			if(this.crossAllList) {
				List<RecListT<Integer>> list1 = sol2.getInteriorList();
				Integer value = sol2.getValue();
				
				sol2.setValue(sol1.getValue());
				sol2.setInteriorList(sol1.getInteriorList());
				
				sol1.setValue(value);
				sol1.setInteriorList(list1);
				
				sol2.resetIndex();
				sol1.resetIndex();
				
			}else { //We only change the used elements and leave the rest
				//Reset index to get the results in order
				sol2.resetIndex();
				sol1.resetIndex();
				
				Integer value = sol2.getValue();
				Symbol symbol = sol2.getS();
				Rule r = problem.getReader().findRule(symbol);
				//We are going to get the used symbols of sol2 for a given production and add it to the newList1
				List<RecListT<Integer>> newlist1 = new ArrayList<RecListT<Integer>>();
				Production p = r.get(value);
				for(Symbol sp : p) {
					RecListT<Integer> nextUsed = sol2.getnextSymbol(sp);
					if(nextUsed != null) {
						newlist1.add(nextUsed);
					}
				}
				
				//We do the same thing for sol1
				value = sol1.getValue();
				List<RecListT<Integer>> newlist2 = new ArrayList<RecListT<Integer>>();
				p = r.get(value);
				for(Symbol sp : p) {
					RecListT<Integer> nextUsed = sol1.getnextSymbol(sp);
					if(nextUsed != null) {
						newlist2.add(nextUsed);
					}
				}
				
				
				//We replace the instance of newList1 in sol1 and delete the leftover elements from newList2
				sol1.replaceList(newlist1);
				value = sol1.getValue();
				sol1.setValue(sol2.getValue());
				if(!duplicate) {
					sol1.deleteFromList(newlist2);
				}else {
					sol1.duplicateInList(newlist2);
				}
				
				//We replace the instances of newList2 in sol1 and delete the leftover elements from newList1
				sol2.replaceList(newlist2);
				sol2.setValue(value);
				if(!duplicate) {
					sol2.deleteFromList(newlist1);
				}else {
					sol2.duplicateInList(newlist1);
				}
				
				//Reset the indexes
				sol1.resetIndex();
				sol2.resetIndex();
				
				
			}
		
		}
		
		return solutions;
	}
	
	private Integer current;
	
	
	public RecListT<Integer> getUsedOcurrence(RecListT<Integer> sol, int pos, Symbol s){
		RecListT<Integer> occurence = null;
		if(sol.getS().toString().equals(s.toString()) && (pos == current)) {
			return sol;
		}else if(sol.getS().toString().equals(s.toString())) {
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
				occurence = getUsedOcurrence(next, pos, s);
				if(occurence != null) {
					sol.resetIndex();
					
					return occurence;
				}
			}
		}
		sol.resetIndex();
		
		return occurence;
	}
	
	
	public RecListT<Integer> getOcurrence(RecListT<Integer> sol, int pos, Symbol s){
		RecListT<Integer> occurence = null;
		if(sol.getS().toString().equals(s.toString()) && (pos == current)) {
			return sol;
		}else if(sol.getS().toString().equals(s.toString())) {
			current++;
		}
		
		for(int i = 0; i< sol.getInteriorList().size(); i++) {

			occurence = getOcurrence(sol.getInteriorList().get(i), pos, s);
			if(occurence != null) {
				return occurence;
			}
		}
		
		return occurence;
	}
	

}
