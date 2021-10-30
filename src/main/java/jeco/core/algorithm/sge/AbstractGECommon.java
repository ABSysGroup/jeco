package jeco.core.algorithm.sge;

import jeco.core.algorithm.moge.Phenotype;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Variable;

public abstract class AbstractGECommon<T extends Variable<?>> extends Problem<T>{

	public AbstractGECommon(int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
		// TODO Auto-generated constructor stub
	}
	
	@Override 
	public String phenotypeToString(Solution<T> solution) {
		return generatePhenotype(solution).toString();
	}

	protected abstract Phenotype generatePhenotype(Solution<T> solution);

}
