package ec.refactoring;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class SetNode extends GPNode {

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		children[0].eval(state, thread, input, stack, individual, problem);
		children[1].eval(state, thread, input, stack, individual, problem);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "SetNode";
	}

}
