package ec.refactoring;

import java.io.*;
import java.util.*;
import ec.refactoring.AnnotatedEdge;
import ec.refactoring.AnnotatedVertex;

/*
 * 
 */
public class SourceGraph {
	private static final long serialVersionUID = -5295342399476105337L;
	public static Random rand;
	public static int RANDOM_SEED = -1;
	private static int nextGraphId = 0;
	private static AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> annotatedGraph;
	private static PatternDetector detector;
	private static ArrayList<String> patternList;
	private static String inputFile = "";
	private static float treeSizePenalty = 0.0F;
	private static float originalGraphQMOOD = 0.0F;
	private static int originalGraphPatterns = 0;
	
	private static int qmoodDPMapRows = 10000;
	private static int qmoodDPMapCols = 2;
	private static float[][] qmoodDPMap = new float[qmoodDPMapRows][qmoodDPMapCols];
	private static int qmoodDPMapIndex = 0;
	
	public static void SetRandom(Random r) {
		rand = r;
	}
	public static Random GetRandom() {
		return rand;
	}
	/**
	 * Loads the data for the graph that we wish to manipulate.
	 */
	private static void BuildGraph(AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> g,
									String filename) {
		//IModelImport importer = new FactImport();
		IModelImport importer = new XMIImport();
		try {
			importer.Import(g, filename);
		} catch (FileNotFoundException e) {
			System.err.println("Could not open file: " + filename);
			e.printStackTrace();
			System.exit(-1);
		}
		System.out.println("--- Graph construction complete ---");
	}
	public static int GetNextGraphId() {
		return nextGraphId++;
	}
	public static AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> GetClone() {
		if (annotatedGraph == null) {
			AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g =
				new AnnotatedGraph<AnnotatedVertex, AnnotatedEdge>(AnnotatedEdge.class);
			BuildGraph(g, GetInputFile());
			if (g.getSize() == 0) {
				System.err.println("ERROR: Empty graph after import.");
				System.exit(-1);
			}
			// Set up a QL instance to do pattern detection
			//detector = new QLWrapper();
			
			// Set up an in-memory pattern detector
			detector = new SQLDetector();
			detector.Setup();
			System.out.println("SQL detector initialized");
			
			annotatedGraph = g;
			
			// Determine the baseline number of patterns in this graph
			SetPatternList(detector.DetectPatterns(g));
			//patternList = detector(g); // Uncomment for QLWrapper! (Lame)
			StringBuilder sb_baseline = new StringBuilder();
			originalGraphQMOOD = QMOODEvaluator.EvaluateGraph(g);
			originalGraphPatterns = GetPatternList().size();
			sb_baseline.append("QMOOD: " +  getOriginalGraphQMOOD() + "\n");
			sb_baseline.append("Patterns: " + getOriginalGraphPatterns() + "\n{ ");
			
			Iterator<String> it = GetPatternList().iterator();
			while (it.hasNext()) {
				String token = it.next();
				token = token.substring(0, token.indexOf(" ")+1);
				sb_baseline.append(token);
			}
			sb_baseline.append("}\n");
			sb_baseline.append("(|V|,|E|): (" + g.getSize() + "," + g.edgeSet().size() + ")");
			System.out.println(sb_baseline.toString());
			try {
				/*
				BufferedWriter out = new BufferedWriter(new FileWriter("output/graphstart.facts"));
				out.write(g.ToFacts());
				out.flush();
				out.close();
				*/
				
				BufferedWriter out = new BufferedWriter(new FileWriter("output/baseline.txt"));
				out.write(sb_baseline.toString());
				out.flush();
				out.close();
				
				out = new BufferedWriter(new FileWriter("output/graphstart.dump"));
				out.write(g.toString());
				out.flush();
				out.close();
				
			    out = new BufferedWriter(new FileWriter("output/graphstart.dot"));
			    out.write(g.ToGraphViz("Starting Graph"));
			    out.flush();
			    out.close();

			    out = new BufferedWriter(new FileWriter("output/patterns.orig"));
			    it = GetPatternList().iterator();
			    while (it.hasNext()) {
			    	out.write(it.next() + "\n");
			    }
			    out.flush();
			    out.close();
            } catch (IOException e) {
			    System.err.println("Could not export initial patterns!");
            }
		} // end if (annotatedGraph == null)
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> graph_clone = 
			new AnnotatedGraph<AnnotatedVertex, AnnotatedEdge>(AnnotatedEdge.class);
		Set<AnnotatedVertex> vertices = annotatedGraph.vertexSet();
		Iterator<AnnotatedVertex> it_vert = vertices.iterator();
		while (it_vert.hasNext()) {
			AnnotatedVertex v_old = it_vert.next();
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
	public static void SetDetector(PatternDetector detector) {
		SourceGraph.detector = detector;
	}
	public static PatternDetector GetDetector() {
		return detector;
	}
	public static void SetPatternList(ArrayList<String> patternList) {
		SourceGraph.patternList = patternList;
	}
	public static ArrayList<String> GetPatternList() {
		return patternList;
	}
	public static void SetInputFile(String inputFile) {
		SourceGraph.inputFile = inputFile;
	}
	public static String GetInputFile() {
		return inputFile;
	}
	public static float getOriginalGraphQMOOD() {
		return originalGraphQMOOD;
	}
	public static int getOriginalGraphPatterns() {
		return originalGraphPatterns;
	}
	public static void setTreeSizePenalty(float treeSizePenalty) {
		SourceGraph.treeSizePenalty = treeSizePenalty;
	}
	public static float getTreeSizePenalty() {
		return treeSizePenalty;
	}
	public static void addQMOODDPMapEntry(float QMOOD, float DP) {
		getQMOODDPMap()[qmoodDPMapIndex][0] = QMOOD;
		getQMOODDPMap()[qmoodDPMapIndex][1] = DP;
		++qmoodDPMapIndex;
	}
	public static float[][] getQMOODDPMap() {
		return qmoodDPMap;
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
