package jeco.core.operator.initialization;

import java.util.ArrayList;
import java.util.List;

import jeco.core.algorithm.sge.RecListT;
import jeco.core.util.bnf.BnfReader;
import jeco.core.util.bnf.Production;
import jeco.core.util.bnf.Rule;
import jeco.core.util.bnf.Symbol;
import jeco.core.util.random.RandomGenerator;

/**PTC2 implementation for grammatical structures. It has been slightly modified to use grammatical rules, this affects
 * some of the steps, such as the generation of terminals/non-terminals. In general the resulting individuals are often larger than the set size due to
 * following the grammar structure. It could be paliated if the minimum expansion number to get to a symbol is taken into
 * account but it would require to modify the base algorithm further 
 * TODO Add option to take into account the minimum number of expansions to get to a terminal. 
 * 
 * @author Marina
 *
 */
public class PTC2 extends Initializator{
	private int probD;
	private int depth;
	private int alpha;
	private BnfReader reader;
	
	public PTC2(int probD, int depth, int alpha, BnfReader reader) {
		this.probD = probD;
		this.depth = depth;
		this.alpha = alpha;
		this.reader = reader;
	}
	
	public PTC2(int probD, int depth, BnfReader reader) {
		this.probD = probD;
		this.depth = depth;
		this.alpha = 1;
		this.reader = reader;
	}
	
	/**
	 * Creates a new tree using PTC2 and returns it, each algorithm that makes use of this function should transform the tree to their representation 
	 */
	@Override
	public RecListT<Integer> initialize() {
		//Final tree
		RecListT<Integer> solution = new RecListT<>();
		
		int size = RandomGenerator.nextInt(alpha, probD+1);
		Production expansion;
		int currentsize = 0;		
		//We set the value of the node to the next symbol
		solution.setS(reader.getRules().get(0).getLHS());
		solution.setDepth(0);
		//We create the list of children
		solution.setInteriorList(new ArrayList<RecListT<Integer>>());
		
		List<RecListT<Integer>> unexpandedSymbols = new ArrayList<>();
		
		if(size == 1) {
			Rule ruleSymbol = reader.getRules().get(0);
			int rand_prod = shortestExpansion(ruleSymbol);
			expansion = ruleSymbol.get(rand_prod);
			solution.setValue(rand_prod);
			
			for(Symbol s: expansion) {
				
				if(!s.isTerminal()) {
					RecListT<Integer> n = solution.getnewEmpty();
					n.setS(s);
					n.setDepth(solution.getDepth()+1);
					//We create the list of children empty
					//n.setInteriorList(new ArrayList<RecListT<Integer>>());
					unexpandedSymbols.add(n);
				}
				
				
			}
			
		}else {
			//Choose production for root of tree and add it to genotype
			Rule ruleSymbol = reader.getRules().get(0);
			int rand_prod = generateNonTerminal(ruleSymbol, solution.getDepth(), 0, 0, size);
			expansion = ruleSymbol.get(rand_prod);
			// For each non-terminal symbol of the production add them to the set of un-expanded symbols
			solution.setValue(rand_prod);
			
			for(Symbol s: expansion) {
				
				if(!s.isTerminal()) {
					RecListT<Integer> n = solution.getnewEmpty();
					n.setS(s);
					n.setDepth(solution.getDepth()+1);
					
					
					unexpandedSymbols.add(n);
				}
			}
			
			//While loop
			currentsize = 1;
			
			while(unexpandedSymbols.size() + currentsize < size && unexpandedSymbols.size() != 0) {
				
				int rand =  RandomGenerator.nextInt(unexpandedSymbols.size());
				RecListT<Integer> nextNode = unexpandedSymbols.remove(rand);
				
				ruleSymbol = reader.findRule(nextNode.getS());
				int next_prod;
				//Check depth
				if(nextNode.getDepth() >= depth) {
					next_prod = shortestExpansion(ruleSymbol);
				}else {
					next_prod = generateNonTerminal(ruleSymbol, nextNode.getDepth(), currentsize, unexpandedSymbols.size(), size);
				
				}
				currentsize++;
				
				expansion = ruleSymbol.get(next_prod);
				nextNode.setValue(next_prod);
				
				for(Symbol s: expansion) {
					
					if(!s.isTerminal()) {
						RecListT<Integer> n = nextNode.getnewEmpty();
						n.setS(s);
						n.setDepth(nextNode.getDepth()+1);
						
						//We create the list of children empty
						//n.setInteriorList(new ArrayList<RecListT<Integer>>());
						
						unexpandedSymbols.add(n);
					}
				}
				
				
				
			}
		}
			
		while(unexpandedSymbols.size() != 0) {
			int rand =  RandomGenerator.nextInt(unexpandedSymbols.size());
			RecListT<Integer> nextNode = unexpandedSymbols.remove(rand);
			
			Rule ruleSymbol = reader.findRule(nextNode.getS());

			int next_prod;
			//Finnish all non-terminals with shortest expansion
			next_prod = shortestExpansion(ruleSymbol);
			nextNode.setValue(next_prod);
			
			expansion = ruleSymbol.get(next_prod);
			for(Symbol s: expansion) {
				
				if(!s.isTerminal()) {
					RecListT<Integer> n = nextNode.getnewEmpty();
					n.setS(s);
					n.setDepth(nextNode.getDepth()+1);
					
					unexpandedSymbols.add(n);
				}
			}
			
		}
			
			
		

		return solution;
	}
	
	
	private int generateTerminal(Rule ruleSymbol) {

		int rand_prod;
		
		//We get the productions that are not recursive with the ruleSymbol and put their indexes in a list
		ArrayList<Integer> listProd = new ArrayList<>();
		int index = 0;
		int min = Integer.MAX_VALUE;
		
		//We search for the minimun depth possible
		for(Production p: ruleSymbol) {
			if(p.getMaximumDepth() == 0) {
				listProd.add(index);
			}
			index++;
		}
		
		//Select one of the indexes of the list
		int selec = RandomGenerator.nextInt(listProd.size());
		rand_prod = listProd.get(selec);
		
		return rand_prod;
	}
	
