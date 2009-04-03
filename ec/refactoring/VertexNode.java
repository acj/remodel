package ec.refactoring;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

/**
 * ClassNode
 * 
 * This class acts as a place-holder in the GP's syntax tree for classes in the
 * target software model.
 * 
 * @author acj
 *
 */
public class VertexNode extends GPNode {
	private static final long serialVersionUID = 5121052556529127964L;
	private AnnotatedVertex annotatedVertex = null;
	
	public VertexNode() {
	}
	
	@Override
	public boolean nodeEquals(GPNode node) {
		// won't work for subclasses; in that case you'll need
        // to change this to isAssignableTo(...)
        if (this.getClass() != node.getClass()) { return false; }
        return (((VertexNode)node).GetAnnotatedVertex() == annotatedVertex);
	}

	@Override
	public int nodeHashCode() {
		if (annotatedVertex == null) {
			// FIXME: Ugly, and I don't know if this is causing problems.
			return this.getClass().hashCode() + 1234567890;
		} else {
			return this.getClass().hashCode() + annotatedVertex.hashCode();
		}
	}

	@Override
	public void resetNode(EvolutionState state, int thread) {
		super.resetNode(state, thread);
		// Fetch a new vertex to represent (next time eval is called, that is).
		annotatedVertex = null;
	}

	public void checkConstraints(final EvolutionState state,
            final int tree,
            final GPIndividual typicalIndividual,
            final Parameter individualBase)
	{
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children.length!=0)
			state.output.error("Incorrect number of children for node " + 
			  toStringForError() + " at " +
			  individualBase);
	}
	
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		RefactorData rd = (RefactorData)input;
		if (annotatedVertex == null) {
			annotatedVertex = rd.GetGraph().GetRandomVertex();
		}
		rd.name = "";
		rd.vertex = annotatedVertex;
	}

	public String toString() {
		// TODO This should say something about the real class object
		return "VertexNode";
	}
	
	public AnnotatedVertex GetAnnotatedVertex() {
		return annotatedVertex;
	}
}
