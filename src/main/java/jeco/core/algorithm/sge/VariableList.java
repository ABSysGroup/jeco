package jeco.core.algorithm.sge;

import java.util.ArrayList;
import java.util.List;

import jeco.core.problem.Variable;

public class VariableList<T> extends Variable<ArrayList<T>>  {

	public VariableList(ArrayList<T> value) {
		super(value);
		// TODO Auto-generated constructor stub
	}
	
	public VariableList() {
		super(new ArrayList<T>());
	}
	
	public void add(T val) {
		this.value.add(val);
	}
	
	public void add( int pos, T val) {
		this.value.add(pos, val);
	}
	
	public void remove( int pos) {
		this.value.remove(pos);
	}
	
	@Override
    public VariableList<T> clone() {
		ArrayList<T> varList = new ArrayList<>();
		for(T var: value) {
			varList.add(var);
		}
        return new VariableList<T>(varList);
    }

	public int size() {
		// TODO Auto-generated method stub
		return value.size();
	}


}
