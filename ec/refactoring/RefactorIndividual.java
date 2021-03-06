package ec.refactoring;
import ec.gp.GPIndividual;
import java.util.*;

public class RefactorIndividual extends GPIndividual {
	private static final long serialVersionUID = -2213239528716782559L;
	private AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> graph;
    private ArrayList<String> patternList;
    private String graphvizOutput;
    private ArrayList<String> mtList; // Order of MTS that have been applied
    private int nodeCount = 0;	// Raw number of nodes in the tree
    private int MTCount = 0;	// Number of MT nodes in the tree

    public RefactorIndividual() {
    	patternList = new ArrayList<String>();
    	mtList = new ArrayList<String>();
    }
	public RefactorIndividual lightClone() {
		RefactorIndividual ri = (RefactorIndividual)(super.lightClone());
		ri.SetGraph(SourceGraph.GetClone());
		return ri;
	}
	@Override
	public int hashCode() {
		return super.hashCode() + graph.hashCode();
	}

	public AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> GetGraph() {
		return graph;
	}
	
	public void SetGraph(AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> ag) {
		graph = ag;
	}

    public ArrayList<String> GetPatternList() {
	    return patternList;
	}

    public void SetPatternList(ArrayList<String> patternInstances) {
	    patternList = patternInstances;
	}
    
    public void setGraphvizOutput(String graphvizOutput) {
		this.graphvizOutput = graphvizOutput;
	}
	public String getGraphvizOutput() {
		return graphvizOutput;
	}
	
	public int GetNodeCount() { return nodeCount; }
	public void setMtList(ArrayList<String> mtList) {
		this.mtList = mtList;
	}
	public ArrayList<String> getMtList() {
		return mtList;
	}
	
    public void IncrementNodeCount() { ++nodeCount; }
    public void ResetNodeCount() { nodeCount = 0; }
    public int GetMTNodeCount() { return MTCount; }
    public void IncrementMTNodeCount() { ++MTCount; }
    public void ResetMTNodeCount() { MTCount = 0; }
}
