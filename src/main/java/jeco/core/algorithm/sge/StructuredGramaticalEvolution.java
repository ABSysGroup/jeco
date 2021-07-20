package jeco.core.algorithm.sge;

import java.util.Comparator;


import java.util.logging.Logger;

import jeco.core.algorithm.Algorithm;
import jeco.core.algorithm.ga.SimpleGeneticAlgorithm;
import jeco.core.algorithm.moga.NSGAII;
import jeco.core.operator.assigner.CrowdingDistance;
import jeco.core.operator.comparator.SimpleDominance;
import jeco.core.operator.crossover.CrossoverOperator;
import jeco.core.operator.crossover.SinglePointCrossover;
import jeco.core.operator.crossover.UniformCrossover;
import jeco.core.operator.mutation.IntegerFlipMutation;
import jeco.core.operator.mutation.IntegerFlipMutationList;
import jeco.core.operator.mutation.MutationOperator;
import jeco.core.operator.selection.BinaryTournament;
import jeco.core.operator.selection.BinaryTournamentNSGAII;
import jeco.core.operator.selection.SelectionOperator;
import jeco.core.problem.Problem;
import jeco.core.problem.Solution;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;



public class StructuredGramaticalEvolution extends SimpleGeneticAlgorithm<Variable<Integer[]>> {

    public StructuredGramaticalEvolution(Problem<Variable<Integer[]>> problem, int maxPopulationSize, int maxGenerations, double probMutation, double probCrossover) {
        super(problem,
                maxPopulationSize,
                maxGenerations, true,
                new IntegerFlipMutationList<Variable<Integer[]>>(problem, probMutation),
                new UniformCrossover<Variable<Integer[]>>( probCrossover),
                new BinaryTournament<Variable<Integer[]>>(new SimpleDominance<>()));
    }

}

/*public class StructuredGramaticalEvolution  extends Algorithm<Variable<Integer[]>>  {
	
	 public static final Logger logger = Logger.getLogger(NSGAII.class.getName());

	  /////////////////////////////////////////////////////////////////////////
	  protected int maxGenerations;
	  protected int maxPopulationSize;
	  /////////////////////////////////////////////////////////////////////////
	  protected int currentGeneration;
	  protected Comparator<Solution<Variable<Integer[]>>> dominance;;
	  protected Solutions<Variable<Integer[]>> population;
	  public Solutions<Variable<Integer[]>> getPopulation() { return population; }
	  protected MutationOperator<Variable<Integer[]>> mutationOperator;
	  protected CrossoverOperator<Variable<Integer[]>> crossoverOperator;
	  protected SelectionOperator<Variable<Integer[]>> selectionOperator;

	public StructuredGramaticalEvolution(Problem<Variable<Integer[]>> problem) {
		super(problem);
		// TODO Auto-generated constructor stub
	}
	
	public StructuredGramaticalEvolution (Problem<Variable<Integer[]>> problem, int maxPopulationSize, int maxGenerations, double probMutation, double probCrossover) {
		super(problem);
		
		 this.maxPopulationSize = maxPopulationSize;
	     this.maxGenerations = maxGenerations;
	     
	     
	     //this.mutationOperator = new IntegerFlipMutation<Variable<Integer>>(problem, probMutation);
	     //this.crossoverOperator = new SinglePointCrossover<Variable<Integer>>(problem, SinglePointCrossover.DEFAULT_FIXED_CROSSOVER_POINT, probCrossover, SinglePointCrossover.ALLOW_REPETITION);
	     //this.selectionOperator = new BinaryTournamentNSGAII<Variable<Integer>>();
	     
	     initialize();
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
		population = this.problem.newRandomSetOfSolutions(this.maxPopulationSize);
		
		problem.evaluate(population);
	      // Compute crowding distance
		 //Ask
	     //CrowdingDistance<Variable<Integer[]>> assigner = new CrowdingDistance<Variable<Integer[]>>(problem.getNumberOfObjectives());
	     //assigner.execute(population);
	    this.dominance = new SimpleDominance<>();
		this.currentGeneration = 0;
	}

	@Override
	public void step() {
	      currentGeneration++;
	      // Create the offSpring solutionSet
	      if (population.size() < 2) {
	          logger.severe("Generation: " + currentGeneration + ". Population size is less than 2.");
	          return;
	      }

	      Solutions<Variable<Integer[]>> childPop = new Solutions<Variable<Integer[]>>();
	      Solution<Variable<Integer[]>> parent1, parent2;
	      //Crossover to do
	      for (int i = 0; i < (maxPopulationSize / 2); i++) {
	          //obtain parents
	          parent1 = selectionOperator.execute(population).get(0);
	          parent2 = selectionOperator.execute(population).get(0);
	          Solutions<Variable<Integer[]>> offSpring = crossoverOperator.execute(parent1, parent2);
	          for (Solution<Variable<Integer[]>> solution : offSpring) {
	              mutationOperator.execute(solution);
	              childPop.add(solution);
	          }
	      } 
	      //Evalute of the children of the crossover
	      problem.evaluate(childPop);

	      // Create the solutionSet union of solutionSet and offSpring
	      Solutions<Variable<Integer[]>> mixedPop = new Solutions<Variable<Integer[]>>();
	      mixedPop.addAll(population);
	      mixedPop.addAll(childPop);

	      // Reducing the union
	      //population = reduce(mixedPop, maxPopulationSize);
	      logger.fine("Generation " + currentGeneration + "/" + maxGenerations + "\n" + population.toString());
		
	}
	
	public Solutions<Variable<Integer[]>> getCurrentSolution() {
	      population.reduceToNonDominated(dominance);
	      return population;
	  }

	@Override
	public Solutions<Variable<Integer[]>> execute() {
		 int nextPercentageReport = 10;
	      while (currentGeneration < maxGenerations) {
	          step();
	          int percentage = Math.round((currentGeneration * 100) / maxGenerations);
	          if (percentage == nextPercentageReport) {
	              logger.info(percentage + "% performed ...");
	              logger.info("@ # Gen. "+currentGeneration+", objective values:");
	              // Print current population
	              Solutions<Variable<Integer[]>> pop = this.getPopulation();
	              for (Solution<Variable<Integer[]>> s : pop) {
	                  for (int i=0; i<s.getObjectives().size();i++) {
	                      logger.fine(s.getObjective(i)+";");
	                  }
	              }
	              //We show results every 10 generations
	              nextPercentageReport += 10;
	          }
	          
	          // Notify observers about current generation (object can be a map with more data)
	          this.setChanged();
	          this.notifyObservers(currentGeneration);
	      }
	      return this.getCurrentSolution();
	}

}*/
