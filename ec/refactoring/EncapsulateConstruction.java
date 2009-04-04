package ec.refactoring;

import java.util.Iterator;

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
		/*
		 // This stuff should be checked by the parameters file:
		 if (children[0].toString() != "ClassNode")
			state.output.error("Invalid child node 0 (should be ClassNode)");
		else if (children[1].toString() != "ClassNode")
			state.output.error("Invalid child node 1 (should be ClassNode)");
		 */
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

		if (creator_v.hashCode() != ag.getVertex(creator_v.toString()).hashCode()) {
			System.err.println("EC hashCode mismatch: " + creator_v.hashCode() +
					" / " + ag.getVertex(creator_v.toString()).hashCode());
		}// else {
			//System.err.println("hashCode match");
		//}
		// Build the "createProduct" method
		AnnotatedVertex abstract_meth_v =
			Helpers.makeAbstract(creator_v, create_method_name, ag);
		
		AnnotatedEdge e;
		Iterator<AnnotatedEdge> edge_it = ag.outgoingEdgesOf(creator_v).iterator();
		while (edge_it.hasNext()) {
			e = edge_it.next();
			if (e.getSinkVertex() == product_v && e.getLabel() == Label.INSTANTIATE) {
				ag.removeEdge(e);
				AnnotatedEdge e_new = new AnnotatedEdge(Label.CALL);
				ag.addEdge(creator_v, abstract_meth_v, e_new);
			}
		}
	}

	@Override
	public String toString() {
		return "EncapsulateConstruction";
	}
}
