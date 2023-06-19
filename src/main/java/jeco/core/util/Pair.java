package jeco.core.util;

import java.util.Objects;

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

	@Override
	public int hashCode() {
		return Objects.hash(a, b);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		return Objects.equals(a, other.a) && Objects.equals(b, other.b);
	}
	
	

}
