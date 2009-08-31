package ec.refactoring;

import ec.gp.GPData;

public class RefactorData extends GPData {
	private static final long serialVersionUID = 2675269476779669254L;
	public String graphvizName;
	public String graphvizData;
	public AnnotatedVertex newVertex;
	public String name;
	
	@Override
	public GPData copyTo(GPData gpd) {
		RefactorData rd = (RefactorData)gpd;
		rd.graphvizName = graphvizName;
		rd.graphvizData = graphvizData;
		rd.name = name;
		rd.newVertex = newVertex;
		return rd;
	}
}