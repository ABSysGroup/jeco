package jeco.core.util;

public class Pair<T,Z> {
	public T a;
	public Z b;
	
	public Pair() {
		
	}
	
	public Pair(T a, Z b){
		this.a = a;
		this.b = b;
	}

	public T getA() {
		return a;
	}

	public void setA(T a) {
		this.a = a;
	}

	public Z getB() {
		return b;
	}

	public void setB(Z b) {
		this.b = b;
	}
	
	

}
