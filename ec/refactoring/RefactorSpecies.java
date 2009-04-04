package ec.refactoring;

import ec.*;
import ec.gp.*;

public class RefactorSpecies extends GPSpecies {
	private static final long serialVersionUID = -744374710770142633L;

	public RefactorIndividual newIndividual(EvolutionState state, int thread) 
    {
    	RefactorIndividual ind = (RefactorIndividual)(super.newIndividual(state, thread));
		ind.SetGraph(SourceGraph.GetInstance().clone());
		return ind;
    }
}
