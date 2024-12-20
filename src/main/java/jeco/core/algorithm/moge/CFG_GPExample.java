package jeco.core.algorithm.moge;

import java.util.logging.Logger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import jeco.core.algorithm.ga.SimpleGeneticAlgorithm;
import jeco.core.algorithm.ga.StaticSimpleGeneticAlgorithmBestWithPopRenovation;
import jeco.core.algorithm.ge.SimpleGrammaticalEvolution_example;
import jeco.core.algorithm.moga.NSGAII;
import jeco.core.algorithm.moge.Phenotype;
import jeco.core.algorithm.sge.NodeTree;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.NodeSubTreeCrossover;
import jeco.core.operator.crossover.SubTreeCrossover;
import jeco.core.operator.crossover.UniformCrossover;
import jeco.core.operator.crossover.UniformTerminalCrossover;
import jeco.core.operator.initialization.PTC2;
import jeco.core.operator.mutation.BasicMutationRecListT;
import jeco.core.operator.mutation.BasicMutationVariableList;
import jeco.core.operator.mutation.BasicMutationVariableListAll;
import jeco.core.operator.mutation.IntegerFlipMutationList;
import jeco.core.operator.mutation.NodeTreeRegenMutation;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.util.bnf.Symbol;

public class CFG_GPExample extends CFG_GP {
	private static final Logger logger = Logger.getLogger(SimpleGrammaticalEvolution_example.class.getName());
	protected ScriptEngine evaluator = null;
	private String[] variables = {"123", "43", "21", "1", "50", "43", "20", "321", "76", "54", "122"};
	private int goal = 254;
	
	public CFG_GPExample(String path, int depht, boolean bloatingControl, boolean treeDepth){
		super(path, 1, depht,bloatingControl, treeDepth);
		 ScriptEngineManager mgr = new ScriptEngineManager();
		evaluator = mgr.getEngineByName("JavaScript");
	}
	
	public CFG_GPExample(String path, int depht, boolean bloatingControl, boolean treeDepth, int InitMax, int InitMinRec, boolean minimumDepthSearch){
		super(path, 1, depht,bloatingControl, treeDepth, InitMax, InitMinRec, minimumDepthSearch);
		 ScriptEngineManager mgr = new ScriptEngineManager();
		evaluator = mgr.getEngineByName("JavaScript");
	}

	@Override
	public void evaluate(Solution<NodeTree> solution, Phenotype phenotype) {

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
	public Problem<NodeTree> clone() {

		CFG_GPExample clone = new CFG_GPExample(super.pathToBnf, 5,true ,true);
		return clone;
	}
	
	@Override
	public void evaluate(Solution<NodeTree> solution) {
		logger.severe("Should not be called");
		
	}
	
	
	public static void main(String[] args) {
        // First create the problem
        
		CFG_GPExample problem = new CFG_GPExample("test\\grammar_example_sge.bnf", 4, true, true,3,2, true);
		problem.setInitializator(new PTC2(30, 10, 5, problem.reader));
		
        // Second create the algorithm
        StaticSimpleGeneticAlgorithmBestWithPopRenovation<NodeTree> algorithm = new StaticSimpleGeneticAlgorithmBestWithPopRenovation<NodeTree>(problem,100,200,false, new NodeTreeRegenMutation<NodeTree>(0.3, problem, true),
                new NodeSubTreeCrossover<NodeTree>(0.7,problem),
                new BinaryTournament<NodeTree>(new SimpleDominance<>()), 0.1);
        // Run
        algorithm.initialize();
        Solutions<NodeTree> solutions = algorithm.execute();
        for (Solution<NodeTree> solution : solutions) {
            logger.info("Fitness = (" + solution.getObjectives().get(0) + ")");
            logger.info("Phenotype = (" + problem.generatePhenotype(solution).toString() + ")");
        }
  }

}
