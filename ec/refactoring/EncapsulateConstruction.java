package ec.refactoring;

import java.util.HashMap;
import java.util.Iterator;
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
		RefactorData rd = (RefactorData)input;
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag = 
			((RefactorIndividual)individual).GetGraph();
		children[0].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex creator_v = ag.getVertex(rd.name);
		children[1].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex product_v = ag.getVertex(rd.name);
		
		String create_method_name = "create" + product_v.toString();
		
		// Build the "createProduct" method
		AnnotatedVertex abstract_meth_v =
			Helpers.makeAbstract(creator_v, create_method_name, ag);
		
		AnnotatedEdge e;
		Iterator<AnnotatedEdge> edge_it = ag.outgoingEdgesOf(creator_v).iterator();
		HashMap<AnnotatedVertex,AnnotatedVertex> add_edges =
			new HashMap<AnnotatedVertex,AnnotatedVertex>();
		Stack<AnnotatedEdge> delete_edges = new Stack<AnnotatedEdge>();
		// This loop simply does bookkeeping to determine which edges need to
		// be added or removed.  Since they are added/removed from the list
		// that is being iterated over, we can't do the add/remove here.
		while (edge_it.hasNext()) {
			e = edge_it.next();
			if (e.getSinkVertex() == product_v && e.getLabel() == Label.INSTANTIATE) {		
				delete_edges.add(e);
				add_edges.put(creator_v, abstract_meth_v);
			}
		}
		// Now process the edges that we marked for deletion.
		while (!delete_edges.empty()) {
			ag.removeEdge(delete_edges.pop());
		}
		// Now process the edges that we marked for addition.
		Iterator<AnnotatedVertex> vertex_it = add_edges.keySet().iterator();
		AnnotatedEdge e_new;
		while (vertex_it.hasNext()) {
			e_new = new AnnotatedEdge(Label.CALL);
			ag.addEdge(creator_v, add_edges.get(creator_v), e_new); //abstract_meth_v
		}
		
		// We pass up the "creator" class here since we do not create a new
		// class with this mini-transformation.
		rd.name = creator_v.toString();
		rd.newVertex = creator_v;
	}

	@Override
	public String toString() {
		return "EncapsulateConstruction";
	}
}