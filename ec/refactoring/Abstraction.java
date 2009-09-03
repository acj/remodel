package ec.refactoring;

import java.util.Random;

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
		((RefactorIndividual)individual).IncrementNodeCount();
		((RefactorIndividual)individual).IncrementMTNodeCount();
		((RefactorIndividual)individual).getMtList().add(this.toString());
		RefactorData rd = (RefactorData)input;
		String thisNodeGraphviz = this.toString();
		rd.graphvizData += thisNodeGraphviz + " [label=\"" + this.toString() + "\",shape=folder];\n";
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag =
			((RefactorIndividual)individual).GetGraph();
		
		children[0].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex product_v = ag.getVertex(rd.name);
		rd.graphvizData += thisNodeGraphviz + " -> " + rd.graphvizName + ";\n";
		children[1].eval(state, thread, input, stack, individual, problem);
		String newName = "I" + rd.name;
		rd.graphvizData += thisNodeGraphviz + " -> " + rd.graphvizName + ";\n";
		
		// Interface inf = abstractClass(c, newName);
		AnnotatedVertex inf = Helpers.abstractClass(product_v, newName, ag);
		product_v.setAddedByEvolution(true);
		// The next step is handled by abstractClass():
		// addInterface(inf);
		
		// addImplementsLink(c, inf);
		AnnotatedEdge e = new AnnotatedEdge(Label.IMPLEMENT);
		e.setAddedByEvolution(true);
		ag.addEdge(product_v, inf, e);
		
		rd.graphvizName = thisNodeGraphviz;
		rd.name = inf.toString();
		rd.newVertex = inf;
	}

	@Override
	public String toString() {
		return "Abstraction";
	}
	
	public String toGraphviz() {
		Random r = SourceGraph.GetRandom();
		
		return "node" + Math.abs(r.nextInt()); 
	}
}
