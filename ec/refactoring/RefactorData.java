package ec.refactoring;

import ec.gp.GPData;

public class RefactorData extends GPData {
	private static final long serialVersionUID = 2675269476779669254L;
	public AnnotatedVertex vertex;
	public String name;
	
	@Override
	public GPData copyTo(GPData gpd) {
		RefactorData rd = (RefactorData)gpd;
		rd.vertex = vertex;
		rd.name = name;
		return rd;
	}

}
