package jeco.core.algorithm.sge;

import jeco.core.algorithm.moge.Phenotype;
import jeco.core.operator.initialization.Initializator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;
import jeco.core.util.bnf.BnfReader;

/**
 * SuperClass for GE and SGE it contains the necessary methods to generate the Phenotype of the individuals
 * for this superclass
 *
 * @param <T> extends Variable, the type of the individuals genotype elements.
 */
public abstract class AbstractGECommon<T extends Variable<?>> extends Problem<T>{

	
	/**Reader object that parses the bnf file into rules, productions and symbols*/
	protected BnfReader reader;
	
	/**Path to bnf file that contains the grammar to be used to generate the individuals*/
	protected String pathToBnf;
	
	/**Object that returns an initialized population of trees to be transformed*/
	protected Initializator initializator = null;
	
	/**
	 * Abstract constructor of AbstractGECommon.
	 *
	 * @param pathtoBnf path to bnf file
	 * @param numberOfVariables number of variable
	 * @param numberOfObjectives number of objectives
	 */
	protected AbstractGECommon(String pathtoBnf, int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
		this.pathToBnf = pathtoBnf;
		
		reader = new BnfReader();
		reader.load(pathToBnf);
		// TODO Auto-generated constructor stub
	}
	
	@Override 
	public String phenotypeToString(Solution<T> solution) {
		return generatePhenotype(solution).toString();
	}
	
	/**Sets the initializator of the method
	 * 
	 * @param init initializator object
	 */
    public void setInitializator(Initializator init) {
        this.initializator = init;
    }
    
	/**
	 * Given a genotype (solution) generates the corresponding genotype, to be implemented for each
	 * type of solution
	 *
	 * @return phenotype of solution
	 */
	protected abstract Solution<T> initializeInd();

	/**Given a genotype (solution) generates the corresponding genotype, to be implemented for each
	 * type of solution
	 * 
	 * @param solution genotype to transform
	 * @return phenotype of solution
	 */
	protected abstract Phenotype generatePhenotype(Solution<T> solution);
	
	
	/**
	 * Generates an initial random solution (individual of population)
	 * @return solution
	 */
	protected abstract Solution<T> generateRandomSolution();
	
	
	/**
	 * To be implemented by the different problems
	 * @param solution solution to evaluate
	 * @param phenotype phenotype of solution
	 */
	protected abstract void evaluate(Solution<T> solution, Phenotype phenotype);
	
	
	@Override
	public Solutions<T> newRandomSetOfSolutions(int size) {
		Solutions<T> solutions = new Solutions<>();
		
		for(int i = 0; i < size; i++) {
			if(this.initializator != null) {
				solutions.add(initializeInd());
			}else {
				solutions.add(generateRandomSolution());
			}
		}
		
		
		return solutions;
	}

}
