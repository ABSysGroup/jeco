package jeco.core.algorithm.moge;

import java.util.LinkedList;

import jeco.core.algorithm.sge.AbstractGECommon;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.bnf.BnfReader;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;
import org.apache.commons.math3.stat.StatUtils;

/**
 * Superclass for all Grammatical Evolution problems, each specific problem that extends from it must implement
 * the evaluate function.
 *
 */
public abstract class AbstractProblemGE extends AbstractGECommon<Variable<Integer>> {

	/**Default value for the length of the cromosomes, if not specified*/
	public static final int CHROMOSOME_LENGTH_DEFAULT = 100;	
	/**Default value for the modulus operator, and maximum amount of productions each rule can have*/
	public static final int CODON_UPPER_BOUND_DEFAULT = 256;
	/**Default value for the maximum amount of wraps performed before and individual is considered invalid*/
	public static final int MAX_CNT_WRAPPINGS_DEFAULT = 3;
	/**Default number of objectives for a problem*/
	public static final int NUM_OF_OBJECTIVES_DEFAULT = 2;

	/**Path to bnf file that contains the grammar to be used to generate the individuals*/
	protected String pathToBnf;
	/**Reader object that parses the bnf file into rules, productions and symbols*/
	protected BnfReader reader;
	protected int maxCntWrappings = MAX_CNT_WRAPPINGS_DEFAULT;
	/**Index to generate phenotype from the list of numbers that corresponds to the genotype */
	protected int currentIdx;
	/**Current wrap number for a given solution*/
	protected int currentWrp;
	/**Boolean that determines if a solution is not invalid */
	protected boolean correctSol;
    /** Boolean that determines whether to consider or not sensible initialization*/
	protected boolean sensibleInitialization;
    /** Percentage of individuals to be initialized using sensible initialization*/
	protected double sensibleInitializationPercentage;

	/**Constructor of a Grammatical Evolution problem that sets all possible variables
	 * 
	 * @param pathToBnf path of bnf file with grammar
	 * @param numberOfObjectives number of objectives for the problem
	 * @param chromosomeLength length of the chromosomes 
	 * @param maxCntWrappings maximum amount of wraps considered
	 * @param codonUpperBound maximum integer number for each allele in the chromosome, number used for modulus operator.
	 */
	public AbstractProblemGE(String pathToBnf, int numberOfObjectives, int chromosomeLength, int maxCntWrappings, int codonUpperBound) {
		super(chromosomeLength, numberOfObjectives);
		this.pathToBnf = pathToBnf;
		reader = new BnfReader();
		reader.load(pathToBnf);
		this.maxCntWrappings = maxCntWrappings;
		for (int i = 0; i < numberOfVariables; i++) {
			lowerBound[i] = 0;
			upperBound[i] = codonUpperBound;
		}
                this.sensibleInitialization = false;
	}

	/**Constructor of a Grammatical Evolution problem that only sets numberOfObjectives, chromosomeLength, Wrappings and CodonUpperBound
	 * are set by the default value.
	 * 
	 * @param pathToBnf path of bnf file with grammar.
	 * @param numberOfObjectives number of objectives for the problem
	 */
	public AbstractProblemGE(String pathToBnf, int numberOfObjectives) {
		this(pathToBnf, numberOfObjectives, CHROMOSOME_LENGTH_DEFAULT, MAX_CNT_WRAPPINGS_DEFAULT, CODON_UPPER_BOUND_DEFAULT);
	}

	/**Constructor of Grammatical Evolution problem with all variables set to the default value 
	 * 
	 * @param pathToBnf path of bnf file with grammar.
	 */
	public AbstractProblemGE(String pathToBnf) {
		this(pathToBnf, NUM_OF_OBJECTIVES_DEFAULT, CHROMOSOME_LENGTH_DEFAULT, MAX_CNT_WRAPPINGS_DEFAULT, CODON_UPPER_BOUND_DEFAULT);
	}
        
	/**Set the value of the sensible initialization
	 * 
	 * @param value boolean that determines whether to perform or not sensible initialization
	 * @param percentage percentage of solutions that will be initialized by sensible initialization
	 */
    public void setSensibleInitialization(boolean value, double percentage) {
        this.sensibleInitialization = value;
        this.sensibleInitializationPercentage = percentage;
    }

