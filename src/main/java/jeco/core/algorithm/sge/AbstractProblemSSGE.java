package jeco.core.algorithm.sge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import jeco.core.algorithm.moge.Phenotype;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.util.bnf.BnfReaderSge;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;

public abstract class AbstractProblemSSGE extends AbstractProblemSGE<VariableArray<Integer>> {

	protected Integer[] indexes;
	protected Map<String, Integer> maxReferencesSymbol;
	protected ArrayList<Integer> maxDerivations;

	
	protected AbstractProblemSSGE(String pathToBnf, int numberOfObjectives, int depth) {
		super(pathToBnf, 0, numberOfObjectives); //I need to read the file before I am able to know the size of the cromosome
		reader.loadSGE(pathToBnf, depth);
		
		initialize();

	}
	
	public void initialize() {
		super.numberOfVariables = reader.number_of_options().size();
		maxReferencesSymbol = reader.find_references_start();
		Map<String, Integer> options = reader.number_of_options();
		
		this.orderSymbols = new ArrayList<>();
		if(options.size() != maxReferencesSymbol.size()) {
			throw new RuntimeException("Wrong loading in bnf file");
		}
		
		List<String> terminalProductions = reader.getTerminalProductions();
		int j = 0;
		for(Map.Entry<String, Integer> entry : maxReferencesSymbol.entrySet()) {
			this.orderSymbols.add(entry.getKey());
			if(terminalProductions.contains(entry.getKey())) {
				this.terminals.add(j);
			}
			
			j++;
		}
		
		Map<String, List<String>> subsequentSymbols = reader.getSubsequentProductions();
		for(int i = 0; i < this.orderSymbols.size(); i++) {
			
			ArrayList<Integer> nextSym = new ArrayList<>();
			this.Non_tToTerminals.add(nextSym);
			for(int k = 0; k < this.orderSymbols.size(); k++) {
				if(subsequentSymbols.get(this.orderSymbols.get(i)).contains(this.orderSymbols.get(k))) {
					nextSym.add(k);
				}
			}
		}
		
        this.lowerBound = new double[numberOfVariables];
        this.upperBound = new double[numberOfVariables];
		
		for (int i = 0; i < numberOfVariables; i++) {
			lowerBound[i] = 0;
			upperBound[i] = options.get(this.orderSymbols.get(i));
		}
	}
	
	private Solution<VariableArray<Integer>> generateRandomSolution() {
        Solution<VariableArray<Integer>> solI = new Solution<>(numberOfObjectives);
        for (int j = 0; j < numberOfVariables; ++j) {
       	 Integer[] list_derivation = new Integer[this.maxReferencesSymbol.get(this.orderSymbols.get(j))];
       	 for(int i = 0; i < list_derivation.length; i++) {
       		 list_derivation[i] = RandomGenerator.nextInteger((int) upperBound[j]);
       	 }
       	 
       	 	VariableArray<Integer> varJ = new VariableArray<>(list_derivation);
            solI.getVariables().add(varJ);
        }
        return solI;
    }
	
	@Override
	public Solutions<VariableArray<Integer>> newRandomSetOfSolutions(int size){
		Solutions<VariableArray<Integer>> solutions = new Solutions<>();
		
		for(int i = 0; i < size; i++) {
			solutions.add(generateRandomSolution());
		}
		
		
		return solutions;
	}
	
	public abstract void evaluate(Solution<VariableArray<Integer>> solution, Phenotype phenotype);
	
	@Override
	public void evaluate(Solutions<VariableArray<Integer>> solutions) {
	        for (Solution<VariableArray<Integer>> solution : solutions) {
	            evaluate(solution, this.generatePhenotype(solution));
	        }
	}
	
	@Override
	public Phenotype generatePhenotype(Solution<VariableArray<Integer>> solution) {

		Phenotype phenotype = new Phenotype();
		Rule firstRule = reader.getRules().get(0);
		int[] index = new int[this.orderSymbols.size()];
		Stack<Symbol> nextRules = new Stack<Symbol>(); 
		nextRules.add(firstRule.getLHS());
		Rule nextRule = null;
		while(!nextRules.empty()) {
			Symbol next = nextRules.pop();
			
			if(next.isTerminal()) {
				phenotype.add(next.toString());
			}
			else {
				nextRule = this.reader.findRule(next);
				Production nextProduction = nextRule.get(solution.getVariables().get(this.orderSymbols.indexOf(nextRule.getLHS().toString())).getValue()[index[this.orderSymbols.indexOf(nextRule.getLHS().toString())]]);
				index[this.orderSymbols.indexOf(next.toString())]++;
				
				for(int i = nextProduction.size()-1 ; i >= 0 ; i--) {
					nextRules.add(nextProduction.get(i));
				}
			}
			
		}
		
  
		return phenotype;
	}
	
	 

}


