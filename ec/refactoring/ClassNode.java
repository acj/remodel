package ec.refactoring;

import java.util.Random;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;
import ec.refactoring.AnnotatedVertex.VertexType;
import ec.util.Parameter;

/**
 * ClassNode
 * 
 * This class acts as a place-holder in the GP's syntax tree for classes in the
 * target software model.
 * 
 * @author acj
 *
 */
public class ClassNode extends GPNode {
	private static final long serialVersionUID = 5121052556529127964L;
	private String vertexName = "";
	private AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> ag;
	
	public ClassNode() {
	}
	
	public ClassNode clone() {
		ClassNode vn = (ClassNode)(super.cloneReplacing());
		vn.ag = null;
		vn.vertexName = vertexName;
		return vn;
	}
	
	@Override
	public boolean nodeEquals(GPNode node) {
        if (((ClassNode)node).ag.GetGraphId() == ag.GetGraphId() &&
        		((ClassNode)node).GetName() == GetName()) {
        	return true;
        } else {
        	return false;
        }
	}

	@Override
	public int nodeHashCode() {
		if (ag == null) {
			return this.getClass().hashCode() + 1234567890 + GetName().hashCode();
		} else {
			return this.getClass().hashCode() + ag.hashCode() + GetName().hashCode();
		}
	}

	@Override
	public void resetNode(EvolutionState state, int thread) {
		super.resetNode(state, thread);
		// Fetch a new vertex to represent (next time eval is called, that is).
		ag = null;
	}

	public void checkConstraints(final EvolutionState state,
            final int tree,
            final GPIndividual typicalIndividual,
            final Parameter individualBase)
	{
		super.checkConstraints(state,tree,typicalIndividual,individualBase);
		if (children.length!=0)
			state.output.error("Incorrect number of children for node " + 
			  toStringForError() + " at " +
			  individualBase);
	}
	
	public void eval(EvolutionState state, int thread, GPData input,
			ADFStack stack, GPIndividual individual, Problem problem) {
		((RefactorIndividual)individual).IncrementNodeCount();
		RefactorData rd = (RefactorData)input;
		//System.out.println("VertexNode");
		// If we don't have a graph or have been assigned to a new tree that
		// has a different graph, then get a new graph pointer.
		if (ag == null || ag.GetGraphId() != ((RefactorIndividual)individual).GetGraph().GetGraphId()) {
			ag = ((RefactorIndividual)individual).GetGraph();
		}
		AnnotatedVertex v = ag.GetRandomVertex(AnnotatedVertex.VertexType.CLASS);
		assert v.getType() == VertexType.CLASS;
		vertexName = v.toString();
		
		String graphvizName = this.toGraphviz();
		rd.graphvizName = graphvizName;
		rd.graphvizData += graphvizName + " [label=\"" + this.toString() + "\",shape=folder];\n";
		rd.name = vertexName;
	}

	public String toString() {
		return "ClassNode<" + vertexName + ">";
	}
	
	public String GetName() {
		return vertexName;
	}
	
	public String toGraphviz() {
		Random r = SourceGraph.GetRandom();
		
		return "node" + Math.abs(r.nextInt()); 
	}
}
