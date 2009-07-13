package ec.refactoring;

import java.util.*;

import ec.*;
import ec.gp.*;
import ec.simple.*;
import ec.util.*;

public class RefactorProblem extends GPProblem implements SimpleProblemForm {
	private static final long serialVersionUID = 4425245889654977390L;
	public RefactorData input;
	
	public RefactorProblem clone() {
		RefactorProblem rp = (RefactorProblem) (super.clone());
		rp.input = (RefactorData) (input.clone());
		return rp;
	}
	
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
	    
		if (!ind.evaluated) {	// Don't reevaluate
			((RefactorIndividual)ind).ResetNodeCount();
			((RefactorIndividual)ind).ResetMTNodeCount();
			((GPIndividual)ind).trees[0].child.eval(
					state,threadnum,input,stack,((GPIndividual)ind), this);
		}
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g = 
			((RefactorIndividual)ind).GetGraph();
		
		// Design pattern detection
		ArrayList<String> patternInstances = SourceGraph.GetDetector().DetectPatterns(g);
		patternInstances.removeAll(SourceGraph.GetPatternList());
		int patternsFound = patternInstances.size();
		((RefactorIndividual)ind).SetPatternList(patternInstances);
		Float fitness_value = 0F;
		float QMOOD_value = QMOODEvaluator.EvaluateGraph(g);
		// Fitness computation
		int nodeCountMT = ((RefactorIndividual)ind).GetMTNodeCount();		
		fitness_value = QMOOD_value + (patternsFound > 0 ? 2.0F*Math.abs(QMOOD_value) : 0F);
		// TODO: parameterize the node count penalty
		float nodeCountPenalty = 0.5F*Math.abs(QMOOD_value)*(float)nodeCountMT;
		fitness_value -= nodeCountPenalty;
		SimpleFitness fit = new SimpleFitness();
		fit.setFitness(state, fitness_value, false);
		/*
		if (patternsFound > 0) {
			System.out.println("Patterns found: " + patternsFound);
		}
		System.err.println("Fitness: " + fitness_value);
		*/
		
		ind.fitness = fit;
		
		stack.reset();
		ind.evaluated = true;
	}
    public void setup(final EvolutionState state, final Parameter base)
	{
		// very important, remember this
		super.setup(state,base);
		
		StringFactory.Setup();
		
		input = new RefactorData();
		input.name = "";
	}
}
