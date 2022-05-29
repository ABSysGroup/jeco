package jeco.core.algorithm.sge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeco.core.algorithm.moge.Phenotype;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.bnf.BnfReaderSge;

public abstract class AbstractProblemSGE<T extends Variable<?>> extends AbstractGECommon<T> {
	
	protected String pathToBnf;
	protected ArrayList<Integer> maxDerivations; //Max derivations for each list
	protected BnfReaderSge reader;
	protected ArrayList<String> orderSymbols; //Order of the Rule symbols in the genotype
	protected ArrayList<Integer> terminals; //List of terminal symbols
	protected ArrayList<ArrayList<Integer>> Non_tToTerminals; //List of each non-terminal to other symbols
	
	public AbstractProblemSGE(String pathToBnf, int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
		this.pathToBnf = pathToBnf;
		this.reader = new BnfReaderSge();
		terminals = new ArrayList<Integer>();
		this.Non_tToTerminals = new ArrayList<ArrayList<Integer>>();
		
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Initialization for the algorithm, common to both DSGE and  the StaticSGE version, set the bounds
	 * for each non-terminal, the order of the lists and a map with the next productions for a certain one.
	 * 
	 */
	protected void initialize() {
		
		super.numberOfVariables = reader.number_of_options().size();
		Map<String, Integer> options = reader.number_of_options();
		
		this.orderSymbols = new ArrayList<>();
		
		
		List<String> terminalProductions = reader.getTerminalProductions();
		
		int j = 0;
		for(Map.Entry<String, Integer> entry : options.entrySet()) {
			//Set the order of the lists to be able to reference them later
			this.orderSymbols.add(entry.getKey());
			
			//Set the rules that only produce terminals
			if(terminalProductions.contains(entry.getKey())) {
				this.terminals.add(j);
			}
			
			j++;
		}
		
		//Get the subsequent symbols of a certain rule
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
		//Set upper and lowerbound for the variables of each list
		for (int i = 0; i < numberOfVariables; i++) {
			lowerBound[i] = 0;
			upperBound[i] = options.get(this.orderSymbols.get(i));
		}
	}
	
	public ArrayList<Integer> getIndexesTerminals(){
		return terminals;
	}
	
	public ArrayList<ArrayList<Integer>> getNextProd(){
		return Non_tToTerminals;
	}
	
	protected abstract Solution<T> generateRandomSolution();
	
	@Override
	public Solutions<T> newRandomSetOfSolutions(int size) {
		Solutions<T> solutions = new Solutions<>();
		
		for(int i = 0; i < size; i++) {
			solutions.add(generateRandomSolution());
		}
		
		
		return solutions;
	}
	
	/**
	 * To be implemented by the different problems
	 * @param solution
	 * @param phenotype
	 */
	protected abstract void evaluate(Solution<T> solution, Phenotype phenotype);
	
}
