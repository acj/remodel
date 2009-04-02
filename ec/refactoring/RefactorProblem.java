package ec.refactoring;

import ec.*;
import ec.app.tutorial4.DoubleData;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;
import ec.util.Parameter;

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
    public void setup(final EvolutionState state, final Parameter base)
	{
    	System.out.println("SETUP!");
		// very important, remember this
		super.setup(state,base);
		
		StringFactory.Setup();
		SourceGraph.GetInstance();
		
		input = new RefactorData();
		input.name = "";
		input.vertex = null;
	}
}
