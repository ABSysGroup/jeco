package jeco.core.problem;

public abstract class Problem<V extends Variable<?>> {
    //private static final Logger logger = Logger.getLogger(Problem.class.getName());

    public static final double INFINITY = Double.POSITIVE_INFINITY;
    protected int numberOfVariables;
    protected int numberOfObjectives;
    protected double[] lowerBound;
    protected double[] upperBound;

    protected int maxEvaluations;
    protected int numEvaluations;

    public Problem(int numberOfVariables, int numberOfObjectives) {
        this.numberOfVariables = numberOfVariables;
        this.numberOfObjectives = numberOfObjectives;
        this.lowerBound = new double[numberOfVariables];
        this.upperBound = new double[numberOfVariables];
        this.maxEvaluations = Integer.MAX_VALUE;
        resetNumEvaluations();
    }

    public int getNumberOfVariables() {
        return numberOfVariables;
    }

    public int getNumberOfObjectives() {
        return numberOfObjectives;
    }

    public double getLowerBound(int i) {
    	if(i < lowerBound.length) {
    		return lowerBound[i];
    	}else {
    		return lowerBound[lowerBound.length -1];
    	}
    }

    public double getUpperBound(int i) {
    	if(i < upperBound.length) {
    		return upperBound[i];
    	}else {
    		return upperBound[upperBound.length -1];
    	}
    }

    public int getMaxEvaluations() {
        return maxEvaluations;
    }

    public void setMaxEvaluations(int maxEvaluations) {
        this.maxEvaluations = maxEvaluations;
    }

    public int getNumEvaluations() {
        return numEvaluations;
    }

    public final void resetNumEvaluations() {
        numEvaluations = 0;
    }
    
    public void setNumEvaluations(int numEvaluations) {
        this.numEvaluations = numEvaluations;
    }

    /**
     * To be implemented by each problem type, returns an initial set of solution of a given size.
     * 
     * @param size size of set of solutions
     * @return A set of solutions
     */
    public abstract Solutions<V> newRandomSetOfSolutions(int size);

    /**
     * Evaluates a set of solutions
     * @param solutions set of solutions to evaluate
     */
    public void evaluate(Solutions<V> solutions) {
        for (Solution<V> solution : solutions) {
            evaluate(solution);
        }
        
    }
    
    /**
     * To be implemented for each problem, for each genotype (solution) returns a String corresponding
     * to the phenotype of a solution
     * @param solution individual to evaluate
     * @return string of phenotype
     */
    public String phenotypeToString(Solution<V> solution) { return "Not implemented"; }

    public abstract void evaluate(Solution<V> solution);

    @Override
    public abstract Problem<V> clone();

    public boolean reachedMaxEvaluations() {
        return (numEvaluations >= maxEvaluations);
    }



    
}
