package ec.refactor;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import ec.EvolutionState;
import ec.Singleton;
import ec.util.Parameter;

public class SourceGraph implements Singleton {
	private static SourceGraph sourceGraph = null;
	private static DirectedGraph<String, DefaultEdge> directedGraph;
	public int NumVertices() {
		if (directedGraph != null) {
			return directedGraph.vertexSet().size();
		}
		return 0;
	}
	private void SourceGraph() {
		directedGraph = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);

        directedGraph.addVertex("Document");
        directedGraph.addVertex("Document__print");
        directedGraph.addVertex("Document__preview");
        directedGraph.addEdge("Document", "Document__print");
        directedGraph.addEdge("Document", "Document__preview");
        directedGraph.addVertex("AsciiDoc");
        directedGraph.addVertex("AsciiDoc__print");
        directedGraph.addVertex("AsciiDoc__preview");
        directedGraph.addEdge("AsciiDoc", "AsciiDoc__print");
        directedGraph.addEdge("AsciiDoc", "AsciiDoc__preview");
        directedGraph.addVertex("PSDoc");
        directedGraph.addVertex("PSDoc__print");
        directedGraph.addVertex("PSDoc__preview");
        directedGraph.addEdge("PSDoc", "PSDoc__print");
        directedGraph.addEdge("PSDoc", "PSDoc__preview");
        directedGraph.addVertex("PDFDoc");
        directedGraph.addVertex("PDFDoc__print");
        directedGraph.addVertex("PDFDoc__preview");
        directedGraph.addEdge("PDFDoc", "PDFDoc__print");
        directedGraph.addEdge("PDFDoc", "PDFDoc__preview");
        directedGraph.addVertex("Previewer");
        directedGraph.addVertex("Previewer__preview");
        directedGraph.addEdge("Previewer", "Previewer__preview");
        directedGraph.addVertex("Printer");
        directedGraph.addVertex("Printer__print");
        directedGraph.addEdge("Printer", "Printer__print");
	}
	
	public void setup(EvolutionState state, Parameter base) {
		// TODO Auto-generated method stub

	}
	public static SourceGraph GetInstance() {
		if (sourceGraph == null) {
			sourceGraph = new SourceGraph();	
		}
		return sourceGraph;
	}
}
