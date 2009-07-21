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

		// Optimization: If this individual contains 0 minitransformation
		// nodes, then it has no effect on the original graph.  Thus, we
		// can save some time by assigning it the same fitness as the
		// original graph.
		Float fitness_value = 0F;
		SimpleFitness fit = new SimpleFitness();
		if (((RefactorIndividual)ind).GetMTNodeCount() == 0) {
			// Note -1.0F coefficient
			fitness_value = -1.0F*ComputeFitness(SourceGraph.getOriginalGraphQMOOD(),
					SourceGraph.getOriginalGraphPatterns(),
					(float)((RefactorIndividual)ind).GetMTNodeCount());
			ArrayList<String> patternInstances = new ArrayList<String>();
			((RefactorIndividual)ind).SetPatternList(patternInstances);
		} else {
			// Otherwise, evaluate the individual as normal
			AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g = 
				((RefactorIndividual)ind).GetGraph();
			// Design pattern detection
			ArrayList<String> patternInstances = SourceGraph.GetDetector().DetectPatterns(g);
			patternInstances.removeAll(SourceGraph.GetPatternList());
			int patternsFound = patternInstances.size();
			((RefactorIndividual)ind).SetPatternList(patternInstances);
			float QMOOD_value = QMOODEvaluator.EvaluateGraph(g);
			// Fitness computation
			fitness_value = ComputeFitness(QMOOD_value, patternsFound, (float)((RefactorIndividual)ind).GetMTNodeCount());
		}
		fit.setFitness(state, fitness_value, false);
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
    private float ComputeFitness(float QMOOD, int numPatterns, float nodeCountMT) {
    	float fitness_value = QMOOD + (numPatterns > 0 ? 2.0F*Math.abs(QMOOD) : 0F);
    	float nodeCountPenalty = SourceGraph.getTreeSizePenalty()*Math.abs(QMOOD)*(float)nodeCountMT;
		fitness_value -= nodeCountPenalty;
    	return fitness_value;
    }
}
