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
 * PartialAbstraction
 * 
 * This mini-transformation ensures that a Creator class must inherit from
 * an abstract class where construction methods are declared abstractly.
 * 
 * Child nodes:
 *  - Class concrete
 *  - String newName
 *  - SetOfString abstractMethods
 * 
 * @author acj
 *
 */
public class PartialAbstraction extends GPNode {
	private static final long serialVersionUID = -2595216501945664674L;

	public void checkConstraints(final EvolutionState state,
            final int tree,
            final GPIndividual typicalIndividual,
            final Parameter individualBase)
	{
		if (children.length!=2)
			state.output.error("Incorrect number of children for node " + 
			  toStringForError() + " at " +
			  individualBase);
	}
	
	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		//System.err.println("PartialAbstraction()");
		((RefactorIndividual)individual).IncrementNodeCount();
		RefactorData rd = (RefactorData)input;
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag =
			((RefactorIndividual)individual).GetGraph();
		
		children[0].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex concrete_v = ag.getVertex(rd.name);
		children[1].eval(state, thread, input, stack, individual, problem);
		String newName = "PAbstract" + rd.name;
		
		// This can create self-inherits loops if the concrete vertex has
		// the same name as the new, abstract vertex.  Handle this gracefully.
		if (concrete_v.toString().equals(newName)) {
			rd.name = concrete_v.toString();
			rd.newVertex = concrete_v;
			
			return;
		}
		
		// Class abstract = createEmptyClass(newName);
		AnnotatedVertex abstract_v = Helpers.createEmptyClass(newName, ag);
		AnnotatedEdge e = new AnnotatedEdge(Label.INHERIT);
		ag.addEdge(concrete_v, abstract_v, e);
		
		// TODO: Move some methods into the abstract class
		rd.name = abstract_v.toString();
		rd.newVertex = abstract_v;
	}

	@Override
	public String toString() {
		return "PartialAbstraction";
	}
}
