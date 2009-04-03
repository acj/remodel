package ec.refactoring;

import ec.gp.GPData;

public class RefactorData extends GPData {
	private static final long serialVersionUID = 2675269476779669254L;
	private AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> graph =
		SourceGraph.GetInstance().clone();
	public AnnotatedVertex vertex;
	public String name;
	
	@Override
	public GPData copyTo(GPData gpd) {
		RefactorData rd = (RefactorData)gpd;
		rd.graph = graph;
		rd.name = name;
		rd.vertex = vertex;
		return rd;
	}
	
	public AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> GetGraph() {
		return graph;
	}
	
	public void SetGraph(AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> ag) {
		graph = ag;
	}
}