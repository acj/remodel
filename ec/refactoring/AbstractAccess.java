package ec.refactoring;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.util.Parameter;

/**
 * AbstractAccess
 * 
 * This mini-transformation decouples a Creator class from a Product
 * class.  The only knowledge that a Creator class can have about the
 * Product it creates is through an interface.
 * 
 * Child nodes:
 *  - Class context
 *  - Class concrete
 *  - Interface inf
 *  - SetOfString skipMethods (currently unused)
 * @author acj
 *
 */
public class AbstractAccess extends GPNode {
	private static final long serialVersionUID = 2170224585003394586L;
	
	public void checkConstraints(final EvolutionState state,
            final int tree,
            final GPIndividual typicalIndividual,
            final Parameter individualBase)
	{
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children.length!=3)
			state.output.error("Incorrect number of children for node " + 
			          toStringForError() + " at " +
			          individualBase);
	}
    
	@Override
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		//System.err.println("AbstractAccess()");
		((RefactorIndividual)individual).IncrementNodeCount();
		((RefactorIndividual)individual).IncrementMTNodeCount();
		((RefactorIndividual)individual).getMtList().add(this.toString());
		// For each "uses" relationship between the context and the concrete
		// class, replace this relationship with an "implements" link to
		// an interface that mirrors the concrete class.
		RefactorData rd = (RefactorData)input;
		String thisNodeGraphviz = this.toString();
		rd.graphvizData += thisNodeGraphviz + " [label=\"" + this.toString() + "\",shape=folder];\n";
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag = 
			((RefactorIndividual)individual).GetGraph();
		children[0].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex context_v = ag.getVertex(rd.name);
		rd.graphvizData += thisNodeGraphviz + " -> " + rd.graphvizName + ";\n";
		children[1].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex concrete_v = ag.getVertex(rd.name);
		rd.graphvizData += thisNodeGraphviz + " -> " + rd.graphvizName + ";\n";
		children[2].eval(state, thread, input, stack, individual, problem);
		AnnotatedVertex iface_v = ag.getVertex(rd.name);
		rd.graphvizData += thisNodeGraphviz + " -> " + rd.graphvizName + ";\n";
		
		// Maintain lists of edges to add and remove (this avoids a
		// concurrent access issue that invalidates the iterator)
		Vector<AnnotatedEdge> add_edges = new Vector<AnnotatedEdge>();
		Vector<AnnotatedEdge> remove_edges = new Vector<AnnotatedEdge>();
		
		AnnotatedEdge e;
		Iterator<AnnotatedEdge> edge_it = ag.outgoingEdgesOf(context_v).iterator();
		while (edge_it.hasNext()) {
			e = edge_it.next();
			if (e.getSinkVertex() == concrete_v) {
				remove_edges.add(e);
				AnnotatedEdge e_new = new AnnotatedEdge(e.getLabel());
				e_new.setAddedByEvolution(true);
				add_edges.add(e_new);
			}
		}
		// Add the new edges
		edge_it = add_edges.iterator();
		while (edge_it.hasNext()) {
			ag.addEdge(context_v, iface_v, edge_it.next());
		}
		// Now remove the old edges
		edge_it = remove_edges.iterator();
		while (edge_it.hasNext()) {
			ag.removeEdge(edge_it.next());
		}
		
		rd.graphvizName = thisNodeGraphviz;
		rd.name = iface_v.toString();
		rd.newVertex = iface_v;
	}

	@Override
	public String toString() {
		return "AbstractAccess";
	}

	public String toGraphviz() {
		Random r = SourceGraph.GetRandom();
		
		return "node" + r.nextInt(); 
	}
}