	private int generateNonTerminal(Rule ruleSymbol, int depth, int currentSize, int sizeT, int size) {

		int rand_prod;
		
		//We get the productions that are not recursive with the ruleSymbol and put their indexes in a list
		ArrayList<Integer> listProd = new ArrayList<>();
		int index = 0;
		
		//We search for the minimun depth possible
		for(Production p: ruleSymbol) {
			if((p.getMaximumDepth() > 0) && ((p.getMinimumDepth()+depth) <= this.depth)
					&&  ((p.getMaximumExpansions()*1.0 + currentSize + sizeT) > size)) {
				listProd.add(index);
			}
			index++;
		}
		
		//Select one of the indexes of the list
		int selec;
		if(listProd.size() == 0) {
			rand_prod = RandomGenerator.nextInt(ruleSymbol.size());
		}else {
			selec = RandomGenerator.nextInt(listProd.size());
			rand_prod = listProd.get(selec);
		}
		
		
		
		return rand_prod;
	}
	
	
	private int shortestExpansion(Rule ruleSymbol) {

		int rand_prod;
		
		//We get the productions that are not recursive with the ruleSymbol and put their indexes in a list
		ArrayList<Integer> listProd = new ArrayList<>();
		int index = 0;
		int min = Integer.MAX_VALUE;
		
		//We search for the minimun depth possible
		for(Production p: ruleSymbol) {
			if(p.getMinimumDepth() < min) {
				min = p.getMinimumDepth();
			}
		}
		
		for(Production p: ruleSymbol) {

			//Productions that have the minimun depth to reach a terminal
			if(p.getMinimumDepth() == min) {
				listProd.add(index);
			}
			index++;
		}
		
		//Select one of the indexes of the list
		int selec = RandomGenerator.nextInt(listProd.size());
		rand_prod = listProd.get(selec);
		
		return rand_prod;
	}
	

	
}
