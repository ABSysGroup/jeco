package jeco.core.algorithm.moge;

import java.util.ArrayList;
import java.util.List;

import jeco.core.algorithm.sge.AbstractGECommon;
import jeco.core.algorithm.sge.NodeTree;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.util.bnf.BnfReader;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;

/**Context free grammar genetic Programming abstract  implementation. The individuals in the population are derivation trees.
 * 
 * @author Marina
 *
 */
public abstract class CFG_GP extends AbstractGECommon<NodeTree> {
	
	//Max depth an individual can be
	private int maxDepth;
	private int maxDepthInit;
	private int minDepthInit;
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
	public CFG_GP(String pathToBnf, int numberOfObjectives, int maxDepth, boolean bloatingControl, boolean treeDepth) {
		super(pathToBnf, 0, numberOfObjectives);
		this.bloatingControl = bloatingControl;
		this.treeDepth = treeDepth;
		this.maxDepth = maxDepth;
		this.minDepthInit = 0;
		this.maxDepthInit = maxDepth;
		
		//initialize();
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Constructor with all parameters including maxInitDepth and minInitDepth
	 * @param pathToBnf path of bnf file with grammar
	 * @param numberOfObjectives of the problem chosen
	 * @param maxDepth maximum depth of the solution tree constructed or amount of times each rule can perform recursion.
	 * @param bloatingControl  boolean that determines whether to limit the depth of the trees in the solution or not during the evolution.
	 * @param treeDepth boolean that determines if the maxDepth refers to the maximun depth of the tress or the maximum depth of each recursion.
	 * @param maxInit maximum depth of the initial solution tree constructed or amount of times each rule can perform recursion in the creation of solutions.
	 * @param minInit minimum depth of the tree considered.
	 */
	public CFG_GP(String pathToBnf, int numberOfObjectives, int maxDepth, boolean bloatingControl, boolean treeDepth, int maxInit, int minRecInit) {
		super(pathToBnf, 0, numberOfObjectives);
		//reader.load(pathToBnf);
		this.bloatingControl = bloatingControl;
		this.treeDepth = treeDepth;
		this.maxDepth = maxDepth;
		this.minDepthInit = minRecInit;
		this.maxDepthInit = maxInit;
		
		//initialize();
		// TODO Auto-generated constructor stub
	}
	
	public BnfReader getReader() {
		return this.reader;
	}
	

	
	/**
	 * Generates a random Individual of type NodeTree (a derivation tree)
	 */
	protected Solution<NodeTree> generateRandomSolution() {
        Solution<NodeTree> solI = new Solution<>(this.numberOfObjectives);
        NodeTree temp = new NodeTree();
        //Create the individual with initial depth 0
        createIndividual(0,0, temp, reader.getRules().get(0).getLHS(), true);

        solI.getVariables().add(temp);
       
        
        return solI;
	}

	/**
	 * Generates a phenotype from a lNodeTree
	 */
	@Override
	protected Phenotype generatePhenotype(Solution<NodeTree> solution) {
		Phenotype phenotype = new Phenotype();
		Rule firstRule = reader.getRules().get(0);
		
		auxCreatePhenotype(firstRule.getLHS(), phenotype, 0,  solution.getVariable(0));
		
  
		return phenotype;
	}
	
	/**Checks if a rule and the children of a node are directly recursive
	 * 
	 * @param r rule
	 * @param n tree
	 * @return true if recursive false otherwise
	 */
	boolean sameRecursion(Rule r, NodeTree n) {
		
		for(NodeTree children: n.getChildren()) {
			if(this.reader.sameRecursion(r, children.getValue())) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @param next
	 * @param phenotype
	 * @param depth
	 * @param solution
	 * @param index
	 */
	private void auxCreatePhenotype(Symbol next, Phenotype phenotype, int depth, NodeTree solution) {
		
		if(next.isTerminal()) { //If next element is a terminal we add it to the phenotype
			phenotype.add(next.toString());
			
		}
		else {
			Rule nextRule = this.reader.findRule(next); //We find the equivalent rule from the symbol of the node
			
			//If we go over the maximum depth through crossover, and the next element is recursive, we generate a new subtree that is within the range required
			if(bloatingControl && nextRule.getRecursive() && (depth >= this.maxDepth)) {
				if(sameRecursion(nextRule, solution)) {
					NodeTree newNode = new NodeTree();
					createIndividual(depth, depth, newNode, next, false);
					solution.setChildren(newNode.getChildren());
				}
			}
			
			//We get the children nodes
			List<NodeTree> children = solution.getChildren();
			
			//For each of the children we perform recursion
			for(int i = 0 ; i < children.size() ; i++) {
				NodeTree nodo = children.get(i);
				
				if(!treeDepth) {
					if(reader.sameRecursion(nextRule, nodo.getValue())) {
						//The next symbol has the same recursion as this rule therefore we add depth+1
						auxCreatePhenotype(nodo.getValue(),phenotype, depth+1, nodo);
					}else {
						//The next symbol is not recursive with the current rule therefore we reset the depth to 0
						auxCreatePhenotype(nodo.getValue(),phenotype, 0, nodo);
					}
				}else {
					//If we consider tree depth we always add one 
					auxCreatePhenotype(nodo.getValue(),phenotype, depth+1, nodo);
				}
				
			}
			
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
	public void createIndividual(int depth, int innitDepth, NodeTree solution, Symbol sym, boolean MinInitD) {
		
		if(sym.isTerminal()) { //If the symbol is a terminal we create a node with the symbol and an empty list of children
			
			solution.setValue(sym);	
			solution.setChildren(new ArrayList<NodeTree>());
			
		}else {
		
			Rule ruleSymbol = reader.findRule(sym);
			int rand_prod = RandomGenerator.nextInt(ruleSymbol.size());
			
			Production expansion = ruleSymbol.get(rand_prod);
			
			//If the rule and expansion is recursive and we have gone over the maxDepth we only generate non_recursive expansions 
			if(reader.sameRecursion(ruleSymbol, expansion)) {
				if(depth >= this.maxDepthInit) {
					rand_prod = TerminalExpansion(ruleSymbol);
					expansion = ruleSymbol.get(rand_prod);
					
				}
				
			}
			
			//If we are considering initial depth and MinInitD is true, meaning we have chosen this branch to expand to minDepth
			//we choose a random expansion for those whose maximum depth is over the minimun innitial depth
			if((innitDepth < this.minDepthInit) && (depth < this.maxDepthInit) && MinInitD) {
				//rand_prod = RecursiveExpansion(ruleSymbol);
				rand_prod = generateExpansionToMinimumDepth(ruleSymbol, rand_prod, (minDepthInit - innitDepth));
				expansion = ruleSymbol.get(rand_prod);
			
			}
			
			//We set the value of the node to the next symbol
			solution.setValue(sym);
			//We create the list of children
			solution.setChildren(new ArrayList<NodeTree>());
			
			//We choose the branch to reach the minimun depth if this branch has been set to expand
			int symToExpandMinInnit = getBranchForMinInit(expansion, MinInitD, innitDepth);
			
			int index = 0;
			
			for(Symbol nextSym: expansion) { //For each new symbol in the expansion we must take some things into account
					
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
	
				index++;
				
			}
		
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
				if(!s.isTerminal() && (reader.findRule(s).getMaximumDepth() > (this.minDepthInit - initD))) {
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
	public void evaluate(Solutions<NodeTree> solutions) {
	        for (Solution<NodeTree> solution : solutions) {
	            evaluate(solution, this.generatePhenotype(solution));
	        }
	}


}