package ec.refactoring;

public interface IModelImport {
	public void Import(AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> g,
			String Filename);
}
