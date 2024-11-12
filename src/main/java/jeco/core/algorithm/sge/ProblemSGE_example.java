package jeco.core.algorithm.sge;

import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jeco.core.algorithm.ga.SimpleGeneticAlgorithm;
import jeco.core.algorithm.ga.StaticSimpleGeneticAlgorithmBestWithPopRenovation;
import jeco.core.algorithm.ge.SimpleGrammaticalEvolution_example;
import jeco.core.algorithm.moga.NSGAII;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.SubTreeCrossover;
import jeco.core.operator.crossover.UniformCrossover;
import jeco.core.operator.crossover.UniformTerminalCrossover;
import jeco.core.operator.mutation.IntegerFlipMutationList;
import jeco.core.operator.mutation.IntegerFlipMutationListAll;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;


public class ProblemSGE_example extends AbstractProblemSSGE{
	
	private static final Logger logger = Logger.getLogger(SimpleGrammaticalEvolution_example.class.getName());
	protected ScriptEngine evaluator = null;
	private String[] variables = {"123", "43", "21", "1", "50", "43", "20", "321", "76", "54", "122"};
	private int goal = 126;
	
	public ProblemSGE_example(String path, int depth){
		super(path, 1, depth);
		 ScriptEngineManager mgr = new ScriptEngineManager();
		evaluator = mgr.getEngineByName("graal.js");
	}
	

	@Override
	public void evaluate(Solution<VariableArray<Integer>> solution, Phenotype phenotype) {

		  String currentFunction = "";
		    double error, totError = 0;
		    for (int i = 0; i < phenotype.size(); ++i) {
		    	if(i > variables.length-1) {
		    		currentFunction += phenotype.get(i).replace("x1", "1")+ " ";
		    	}else {
		    		currentFunction += phenotype.get(i).replace("x1", variables[i])+ " ";
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
	public Problem<VariableArray<Integer>> clone() {

		ProblemSGE_example clone = new ProblemSGE_example(super.pathToBnf, 10);
		return clone;
	}
	
	@Override
	public void evaluate(Solution<VariableArray<Integer>> solution) {
		logger.severe("Should not be called");
		
	}
	
	
	public static void main(String[] args) {
        // First create the problem
       
		ProblemSGE_example problem = new ProblemSGE_example("test/grammar_example.bnf", 4);
        
		// Second create the algorithm
        StaticSimpleGeneticAlgorithmBestWithPopRenovation<VariableArray<Integer>> algorithm = new StaticSimpleGeneticAlgorithmBestWithPopRenovation<>(problem,100,500,false,new IntegerFlipMutationListAll<VariableArray<Integer>>(problem, 0.3),
                new SubTreeCrossover<VariableArray<Integer>>(problem, 0.7, 0.5),
                new BinaryTournament<VariableArray<Integer>>(new SimpleDominance<>()), 0.1);
        // Run
        algorithm.initialize();
        Solutions<VariableArray<Integer>> solutions = algorithm.execute();
		System.out.println("\n\n>>>>> Final solutions: " + solutions.size());
        for (Solution<VariableArray<Integer>> solution : solutions) {
			System.out.println("Fitness = (" + solution.getObjectives().get(0) + ") -- Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
        }
  }


}
