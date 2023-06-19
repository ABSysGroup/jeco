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

/**
 * Abstract problem for Dynamic Structured Grammatical Evolution, the Evaluate method must be implemented for each problem.
 *
 */
public abstract class AbstractProblemDSGE extends AbstractProblemSGE<VariableList<Integer>> {
	
	//Max depth an individual can be
	private int maxDepth;
	private int maxDepthInit;
	private int minRecDepthInit;
	private boolean bloatingControl;
	private boolean treeDepth;
	
	/**
	 * Constructor without initialMaxdepth, set to the maxDepth, nor initialMinRecDepth which is set to 0
	 * @param pathToBnf path of bnf file with grammar
	 * @param numberOfObjectives of the problem chosen
	 * @param maxDepth maximum depth of the solution tree constructed or amount of times each rule can perform recursion.
	 * @param bloatingControl boolean that determines whether to limit the depth of the trees in the solution or not during the evolution.
	 * @param treeDepth boolean that determines if the maxDepth refers to the maximun depth of the tress or the maximum depth of each recursion.
	 */
	public AbstractProblemDSGE(String pathToBnf, int numberOfObjectives, int maxDepth, boolean bloatingControl, boolean treeDepth) {
		super(pathToBnf, 0, numberOfObjectives);
		reader.load(pathToBnf);
		this.bloatingControl = bloatingControl;
		this.treeDepth = treeDepth;
		this.maxDepth = maxDepth;
		this.minRecDepthInit = 0;
		this.maxDepthInit = maxDepth;
		initialize();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Constructor with all parameters including maxInitDepth and minRecInitDepth
	 * @param pathToBnf path of bnf file with grammar
	 * @param numberOfObjectives of the problem chosen
	 * @param maxDepth maximum depth of the solution tree constructed or amount of times each rule can perform recursion.
	 * @param bloatingControl  boolean that determines whether to limit the depth of the trees in the solution or not during the evolution.
	 * @param treeDepth boolean that determines if the maxDepth refers to the maximun depth of the tress or the maximum depth of each recursion.
	 * @param maxInit maximum depth of the initial solution tree constructed or amount of times each rule can perform recursion in the creation of solutions.
	 * @param minRecInit minimum recursive depth for all rules that all initial solutions must have.
	 */
	public AbstractProblemDSGE(String pathToBnf, int numberOfObjectives, int maxDepth, boolean bloatingControl, boolean treeDepth, int maxInit, int minRecInit) {
		super(pathToBnf, 0, numberOfObjectives);
		reader.load(pathToBnf);
		this.bloatingControl = bloatingControl;
		this.treeDepth = treeDepth;
		this.maxDepth = maxDepth;
		this.minRecDepthInit = minRecInit;
		this.maxDepthInit = maxInit;
		initialize();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Generates a random Individual of type VariableList of Integers
	 */
	protected Solution<VariableList<Integer>> generateRandomSolution() {
        Solution<VariableList<Integer>> solI = new Solution<>(this.numberOfObjectives);
        ArrayList<VariableList<Integer>> temp = new ArrayList<>();
        
        //Add a VariableList for each non-terminal in accordance to the order of the rules
        for(int i = 0; i < this.orderSymbols.size(); i++) {
        	
        	VariableList<Integer> tempVar = new VariableList<Integer>();
        	temp.add(tempVar);
        } 
        
        //Create the individual with initial depth 0
        createIndividual(0,0, temp, reader.getRules().get(0).getLHS(), true);

        for(VariableList<Integer> var : temp) {
        	solI.getVariables().add(var);

        }
       
        
        return solI;
	}

	/**
	 * Generates a phenotype from a list of VariableList
	 */
	@Override
	protected Phenotype generatePhenotype(Solution<VariableList<Integer>> solution) {
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
			
			//If the alele we are trying to expand does not exist (can happen due to a mutation or a crossover) we generate a new alele
			if(index[this.orderSymbols.indexOf(next.toString())] >= solution.getVariable(this.orderSymbols.indexOf(next.toString())).size()) {
				if(depth >= this.maxDepth) {
					generateTerminalExpansion(next, solution);
				}else {
					generateExpansion(next, solution);
				}
				
				//If the alele we are trying to expand goes over the maximum depth (can happen due to a mutation or a crossover) and it was recursive
				//We transform it into a non-recursive call
			}else if(bloatingControl && nextRule.getRecursive() && (depth >= this.maxDepth)) {
				Production nextProduction = nextRule.get(solution.getVariables().get(this.orderSymbols.indexOf(next.toString())).getValue().get(index[this.orderSymbols.indexOf(next.toString())]));
				if(reader.sameRecursion(nextRule, nextProduction)) {
					transformToTerminalExpansion(next, solution, index[this.orderSymbols.indexOf(next.toString())]);
				}
			}
			
			
			Production nextProduction = nextRule.get(solution.getVariables().get(this.orderSymbols.indexOf(next.toString())).getValue().get(index[this.orderSymbols.indexOf(next.toString())]));
			
			index[this.orderSymbols.indexOf(next.toString())]++;
			
			for(int i = 0 ; i < nextProduction.size() ; i++) {
				
				if(!treeDepth) {
					if(reader.sameRecursion(nextRule, nextProduction.get(i))) {
						//The next symbol has the same recursion as this rule therefore we add depth+1
						auxCreatePhenotype(nextProduction.get(i),phenotype, depth+1, solution, index);
					}else {
						//The next symbol is not recursive with the current rule therefore we reset the depth to 0
						auxCreatePhenotype(nextProduction.get(i),phenotype, 0, solution, index);
					}
				}else {
					//If we consider tree depth we always add one 
					auxCreatePhenotype(nextProduction.get(i),phenotype, depth+1, solution, index);
				}
				
			}
			
		}
	}
	
	/**
	 * Add a non-recursive production (in relation to the rule) to the individual Solution in the list determined by sym
	 * @param sym
	 * @param solution
	 * @return
	 */
	private void generateTerminalExpansion(Symbol sym, Solution<VariableList<Integer>> solution) {
		int rand_prod;
		Rule ruleSymbol = this.reader.findRule(sym);
		
		rand_prod = TerminalExpansion(ruleSymbol);
		
		solution.getVariable(this.orderSymbols.indexOf(sym.toString())).add(rand_prod);

	}
	
	private int TerminalExpansion(Rule ruleSymbol) {

		int rand_prod;
		
		//We get the productions that are not recursive with the ruleSymbol and put their indexes in a list
		ArrayList<Integer> listProd = new ArrayList<>();
		int index = 0;
		int min = Integer.MAX_VALUE;
		
		//We search for the minimun depth possible
		for(Production p: ruleSymbol) {
			if(p.getMinimumDepth() < min) {
				min = p.getMinimumDepth();
			}
		}
		
		for(Production p: ruleSymbol) {
			/*if(!reader.sameRecursion(ruleSymbol, p)) {
				listProd.add(index);
			}
			index++;*/
			
			//Productions that have the minimun depth to reach a terminal
			if(p.getMinimumDepth() == min) {
				listProd.add(index);
			}
			index++;
		}
		
		//Select one of the indexes of the list
		int selec = RandomGenerator.nextInt(listProd.size());
		rand_prod = listProd.get(selec);
		
		return rand_prod;
	}
	
	/*private int RecursiveExpansion(Rule ruleSymbol) {
		int rand_prod;
		
		//We get the productions that are recursive with the ruleSymbol and put their indexes in a list
		ArrayList<Integer> listProd = new ArrayList<>();
		int index = 0; 
		for(Production p: ruleSymbol) {
			if(reader.sameRecursion(ruleSymbol, p)) {
				listProd.add(index);
			}
			index++;
		}
		
		//Select one of the indexes of the list
		int selec = RandomGenerator.nextInt(listProd.size());
		rand_prod = listProd.get(selec);
		
		return rand_prod;
	}*/
	
	/**
	 * Transform the production at pos to a non-recursive production to control bloating due to the tree becoming too big through mutation and crossover
	 * @param sym
	 * @param solution
	 * @param pos
	 * @return
	 */
	private void transformToTerminalExpansion(Symbol sym, Solution<VariableList<Integer>> solution, int pos) {
		int rand_prod;
		Rule ruleSymbol = this.reader.findRule(sym);
		
		rand_prod = TerminalExpansion(ruleSymbol);
		
		//Remove the previous values that we had on the position and add the new non-recursive value
		solution.getVariable(this.orderSymbols.indexOf(sym.toString())).remove(pos);
		solution.getVariable(this.orderSymbols.indexOf(sym.toString())).add(pos,rand_prod);


	}
	
	
	/**
	 * Adds a random expansion to the list of the rule identified by sym to the solution
	 * @param sym
	 * @param solution
	 * @return
	 */
	private void generateExpansion(Symbol sym, Solution<VariableList<Integer>> solution) {
		Rule ruleSymbol = this.reader.findRule(sym);
		int rand_prod = RandomGenerator.nextInt(ruleSymbol.size());
		
		solution.getVariable(this.orderSymbols.indexOf(sym.toString())).add(rand_prod);
		
	}
	
	
	/**
	 * Adds a random expansion to the list of the rule identified by sym to the solution
	 * @param sym
	 * @param solution
	 * @return
	 */
	private int generateExpansionToMinimumDepth(Rule ruleSymbol, int expansion, int depth_to_expand) {
		int rand_prod;
		
		//If the expansion has enough minimum depth we just return that expansion
		if(ruleSymbol.get(expansion).getMaximumDepth() >= depth_to_expand) {
			return expansion;
		}
		
		ArrayList<Integer> listProd = new ArrayList<>();
		
		/*int max = 0;
		//We search for the maximum minimum depth possible
		for(Production p: ruleSymbol) {
			if(p.getMinimumDepth() > max) {
				max = p.getMinimumDepth();
			}
		}
		
		//If some rule has the minimum depth over the minimum initial depth_to_expand we choose one 
		if(max >= depth_to_expand) {
			
			
			int index = 0;
			for(Production p: ruleSymbol) {*/
				/*if(!reader.sameRecursion(ruleSymbol, p)) {
					listProd.add(index);
				}
				index++;*/
				
				//Productions that have the minimun depth to reach a terminal
				/*if(p.getMinimumDepth() >= depth_to_expand) {
					listProd.add(index);
				}
				index++;
			}
			
			//Select one of the indexes of the list randomly
			int selec = RandomGenerator.nextInt(listProd.size());
			rand_prod = listProd.get(selec);
		}
		else { //NO rules minimum depth is over the minimum initial depth, therefore we look at the maximum depths (which will probably mean we have to enter recursion)
			*/
			int index = 0;
			for(Production p: ruleSymbol) {
				
				//Productions that have the maximum depth over the depth to expand
				if(p.getMaximumDepth() >= depth_to_expand) {
					listProd.add(index);
				}
				index++;
			}
			
			//Select one of the indexes of the list randomly
			int selec = RandomGenerator.nextInt(listProd.size());
			rand_prod = listProd.get(selec);
			
			
		//}
		
		return rand_prod;
		
	}
	
	/**
	 * Creates a new solution with a certain initial depth
	 * @param depth
	 * @param initDepth
	 * @param solution
	 * @param sym
	 */
	private void createIndividual(int depth, int innitDepth, ArrayList<VariableList<Integer>> solution, Symbol sym, boolean MinInitD) {
		Rule ruleSymbol = reader.findRule(sym);
		int rand_prod = RandomGenerator.nextInt(ruleSymbol.size());
		
		Production expansion = ruleSymbol.get(rand_prod);
		
		//If the rule and expansion is recursive and we have gone over the maxDepth we only generate non_recursive expansions 
		if(reader.sameRecursion(ruleSymbol, expansion)) {
			if(depth >= this.maxDepthInit) {
				rand_prod = TerminalExpansion(ruleSymbol);
				expansion = ruleSymbol.get(rand_prod);
				
			}
			
		}/*else {
			//If the rule is recursive and we are expanding a non-recursive production but the minRecDepthInit is less than the minimum then we generate only recursive expansions
			//We only generate the new recursive rule of we have not gone over the max depth limit
			if((innitDepth < this.minRecDepthInit) && (depth < this.maxDepthInit) && MinInitD) {
				//rand_prod = RecursiveExpansion(ruleSymbol);
				rand_prod = generateExpansionToMinimumDepth(ruleSymbol);
				expansion = ruleSymbol.get(rand_prod);
			
			}
		}*/
		
		if((innitDepth < this.minRecDepthInit) && (depth < this.maxDepthInit) && MinInitD) {
			//rand_prod = RecursiveExpansion(ruleSymbol);
			rand_prod = generateExpansionToMinimumDepth(ruleSymbol, rand_prod, (minRecDepthInit - innitDepth));
			expansion = ruleSymbol.get(rand_prod);
		
		}
		
		solution.get(this.orderSymbols.indexOf(sym.toString())).add(rand_prod);
		
		int symToExpandMinInnit = getBranchForMinInit(expansion, MinInitD, innitDepth);
		
		int index = 0;
		for(Symbol nextSym: expansion) {
			if(!nextSym.isTerminal()) { //If the next symbol not a terminal we continue to generate the individual
				
				//Para la rama elegida hacemos que continue creando el camino más largo
				boolean nextInitDepth = false;
				if(index == symToExpandMinInnit) {
					nextInitDepth = true;
				}
				
				boolean sameRecursion = reader.sameRecursion(ruleSymbol, nextSym); 

				if(!treeDepth) {
					if(sameRecursion) {
						//The next symbol has the same recursion as this rule therefore we add depth+1 and innitDepth+1
						createIndividual(depth+1,innitDepth+1, solution, nextSym,nextInitDepth);
					}else {
						//The next symbol is not recursive with the current rule therefore we reset the depth to 0
						createIndividual(0, innitDepth+1, solution, nextSym,nextInitDepth);
					}
				}else {
					
					//The next symbol has the same recursion as this symbol so we can add one to the initial Rec depth
					createIndividual(depth+1, innitDepth+1, solution, nextSym,nextInitDepth);
					
				}

			}
			
			index++;
			
		}
		
		
		
	}
	
	/**Get the branch that we are going to expand
	 * 
	 * @param p
	 * @param reached
	 * @param initD
	 * @return
	 */
	private int getBranchForMinInit(Production p, boolean expand, int initD) {
		int sym = -1;
		
		if(expand) {
			int pos = 0;
			List<Integer> listProd = new ArrayList<Integer>();
			for(Symbol s: p) {
				if(!s.isTerminal() && (reader.findRule(s).getMaximumDepth() > (this.maxDepthInit - initD))) {
					listProd.add(pos);
				}
				
				pos++;
			}
			
			if(listProd.size() > 0) {
				int selec = RandomGenerator.nextInt(listProd.size());
				sym = listProd.get(selec);
			}
		}
		
		
		return sym;
	}
	
	
	/**
	 * Calls evaluate method for each solution in a list of solutions.
	 */
	@Override
	public void evaluate(Solutions<VariableList<Integer>> solutions) {
	        for (Solution<VariableList<Integer>> solution : solutions) {
	            evaluate(solution, this.generatePhenotype(solution));
	        }
	}



}
