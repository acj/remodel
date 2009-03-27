package ec.refactoring;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * HAbstractMethod
 * 
 * This helper constructs and returns an abstract method that has the same
 * name and signature as the parameter method.
 * 
 * Child nodes:
 *  - Method m
 * 
 * @author acj
 *
 */
public class HAbstractMethod extends GPNode {

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		VertexNode child_vertex = (VertexNode)children[0];
		
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag =
			SourceGraph.GetCurrentClone();
		
		// Construct an abstract (TODO: how?) method that has the same name
		// and signature as the child method.
		AnnotatedVertex v = child_vertex.GetAnnotatedVertex();
		AnnotatedVertex abstract_v = new AnnotatedVertex("NewAbstractMethod",
										AnnotatedVertex.VertexType.OPERATION,
										v.getVisibility());
		ag.addVertex(abstract_v);
		// TODO: Pack this vertex into a GPData object and pass it up.
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "HAbstractMethod";
	}

}
