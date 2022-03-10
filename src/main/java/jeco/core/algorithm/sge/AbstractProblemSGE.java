package jeco.core.algorithm.sge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeco.core.problem.Variable;
import jeco.core.util.bnf.BnfReaderSge;

public abstract class AbstractProblemSGE<T extends Variable<?>> extends AbstractGECommon<T> {
	
	protected String pathToBnf;
	protected ArrayList<Integer> maxDerivations;
	protected BnfReaderSge reader;
	protected ArrayList<String> orderSymbols;
	protected ArrayList<Integer> terminals;
	protected ArrayList<ArrayList<Integer>> Non_tToTerminals;
	
	public AbstractProblemSGE(String pathToBnf, int numberOfVariables, int numberOfObjectives) {
		super(numberOfVariables, numberOfObjectives);
		this.pathToBnf = pathToBnf;
		this.reader = new BnfReaderSge();
		terminals = new ArrayList<Integer>();
		this.Non_tToTerminals = new ArrayList<ArrayList<Integer>>();
		
		// TODO Auto-generated constructor stub
	}
	
	public abstract void initialize();
	
	public ArrayList<Integer> getIndexesTerminals(){
		return terminals;
	}
	
	public ArrayList<ArrayList<Integer>> getNextProd(){
		return Non_tToTerminals;
	}
	
}
