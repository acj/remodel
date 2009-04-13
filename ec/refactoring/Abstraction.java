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
	}
	
	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		//System.err.println("Abstraction()");
		RefactorData rd = (RefactorData)input;
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag =
			((RefactorIndividual)individual).GetGraph();
		
		children[0].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex product_v = ag.getVertex(rd.name);
		children[1].eval(state, thread, input, stack, individual, problem);
		String newName = rd.name;
		
		// Interface inf = abstractClass(c, newName);
		AnnotatedVertex inf = Helpers.abstractClass(product_v, newName, ag);

		// The next step is handled by abstractClass():
		// addInterface(inf);
		
		// addImplementsLink(c, inf);
		AnnotatedEdge e = new AnnotatedEdge(Label.IMPLEMENT);
		ag.addEdge(product_v, inf, e);
		
		rd.name = inf.toString();
		rd.newVertex = inf;
	}

	@Override
	public String toString() {
		return "Abstraction";
	}

}
