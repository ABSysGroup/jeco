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
import jeco.core.operator.crossover.RecListTCrossover;
import jeco.core.operator.crossover.SubTreeCrossover;
import jeco.core.operator.crossover.UniformCrossover;
import jeco.core.operator.crossover.UniformTerminalCrossover;
import jeco.core.operator.initialization.PTC2;
import jeco.core.operator.mutation.BasicMutationRecListT;
import jeco.core.operator.mutation.BasicMutationVariableList;
import jeco.core.operator.mutation.BasicMutationVariableListAll;
import jeco.core.operator.mutation.IntegerFlipMutationList;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;

public class ProblemTreeGEExample extends AbstractProblemTreeGE {
	private static final Logger logger = Logger.getLogger(SimpleGrammaticalEvolution_example.class.getName());
	protected ScriptEngine evaluator = null;
	private String[] variables = {"123", "43", "21", "1", "50", "43", "20", "321", "76", "54", "122"};
	private int goal = 126;
	
	public ProblemTreeGEExample(String path, int depht, boolean bloatingControl, boolean treeDepth){
		super(path, 1, depht,bloatingControl, treeDepth, depht+2);
		 ScriptEngineManager mgr = new ScriptEngineManager();
		evaluator = mgr.getEngineByName("JavaScript");
	}
	
	public ProblemTreeGEExample(String path, int depht, boolean bloatingControl, boolean treeDepth, int InitMax, int InitMinRec){
		super(path, 1, depht,bloatingControl, treeDepth, InitMax, InitMinRec, depht+2);
		 ScriptEngineManager mgr = new ScriptEngineManager();
		evaluator = mgr.getEngineByName("JavaScript");
	}

	@Override
	public void evaluate(Solution<RecListT<Integer>> solution, Phenotype phenotype) {

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
	public Problem<RecListT<Integer>> clone() {

		ProblemTreeGEExample clone = new ProblemTreeGEExample(super.pathToBnf, 5,true ,true);
		return clone;
	}
	
	@Override
	public void evaluate(Solution<RecListT<Integer>> solution) {
		logger.severe("Should not be called");
		
	}
	
	
	public static void main(String[] args) {
        // First create the problem
        
		//C:\\Users\\Marina\\Documents\\T-GE-NEN\\Vlad-4\\Grammar2.bnf
		//"test\\grammar_example.bnf"
		ProblemTreeGEExample problem = new ProblemTreeGEExample("C:\\Users\\Marina\\Documents\\T-GE-NEN\\Vlad-4\\Grammar2.bnf", 4, true, false,3,2);
		problem.setInitializator(new PTC2(100, 10, 1, problem.reader));
		
        // Second create the algorithm
        StaticSimpleGeneticAlgorithmBestWithPopRenovation<RecListT<Integer>> algorithm = new StaticSimpleGeneticAlgorithmBestWithPopRenovation<RecListT<Integer>>(problem,100,200,false, new BasicMutationRecListT<RecListT<Integer>>(0.3, problem, false, true),
                new RecListTCrossover<RecListT<Integer>>(0.7,problem, false, true, false),
                new BinaryTournament<RecListT<Integer>>(new SimpleDominance<>()), 0.1);
        // Run
        algorithm.initialize();
        Solutions<RecListT<Integer>> solutions = algorithm.execute();
        for (Solution<RecListT<Integer>> solution : solutions) {
            logger.info("Fitness = (" + solution.getObjectives().get(0) + ")");
            logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
        }
  }

}
