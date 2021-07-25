package jeco.core.algorithm.sge;

import jeco.core.problem.Variable;

public class VariableArray<T> extends Variable<T[]> {

	public VariableArray(T[] value) {
		super(value);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public VariableArray<T> clone() {
        return new VariableArray<>( value.clone());
    }

}
