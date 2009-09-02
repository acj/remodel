package ec.refactoring;

import java.util.ArrayList;
import java.util.Random;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.refactoring.AnnotatedEdge.Label;

/**
 * Delegation
 * 
 * This mini-transformation is used to move part of an existing class to
 * a (new) component class and then pass responsibility to that component
 * class.  An instance of the component class is placed in the original
 * (delegating) class as a field.
 * 
 * Child nodes:
 *  - class Owner
 *  
 * @author acj
 */
public class Delegation extends GPNode {

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		((RefactorIndividual)individual).IncrementNodeCount();
		((RefactorIndividual)individual).IncrementMTNodeCount();
		((RefactorIndividual)individual).getMtList().add(this.toString());
		RefactorData rd = (RefactorData)input;
		String thisNodeGraphviz = this.toGraphviz();
		rd.graphvizData += thisNodeGraphviz + " [label=\"" + this.toString() + "\",shape=folder];\n";
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag =
			((RefactorIndividual)individual).GetGraph();
		children[0].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex original_v = ag.getVertex(rd.name);
		rd.graphvizData += thisNodeGraphviz + " -> " + rd.graphvizName + ";\n";
		String componentClassName = original_v.toString() + "Component";
		int name_ndx = 1;
		while (ag.getVertex(componentClassName) != null) {
			if (name_ndx > 10) { System.out.println("Warning (Delegation): looped > 10 times"); }
			componentClassName = original_v.toString() + "Component" + name_ndx;
			++name_ndx;
		}
		AnnotatedVertex component_v = Helpers.createEmptyClass(componentClassName, ag);
		ag.addEdge(original_v, component_v, new AnnotatedEdge(Label.AGGREGATE));
		ag.addEdge(original_v, component_v, new AnnotatedEdge(Label.CALL));
		
		// Move some methods to the component class
		ArrayList<AnnotatedEdge> ownEdges = ag.GetEdges(original_v, Label.OWN);
		// Number of methods to move (if available).  If this many methods are
		// not available, then do not move any.
		final int numMoveVertices = 2;
		if (ownEdges.size() >= numMoveVertices) {
			Random rand = SourceGraph.GetRandom();
			for (int ndx=0; ndx<numMoveVertices; ++ndx) {
				int methodNdx = rand.nextInt(ownEdges.size());
				ag.addEdge(component_v, ownEdges.get(methodNdx).getSinkVertex(), new AnnotatedEdge(Label.OWN));
				ag.removeEdge(ownEdges.get(methodNdx)); // Remove old "OWN" edge
			}
		}
		rd.graphvizName = thisNodeGraphviz;
		rd.name = component_v.toString();
		rd.newVertex = component_v;
	}

	@Override
	public String toString() {
		return "Delegation";
	}
	
	public String toGraphviz() {
		Random r = SourceGraph.GetRandom();
		
		return "node" + Math.abs(r.nextInt()); 
	}
}