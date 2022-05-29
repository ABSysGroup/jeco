package jeco.core.util.bnf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

public class BnfReaderSge extends BnfReader {
	
	/**
	 * Transform a recursive grammar to a non-recursive grammar with depth max_depth
	 * @param pathToBnfFile
	 * @param max_depth
	 * @return
	 */
	 public boolean loadSGE(String pathToBnfFile, int max_depth) {

		 boolean load = super.load(pathToBnfFile);
		 ArrayList<Rule> newRules = new ArrayList<>();
		  
		  //Transform the grammar to take away recursion
		  for(Rule r: this.rules) {
			  //If the rule is recursive
			  if(r.recursive) {
				  
				  Rule temp = r; 
				  Rule extra = r.clone();
				  Symbol ruleLhs = temp.getLHS();
				  
				 //generate non-recursive rules up to max_depth
				  for(int i = 0; i < max_depth; i++) {
					  
					  String newSymbol = null;
					  for(Production p: temp) {
						  for(Symbol s: p) {
							  if(s.equals(ruleLhs)) {
								  newSymbol = s.symbolString.substring(0, s.symbolString.length()-1) + i + ">";
								  s.symbolString = s.symbolString.substring(0, s.symbolString.length()-1) + i + ">";
							  }
						  }
						  
					  }
					  
					  temp = extra.clone();
					  temp.lhs.symbolString = newSymbol;
					  newRules.add(temp);
				  }
				  
				  Rule temp2 = new Rule();
				  temp2.lhs = temp.lhs;
				  
				  boolean enter = false;
				  for(int j = 0; j < temp.size(); j++) {
					  enter = false;
					  for(int k = 0 ; k <temp.get(j).size(); k++) {
						  if(temp.get(j).get(k).equals(ruleLhs)) {
							  enter = true;
						
						  }
						
					  }
					  if(!enter) {
						  temp2.add(temp.get(j));
					  }
					
				  }
				  
				  newRules.remove(temp);
				  newRules.add(temp2);
				  
			  }
		  }
		
		  
		  for(Rule r : newRules) {
			  //If max_depth is set to 0 we have to eliminate the original rule before inserting the new one without recursion
			  if(max_depth == 0) {
				  Rule toDelete= null;
				  for(Rule old: this.rules) {
					  if(r.lhs == old.lhs) {
						  toDelete = old;
					  }
				  }
				  
				  this.rules.remove(toDelete);
			  }
			  
			  this.rules.add(r);
		  }
		  
		 //Update the recursion fields
		 this.updateRuleFields();
		 return load;
	 }
    
	 /**
	  * Counts the amount of references to a Rule, must be performed after recursion has been deleted from the grammar.
	  * Returns a Map with each Rule as a String and an Integer with the amount of references
	  * @param rule
	  * @return
	  */
    public Map<String, Integer> count_references(Rule r) {
    	Map<String, Integer> max_ref = new HashMap<String, Integer>();
    	
    	//We introduce a reference to the rule r as 1 from the outside
    	max_ref.put(r.lhs.symbolString, 1);
    	
    	//For its productions we have to count the references of each symbol and get the highest value
    	for(Production p: r) {
    		Map<String, Integer> temp = new HashMap<String, Integer>();
    		for(Symbol s: p) {
    			
    			if(!s.isTerminal()) {
    				//We call  count_references for the non-terminal symbol s into temp2
	    			Map<String, Integer> temp2 = count_references(this.findRule(s));
	    			
	    			//We add into the reference count all instances for a certain production
	    			for(Entry<String, Integer> entry: temp2.entrySet()) {
	    				if(temp.containsKey(entry.getKey())) {
	    					temp.put(entry.getKey(), entry.getValue() + temp.get(entry.getKey()));
	    				}else {
		    				temp.put(entry.getKey(), entry.getValue());
		    			}
	    			}
	    			
    			}
    			
    		}
    		
    		//For each entry of a production we must keep the highest value into max_ref from itself and temp
			for(Entry<String, Integer> entry: temp.entrySet()) {
				if(max_ref.containsKey(entry.getKey())) {
					if(max_ref.get(entry.getKey()) < entry.getValue()) {
						max_ref.put(entry.getKey(), entry.getValue());
					}
				}else {
					max_ref.put(entry.getKey(), entry.getValue());
    			}
			}
    	}
    	
    	return max_ref;
    }
    
    
    public Map<String, Integer> find_references_start(){
    	return count_references(this.rules.get(0));
    }
    
    
    public Map<String, Integer> number_of_options(){
    	Map<String, Integer> options = new HashMap<>();
    	
    	for(Rule r: this.rules) {
    		options.put(r.lhs.symbolString, r.size());
    	}
    	
    	return options;
    }
    
    /**
     * Get a list with only the terminal productions of a grammar
     * @return
     */
    public List<String> getTerminalProductions(){
    	List<String> terminals = new ArrayList<>();
    	
    	for(Rule r: this.rules) {
    		boolean terminal = true;
    		for(Production p: r) {
    			for(Symbol s: p) {
    				if(!s.isTerminal()) {
    					terminal = false;
    				}
    			}
    		}
    		
    		if(terminal) {
    			terminals.add(r.lhs.symbolString);
    		}
    	}
    	
    	return terminals;
    }
    
    /**
     * Returns a Map with each Rule as a String related to list of the symbols that said Rule can produce
     * @return
     */
    public Map<String, List<String>> getSubsequentProductions(){
    	Map<String, List<String>> nextSymbols= new HashMap<>();
    	
    	for(Rule r: this.rules) {
    		nextSymbols.put(r.lhs.symbolString, new ArrayList<>());
    		for(Production p: r) {
    			for(Symbol s: p) {
    				if(!nextSymbols.get(r.lhs.symbolString).contains(s.symbolString)) {
    					nextSymbols.get(r.lhs.symbolString).add(s.symbolString);
    				}
    			}
    		}
    		
    	}
    	
    	return nextSymbols;
    }

    public static void main(String[] args) {
        BnfReaderSge bnfReader = new BnfReaderSge();
        bnfReader.loadSGE("test/grammar.bnf", 4);

        for (Rule rule : bnfReader.rules) {
        	System.out.println("Rule recursive: "+ rule.recursive);
            System.out.println(rule.toString());
            System.out.println(rule.lhs.toString());
			System.out.println(bnfReader.isRecursive(new ArrayList<Rule>(), rule));
			for(Production p: rule) {
				System.out.println(p.toString() + " " + p.recursive);
			}
        
        }
        Map<String, Integer> ref = bnfReader.find_references_start();
        int max_length= 0;
        for(Entry<String, Integer> entry: ref.entrySet()) {
        	System.out.println("Rule: "+ entry.getKey() + " references:" + entry.getValue());
        	max_length += entry.getValue();
        }
        
        System.out.println(max_length);
        
    }

}
