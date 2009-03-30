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
	private AnnotatedVertex annotatedVertex;
	
	public VertexNode() {
		// TODO: Bind to a vertex
		//annotatedVertex = v;
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
		rd.name = toString();
		rd.vertex = annotatedVertex;
	}

	public String toString() {
		// TODO This should say something about the real class object
		return "VertexNode";
	}
	
	public AnnotatedVertex GetAnnotatedVertex() { return annotatedVertex; }
}
