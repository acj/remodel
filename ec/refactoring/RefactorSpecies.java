package ec.refactoring;

import java.util.Random;

import ec.*;
import ec.gp.*;
import ec.util.Parameter;

public class RefactorSpecies extends GPSpecies {
	private static final long serialVersionUID = -744374710770142633L;

	public RefactorIndividual newIndividual(EvolutionState state, int thread) 
    {
		// Read in our custom parameters.  Can this be placed somewhere else so
		// that it only runs once?
		if (SourceGraph.RANDOM_SEED == -1) {
			SourceGraph.RANDOM_SEED = state.parameters.getInt(new Parameter("seed.0"), null);
			SourceGraph.SetRandom(new Random(SourceGraph.RANDOM_SEED));
			System.out.println("Seed used for refactoring: " + SourceGraph.RANDOM_SEED);
			// Set up input file parameter
			SourceGraph.SetInputFile(state.parameters.getString(new Parameter("ec.refactoring.inputfile"), null));
			System.out.println("Input file: " + SourceGraph.GetInputFile());
			// Tree size penalty
			SourceGraph.setTreeSizePenalty(state.parameters.getFloat(new Parameter("ec.refactoring.treesizepenalty"), null, 0.0));
			System.out.println("Tree size penalty: " + SourceGraph.getTreeSizePenalty());
			// DP Reward Coefficient
			SourceGraph.setDpRewardCoefficient(state.parameters.getFloat(new Parameter("ec.refactoring.dprewardcoefficient"), null, 0.0));
			System.out.println("DP Reward Coefficient: " + SourceGraph.getDpRewardCoefficient());
			// MT Reward Coefficient
			SourceGraph.setMtRewardCoefficient(state.parameters.getFloat(new Parameter("ec.refactoring.mtrewardcoefficient"), null, 0.0));
			System.out.println("MT Reward Coefficient: " + SourceGraph.getMtRewardCoefficient());
			System.out.println("Prototype individual set up");
		}
    	RefactorIndividual ind = (RefactorIndividual)(super.newIndividual(state, thread));
		ind.SetGraph(SourceGraph.GetClone());
		
		return ind;
    }
}
