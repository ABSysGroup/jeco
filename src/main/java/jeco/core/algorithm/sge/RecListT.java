package jeco.core.algorithm.sge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jeco.core.problem.Variable;
import jeco.core.util.bnf.BnfReader;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;

public class RecListT<T>  extends Variable<T>  {
	private List<RecListT<T>> InteriorList;
	private Map<String, Integer> indexes;
	private Symbol s;
	private int depth;
	
	public List<RecListT<T>> getInteriorList() {
		return InteriorList;
	}

	public void setInteriorList(List<RecListT<T>> interiorList) {
		InteriorList = interiorList;
	}
	
	public void resetIndex() {
		for(RecListT<T> rec: InteriorList) {
			indexes.put(rec.s.toString(), 0);
		}
	}
	
	public int countUsedSymbols(BnfReader r, Symbol s) {
		resetIndex();
		int count = 0;
		
		if(s == null) {
			count++;
		}
		else if(s.toString().equals(this.s.toString())) {
			count++;
		}
		
		Rule rule = r.findRule(this.s);
		Integer i = (Integer) this.value;
		
		for(Symbol sym: rule.get(i)) {
			RecListT<T> next = getnextSymbol(sym);
			if(next != null) {
				count += next.countUsedSymbols(r, s);
			}
		}
		
		resetIndex();
		return count;
	}
	
	/*public int countUsedNodes(BnfReader r) {
		resetIndex();
		int count = 1;

		
		Rule rule = r.findRule(this.s);
		Integer i = (Integer) this.value;
		
		for(Symbol sym: rule.get(i)) {
			RecListT<T> next = getnextSymbol(sym);
			if(next != null) {
				count += next.countUsedNodes(r);
			}
		}
		
		resetIndex();
		return count;
	}*/
	
	public int countSymbols(Symbol s){
		int count = 0;
		
		if(s == null) {
			count++;
		}
		else if(s.toString().equals(this.s.toString())) {
			count++;
		}
		
		for(RecListT<T> r: InteriorList) {
			 count += r.countSymbols(s);
		}
		
		return count;
	}

	/*public RecListT(Symbol s, ArrayList<RecListT<T>> value, T i) {
		super(i);
		this.s = s;
		InteriorList = value;
	}*/
	
	public RecListT() {
		super(null);
		InteriorList = new ArrayList<>();
		indexes = new HashMap<>();
		
	}

	public Symbol getS() {
		return s;
	}

	public void setS(Symbol s) {
		this.s = s;
	}
	
	public RecListT<T> getnewEmpty(){
		RecListT<T> r = new RecListT<>();
		InteriorList.add(r);
		
		return r;
	}
	
	@Override
	public RecListT<T> clone() {
		RecListT<T> clone = new RecListT<T>();
		clone.setValue(super.clone().getValue());
		clone.setS(s.clone());
		List<RecListT<T>> interiorList = new ArrayList<>();
		
		for(RecListT<T> r: this.InteriorList) {
			interiorList.add(r.clone());
		}
		clone.setInteriorList(interiorList);
		clone.resetIndex();
		
		if(clone.equals(this)) {
			throw new RuntimeException("Clone error, equals true");
		}
		
		return clone;
	}
	
	public RecListT<T> getnextSymbol(Symbol s){
		RecListT<T> next = null;
		
		if(this.indexes.containsKey(s.toString())) {
			int pos = this.indexes.get(s.toString());
			int j = 0;
			for(int i = 0; i< this.InteriorList.size(); i++) {
				if(s.toString().equals(this.InteriorList.get(i).s.toString())) {
					if(j == pos) {
						next = this.InteriorList.get(i);
						this.indexes.put(s.toString(), this.indexes.get(s.toString())+1);
						return next;
					}else {
						j++;
					}
				}
				
			}
		}
		
		return next;
	}
	
	public Integer getnextSymbolPos(Symbol s){
		Integer next = null;
		
		if(this.indexes.containsKey(s.toString())) {
			int pos = this.indexes.get(s.toString());
			int j = 0;
			for(int i = 0; i< this.InteriorList.size(); i++) {
				if(s.toString().equals(this.InteriorList.get(i).s.toString())) {
					if(j == pos) {
						next = i;
						this.indexes.put(s.toString(), this.indexes.get(s.toString())+1);
						return next;
					}else {
						j++;
					}
				}
				
			}
		}
		
		return next;
	}
	
