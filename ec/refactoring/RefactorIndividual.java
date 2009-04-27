package ec.refactoring;
import ec.gp.GPIndividual;
import java.util.*;

public class RefactorIndividual extends GPIndividual {
	private static final long serialVersionUID = -2213239528716782559L;
	private AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> graph;
        private ArrayList<String[]> patternList;

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

        public ArrayList<String[]> GetPatternList() {
	    return patternList;
	}

        public void SetPatternList(ArrayList<String[]> patternInstances) {
	    patternList = patternInstances;
	}
}
