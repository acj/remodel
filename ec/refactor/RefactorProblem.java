package ec.refactor;

import java.util.Iterator;
import java.util.Set;

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
		AnnotatedGraph<SourceVertex, SourceEdge> g = SourceGraph.GetInstance().clone();
		cpu.SetGenome(genome);
		cpu.SimulateGenome(g);
		SimpleFitness fit = new SimpleFitness();
		Float fitness_value = 0F;
		fitness_value = 100 - g.edgeSet().size() - (float)g.getSize();
		fit.setFitness(state, fitness_value, false);
		ind.fitness = fit;
		
		//g.clear();
		g = null;
		cpu = null;
	}

	private float DesignSizeInClasses(AnnotatedGraph<SourceVertex, SourceEdge> g) {
		Set<SourceVertex> vertices = g.vertexSet();
		Iterator<SourceVertex> it = vertices.iterator();
		int class_count = 0;
		SourceVertex v;
		while (it.hasNext()) {
			v = it.next();
			if (v.getType() == SourceVertex.VertexType.CLASS) {
				++class_count;
			}
		}
		return (float)class_count;
	}
	private float NumberOfHierarchies(AnnotatedGraph<SourceVertex, SourceEdge> g) {
		return 0.0F;
	}
	private float DataAccessMetric(AnnotatedGraph<SourceVertex, SourceEdge> g) {
		return 0.0F;
	}
	private float DirectClassCoupling(AnnotatedGraph<SourceVertex, SourceEdge> g) {
		return 0.0F;
	}
	private float CohesionAmongClassMethods(AnnotatedGraph<SourceVertex, SourceEdge> g) {
		return 0.0F;
	}
	private float MeasureOfAggregation(AnnotatedGraph<SourceVertex, SourceEdge> g) {
		return 0.0F;
	}
	private float MeasureOfFunctionalAbstraction(AnnotatedGraph<SourceVertex, SourceEdge> g) {
		return 0.0F;
	}
	private float NumberOfPolymorphicMethods(AnnotatedGraph<SourceVertex, SourceEdge> g) {
		return 0.0F;
	}
	private float ClassInterfaceSize(AnnotatedGraph<SourceVertex, SourceEdge> g) {
		return 0.0F;
	}
	private float NumberOfMethods(AnnotatedGraph<SourceVertex, SourceEdge> g) {
		return 0.0F;
	}
}
