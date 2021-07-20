package jeco.core.algorithm.sge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Stack;

import jeco.core.algorithm.moge.GrammaticalEvolution;
import jeco.core.algorithm.moge.GrammaticalEvolution_example;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.operator.assigner.CrowdingDistance;
import jeco.core.operator.assigner.FrontsExtractor;
import jeco.core.operator.comparator.ComparatorNSGAII;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.bnf.BnfReader;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;

public abstract class AbstractProblemSGE extends Problem<Variable<Integer[]>> {

	
	protected String pathToBnf;
	protected BnfReader reader;
	protected Integer[] indexes;
	protected Map<String, Integer> max_references_symbol;
	protected ArrayList<Integer> max_derivations;
	
	protected ArrayList<String> order_symbols;

	
	public AbstractProblemSGE(String pathToBnf, int numberOfObjectives, int maxCntWrappings) {
		super(0, numberOfObjectives); //I need to read the file before I am able to know the size of the cromosome
		this.pathToBnf = pathToBnf;
		this.reader = new BnfReader();
		reader.load(pathToBnf);
		
		initialize();
		
		// TODO Auto-generated constructor stub
	}
	
	public void initialize() {
		super.numberOfVariables = reader.number_of_options().size();
		max_references_symbol = reader.find_references_start();
		Map<String, Integer> options = reader.number_of_options();
		
		this.order_symbols = new ArrayList<>();
		if(options.size() != max_references_symbol.size()) {
			throw new RuntimeException("Wrong loading in bnf file");
		}
		
		for(Map.Entry<String, Integer> entry : max_references_symbol.entrySet()) {
			this.order_symbols.add(entry.getKey());
		}
		
        this.lowerBound = new double[numberOfVariables];
        this.upperBound = new double[numberOfVariables];
		
		for (int i = 0; i < numberOfVariables; i++) {
			lowerBound[i] = 0;
			upperBound[i] = options.get(this.order_symbols.get(i));
		}
	}
	
	private Solution<Variable<Integer[]>> generateRandomSolution() {
        Solution<Variable<Integer[]>> solI = new Solution<>(numberOfObjectives);
        for (int j = 0; j < numberOfVariables; ++j) {
       	 Integer list_derivation[] = new Integer[this.max_references_symbol.get(this.order_symbols.get(j))];
       	 for(int i = 0; i < list_derivation.length; i++) {
       		 list_derivation[i] = RandomGenerator.nextInteger((int) upperBound[j]);
       	 }
       	 
            Variable<Integer[]> varJ = new Variable<>(list_derivation);
            solI.getVariables().add(varJ);
        }
        return solI;
    }
	
	@Override
	public Solutions<Variable<Integer[]>> newRandomSetOfSolutions(int size){
		Solutions<Variable<Integer[]>> solutions = new Solutions<Variable<Integer[]>>();
		
		for(int i = 0; i < size; i++) {
			solutions.add(generateRandomSolution());
		}
		
		
		return solutions;
	}
	
	abstract public void evaluate(Solution<Variable<Integer[]>> solution, Phenotype phenotype);
	
	@Override
	public void evaluate(Solutions<Variable<Integer[]>> solutions) {
	        for (Solution<Variable<Integer[]>> solution : solutions) {
	            evaluate(solution, this.generatePhenotype(solution));
	        }
	}
	
	public Phenotype generatePhenotype(Solution<Variable<Integer[]>> solution) {

		Phenotype phenotype = new Phenotype();
		Rule firstRule = reader.getRules().get(0);
		int index[] = new int[this.order_symbols.size()];
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
				Production nextProduction = nextRule.get(solution.getVariables().get(this.order_symbols.indexOf(nextRule.getLHS().toString())).getValue()[index[this.order_symbols.indexOf(nextRule.getLHS().toString())]]);
				index[this.order_symbols.indexOf(next.toString())]++;
				
				for(int i = nextProduction.size()-1 ; i >= 0 ; i--) {
					nextRules.add(nextProduction.get(i));
				}
			}
			
		}
		
  
		return phenotype;
	}
	
	 

}