	public void addnewSymbol(Symbol s){
		if(this.indexes.containsKey(s.toString())) {
			this.indexes.put(s.toString(), this.indexes.get(s.toString())+1);
		}else {
			this.indexes.put(s.toString(), 1);
		}
	}
	
	//Replaces he first instances of list by other instances, if none exists it adds them without replacing
	public void replaceList(List<RecListT<T>> newList) {
		
		resetIndex();
		for(RecListT<T> newR: newList) {
			
			RecListT<T> old =getnextSymbol(newR.s);
			if(old != null) {
				//System.out.println("Correct crossover");
				int index = this.InteriorList.indexOf(old);
				this.InteriorList.remove(old);
				this.InteriorList.add(index, newR);
			}else {
				this.InteriorList.add(newR);
				addnewSymbol(newR.s);
				
			}
			
		}
		
		resetIndex();
		
		for(RecListT<T> newR: newList) {
			RecListT<T> old =getnextSymbol(newR.s);
			if(old != newR) {
				System.out.println(old+" "+ newR);
				throw new RuntimeException("Error in replacement");
			}
		}
		
		resetIndex();

	}
	
	public void deleteFromList(List<RecListT<T>> oldList) {
		int lenght = this.InteriorList.size();
		//System.out.println("Orig list size"+ lenght);
		for(RecListT<T> oldR : oldList) {
			this.InteriorList.remove(oldR);
		}
		
	}
	
	
	public void deleteFromListInt(List<Integer> oldList) {
		int lenght = this.InteriorList.size();
		List<RecListT<T>> temp = new ArrayList<RecListT<T>>();
		int i = 0;
		for(Integer oldR : oldList) {
			if (i == (int) oldR) {
				temp.add(this.InteriorList.get(i));
			}
			i++;
			
		}
		for(RecListT<T> oldR : temp) {
			this.InteriorList.remove(oldR);
		}
		if(oldList.size() > 0) {
			if(lenght <= this.InteriorList.size()) {
				throw new RuntimeException("Delete incorrect");
			}
		}
	}
	

	public void duplicateInList(List<RecListT<T>> oldList) {
		for(RecListT<T> oldR : oldList) {
			if(this.InteriorList.contains(oldR)) {
				RecListT<T> clone = oldR.clone();
				int index = this.InteriorList.indexOf(oldR);
				this.InteriorList.remove(oldR);
				this.InteriorList.add(index, clone);
			}
		}
	}
	
	
	public void addtoList(List<RecListT<T>> Genes) {
		for(RecListT<T> rec: Genes) {
			this.InteriorList.add(rec);
		}
	}
	
    @SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object right) {
    	RecListT<T> var = (RecListT<T>)right;
        return (var == this);
    }
	
	public boolean containsSymbol(Symbol s) {
		boolean contains = false;
		
		if(this.s.toString().equals(s.toString())) {
			return true;
		}
		
		for(int i = 0; i< this.InteriorList.size(); i++) {
			if(this.InteriorList.get(i).containsSymbol(s)) {
				return true;
			}
		}
		
		return contains;
	}
	
	
	public boolean containsUsedSymbol(BnfReader r, Symbol s) {
		boolean contains = false;
		resetIndex();
		
		if(this.s.toString().equals(s.toString())) {
			return true;
		}
		
		Rule rule = r.findRule(this.s);
		Integer i = (Integer) this.value;
		
		for(Symbol sym: rule.get(i)) {
			RecListT<T> next = getnextSymbol(sym);
			if(next != null) {
				if(next.containsUsedSymbol(r,s)) {
					resetIndex();
					return true;
				}
			}
		}
		
		resetIndex();
		
		return contains;
	}


	
	public void setDepth(int i) {
		// TODO Auto-generated method stub
		this.depth = i;
		
	}
	
	public int getDepth() {
		// TODO Auto-generated method stub
		return this.depth;
		
	}


}
