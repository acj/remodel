package ec.refactoring;

import java.util.Random;

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
	String graphvizOutput;

	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		((RefactorIndividual)individual).IncrementNodeCount();
		RefactorData rd = (RefactorData)input;
		rd.graphvizData = "";
		String graphvizOutput = "digraph G {\n" +
			"label=\"AST\"\n" +
			"rootnode [label=\"Root\",shape=folder];\n";
		for (int i = 0; i < children.length; ++i) {
			children[i].eval(state, thread, input, stack, individual, problem);
			graphvizOutput += "rootnode -> " + rd.graphvizName + ";\n\n";
		}
		graphvizOutput += rd.graphvizData;
		
		graphvizOutput += "}\n";
		
		((RefactorIndividual)individual).setGraphvizOutput(graphvizOutput);
	}

	@Override
	public String toString() {
		return "RootNode";
	}
}
