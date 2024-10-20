package jeco.core.algorithm.moge;

import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.logging.Logger;

/**
 * Example
 * Pease note that using the Script Engine is too slow.
 * We recommend using an external evaluation library like JEval (sourceforge).
 * @author J. Manuel Colmenar
 *
 */
public class MultiObjectiveGrammaticalEvolution_example extends AbstractProblemGE {
	private static final Logger logger = Logger.getLogger(MultiObjectiveGrammaticalEvolution_example.class.getName());

	protected ScriptEngine evaluator;
	protected double[] func = {0, 4, 30, 120, 340, 780, 1554}; //x^4+x^3+x^2+x

	public MultiObjectiveGrammaticalEvolution_example(String pathToBnf) {
		super(pathToBnf);
		ScriptEngineManager mgr = new ScriptEngineManager();
		evaluator = mgr.getEngineByName("JavaScript");
	}

	public void evaluate(Solution<Variable<Integer>> solution, Phenotype phenotype) {
		String originalFunction = phenotype.toString();
		double error, totError = 0, maxError = Double.NEGATIVE_INFINITY;
		for(int i=0; i<func.length; ++i) {
			String currentFunction = originalFunction.replaceAll("X", String.valueOf(i));	
			double funcI;
			try {
				String aux = evaluator.eval(currentFunction).toString();
				if(aux.equals("NaN"))
					funcI = Double.POSITIVE_INFINITY;
				else
					funcI = Double.valueOf(aux);
			} catch (NumberFormatException|ScriptException e) {
				logger.severe(e.getLocalizedMessage());
				funcI = Double.POSITIVE_INFINITY;
			}
			error = Math.abs(funcI-func[i]);
			totError += error;
			if(error>maxError)
				maxError = error;
		}
		solution.getObjectives().set(0, maxError);
		solution.getObjectives().set(1, totError);
	}
	
	

  @Override
  public MultiObjectiveGrammaticalEvolution_example clone() {
  	return new MultiObjectiveGrammaticalEvolution_example(super.pathToBnf);
  }

  public static void main(String[] args) {
		// First create the problem
		MultiObjectiveGrammaticalEvolution_example problem = new MultiObjectiveGrammaticalEvolution_example("test/grammar_example.bnf");
		// Second create the algorithm
	  	MultiObjectiveGrammaticalEvolution algorithm = new MultiObjectiveGrammaticalEvolution(problem, 50, 200, 0.2,0.8,2);
	  	//algorithm.setHypervolumeReportGenerationsGap(25);
		algorithm.initialize();
		Solutions<Variable<Integer>> solutions = algorithm.execute();
	  	System.out.println("Final solutions: "+solutions.size());
		for (Solution<Variable<Integer>> solution : solutions) {
			logger.info("Fitness = (" + solution.getObjectives().get(0) + ", " + solution.getObjectives().get(1) + ")");
			logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
		}
	}		

}
