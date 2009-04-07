package ec.refactoring;

import java.io.FileNotFoundException;

public interface IModelImport {
	public void Import(AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> g,
			String Filename) throws FileNotFoundException;
}
