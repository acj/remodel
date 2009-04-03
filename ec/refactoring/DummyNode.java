package ec.refactoring;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * This class fills the role of a placeholder in the GP tree.  It is
 * equivalent to a "nop" machine instruction.
 * @author acj
 *
 */
public class DummyNode extends GPNode {
	private static final long serialVersionUID = 8513001836507785931L;

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		// Do nothing.
	}

	@Override
	public String toString() {
		return "DummyNode";
	}

}
