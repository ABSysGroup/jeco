package jeco.core.operator.initialization;

import jeco.core.algorithm.sge.RecListT;
import jeco.core.util.bnf.BnfReader;
import jeco.core.util.bnf.Symbol;

public abstract class Initializator {
	
	abstract public RecListT<Integer> initialize();
}
