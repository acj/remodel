package ec.refactoring;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

/**
 * This class is the root of a GP tree.  It simply evaluates its children in
 * order.
 * 
 * @author acj
 */
public class RootNode extends GPNode {
	private static final long serialVersionUID = 1145928055125731151L;

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		for (int i = 0; i < children.length; ++i) {
			children[i].eval(state, thread, input, stack, individual, problem);
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "RootNode";
	}

}
