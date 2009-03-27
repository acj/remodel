package ec.refactoring;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

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
	private AnnotatedVertex annotatedVertex;
	
	public VertexNode(AnnotatedVertex v) {
		annotatedVertex = v;
	}
	
	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString() {
		// TODO This should say something about the real class object
		return "ClassNode";
	}
	
	public AnnotatedVertex GetAnnotatedVertex() { return annotatedVertex; }
}
