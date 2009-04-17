package ec.refactoring;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * These nodes contain strings that are extracted from (e.g.) a requirements
 * document.  They are used as the alphabet of new classes, operations, and
 * so on, that must be given a name.
 * 
 * @author acj
 *
 */
public class StringNode extends GPNode {
	private static final long serialVersionUID = 2370785965364353885L;
	String stringName;
	
	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		//System.out.println("StringNode");
		RefactorData rd = (RefactorData)input;
		rd.name = stringName;
	}
	
	@Override
	public boolean nodeEquals(GPNode node) {
		// won't work for subclasses; in that case you'll need
        // to change this to isAssignableTo(...)
        if (this.getClass() != node.getClass()) { return false; }
        return (((StringNode)node).toString() == stringName);
	}

	@Override
	public int nodeHashCode() {
		return this.getClass().hashCode() + stringName.hashCode();
	}

	@Override
	public void resetNode(EvolutionState state, int thread) {
		super.resetNode(state, thread);
		// Fetch a new vertex to represent.
		stringName = StringFactory.GetRandomString();
	}

	@Override
	public String toString() {
		return "StringNode<" + stringName + ">";
	}

}
