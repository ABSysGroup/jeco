package jeco.core.algorithm.sge;

import jeco.core.algorithm.moge.Phenotype;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

/**
 * SuperClass for GE and SGE it contains the necessary methods to generate the Phenotype of the individuals
 * for this superclass
 *
 * @param <T> extends Variable, the type of the individuals genotype elements.
 */
public abstract class AbstractGECommon<T extends Variable<?>> extends Problem<T>{

	/**
	 * Abstract constructor of AbstractGECommon
	 * @param numberOfVariables number of variable
	 * @param numberOfObjectives number of objectives
	 */
	protected AbstractGECommon(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
		// TODO Auto-generated constructor stub
	}
	
	@Override 
	public String phenotypeToString(Solution<T> solution) {
		return generatePhenotype(solution).toString();
	}

	/**Given a genotype (solution) generates the corresponding genotype, to be implemented for each
	 * type of solution
	 * 
	 * @param solution genotype to transform
	 * @return phenotype of solution
	 */
	protected abstract Phenotype generatePhenotype(Solution<T> solution);

}
