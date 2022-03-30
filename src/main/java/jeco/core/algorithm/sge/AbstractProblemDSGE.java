package jeco.core.algorithm.sge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import jeco.core.algorithm.moge.Phenotype;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.bnf.BnfReader;
import jeco.core.util.bnf.BnfReaderSge;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;

public abstract class AbstractProblemDSGE extends AbstractProblemSGE<VariableList<Integer>> {

	private int maxDepth;
	private boolean bloatingControl;
	private boolean treeDepth;
	
	public AbstractProblemDSGE(String pathToBnf, int numberOfObjectives, int maxDepth, boolean bloatingControl, boolean treeDepth) {
		super(pathToBnf, 0, numberOfObjectives);
		reader.load(pathToBnf);
		this.bloatingControl = bloatingControl;
		this.treeDepth = treeDepth;
		this.maxDepth = maxDepth;
		initialize();
		// TODO Auto-generated constructor stub
	}
	
	
	public void initialize() {
		
		Map<String, Integer> options = reader.number_of_options();
		this.numberOfVariables = options.size();
		this.orderSymbols = new ArrayList<>();
		
		List<String> terminalProductions = reader.getTerminalProductions();
		int j = 0;
		
		for(Map.Entry<String, Integer> entry : options.entrySet()) {
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
	
	private Solution<VariableList<Integer>> generateRandomSolution() {
        Solution<VariableList<Integer>> solI = new Solution<>(this.numberOfObjectives);
        ArrayList<VariableList<Integer>> temp = new ArrayList<>();
        for(int i = 0; i < this.orderSymbols.size(); i++) {

        	VariableList<Integer> tempVar = new VariableList<Integer>();
        	temp.add(tempVar);
        } 
        
        createIndividual(0, temp, reader.getRules().get(0).getLHS());

        for(VariableList<Integer> var : temp) {
        	solI.getVariables().add(var);

        }
        
        return solI;
	}

	@Override
	protected Phenotype generatePhenotype(Solution<VariableList<Integer>> solution) {
		int depth = 0;
		Phenotype phenotype = new Phenotype();
		Rule firstRule = reader.getRules().get(0);
		int[] index = new int[this.orderSymbols.size()];
		Stack<Symbol> nextRules = new Stack<Symbol>(); 
		nextRules.add(firstRule.getLHS());
		
		auxCreatePhenotype(firstRule.getLHS(), phenotype, 0,  solution,  index);
		
  
		return phenotype;
	}
	
	
	private void auxCreatePhenotype(Symbol next, Phenotype phenotype, int depth, Solution<VariableList<Integer>> solution, int[] index) {
		
		if(next.isTerminal()) {
			phenotype.add(next.toString());
			
		}
		else {
			Rule nextRule = this.reader.findRule(next);
			
			//If the alele we are trying to expand does not exist (can happen due to a mutation or a crossover)
			if(index[this.orderSymbols.indexOf(next.toString())] + 1 > solution.getVariable(this.orderSymbols.indexOf(next.toString())).size()) {
				if(depth >= this.maxDepth) {
					generateTerminalExpansion(next, solution);
				}else {
					generateExpansion(next, solution);
				}
				
				//If the alele we are trying to expand goes over the maximun depth (can happen due to a mutation or a crossover) and it was recursive
				//We transform it into a non-recursive call
			}else if(bloatingControl && nextRule.getRecursive() && (depth >= this.maxDepth)) {
				Production nextProduction = nextRule.get(solution.getVariables().get(this.orderSymbols.indexOf(next.toString())).getValue().get(index[this.orderSymbols.indexOf(next.toString())]));
				if(nextProduction.getRecursive()) {
					transformToTerminalExpansion(next, solution, index[this.orderSymbols.indexOf(next.toString())]);
				}
			}
			
			
			Production nextProduction = nextRule.get(solution.getVariables().get(this.orderSymbols.indexOf(next.toString())).getValue().get(index[this.orderSymbols.indexOf(next.toString())]));
			
			//If we are considering tree depth instead of recursive we always add one 
			if(!treeDepth) {
				if(nextProduction.getRecursive()) {
					depth = depth+1;
				}else {
					depth = 0;
				}	
			}else {
				depth = depth+1;
			}
			
			index[this.orderSymbols.indexOf(next.toString())]++;
			
			for(int i = 0 ; i < nextProduction.size() ; i++) {
				
				/*if(treeDepth) {
					auxCreatePhenotype(nextProduction.get(i),phenotype, depth + 1, solution, index);
				}else {*/
				auxCreatePhenotype(nextProduction.get(i),phenotype, depth, solution, index);
				//}
			}
			
		}
	}
	
	
	private int generateTerminalExpansion(Symbol sym, Solution<VariableList<Integer>> solution) {
		int rand_prod;
		Rule ruleSymbol = this.reader.findRule(sym);
		
		ArrayList<Integer> listProd = new ArrayList<>();
		int index = 0; 
		for(Production p: ruleSymbol) {
			if(!p.getRecursive()) {
				listProd.add(index);
			}
			index++;
		}
		int selec = RandomGenerator.nextInt(listProd.size());
		rand_prod = listProd.get(selec);
		
		solution.getVariable(this.orderSymbols.indexOf(sym.toString())).add(rand_prod);

		return rand_prod;
	}
	
	/**
	 * Transform the production at pos to a non-recursive production to control bloating due to the tree becoming too big through mutation and crossover
	 * @param sym
	 * @param solution
	 * @param pos
	 * @return
	 */
	private int transformToTerminalExpansion(Symbol sym, Solution<VariableList<Integer>> solution, int pos) {
		int rand_prod;
		Rule ruleSymbol = this.reader.findRule(sym);
		
		//We get the productions that are not recursive and put their indexes in a list
		ArrayList<Integer> listProd = new ArrayList<>();
		int index = 0; 
		for(Production p: ruleSymbol) {
			if(!p.getRecursive()) {
				listProd.add(index);
			}
			index++;
		}
		
		//Select one of the indexes of the list
		int selec = RandomGenerator.nextInt(listProd.size());
		rand_prod = listProd.get(selec);
		
		//Remove the previous values that we had on the position and add the new non-recursive value
		solution.getVariable(this.orderSymbols.indexOf(sym.toString())).remove(pos);
		solution.getVariable(this.orderSymbols.indexOf(sym.toString())).add(pos,rand_prod);

		return rand_prod;
	}
	
	private int generateExpansion(Symbol sym, Solution<VariableList<Integer>> solution) {
		Rule ruleSymbol = this.reader.findRule(sym);
		int rand_prod = RandomGenerator.nextInt(ruleSymbol.size());
		
		solution.getVariable(this.orderSymbols.indexOf(sym.toString())).add(rand_prod);
		
		return rand_prod;
	}
	
	/**
	 * Creates a new solution with a certain depth
	 * @param depth
	 * @param solution
	 * @param sym
	 */
	private void createIndividual(int depth, ArrayList<VariableList<Integer>> solution, Symbol sym) {
		int rand_prod = RandomGenerator.nextInt(reader.findRule(sym).size());
		Rule ruleSymbol = reader.findRule(sym);
		Production expansion = ruleSymbol.get(rand_prod);
		
		//If we are considering tree depth instead of recursive we always add one 
		if(!treeDepth) {
			if(expansion.getRecursive()) {
				depth = depth+1;
			}else {
				depth = 0;
			}	
		}else {
			depth = depth+1;
		}
		
		if(ruleSymbol.getRecursive()) {
			if(expansion.getRecursive()) {
				if(depth >= this.maxDepth) {
					ArrayList<Integer> listProd = new ArrayList<>();
					int index = 0; 
					for(Production p: ruleSymbol) {
						if(!p.getRecursive()) {
							listProd.add(index);
						}
						index++;
					}
					int selec = RandomGenerator.nextInt(listProd.size());
					rand_prod = listProd.get(selec);
					expansion = ruleSymbol.get(rand_prod);
					
				}
			}
			
		}

		solution.get(this.orderSymbols.indexOf(sym.toString())).add(rand_prod);
		
		for(Symbol nextSym: expansion) {
			if(!nextSym.isTerminal()) {
				//createIndividual(depth +1, solution, nextSym);
				createIndividual(depth, solution, nextSym);
			}
			
		}
		
		
		
	}

	@Override
	public Solutions<VariableList<Integer>> newRandomSetOfSolutions(int size) {
		Solutions<VariableList<Integer>> solutions = new Solutions<>();
		
		for(int i = 0; i < size; i++) {
			solutions.add(generateRandomSolution());
		}
		
		
		return solutions;
	}
	
	@Override
	public void evaluate(Solutions<VariableList<Integer>> solutions) {
	        for (Solution<VariableList<Integer>> solution : solutions) {
	            evaluate(solution, this.generatePhenotype(solution));
	        }
	}

	public abstract void evaluate(Solution<VariableList<Integer>> solution, Phenotype phenotype);


}
