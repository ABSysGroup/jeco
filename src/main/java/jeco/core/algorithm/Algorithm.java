package jeco.core.algorithm;

import java.util.Observable;
import jeco.core.problem.Problem;
import jeco.core.problem.Solutions;
import jeco.core.problem.Variable;

/**
 * Abstract class for algorithms
 * @param <V> Variable type
 * @author José L. Risco-Martín
 *
 */
public abstract class Algorithm<V extends Variable<?>> extends Observable {

  /**Problem for algorithm*/
  protected Problem<V> problem = null;
  /**Attribute to stop execution of the algorithm. */
  protected boolean stop = false;
  
  /**
   * Allows to stop execution after finishing the current generation; must be
   * taken into account in children classes.
   */
  public void stopExection() {
      stop = true;}
  

  /**Constructor of algorithm
   * @param problem problem to set to the algorithm
   */
  public Algorithm(Problem<V> problem) {
    this.problem = problem;}
  
  /**Set problem for algorithm
   * @param problem problem to set
   */
  public void setProblem(Problem<V> problem) {
    this.problem = problem;}
  
  
  /**Initializes algorithm
   */
  public abstract void initialize();

  /**Performs one generation of the algorithm
   */
  public abstract void step();
  
  /**Gets population of an algorithm
   * @return a list of solutions
   */
  public Solutions<V> getPopulation() {
	  throw new RuntimeException("Not implemented in specific algorithm type");
  }
  
  /**Executes algorithm
   * @return solutions
   */
  public abstract Solutions<V> execute();
  
}
