package jeco.core.algorithm.sge;

import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jeco.core.algorithm.ge.SimpleGrammaticalEvolution;
import jeco.core.algorithm.ge.SimpleGrammaticalEvolution_example;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

public class ProblemSGE_example extends AbstractProblemSGE{
	
	private static final Logger logger = Logger.getLogger(SimpleGrammaticalEvolution_example.class.getName());
	protected ScriptEngine evaluator = null;
	private String[] var = {"123", "43", "21", "1", "50", "43", "20", "321", "76", "54", "122"};
	private int goal = 148;
	
	public ProblemSGE_example(){
		super("D:\\Documento\\UNI\\TFG\\jeco-master\\test\\grammar.bnf", 1, 0);
		 ScriptEngineManager mgr = new ScriptEngineManager();
		evaluator = mgr.getEngineByName("JavaScript");
	}
	

	@Override
	public void evaluate(Solution<VariableArray<Integer>> solution, Phenotype phenotype) {

		  String currentFunction = "";
		    double error, totError = 0;
		    for (int i = 0; i < phenotype.size(); ++i) {
		    	if(i > var.length-1) {
		    		currentFunction += phenotype.get(i).replaceAll("x1", "1")+ " ";
		    	}else {
		    		currentFunction += phenotype.get(i).replaceAll("x1", var[i])+ " ";
		    	}
		      
		    }  
		      double funcI;
		      try {
		        String aux = evaluator.eval(currentFunction).toString();
		        if (aux.equals("NaN")) {
		          funcI = Double.POSITIVE_INFINITY;
		        } else {
		          funcI = Double.valueOf(aux);
		        }
		      } catch (NumberFormatException e) {
		        logger.severe(e.getLocalizedMessage());
		        funcI = Double.POSITIVE_INFINITY;
		      } catch (ScriptException e) {
		        logger.severe(e.getLocalizedMessage());
		        funcI = Double.POSITIVE_INFINITY;
		      }
		      error = Math.pow(funcI - goal, 2);
		      totError += error;
		    solution.getObjectives().set(0, totError);
		
	}

	@Override
	public void evaluate(Solution<VariableArray<Integer>> solution) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Problem<VariableArray<Integer>> clone() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	public static void main(String[] args) {
        // First create the problem
        ProblemSGE_example problem = new ProblemSGE_example();
     
        // Second create the algorithm
        StructuredGramaticalEvolution algorithm = new StructuredGramaticalEvolution(problem,100,200,0.3,0.7);
        // Run
        algorithm.initialize();
        Solutions<VariableArray<Integer>> solutions = algorithm.execute();
        for (Solution<VariableArray<Integer>> solution : solutions) {
            logger.info("Fitness = (" + solution.getObjectives().get(0) + ")");
            logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
        }
  }
}
