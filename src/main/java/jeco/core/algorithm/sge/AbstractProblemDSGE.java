package jeco.core.algorithm.sge;

import java.util.ArrayList;
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

public abstract class AbstractProblemDSGE extends AbstractGECommon<VariableList<Integer>> {

	private int maxDepth;
	protected String pathToBnf;
	protected BnfReaderSge reader;
	
	protected ArrayList<Integer> maxDerivations;
	protected ArrayList<String> orderSymbols;
	
	public AbstractProblemDSGE(String pathToBnf, int numberOfObjectives, int maxDepth) {
		super(0, numberOfObjectives);
		this.maxDepth = maxDepth;
		reader = new BnfReaderSge();
		reader.load(pathToBnf);
		
		
		initialize();
		// TODO Auto-generated constructor stub
	}
	
	
	private void initialize() {
		
		Map<String, Integer> options = reader.number_of_options();
		this.numberOfVariables = options.size();
		this.orderSymbols = new ArrayList<>();
		
		for(Map.Entry<String, Integer> entry : options.entrySet()) {
			this.orderSymbols.add(entry.getKey());
		}
		
        this.lowerBound = new double[numberOfVariables];
        this.upperBound = new double[numberOfVariables];
		
		for (int i = 0; i < numberOfVariables; i++) {
			lowerBound[i] = 0;
			upperBound[i] = options.get(this.orderSymbols.get(i));
		}
	}
	
	private Solution<VariableList<Integer>> generateRandomSolution() {
        Solution<VariableList<Integer>> solI = new Solution<>(this.numberOfVariables);
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
		/*Rule nextRule = null;
		while(!nextRules.empty()) {
			Symbol next = nextRules.pop();
			
			
			if(next.isTerminal()) {
				phenotype.add(next.toString());
				
			
			}
			else {
				nextRule = this.reader.findRule(next);
				if(index[this.orderSymbols.indexOf(next.toString())] + 1 >= solution.getVariable(this.orderSymbols.indexOf(next.toString())).size()) {
					if(depth >= this.maxDepth) {
						generateTerminalExpansion(next, solution);
					}else {
						generateExpansion(next, solution);
					}
				}
				
				Production nextProduction = nextRule.get(solution.getVariables().get(this.orderSymbols.indexOf(next.toString())).getValue().get(index[this.orderSymbols.indexOf(next.toString())]));
				index[this.orderSymbols.indexOf(next.toString())]++;
				
				for(int i = nextProduction.size()-1 ; i >= 0 ; i--) {
					nextRules.add(nextProduction.get(i));
				}
			}
			
		}
		*/
		
		auxCreatePhenotype(firstRule.getLHS(), phenotype, 0,  solution,  index);
		
  
		return phenotype;
	}
	
	
	private void auxCreatePhenotype(Symbol next, Phenotype phenotype, int depth, Solution<VariableList<Integer>> solution, int[] index) {
		
		if(next.isTerminal()) {
			phenotype.add(next.toString());
			
		}
		else {
			Rule nextRule = this.reader.findRule(next);
			if(index[this.orderSymbols.indexOf(next.toString())] + 1 > solution.getVariable(this.orderSymbols.indexOf(next.toString())).size()) {
				if(depth >= this.maxDepth) {
					generateTerminalExpansion(next, solution);
				}else {
					generateExpansion(next, solution);
				}
			}
			
			Production nextProduction = nextRule.get(solution.getVariables().get(this.orderSymbols.indexOf(next.toString())).getValue().get(index[this.orderSymbols.indexOf(next.toString())]));
			index[this.orderSymbols.indexOf(next.toString())]++;
			
			for(int i = 0 ; i < nextProduction.size() ; i++) {
				
				auxCreatePhenotype(nextProduction.get(i),phenotype, depth + 1, solution, index);
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
	
	private int generateExpansion(Symbol sym, Solution<VariableList<Integer>> solution) {
		Rule ruleSymbol = this.reader.findRule(sym);
		int rand_prod = RandomGenerator.nextInt(ruleSymbol.size());
		
		solution.getVariable(this.orderSymbols.indexOf(sym.toString())).add(rand_prod);
		
		return rand_prod;
	}
	
	
	private void createIndividual(int depth, ArrayList<VariableList<Integer>> solution, Symbol sym) {
		int rand_prod = RandomGenerator.nextInt(reader.findRule(sym).size());
		Rule ruleSymbol = reader.findRule(sym);
		Production expansion = ruleSymbol.get(rand_prod);
		int depthrec = depth;
		
		
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
				createIndividual(depth +1, solution, nextSym);
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
