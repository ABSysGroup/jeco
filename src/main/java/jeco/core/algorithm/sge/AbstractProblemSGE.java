package jeco.core.algorithm.sge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jeco.core.problem.Variable;
import jeco.core.util.bnf.BnfReaderSge;

/**
 * Class with the common methods of the Static SGE and Dynamic SGE
 *
 * @param <T> extends Variable, the type of the individuals genotype elements.
 */
public abstract class AbstractProblemSGE<T extends Variable<?>> extends AbstractGECommon<T> {
	
	/**Max derivations for each list */
	protected ArrayList<Integer> maxDerivations;
	/**Reader that parses the grammar into rules, productions and symbols*/
	protected BnfReaderSge reader;
	/**Order of the Rule symbols in the genotype */
	protected ArrayList<String> orderSymbols; 
	/**List of terminal symbols */
	protected ArrayList<Integer> terminals;
	/**List of each non-terminal to other symbols */
	protected ArrayList<ArrayList<Integer>> Non_tToTerminals; 
	
	/**Constructor for Structured Grammatical Evolution.
	 * 
	 * @param pathToBnf  Path to bnf file with the grammar to be used
	 * @param numberOfVariables number of variables
	 * @param numberOfObjectives number of objectives
	 */
	protected AbstractProblemSGE(String pathToBnf, int numberOfVariables, int numberOfObjectives) {
		super(pathToBnf,numberOfVariables, numberOfObjectives);
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
	
	/**Returns the indexes of the terminals from the grammar.
	 * 
	 * @return ArrayList of indexes
	 */
	public ArrayList<Integer> getIndexesTerminals(){
		return terminals;
	}
	
	/**List of lists from non terminals to terminals 
	 * 
	 * @return Non_tToTerminals
	 */
	public ArrayList<ArrayList<Integer>> getNextProd(){
		return Non_tToTerminals;
	}
	
	
}
