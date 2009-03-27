package ec.refactoring;

import java.util.*;
import org.jgrapht.graph.Pseudograph;
import org.jgrapht.graph.DefaultEdge;

import ec.EvolutionState;
import ec.Singleton;
import ec.Prototype;
import ec.simple.SimpleDefaults;
import ec.util.Parameter;
import ec.refactoring.AnnotatedEdge;
import ec.refactoring.AnnotatedVertex;

/*
 * This is an odd class.  It contains two singleton patterns.  The first manages
 * the lone instance of SourceGraph that exists.  The next manages the only
 * "current" clone of that SourceGraph.  The latter is basically a globally
 * accessible object that is modified by the GP tree as it is executed.
 * 
 * TODO: Decide if this is really necessary.  If not, clean it up.
 */
public class SourceGraph implements Prototype, Singleton {
	private static SourceGraph sourceGraph = null;
	private static AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> currentClone = null;

	private AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> annotatedGraph;

	
	public void setup(EvolutionState state, Parameter param) {
	}
	public Parameter defaultBase() {
		return SimpleDefaults.base().push("SourceGraph");
	}
	protected SourceGraph() {
	}
	public static AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> GetCurrentClone() {
		return currentClone;
	}
	public AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> clone() {
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> graph_clone = 
			new AnnotatedGraph<AnnotatedVertex, AnnotatedEdge>(AnnotatedEdge.class);
		Set<AnnotatedVertex> vertices = annotatedGraph.vertexSet();
		Iterator<AnnotatedVertex> it = vertices.iterator();
		while (it.hasNext()) {
			AnnotatedVertex v_old = it.next();
			AnnotatedVertex v = new AnnotatedVertex(v_old.toString(), v_old.getType(), v_old.getVisibility());
			graph_clone.addVertex(v);
		}
		Set<AnnotatedEdge> edges = annotatedGraph.edgeSet();
		Iterator<AnnotatedEdge> it_edge = edges.iterator();
		String old_src_v;
		String old_snk_v;
		while (it_edge.hasNext()) {
			AnnotatedEdge e_old = it_edge.next();
			AnnotatedEdge e = new AnnotatedEdge(e_old.getLabel());
			old_src_v = e_old.getSourceVertex().toString();
			old_snk_v = e_old.getSinkVertex().toString();
			graph_clone.addEdge(graph_clone.getVertex(old_src_v),
								graph_clone.getVertex(old_snk_v),
								e);
		}
		currentClone = graph_clone;
		return graph_clone;
	}
	public static SourceGraph GetInstance() {
		// Set up a new instance of SourceGraph if necessary
		if (sourceGraph == null) {
			System.out.println("Creating new instance!");
			sourceGraph = new SourceGraph();
			AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> d = 
				new AnnotatedGraph<AnnotatedVertex, AnnotatedEdge>(AnnotatedEdge.class);

			// Based on Mens et al. (2004)
	        d.addVertex(new AnnotatedVertex("Document", AnnotatedVertex.VertexType.CLASS, AnnotatedVertex.Visibility.PUBLIC));
	        d.addVertex(new AnnotatedVertex("Document__print", AnnotatedVertex.VertexType.OPERATION, AnnotatedVertex.Visibility.PUBLIC));
	        d.addVertex(new AnnotatedVertex("Document__preview", AnnotatedVertex.VertexType.OPERATION, AnnotatedVertex.Visibility.PUBLIC));
	        d.addEdge(d.getVertex("Document"), d.getVertex("Document__print"), new AnnotatedEdge(AnnotatedEdge.Label.OWN));
	        d.addEdge(d.getVertex("Document"), d.getVertex("Document__preview"), new AnnotatedEdge(AnnotatedEdge.Label.OWN));
	        d.addVertex(new AnnotatedVertex("AsciiDoc", AnnotatedVertex.VertexType.CLASS, AnnotatedVertex.Visibility.PUBLIC));
	        d.addVertex(new AnnotatedVertex("AsciiDoc__print", AnnotatedVertex.VertexType.OPERATION, AnnotatedVertex.Visibility.PUBLIC));
	        d.addVertex(new AnnotatedVertex("AsciiDoc__preview", AnnotatedVertex.VertexType.OPERATION, AnnotatedVertex.Visibility.PUBLIC));
	        d.addEdge(d.getVertex("AsciiDoc"), d.getVertex("AsciiDoc__print"), new AnnotatedEdge(AnnotatedEdge.Label.OWN));
	        d.addEdge(d.getVertex("AsciiDoc"), d.getVertex("AsciiDoc__preview"), new AnnotatedEdge(AnnotatedEdge.Label.OWN));
	        d.addVertex(new AnnotatedVertex("PSDoc", AnnotatedVertex.VertexType.CLASS, AnnotatedVertex.Visibility.PUBLIC));
	        d.addVertex(new AnnotatedVertex("PSDoc__print", AnnotatedVertex.VertexType.OPERATION, AnnotatedVertex.Visibility.PUBLIC));
	        d.addVertex(new AnnotatedVertex("PSDoc__preview", AnnotatedVertex.VertexType.OPERATION, AnnotatedVertex.Visibility.PUBLIC));
	        d.addEdge(d.getVertex("PSDoc"), d.getVertex("PSDoc__print"), new AnnotatedEdge(AnnotatedEdge.Label.OWN));
	        d.addEdge(d.getVertex("PSDoc"), d.getVertex("PSDoc__preview"), new AnnotatedEdge(AnnotatedEdge.Label.OWN));
	        d.addVertex(new AnnotatedVertex("PDFDoc", AnnotatedVertex.VertexType.CLASS, AnnotatedVertex.Visibility.PUBLIC));
	        d.addVertex(new AnnotatedVertex("PDFDoc__print", AnnotatedVertex.VertexType.OPERATION, AnnotatedVertex.Visibility.PUBLIC));
	        d.addVertex(new AnnotatedVertex("PDFDoc__preview", AnnotatedVertex.VertexType.OPERATION, AnnotatedVertex.Visibility.PUBLIC));
	        d.addEdge(d.getVertex("PDFDoc"), d.getVertex("PDFDoc__print"), new AnnotatedEdge(AnnotatedEdge.Label.OWN));
	        d.addEdge(d.getVertex("PDFDoc"), d.getVertex("PDFDoc__preview"), new AnnotatedEdge(AnnotatedEdge.Label.OWN));
	        d.addVertex(new AnnotatedVertex("Previewer", AnnotatedVertex.VertexType.CLASS, AnnotatedVertex.Visibility.PUBLIC));
	        d.addVertex(new AnnotatedVertex("Previewer__preview", AnnotatedVertex.VertexType.OPERATION, AnnotatedVertex.Visibility.PUBLIC));
	        d.addEdge(d.getVertex("Previewer"), d.getVertex("Previewer__preview"), new AnnotatedEdge(AnnotatedEdge.Label.OWN));
	        d.addVertex(new AnnotatedVertex("Printer", AnnotatedVertex.VertexType.CLASS, AnnotatedVertex.Visibility.PUBLIC));
	        d.addVertex(new AnnotatedVertex("Printer__print", AnnotatedVertex.VertexType.OPERATION, AnnotatedVertex.Visibility.PUBLIC));
	        d.addEdge(d.getVertex("Printer"), d.getVertex("Printer__print"), new AnnotatedEdge(AnnotatedEdge.Label.OWN));
	        
	        d.addEdge(d.getVertex("AsciiDoc"), d.getVertex("Document"), new AnnotatedEdge(AnnotatedEdge.Label.INHERIT));
	        d.addEdge(d.getVertex("PSDoc"), d.getVertex("Document"), new AnnotatedEdge(AnnotatedEdge.Label.INHERIT));
	        d.addEdge(d.getVertex("PDFDoc"), d.getVertex("Document"), new AnnotatedEdge(AnnotatedEdge.Label.INHERIT));
	        d.addEdge(d.getVertex("Previewer"), d.getVertex("Document"), new AnnotatedEdge(AnnotatedEdge.Label.ASSOCIATE));
	        d.addEdge(d.getVertex("Printer"), d.getVertex("Document"), new AnnotatedEdge(AnnotatedEdge.Label.ASSOCIATE));
	        //System.out.println("New graph has " + d.vertexSet().size() + " vertices");
	        sourceGraph.annotatedGraph = d;
		}
		return sourceGraph;
	}
}
