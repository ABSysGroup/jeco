package jeco.core.algorithm.sge;

import java.util.ArrayList;
import java.util.List;

import jeco.core.problem.Variable;
import jeco.core.util.bnf.Symbol;

public class NodeTree extends Variable<Symbol> {
	//Symbol s;
	
	public NodeTree(Symbol value) {
		super(value);
		//this.s = value;
	}
	
	public NodeTree() {
		super(null);
	}
	
	List<NodeTree> children;
	int depth;
	
	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	/*public Symbol getS() {
		return s;
	}
	public void setS(Symbol s) {
		this.s = s;
	}*/
	
	public List<NodeTree> getChildren() {
		return children;
	}
	public void setChildren(List<NodeTree> children) {
		this.children = children;
	}

	public NodeTree getnewEmpty() {
		NodeTree r = new NodeTree();
		children.add(r);
		return r;
	}
	
	public boolean containsSymbol(Symbol s) {
		boolean contains = false;
		
		if(this.value.toString().equals(s.toString())) {
			return true;
		}
		
		for(int i = 0; i< this.children.size(); i++) {
			if(this.children.get(i).containsSymbol(s)) {
				return true;
			}
		}
		
		return contains;
	}
	
	public int countSymbols(Symbol s){
		int count = 0;
		if(s.toString().equals(this.value.toString())) {
			count++;
		}
		
		for(NodeTree r: children) {
			 count += r.countSymbols(s);
		}
		
		return count;
	}
	
	public int countNTSymbols(){
		int count = 0;
		if(!value.isTerminal()) {
			count++;
		}
		
		for(NodeTree r: children) {
			 count += r.countNTSymbols();
		}
		
		return count;
	}
	
	@Override
	public NodeTree clone() {
		NodeTree clone = new NodeTree();
		clone.setValue(super.clone().getValue());
		List<NodeTree> interiorList = new ArrayList<>();
		
		for(NodeTree r: this.children) {
			interiorList.add(r.clone());
		}
		clone.setChildren(interiorList);
		
		return clone;
	}
	
	

}
