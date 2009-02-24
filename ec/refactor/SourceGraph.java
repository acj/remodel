package ec.refactor;

import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.DefaultEdge;

import ec.EvolutionState;
import ec.Singleton;
import ec.Prototype;
import ec.simple.SimpleDefaults;
import ec.util.Parameter;

public class SourceGraph implements Prototype, Singleton {
	private static SourceGraph sourceGraph = null;

	private AnnotatedGraph<SourceVertex, SourceEdge> annotatedGraph;
	
	public AnnotatedGraph<SourceVertex, SourceEdge> clone() {
		return (AnnotatedGraph)annotatedGraph.clone();
	}
	
	public void setup(EvolutionState state, Parameter param) {
	}
	public Parameter defaultBase() {
		return SimpleDefaults.base().push("SourceGraph");
	}
	protected void SourceGraph() {
	}

	public static SourceGraph GetInstance() {
		// Set up a new instance of SourceGraph if necessary
		if (sourceGraph == null) {
			System.out.println("Creating new instance!");
			sourceGraph = new SourceGraph();
			AnnotatedGraph<SourceVertex, SourceEdge> d = 
				new AnnotatedGraph<SourceVertex, SourceEdge>(SourceEdge.class);

	        d.addVertex(new SourceVertex("Document", SourceVertex.VertexType.CLASS));
	        d.addVertex(new SourceVertex("Document__print", SourceVertex.VertexType.OPERATION));
	        d.addVertex(new SourceVertex("Document__preview", SourceVertex.VertexType.OPERATION));
	        d.addEdge(d.getVertex("Document"), d.getVertex("Document__print"), new SourceEdge(SourceEdge.Label.OWN));
	        d.addEdge(d.getVertex("Document"), d.getVertex("Document__preview"), new SourceEdge(SourceEdge.Label.OWN));
	        d.addVertex(new SourceVertex("AsciiDoc", SourceVertex.VertexType.CLASS));
	        d.addVertex(new SourceVertex("AsciiDoc__print", SourceVertex.VertexType.OPERATION));
	        d.addVertex(new SourceVertex("AsciiDoc__preview", SourceVertex.VertexType.OPERATION));
	        d.addEdge(d.getVertex("AsciiDoc"), d.getVertex("AsciiDoc__print"), new SourceEdge(SourceEdge.Label.OWN));
	        d.addEdge(d.getVertex("AsciiDoc"), d.getVertex("AsciiDoc__preview"), new SourceEdge(SourceEdge.Label.OWN));
	        d.addVertex(new SourceVertex("PSDoc", SourceVertex.VertexType.CLASS));
	        d.addVertex(new SourceVertex("PSDoc__print", SourceVertex.VertexType.OPERATION));
	        d.addVertex(new SourceVertex("PSDoc__preview", SourceVertex.VertexType.OPERATION));
	        d.addEdge(d.getVertex("PSDoc"), d.getVertex("PSDoc__print"), new SourceEdge(SourceEdge.Label.OWN));
	        d.addEdge(d.getVertex("PSDoc"), d.getVertex("PSDoc__preview"), new SourceEdge(SourceEdge.Label.OWN));
	        d.addVertex(new SourceVertex("PDFDoc", SourceVertex.VertexType.CLASS));
	        d.addVertex(new SourceVertex("PDFDoc__print", SourceVertex.VertexType.OPERATION));
	        d.addVertex(new SourceVertex("PDFDoc__preview", SourceVertex.VertexType.OPERATION));
	        d.addEdge(d.getVertex("PDFDoc"), d.getVertex("PDFDoc__print"), new SourceEdge(SourceEdge.Label.OWN));
	        d.addEdge(d.getVertex("PDFDoc"), d.getVertex("PDFDoc__preview"), new SourceEdge(SourceEdge.Label.OWN));
	        d.addVertex(new SourceVertex("Previewer", SourceVertex.VertexType.CLASS));
	        d.addVertex(new SourceVertex("Previewer__preview", SourceVertex.VertexType.OPERATION));
	        d.addEdge(d.getVertex("Previewer"), d.getVertex("Previewer__preview"), new SourceEdge(SourceEdge.Label.OWN));
	        d.addVertex(new SourceVertex("Printer", SourceVertex.VertexType.CLASS));
	        d.addVertex(new SourceVertex("Printer__print", SourceVertex.VertexType.OPERATION));
	        d.addEdge(d.getVertex("Printer"), d.getVertex("Printer__print"), new SourceEdge(SourceEdge.Label.OWN));
	        
	        d.addEdge(d.getVertex("AsciiDoc"), d.getVertex("Document"), new SourceEdge(SourceEdge.Label.INHERIT));
	        d.addEdge(d.getVertex("PSDoc"), d.getVertex("Document"), new SourceEdge(SourceEdge.Label.INHERIT));
	        d.addEdge(d.getVertex("PDFDoc"), d.getVertex("Document"), new SourceEdge(SourceEdge.Label.INHERIT));
	        d.addEdge(d.getVertex("Previewer"), d.getVertex("Document"), new SourceEdge(SourceEdge.Label.ASSOCIATE));
	        d.addEdge(d.getVertex("Printer"), d.getVertex("Document"), new SourceEdge(SourceEdge.Label.ASSOCIATE));
	        //System.out.println("New graph has " + d.vertexSet().size() + " vertices");
	        sourceGraph.annotatedGraph = d;
		}
		return sourceGraph;
	}
}
