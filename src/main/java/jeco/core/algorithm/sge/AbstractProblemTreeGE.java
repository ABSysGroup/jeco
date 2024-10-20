package jeco.core.algorithm.sge;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import jeco.core.algorithm.moge.Phenotype;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.util.Pair;
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
public abstract class AbstractProblemTreeGE extends AbstractGECommon<RecListT<Integer>> {
	
	//Max depth an individual can be
	private int maxDepth;
	private int maxDepthInit;
	private int minRecDepthInit;
	private boolean bloatingControl;
	private boolean treeDepth;
	private int maxDepthForNonCodifiers;
	
	
	
	/**
	 * Constructor without initialMaxdepth, set to the maxDepth, nor initialMinRecDepth which is set to 0
	 * @param pathToBnf path of bnf file with grammar
	 * @param numberOfObjectives of the problem chosen
	 * @param maxDepth maximum depth of the solution tree constructed or amount of times each rule can perform recursion.
	 * @param bloatingControl boolean that determines whether to limit the depth of the trees in the solution or not during the evolution.
	 * @param treeDepth boolean that determines if the maxDepth refers to the maximun depth of the tress or the maximum depth of each recursion.
	 */
	public AbstractProblemTreeGE(String pathToBnf, int numberOfObjectives, int maxDepth, boolean bloatingControl, boolean treeDepth, int maxDepthForNonCodifiers) {
		super(pathToBnf, 0, numberOfObjectives);
		this.bloatingControl = bloatingControl;
		this.treeDepth = treeDepth;
		this.maxDepth = maxDepth;
		this.minRecDepthInit = 0;
		this.maxDepthInit = maxDepth;
		this.maxDepthForNonCodifiers = maxDepthForNonCodifiers;
		if(this.maxDepthForNonCodifiers < this.maxDepth) {
			throw new RuntimeException("Non-codifier depth must be the same or higher than codifing depth");
		}
		
		//initialize();
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
	public AbstractProblemTreeGE(String pathToBnf, int numberOfObjectives, int maxDepth, boolean bloatingControl, boolean treeDepth, int maxInit, int minRecInit,int maxDepthForNonCodifiers) {
		super(pathToBnf, 0, numberOfObjectives);
		//reader.load(pathToBnf);
		this.bloatingControl = bloatingControl;
		this.treeDepth = treeDepth;
		this.maxDepth = maxDepth;
		this.minRecDepthInit = minRecInit;
		this.maxDepthInit = maxInit;
		this.maxDepthForNonCodifiers = maxDepthForNonCodifiers;
		
		if(this.maxDepthForNonCodifiers < this.maxDepth) {
			throw new RuntimeException("Non-codifier depth must be the same or higher than codifing depth");
		}
		
		//initialize();
		// TODO Auto-generated constructor stub
	}
	
	public BnfReader getReader() {
		return this.reader;
	}

	
	/**
	 * Generates a random Individual of type VariableList of Integers
	 */
	protected Solution<RecListT<Integer>> generateRandomSolution() {
        Solution<RecListT<Integer>> solI = new Solution<>(this.numberOfObjectives);
        RecListT<Integer> temp = new RecListT<Integer>();
        //Create the individual with initial depth 0
        createIndividual(0,0, temp, reader.getRules().get(0).getLHS(), true);

        solI.getVariables().add(temp);
       
        
        return solI;
	}

	/**
	 * Generates a phenotype from a list of VariableList
	 */
	@Override
	protected Phenotype generatePhenotype(Solution<RecListT<Integer>> solution) {
		Phenotype phenotype = new Phenotype();
		Rule firstRule = reader.getRules().get(0);
		
		int numNodes = auxCutTree(firstRule.getLHS(), 0,  solution.getVariable(0));
		//System.out.println(numNodes);
		auxCreatePhenotype(firstRule.getLHS(), phenotype, 0,  solution.getVariable(0));
		//System.out.println(phenotype.toString());
		solution.setNumberGenes(numNodes);
  
		return phenotype;
	}
	
	private int auxCutTree(Symbol next,int depth, RecListT<Integer> solution) {
		
		int numExp = 0;
		Rule nextRule = this.reader.findRule(next);
		Production nextProduction = nextRule.get(solution.getValue());
		
		solution.resetIndex(); //So we start counting from 0 each time
		
		if(bloatingControl && nextRule.getRecursive() && (depth >= this.maxDepthForNonCodifiers)) {
			//System.out.println("Depth: "+ depth);
			if(reader.sameRecursion(nextRule, nextProduction)) {
				//System.out.println("Depth rec: "+ depth);

				transformToTerminalExpansion(next, solution);
				//nextProduction = nextRule.get(solution.getValue());
				
			}
			deleteExtraChildren(nextRule.getLHS(), solution);

		}
		
		numExp++;
		for(RecListT<Integer> child: solution.getInteriorList()) {
			
			if(!treeDepth) {
				if(reader.sameRecursion(nextRule, child.getS())) {
					//The next symbol has the same recursion as this rule therefore we add depth+1
					numExp += auxCutTree(child.getS(), depth+1, child);
				}else {
					//The next symbol is not recursive with the current rule therefore we reset the depth to 0
					numExp += auxCutTree(child.getS(), 0, child);
				}
			}else {
				//If we consider tree depth we always add one 
				numExp += auxCutTree(child.getS(), depth+1, child);
			}
			
		}
			
		return numExp;
	}
	
	
	/**
	 * @param next
	 * @param phenotype
	 * @param depth
	 * @param solution
	 * @param index
	 */
	private void auxCreatePhenotype(Symbol next, Phenotype phenotype, int depth, RecListT<Integer> solution) {
		
		if(next.isTerminal()) {
			phenotype.add(next.toString());
			
		}
		else {
			Rule nextRule = this.reader.findRule(next);
			Production nextProduction = nextRule.get(solution.getValue());
			
			solution.resetIndex(); //So we start counting from 0 each time
			
			if(bloatingControl && nextRule.getRecursive() && (depth >= this.maxDepth)) {
				if(reader.sameRecursion(nextRule, nextProduction)) {
					transformToTerminalExpansion(next, solution);
					nextProduction = nextRule.get(solution.getValue());
					
					
				}
			}
			
			phenotype.setUsedGenes(phenotype.getUsedGenes()+1);

			
			for(int i = 0 ; i < nextProduction.size() ; i++) {
				RecListT<Integer> nextList = solution.getnextSymbol(nextProduction.get(i));
				
				//If nextList is null means the individual became invalid after a mutation or crossover, therefore
				//we must add a new element during execution
				if(nextList == null && !nextProduction.get(i).isTerminal()) {
					nextList = solution.getnewEmpty();
					solution.addnewSymbol(nextProduction.get(i));
					nextList.setS(nextProduction.get(i));
					nextList.setValue(generateExpansion(nextProduction.get(i)));
					
				}
				
				if(!treeDepth) {
					if(reader.sameRecursion(nextRule, nextProduction.get(i))) {
						//The next symbol has the same recursion as this rule therefore we add depth+1
						auxCreatePhenotype(nextProduction.get(i),phenotype, depth+1, nextList);
					}else {
						//The next symbol is not recursive with the current rule therefore we reset the depth to 0
						auxCreatePhenotype(nextProduction.get(i),phenotype, 0, nextList);
					}
				}else {
					//If we consider tree depth we always add one 
					auxCreatePhenotype(nextProduction.get(i),phenotype, depth+1, nextList);
				}
				
			}
			
			solution.resetIndex();
			
		}
	}
	
	private int TerminalExpansion(Rule ruleSymbol) {

		int rand_prod;
		
		//We get the productions that are not recursive with the ruleSymbol and put their indexes in a list
		ArrayList<Integer> listProd = new ArrayList<>();
		int min = Integer.MAX_VALUE;
		
		//We search for the minimun depth possible
		for(Production p: ruleSymbol) {
			if(p.getMinimumDepth() < min) {
				min = p.getMinimumDepth();
			}
		}
		
		int index = 0;
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
	
	
	/*private int TerminalExpansion(Rule ruleSymbol) {

		int rand_prod;
		
		//We get the productions that are not recursive with the ruleSymbol and put their indexes in a list
		ArrayList<Integer> listProd = new ArrayList<>();
		int index = 0; 
		for(Production p: ruleSymbol) {
			if(!reader.sameRecursion(ruleSymbol, p)) {
				listProd.add(index);
			}
			index++;
		}
		
		//Select one of the indexes of the list
		int selec = RandomGenerator.nextInt(listProd.size());
		rand_prod = listProd.get(selec);
		
		return rand_prod;
	}*/
	
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
	public void transformToTerminalExpansion(Symbol sym, RecListT<Integer> solution) {
		int rand_prod;
		Rule ruleSymbol = this.reader.findRule(sym);
		
		rand_prod = TerminalExpansion(ruleSymbol);

		solution.setValue(rand_prod);
		
		//deleteExtraChildren(sym, solution);
		
	}
	
	public void deleteExtraChildren(Symbol sym,  RecListT<Integer> solution) {
		/////Eliminate extra children
		
		List<RecListT<Integer>> children = solution.getInteriorList();
		List<RecListT<Integer>> toDelete = new ArrayList<>();
		
		/*for(Production p: ruleSymbol) {
			if(this.reader.sameRecursion(ruleSymbol, p)) {
				for(Symbol s: )
			}
		}*/
		for(RecListT<Integer> list: children) {
			if(list.getS().toString().equals(sym.toString())) {
				toDelete.add(list);
			}
		}
		//System.out.println("To delete size"+toDelete.size());
		solution.deleteFromList(toDelete);
		
		/*List<Integer> toDelete = new ArrayList<>();
		int i = 0;
		for(RecListT<Integer> list: children) {
			if(list.getS().toString().equals(sym.toString())) {
				toDelete.add(i);
			}
			i++;
		}
		
		solution.deleteFromListInt(toDelete);*/
	}
	
	
	/**
	 * Returns a random expansion of the rule identified by sym
	 * @param sym
	 * @param solution
	 * @return
	 */
	public Integer generateExpansion(Symbol sym) {
		Rule ruleSymbol = this.reader.findRule(sym);
		int rand_prod = RandomGenerator.nextInt(ruleSymbol.size());
		
		return rand_prod;
		
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
	 * @param solution
	 * @param sym
	 */
	private void createIndividual(int depth, int innitDepth, RecListT<Integer> solution, Symbol sym, boolean MinInitD) {
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
			if(ruleSymbol.getRecursive() && (Recdepth < this.minRecDepthInit) && (depth < this.maxDepthInit)) {
				rand_prod = RecursiveExpansion(ruleSymbol);
				expansion = ruleSymbol.get(rand_prod);
			
			}
		}*/
		
		if((innitDepth < this.minRecDepthInit) && (depth < this.maxDepthInit) && MinInitD) {
			//rand_prod = RecursiveExpansion(ruleSymbol);
			rand_prod = generateExpansionToMinimumDepth(ruleSymbol, rand_prod, (minRecDepthInit - innitDepth));
			expansion = ruleSymbol.get(rand_prod);
		
		}
		
		
		solution.setS(sym);
		solution.setValue(rand_prod);
		
		solution.setInteriorList(new ArrayList<RecListT<Integer>>());
		//solution.get(this.orderSymbols.indexOf(sym.toString())).add(rand_prod);
		
		int symToExpandMinInnit = getBranchForMinInit(expansion, MinInitD, innitDepth);
		
		int index = 0;
		
		for(Symbol nextSym: expansion) { //For each new symbol in the expansion we must take some things into account
			if(!nextSym.isTerminal()) { //If the next symbol not a terminal we continue to generate the individual
				
				boolean sameRecursion = reader.sameRecursion(ruleSymbol, nextSym); 

				//Para la rama elegida hacemos que continue creando el camino más largo
				boolean nextInitDepth = false;
				if(index == symToExpandMinInnit) {
					nextInitDepth = true;
				}
				
				if(!treeDepth) {
					if(sameRecursion) {
						//The next symbol has the same recursion as this rule therefore we add depth+1 and Recdepth+1
						createIndividual(depth+1,innitDepth+1, solution.getnewEmpty(), nextSym, nextInitDepth);
					}else {
						//The next symbol is not recursive with the current rule therefore we reset the depth to 0
						createIndividual(0,innitDepth+1, solution.getnewEmpty(), nextSym, nextInitDepth);
					}
				}else {
					//The next symbol has the same recursion as this symbol so we can add one to the initial depth
					createIndividual(depth+1,innitDepth+1, solution.getnewEmpty(), nextSym, nextInitDepth);

					
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
				if(!s.isTerminal() && (reader.findRule(s).getMaximumDepth() > (this.minRecDepthInit - initD))) {
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
	public void evaluate(Solutions<RecListT<Integer>> solutions) {
	        for (Solution<RecListT<Integer>> solution : solutions) {
	            evaluate(solution, this.generatePhenotype(solution));
	        }
	}

	public int getMaxDepth() {
		// TODO Auto-generated method stub
		return this.maxDepth;
	}
	
    @Override
    protected Solution<RecListT<Integer>> initializeInd(){
    	Solution<RecListT<Integer>> solI = new Solution<>(numberOfObjectives);
        
    	RecListT<Integer> n = this.initializator.initialize();

        solI.getVariables().add(n);
        
    	return solI;
    }

	

}