	/**Evaluate method to the implemented by each problem
	 * 
	 * @param solution an individuals genotype
	 * @param phenotype the corresponding Phenotype of the solution
	 */
	abstract public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype);
	
	/**
	 * Calls evaluate method for each solution in a list of solutions.
	 */
	public void evaluate(Solutions<Variable<Integer>> solutions) {
		for(Solution<Variable<Integer>> solution : solutions)
			evaluate(solution);
	}

	/**Evaluate of a solution, generates the Phenotype of a solution and then calls the abstract
	 * evaluate with the Phenotype to be implemented by he specific Problem
	 * 
	 */
	public void evaluate(Solution<Variable<Integer>> solution) {
		Phenotype phenotype = generatePhenotype(solution);
		if(correctSol)
			evaluate(solution, phenotype);
		else {
			for(int i=0; i<super.numberOfObjectives; ++i) {
				solution.getObjectives().set(i, Double.POSITIVE_INFINITY);
			}
		}
	}

	@Override
	public Phenotype generatePhenotype(Solution<Variable<Integer>> solution) {
		currentIdx = 0;
		currentWrp = 0;
		correctSol = true;
		Phenotype phenotype = new Phenotype();
		Rule firstRule = reader.getRules().get(0);
		Production firstProduction = firstRule.get(solution.getVariables().get(currentIdx++).getValue() % firstRule.size());
		processProduction(firstProduction, solution, phenotype);
                // Account for the number of genes that were used in decodification.
                phenotype.setUsedGenes(currentIdx + (currentWrp * solution.getVariables().size()));
		return phenotype;
	}

	/**Given a production we will add all the terminal symbols into the Phenotype, and for all the non-terminal symbols
	 * we find the rule for the symbol, get the production corresponding to the next element of the solution and makes a recursive
	 * call with the next Production.
	 * 
	 * @param currentProduction production that is processed to generate the phenotype of a solution
	 * @param solution solution from which to compute the phenotype
	 * @param phenotype phenotype to be computed
	 */
	public void processProduction(Production currentProduction, Solution<Variable<Integer>> solution, LinkedList<String> phenotype) {
		if(!correctSol)
			return;
		for (Symbol symbol : currentProduction) {
			if (symbol.isTerminal()) {
				phenotype.add(symbol.toString());
			} else {
				if(currentIdx >= solution.getVariables().size() && currentWrp<maxCntWrappings) {
					currentIdx = 0;
					currentWrp++;
				}
				if (currentIdx < solution.getVariables().size()) {
					Rule rule = reader.findRule(symbol);
					Production production = rule.get(solution.getVariables().get(currentIdx++).getValue() % rule.size());
					processProduction(production, solution, phenotype);
				}
				else {
					correctSol = false;
					return;
				}
			}
		}
	}

	
	@Override
	public Solutions<Variable<Integer>> newRandomSetOfSolutions(int size) {
            
                int randomSize = size;
                double[] consumedGenes = null;
                int idx = 0;
                
                // In case of sensible initialization, half of the inidividuals are random.
                if (sensibleInitialization) {
                    randomSize = size / 2;
                    // And store the consumed genes for each individual.
                    consumedGenes = new double[randomSize];
                }
            
		Solutions<Variable<Integer>> solutions = new Solutions<Variable<Integer>>();
                for (int i = 0; i < randomSize; ++i) {

                    Solution<Variable<Integer>> solI = generateRandomSolution();
                    solutions.add(solI);

                    // Account for the number of genes that are consumed
                    if (sensibleInitialization) {
                        // Generate phenotype and make the count
                        generatePhenotype(solI);
                        consumedGenes[idx++] = (solI.getVariables().size() * currentWrp) + currentIdx;
                    }
                }
                
                // Complete the solutions creating long elements:
                if (sensibleInitialization) {
                    // The minimum size of the individuals depends on the previous maximum
                    double minSize = sensibleInitializationPercentage * StatUtils.max(consumedGenes);

                    do {
                        Solution<Variable<Integer>> solI = generateRandomSolution();
                        generatePhenotype(solI);
                        if (((solI.getVariables().size() * currentWrp) + currentIdx) >= minSize) {
                            solutions.add(solI);
                        }
                    } while (solutions.size() < size);
                    
                }
                
		return solutions;
	}

        
        private Solution<Variable<Integer>> generateRandomSolution() {
            Solution<Variable<Integer>> solI = new Solution<>(numberOfObjectives);
            for (int j = 0; j < numberOfVariables; ++j) {
                Variable<Integer> varJ = new Variable<>(RandomGenerator.nextInteger((int) upperBound[j]));
                solI.getVariables().add(varJ);
            }
            return solI;
        }

}
