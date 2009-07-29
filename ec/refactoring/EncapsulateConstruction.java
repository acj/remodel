package ec.refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Stack;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.refactoring.AnnotatedEdge.Label;
import ec.util.Parameter;

/**
 * EncapsulateConstruction
 * 
 * This mini-transformation ensures that construction of Product objects is
 * encapsulated inside dedicated, overridable methods in the Creator class.
 * 
 * Child nodes:
 * 	- Class creator
 *  - Class product
 *  - String createProduct (currently built automatically)
 * 
 * @author acj
 *
 */
public class EncapsulateConstruction extends GPNode {
	private static final long serialVersionUID = 8815017364068871436L;

	public void checkConstraints(final EvolutionState state,
					            final int tree,
					            final GPIndividual typicalIndividual,
					            final Parameter individualBase)
	{	
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children.length!=2)
			state.output.error("Incorrect number of children for node " + 
			  toStringForError() + " at " +
			  individualBase);
	}
	
	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		//System.err.println("EncapsulateConstruction()");
		((RefactorIndividual)individual).IncrementNodeCount();
		((RefactorIndividual)individual).IncrementMTNodeCount();
		RefactorData rd = (RefactorData)input;
		String thisNodeGraphviz = this.toString();
		rd.graphvizData += thisNodeGraphviz + " [label=\"" + this.toString() + "\",shape=folder];\n";
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag = 
			((RefactorIndividual)individual).GetGraph();
		children[0].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex creator_v = ag.getVertex(rd.name);
		rd.graphvizData += thisNodeGraphviz + " -> " + rd.graphvizName + ";\n";
		children[1].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex product_v = ag.getVertex(rd.name);
		rd.graphvizData += thisNodeGraphviz + " -> " + rd.graphvizName + ";\n";
		
		Iterator<AnnotatedEdge> edge_it = ag.outgoingEdgesOf(creator_v).iterator();
		AnnotatedEdge e;
		while (edge_it.hasNext()) {
			e = edge_it.next();
			
			if (e.getSinkVertex() == product_v && e.getLabel() == Label.INSTANTIATE) {
				// Remove the old INSTANTIATE edge
				ag.removeEdge(e);
		
				// Build the "createProduct" method
				String create_method_name = "create" + product_v.toString();
				AnnotatedVertex abstract_meth_v =
					Helpers.makeAbstract(creator_v, create_method_name, ag);
				
				// Add an INSTANTIATE edge between the new method and the
				// product that it creates.
				AnnotatedEdge e_new = new AnnotatedEdge(Label.INSTANTIATE);
				ag.addEdge(abstract_meth_v, product_v, e_new);
				
				// The creator now calls the creator method.  Add an
				// edge to represent this.
				ag.addEdge(creator_v, abstract_meth_v, new AnnotatedEdge(Label.CALL));
				
				// We assume here that the fact extraction pipeline removed any
				// duplicate edges.  Therefore, we can bail out once we've found
				// the lone INSTANTIATE edge.
				break;
			}
		}
		
		// We pass up the "creator" class here since we do not create a new
		// class with this mini-transformation.
		rd.graphvizName = thisNodeGraphviz;
		rd.name = creator_v.toString();
		rd.newVertex = creator_v;
	}

	@Override
	public String toString() {
		return "EncapsulateConstruction";
	}
	
	public String toGraphviz() {
		Random r = SourceGraph.GetRandom();
		
		return "node" + Math.abs(r.nextInt()); 
	}
}