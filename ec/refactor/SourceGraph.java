package ec.refactor;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import ec.EvolutionState;
import ec.Singleton;
import ec.util.Parameter;

public class SourceGraph implements Singleton {
	private static SourceGraph sourceGraph = null;
	private DefaultDirectedGraph<String, DefaultEdge> directedGraph;
	protected void SourceGraph() {
		System.out.println("SourceGraph()");
	}
	public void setup(EvolutionState state, Parameter base) {
	}
	public DefaultDirectedGraph<String, DefaultEdge> GetCopy() {
		return (DefaultDirectedGraph)directedGraph.clone();
	}
	public static SourceGraph GetInstance() {
		// Set up a new instance of SourceGraph if necessary
		if (sourceGraph == null) {
			System.out.println("Creating new instance!");
			sourceGraph = new SourceGraph();
			DefaultDirectedGraph<String, DefaultEdge> d = new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
	        d.addVertex("Document");
	        d.addVertex("Document__print");
	        d.addVertex("Document__preview");
	        d.addEdge("Document", "Document__print");
	        d.addEdge("Document", "Document__preview");
	        d.addVertex("AsciiDoc");
	        d.addVertex("AsciiDoc__print");
	        d.addVertex("AsciiDoc__preview");
	        d.addEdge("AsciiDoc", "AsciiDoc__print");
	        d.addEdge("AsciiDoc", "AsciiDoc__preview");
	        d.addVertex("PSDoc");
	        d.addVertex("PSDoc__print");
	        d.addVertex("PSDoc__preview");
	        d.addEdge("PSDoc", "PSDoc__print");
	        d.addEdge("PSDoc", "PSDoc__preview");
	        d.addVertex("PDFDoc");
	        d.addVertex("PDFDoc__print");
	        d.addVertex("PDFDoc__preview");
	        d.addEdge("PDFDoc", "PDFDoc__print");
	        d.addEdge("PDFDoc", "PDFDoc__preview");
	        d.addVertex("Previewer");
	        d.addVertex("Previewer__preview");
	        d.addEdge("Previewer", "Previewer__preview");
	        d.addVertex("Printer");
	        d.addVertex("Printer__print");
	        d.addEdge("Printer", "Printer__print");
	        
	        System.out.println("New graph has " + d.vertexSet().size() + " vertices");
	        sourceGraph.directedGraph = d;
		}
		return sourceGraph;
	}
}
