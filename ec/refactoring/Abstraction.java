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
	public void checkConstraints(final EvolutionState state,
					            final int tree,
					            final GPIndividual typicalIndividual,
					            final Parameter individualBase)
	{
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag = 
			SourceGraph.GetCurrentClone();
		
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children[0].toString() != "ClassNode")
			state.output.error("Invalid child node 0 (should be ClassNode)");
		else if (children[0].toString() != "StringNode")
			state.output.error("Invalid child node 1 (should be StringNode)");
		else if (ag.getVertex(/* TODO: need string name */) != null)
			state.output.error("Invalid child node 1 (name already exists)");
		else if (children.length!=2)
			state.output.error("Incorrect number of children for node " + 
			          toStringForError() + " at " +
			          individualBase);
	}
	
	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag = 
			SourceGraph.GetCurrentClone();
		
		// Interface inf = abstractClass(c, newName);
		AnnotatedVertex inf = abstractClass(input/* TODO */, "NewInterface");
		
		// The next step is handled by abstractClass():
		// addInterface(inf);
		
		// addImplementsLink(c, inf);
		AnnotatedEdge e = new AnnotatedEdge(Label.IMPLEMENT);
		ag.addEdge(input/* TODO */, inf, e);
	}

	@Override
	public String toString() {
		return "Abstraction";
	}

}
