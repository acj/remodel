package ec.refactoring;

import java.io.FileNotFoundException;
import java.util.*;
import ec.EvolutionState;
import ec.Singleton;
import ec.Prototype;
import ec.simple.SimpleDefaults;
import ec.util.Parameter;
import ec.refactoring.AnnotatedEdge;
import ec.refactoring.AnnotatedVertex;

/*
 * 
 * TODO: Decide if this is really necessary.  If not, clean it up.
 */
public class SourceGraph {
	private static final long serialVersionUID = -5295342399476105337L;
	public static final int RANDOM_SEED = 0;
	private static int nextGraphId = 0;
	private static AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> annotatedGraph;
	
	/**
	 * Loads the data for the graph that we wish to manipulate.
	 */
	private static void BuildGraph(AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> g,
									String filename) {
		IModelImport importer = new FactImport();
		try {
			importer.Import(g, filename);
		} catch (FileNotFoundException e) {
			System.err.println("Could not open file: " + filename);
			e.printStackTrace();
			System.exit(-1);
		}
	}
	public static int GetNextGraphId() {
		return nextGraphId++;
	}
	public static AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> GetClone() {
		if (annotatedGraph == null) {
			AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g =
				new AnnotatedGraph<AnnotatedVertex, AnnotatedEdge>(AnnotatedEdge.class);
			BuildGraph(g, "beaver-annotated.facts"); // TODO: parameterize this
			if (g.getSize() == 0) {
				System.err.println("ERROR: Empty graph after import.");
				System.exit(-1);
			}
			annotatedGraph = g;
		}
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
		//System.err.println("New clone: " + graph_clone.getSize() + " vertices");
		return graph_clone;
	}

	/*
		// Set up a new instance of SourceGraph if necessary
		if (sourceGraph == null) {
			System.out.println("Creating new SourceGraph instance!");
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
	        	        
	        System.err.println("Initial graph has " + d.vertexSet().size() + " vertices");
	        sourceGraph.annotatedGraph = d;
		}
		return sourceGraph;
	
	*/
}
