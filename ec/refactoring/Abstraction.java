package ec.refactoring;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.refactoring.AnnotatedEdge.Label;
import ec.util.Parameter;

/**
 * Abstraction
 * 
 * This mini-transformation ensures that a Product class has an interface that
 * reflects how a Creator class uses the instances of a Product that it
 * creates.
 *  
 * Child nodes:
 * 	- Class c
 *  - String newName
 * 
 * @author acj
 *
 */
public class Abstraction extends GPNode {
	private static final long serialVersionUID = -2944331110781013648L;

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
		else if (children[0].toString() != "ClassNode")
			state.output.error("Invalid child node 0 (should be ClassNode)");
		else if (children[0].toString() != "StringNode")
			state.output.error("Invalid child node 1 (should be StringNode)");
		*/
		//else if (ag.getVertex() != null)
		//	state.output.error("Invalid child node 1 (name already exists)");

	}
	
	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		RefactorData rd = (RefactorData)input;
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag = 
			SourceGraph.GetCurrentClone();
		
		children[0].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex product_v = rd.vertex;
		children[1].eval(state, thread, input, stack, individual, problem);
		String newName = rd.name;
		
		// TODO: Does this work in eval()?  Copied from checkConstraints()
		if (ag.getVertex("newName") != null)
		{
			state.output.error("Invalid child node 1 (name already exists)");
			return;
		}
		
		// Interface inf = abstractClass(c, newName);
		AnnotatedVertex inf = Helpers.abstractClass(product_v, newName);
		
		// The next step is handled by abstractClass():
		// addInterface(inf);
		
		// addImplementsLink(c, inf);
		AnnotatedEdge e = new AnnotatedEdge(Label.IMPLEMENT);
		ag.addEdge(product_v, inf, e);
	}

	@Override
	public String toString() {
		return "Abstraction";
	}

}
