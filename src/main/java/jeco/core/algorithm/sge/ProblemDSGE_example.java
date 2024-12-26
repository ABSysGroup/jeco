package jeco.core.algorithm.sge;

import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jeco.core.algorithm.ga.StaticSimpleGeneticAlgorithmBestWithPopRenovation;
import jeco.core.algorithm.ge.SimpleGrammaticalEvolution_example;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.UniformCrossover;
import jeco.core.operator.crossover.UniformTerminalCrossover;
import jeco.core.operator.initialization.PTC2;
import jeco.core.operator.mutation.BasicMutationVariableList;
import jeco.core.operator.mutation.BasicMutationVariableListAll;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;

public class ProblemDSGE_example extends AbstractProblemDSGE {
	private static final Logger logger = Logger.getLogger(SimpleGrammaticalEvolution_example.class.getName());
	protected ScriptEngine evaluator = null;
	private String[] variables = {"123", "43", "21", "1", "50", "43", "20", "321", "76", "54", "122"};
	private int goal = 254;
	
	public ProblemDSGE_example(String path, int depht, boolean bloatingControl, boolean treeDepth){
		super(path, 1, depht,bloatingControl, treeDepth);
		 ScriptEngineManager mgr = new ScriptEngineManager();
		evaluator = mgr.getEngineByName("graal.js");
	}
	
	public ProblemDSGE_example(String path, int depht, boolean bloatingControl, boolean treeDepth, int InitMax, int InitMinRec, boolean minimumSearch){
		super(path, 1, depht,bloatingControl, treeDepth, InitMax, InitMinRec,minimumSearch);
		 ScriptEngineManager mgr = new ScriptEngineManager();
		evaluator = mgr.getEngineByName("graal.js");
	}

	@Override
	public void evaluate(Solution<VariableList<Integer>> solution, Phenotype phenotype) {

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
	public Problem<VariableList<Integer>> clone() {

		ProblemDSGE_example clone = new ProblemDSGE_example(super.pathToBnf, 5,true ,true);
		return clone;
	}
	
	@Override
	public void evaluate(Solution<VariableList<Integer>> solution) {
		logger.severe("Should not be called");
		
	}
	
	
	public static void main(String[] args) {
        // First create the problem
        

		ProblemDSGE_example problem = new ProblemDSGE_example("test/grammar_example_sge.bnf", 10, true, true, 5, 4, true);
		//ProblemDSGE_example problem = new ProblemDSGE_example("C:\\Users\\Marina\\Documents\\TFG\\SimbolicRegression.bnf", 10, true, true);
		problem.setInitializator(new PTC2(30, 10, 5, problem.reader));

        // Second create the algorithm
        StaticSimpleGeneticAlgorithmBestWithPopRenovation<VariableList<Integer>> algorithm = new StaticSimpleGeneticAlgorithmBestWithPopRenovation<VariableList<Integer>>(problem,100,200,false, new BasicMutationVariableListAll<VariableList<Integer>>(0.3, problem),
                new UniformCrossover<VariableList<Integer>>(0.7,0.25),
                new BinaryTournament<VariableList<Integer>>(new SimpleDominance<>()), 0.1);
        // Run
        algorithm.initialize();
        Solutions<VariableList<Integer>> solutions = algorithm.execute();
		System.out.println("\n\n>>>>> Final solutions: " + solutions.size());
        for (Solution<VariableList<Integer>> solution : solutions) {
			System.out.println("Fitness = (" + solution.getObjectives().get(0) + ") -- Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
        }
  }
}
