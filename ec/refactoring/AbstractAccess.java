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
 * AbstractAccess
 * 
 * This mini-transformation decouples a Creator class from a Product
 * class.  The only knowledge that a Creator class can have about the
 * Product it creates is through an interface.
 * 
 * Child nodes:
 *  - Class context
 *  - Class concrete
 *  - Interface inf
 *  - SetOfString skipMethods (currently unused)
 * @author acj
 *
 */
public class AbstractAccess extends GPNode {
	private static final long serialVersionUID = 2170224585003394586L;
	
	public void checkConstraints(final EvolutionState state,
            final int tree,
            final GPIndividual typicalIndividual,
            final Parameter individualBase)
	{
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children[0].toString() != "ClassNode")
			state.output.error("Invalid child node 0 (should be ClassNode)");
		else if (children[1].toString() != "ClassNode")
			state.output.error("Invalid child node 1 (should be ClassNode)");
		else if (children[2].toString() != "ClassNode")
			state.output.error("Invalid child node 2 (should be ClassNode)");
		if (children.length!=3)
		state.output.error("Incorrect number of children for node " + 
		          toStringForError() + " at " +
		          individualBase);
	}
    
	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		// For each "uses" relationship between the context and the concrete
		// class, replace this relationship with an "implements" link to
		// an interface that mirrors the concrete class.
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag = 
			SourceGraph.GetCurrentClone();
		RefactorData rd = (RefactorData)input;
		children[0].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex context_v = rd.vertex;
		children[1].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex concrete_v = rd.vertex;
		children[2].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex iface_v = rd.vertex;
		
		AnnotatedEdge e;
		Iterator<AnnotatedEdge> edge_it = ag.outgoingEdgesOf(context_v).iterator();
		while (edge_it.hasNext()) {
			e = edge_it.next();
			if (e.getSinkVertex() == concrete_v && e.getLabel() == Label.REFERENCE) {
				ag.removeEdge(e);
				AnnotatedEdge e_new = new AnnotatedEdge(Label.REFERENCE);
				ag.addEdge(context_v, iface_v, e_new);
			}
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "AbstractAccess";
	}

}
