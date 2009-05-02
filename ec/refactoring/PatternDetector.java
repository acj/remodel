package ec.refactoring;

import java.util.ArrayList;

public interface PatternDetector {
	public void Setup();
	public ArrayList<String> DetectPatterns(AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> g);
}
