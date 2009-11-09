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
			((RefactorIndividual)ind).getMtList().clear();
			((GPIndividual)ind).trees[0].child.eval(
					state,threadnum,input,stack,((GPIndividual)ind), this);
		}

		// Optimization: If this individual contains 0 minitransformation
		// nodes, then it has no effect on the original graph.  Thus, we
		// can save some time by assigning it the (adjusted) fitness of the
		// original graph.
		Float fitness_value = 0F;
		SimpleFitness fit = new SimpleFitness();
		RefactorIndividual rInd = (RefactorIndividual)ind;
		if (rInd.GetMTNodeCount() == 0) {
			// Note -1.0F coefficient
			fitness_value = -1.0F*ComputeFitness(SourceGraph.getOriginalGraphQMOOD().qmood,
					SourceGraph.getOriginalGraphPatterns(),
					(float)rInd.GetMTNodeCount());
			ArrayList<String> patternInstances = new ArrayList<String>();
			rInd.SetPatternList(patternInstances);
		} else {
			// Otherwise, evaluate the individual as normal
			AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g = rInd.GetGraph();
			// Design pattern detection
			ArrayList<String> patternInstances = SourceGraph.GetDetector().DetectPatterns(g);
			patternInstances.removeAll(SourceGraph.GetPatternList());
			int patternsFound = patternInstances.size();
			rInd.SetPatternList(patternInstances);
			StatDataPoint stat = QMOODEvaluator.EvaluateGraph(g);
			// Record this individual's QMOOD value and its DP instances
			stat.dpCount = patternsFound;
			SourceGraph.addStatMapEntry(stat);
			// Fitness computation
			fitness_value = ComputeFitness(stat.qmood, patternsFound, (float)rInd.GetMTNodeCount());
			// Look for MT sequences that should be rewarded
			fitness_value += SourceGraph.getMtRewardCoefficient()*countMatchingSubsequences(rInd.getMtList());
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
    	float fitness_value = QMOOD + (numPatterns > 0 ? SourceGraph.getDpRewardCoefficient()*Math.abs(QMOOD) : 0F);
    	float nodeCountPenalty = SourceGraph.getTreeSizePenalty()*Math.abs(QMOOD)*(float)nodeCountMT;
		fitness_value -= nodeCountPenalty;
    	return fitness_value;
    }
    private float countMatchingSubsequences(ArrayList<String> seq) {
    	float matchBonus = 0.0F;
    	String seqStr = seq.toString();
    	// Adapter, Proxy
    	if (seqStr.matches("\\[.*Wrapper.*Abstraction.*AbstractAccess.*\\]")) {
    		matchBonus += 1.0F;
    	}
    	// Bridge
    	if (seqStr.matches("\\[.*Wrapper.*\\]")) {
    		matchBonus += 1.0F;
    	}
    	// Composite
    	if (seqStr.matches("\\[.*Abstraction.*AbstractAccess.*\\]")) {
    		matchBonus += 1.0F;
    	}
    	// Abstract Factory
    	if (seqStr.matches("\\[.*Abstraction.*AbstractAccess.*EncapsulateConstruction.*\\]")) {
    		matchBonus += 1.0F;
    	}
    	// Decorator
    	if (seqStr.matches("\\[.*Abstraction.*AbstractAccess.*Wrapper.*\\]")) {
    		matchBonus += 1.0F;
    	}
    	return matchBonus;
    }
}
