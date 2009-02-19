package ec.refactor;

import ec.EvolutionState;
import ec.Individual;
import ec.vector.IntegerVectorIndividual;
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
		final int[] genome = (int[])((IntegerVectorIndividual)ind).getGenome();
		RefactorCPU cpu = new RefactorCPU();
		cpu.SetGenome(genome);
		cpu.SimulateGenome(sg.GetCopy());
		SourceGraph sg = SourceGraph.GetInstance();		
		SimpleFitness fit = new SimpleFitness();
		fit.setFitness(state, (float)1.0, false);
		ind.fitness = fit;
	}

}
