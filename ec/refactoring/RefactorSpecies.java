package ec.refactoring;

import ec.*;
import ec.gp.*;
import ec.util.Parameter;

public class RefactorSpecies extends GPSpecies {
	private static final long serialVersionUID = -744374710770142633L;

	public RefactorIndividual newIndividual(EvolutionState state, int thread) 
    {
		// Set the random seed.  Can this be placed somewhere else so that it
		// only runs once?
		SourceGraph.RANDOM_SEED = state.parameters.getInt(new Parameter("seed.0"), null);
    	RefactorIndividual ind = (RefactorIndividual)(super.newIndividual(state, thread));
		ind.SetGraph(SourceGraph.GetClone());
		return ind;
    }
}
