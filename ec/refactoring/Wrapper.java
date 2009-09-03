package ec.refactoring;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.refactoring.AnnotatedEdge.Label;
import ec.util.Parameter;

/**
 * Wrapper
 * 
 * This mini-transformation wraps a class with another class, enabling the
 * wrapper class to handle intelligently any requests that require special
 * attention.  For example, a wrapper class could pick a different
 * implementation depending on the type of calling class.
 * 
 * Child nodes:
 * 	- Interface iface
 * 
 * @author acj
 *
 */
public class Wrapper extends GPNode {

	@Override
	public void checkConstraints(EvolutionState state, int tree,
			GPIndividual typicalIndividual, Parameter individualBase) {
		super.checkConstraints(state, tree, typicalIndividual, individualBase);
	}
	
	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		/*
		 *  Outline:
		 *  	Call createWrapperClass
		 *  	Add the class to the graph.
		 *  	Make the wrapper class instantiate the wrapped class.
		 *  	For all classes that use the wrapped class, change the edges to point to the wrapper class.
		 *  	
		 */
		((RefactorIndividual)individual).IncrementNodeCount();
		((RefactorIndividual)individual).IncrementMTNodeCount();
		((RefactorIndividual)individual).getMtList().add(this.toString());
		RefactorData rd = (RefactorData)input;
		String thisNodeGraphviz = this.toString();
		rd.graphvizData += thisNodeGraphviz + " [label=\"" + this.toString() + "\",shape=folder];\n";
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag =
			((RefactorIndividual)individual).GetGraph();
		children[0].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex iface_v = ag.getVertex(rd.name);
		AnnotatedVertex iface_wrapper = Helpers.createWrapperClass(iface_v, iface_v.toString() + "Wrapper", "wrapped" + iface_v.toString(), ag);
		iface_wrapper.setAddedByEvolution(true);
		rd.graphvizData += thisNodeGraphviz + " -> " + rd.graphvizName + ";\n";
		
		Vector<AnnotatedEdge> edgesToRemove = new Vector<AnnotatedEdge>();
		Iterator<AnnotatedEdge> it_e = ag.edgesOf(iface_v).iterator();
		while (it_e.hasNext()) {
			AnnotatedEdge e = it_e.next();
			// Assumption: iface_v is the sink.  It's an interface, so this
			// should always be true.
			AnnotatedEdge e_new = new AnnotatedEdge(e.getLabel());
			e_new.setAddedByEvolution(true);
			ag.addEdge(e.getSourceVertex(), iface_wrapper, e_new);
			edgesToRemove.add(e);
		}
		// Now remove the edges that we marked
		it_e = edgesToRemove.iterator();
		while (it_e.hasNext()) {
			ag.removeEdge(it_e.next());
		}
		// Add calls/instantiates edges to complete the wrapper
		AnnotatedEdge e_inst = new AnnotatedEdge(Label.INSTANTIATE);
		e_inst.setAddedByEvolution(true);
		ag.addEdge(iface_wrapper, iface_v, e_inst);
		AnnotatedEdge e_call = new AnnotatedEdge(Label.CALL);
		e_call.setAddedByEvolution(true);
		ag.addEdge(iface_wrapper, iface_v, e_call);
		
		rd.graphvizName = thisNodeGraphviz;
		rd.name = iface_wrapper.toString();
		rd.newVertex = iface_wrapper;
	}

	@Override
	public String toString() {
		return "Wrapper";
	}
	
	public String toGraphviz() {
		Random r = SourceGraph.GetRandom();
		
		return "node" + Math.abs(r.nextInt()); 
	}
}
