package ec.refactoring;

import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;

public class RefactorProblem extends GPProblem implements SimpleProblemForm {
	private static final long serialVersionUID = 4425245889654977390L;
	public RefactorData input;
	
	public Object clone() {
		RefactorProblem rp = (RefactorProblem) (super.clone());
		rp.input = (RefactorData) (input.clone());
		return rp;
	}
	
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		// TODO: Load in the graph
		// TODO: Evaluate fitness based on QMOOD, DPs, etc...
		if (!ind.evaluated) {	// Don't reevaluate
			((GPIndividual)ind).trees[0].child.eval(
					state,threadnum,input,stack,((GPIndividual)ind), this);
		}
	}

}
