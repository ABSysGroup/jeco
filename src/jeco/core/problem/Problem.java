package jeco.core.problem;

public abstract class Problem<V extends Variable<?>> {
  //private static final Logger logger = Logger.getLogger(Problem.class.getName());

  public static final double INFINITY = Double.POSITIVE_INFINITY;
  protected int numberOfVariables;
  protected int numberOfObjectives;
  protected double[] lowerBound;
  protected double[] upperBound;

  public Problem(int numberOfVariables, int numberOfObjectives) {
    this.numberOfVariables = numberOfVariables;
    this.numberOfObjectives = numberOfObjectives;
    this.lowerBound = new double[numberOfVariables];
    this.upperBound = new double[numberOfVariables];
  }

  public int getNumberOfVariables() {
    return numberOfVariables;
  }

  public int getNumberOfObjectives() {
    return numberOfObjectives;
  }

  public double getLowerBound(int i) {
    return lowerBound[i];
  }

  public double getUpperBound(int i) {
    return upperBound[i];
  }

  public abstract Solutions<V> newRandomSetOfSolutions(int size);

  public void evaluate(Solutions<V> solutions) {
    for(Solution<V> solution : solutions) {
      evaluate(solution);
    }
  }
  
  public abstract void evaluate(Solution<V> solution);

  @Override
  public abstract Problem<V> clone();
}
