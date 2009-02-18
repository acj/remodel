package ec.refactor;

import ec.EvolutionState;
import ec.Individual;
import ec.simple.SimpleProblemForm;
import ec.simple.SimpleFitness;
import ec.Problem;

public class RefactorProblem extends Problem implements SimpleProblemForm {

	public void describe(Individual ind, EvolutionState state,
			int subpopulation, int threadnum, int log, int verbosity) {
		// TODO Auto-generated method stub

	}

	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		SourceGraph sg = SourceGraph.GetInstance();
		System.out.println("Generated graph has " + sg.NumVertices() + " vertices");
		SimpleFitness fit = new SimpleFitness();
		fit.setFitness(state, (float)1.0, false);
		ind.fitness = fit;
	}

}
