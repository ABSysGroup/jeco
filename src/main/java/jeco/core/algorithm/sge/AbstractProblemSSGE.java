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

	//String of non-terminal to Integer of number of derivations
	protected Map<String, Integer> maxReferencesSymbol;
	
	protected AbstractProblemSSGE(String pathToBnf, int numberOfObjectives, int depth) {
		super(pathToBnf, 0, numberOfObjectives); //I need to read the file before I am able to know the size of the cromosome
		reader.loadSGE(pathToBnf, depth);
		
		maxReferencesSymbol = reader.find_references_start();
		Map<String, Integer> options = reader.number_of_options();
		
		if(options.size() != maxReferencesSymbol.size()) {
			throw new RuntimeException("Wrong loading in bnf file");
		}
		
		initialize();
		

	}
	
	/**
	 * Random solution implemented by original SGE (StaticSGE) implementation
	 */
	protected Solution<VariableArray<Integer>> generateRandomSolution() {
        Solution<VariableArray<Integer>> solI = new Solution<>(numberOfObjectives);
        //For each variable
        for (int j = 0; j < numberOfVariables; ++j) {
        	//Generate the non-terminal list
			Integer[] list_derivation = new Integer[this.maxReferencesSymbol.get(this.orderSymbols.get(j))];
			
			//Generate the alleles
			for(int i = 0; i < list_derivation.length; i++) {
				list_derivation[i] = RandomGenerator.nextInteger((int) lowerBound[j], (int) upperBound[j]);
			}
			 
			VariableArray<Integer> varJ = new VariableArray<>(list_derivation);
			solI.getVariables().add(varJ);
        }
        return solI;
    }
	
	
	@Override
	public void evaluate(Solutions<VariableArray<Integer>> solutions) {
	        for (Solution<VariableArray<Integer>> solution : solutions) {
	            evaluate(solution, this.generatePhenotype(solution));
	        }
	}
	
	/**
	 * Generates a phenotype from a list of VariableArray
	 */
	@Override
	public Phenotype generatePhenotype(Solution<VariableArray<Integer>> solution) {

		Phenotype phenotype = new Phenotype();
		Rule firstRule = reader.getRules().get(0);
		int[] index = new int[this.orderSymbols.size()];

		auxCreatePhenotype(firstRule.getLHS(), phenotype, solution, index);
		
		return phenotype;
	}
	
	private void auxCreatePhenotype(Symbol next, Phenotype phenotype, Solution<VariableArray<Integer>> solution, int[] index) {
		
		if(next.isTerminal()) {
			phenotype.add(next.toString());
			
		}
		else {
			Rule nextRule = this.reader.findRule(next);
			
			Production nextProduction = nextRule.get(solution.getVariables().get(this.orderSymbols.indexOf(next.toString())).getValue()[index[this.orderSymbols.indexOf(next.toString())]]);
			
			index[this.orderSymbols.indexOf(next.toString())]++;
			
			for(int i = 0 ; i < nextProduction.size() ; i++) {

				auxCreatePhenotype(nextProduction.get(i),phenotype, solution, index);
				
			}
			
		}
	}
	
	 

}


