package jeco.core.util.bnf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BnfReaderSge extends BnfReader {
	
	  /* For structured gramatical evolution 
     * 
     * The code might produce lists that are longer than needed if the instructions in the bnf produce more of a certain symbol down the line
     * For example if we have
     * <line> ::= <var> <var> | <z> <var>
     * <z> ::= <var> <var>
     * 
     *  the correct answer should be to count <var> as 1 instante <z> as 1 instance and then <z> will multiply the <var> by 2 and add the 1
     *  for line, what  acctually happens is that the Map of references takes the biggest value for <var> and for <z> but they don't look into
     *  either so when we calculate the max references for each variable we will count 2 for <var> plus 1 <z> plus 2 <var> that come from the recursive
     *  call in <z> with has been already calculated (and it has it's own multiplication number) so in total after calculating the references in line
     *  we will get 4 <var> and 1 <z> which would be incorrect since the actual maximum for <var> is 3
     *  
     *  This won't affect currently in the code but must be fixed later on.
     *   
     */
    public Map<String, Map<String ,Integer>> count_references() {

    	Map<String, Map<String ,Integer>> count_references = new HashMap<>();
  	
    	for(Rule r : this.rules) {
    		Map<String, Integer> temp = new HashMap<>();
    		for(Production p: r) {
    			Map<String, Integer> count = new HashMap<>();
    			for(Symbol s: p) {
    				if(!s.isTerminal()) {
    					if(!count.containsKey(s.symbolString)) {
    						count.put(s.symbolString, 0);
    					}
    					
    					count.put(s.symbolString, count.get(s.symbolString) +1);
    				}
    				
    				for(Map.Entry<String, Integer> entry : count.entrySet()) {
    					if(!temp.containsKey(entry.getKey())) {
    						temp.put(entry.getKey(), 0);
    					}
    					
    					if(temp.get(entry.getKey()) < entry.getValue()) {
    						temp.put(entry.getKey(), entry.getValue());
    					}
    				}
    			}
    			
    			
    		}
    		count_references.put(r.lhs.symbolString, temp);
    	}
    	
    	return count_references;
    }
    
    public Map<String, Integer> find_references_start(){
    	return find_references(this.rules.get(0), this.count_references(), 1);
    }
    
    public Map<String, Integer> find_references(Rule r, Map<String, Map<String, Integer>> references, int mult) {
    	
    	Map<String, Integer> references_rule = references.get(r.lhs.symbolString);
    	Map<String, Integer> this_references = new HashMap<>();
    	
    	for(Map.Entry<String, Integer> entry : references_rule.entrySet()) {
    				
    		Map<String, Integer> recursive_call_ref = find_references(this.findRule(entry.getKey()), references, entry.getValue());
    		
    		for(Map.Entry<String, Integer> entry_2 : recursive_call_ref.entrySet()) {
    			if(!this_references.containsKey(entry_2.getKey())) {
    				this_references.put(entry_2.getKey(), 0);
    			}
    			
    			this_references.put(entry_2.getKey(), this_references.get(entry_2.getKey())+ entry_2.getValue() );
    		}
    		
    	}
    	this_references.put(r.lhs.symbolString, 1);
    	
    	for(Map.Entry<String, Integer> entry : this_references.entrySet()) {
    		this_references.put(entry.getKey(), entry.getValue()*mult);
    	}
    	
    	
    	return this_references;
    }
    
    public Map<String, Integer> number_of_options(){
    	Map<String, Integer> options = new HashMap<>();
    	
    	for(Rule r: this.rules) {
    		options.put(r.lhs.symbolString, r.size());
    	}
    	
    	return options;
    }

    public static void main(String[] args) {
        BnfReaderSge bnfReader = new BnfReaderSge();
        bnfReader.load("test/grammar.bnf");
        for (Rule rule : bnfReader.rules) {
            System.out.println(rule.toString());
            System.out.println(rule.lhs.toString());
			System.out.println(bnfReader.isRecursive(new ArrayList<Rule>(), rule));
        
        }
        bnfReader.find_references_start();
        
        
    }

}
