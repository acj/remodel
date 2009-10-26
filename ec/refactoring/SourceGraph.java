package ec.refactoring;

import java.io.*;
import java.util.*;

import ec.refactoring.AnnotatedEdge;
import ec.refactoring.AnnotatedVertex;
import ec.refactoring.AnnotatedVertex.VertexType;

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
	private static float dpRewardCoefficient = 0.0F;
	private static StatDataPoint originalGraphStat;
	private static int originalGraphPatterns = 0;
	
	private static Vector<StatDataPoint> StatMap = new Vector<StatDataPoint>();
	
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
			
			// Set up an in-memory pattern detector
			detector = new SQLDetector();
			//detector = new PrologDetector();
			detector.Setup();
			System.out.println("Pattern detector initialized");
			
			annotatedGraph = g;
			
			// Determine the baseline number of patterns in this graph
			SetPatternList(detector.DetectPatterns(g));
			//patternList = detector(g); // Uncomment for QLWrapper! (Lame)
			StringBuilder sb_baseline = new StringBuilder();
			originalGraphStat = QMOODEvaluator.EvaluateGraph(g);
			originalGraphPatterns = GetPatternList().size();
			sb_baseline.append("QMOOD: " +  getOriginalGraphQMOOD().qmood + "\n");
			sb_baseline.append("Patterns: " + getOriginalGraphPatterns() + "\n{ ");
			
			Iterator<String> it = GetPatternList().iterator();
			while (it.hasNext()) {
				String token = it.next();
				token = token.substring(0, token.indexOf(" ")+1);
				sb_baseline.append(token);
			}
			sb_baseline.append("}\n");
			sb_baseline.append("(|V|,|E|): (" + g.getSize() + "," + g.edgeSet().size() + ")\n");
			sb_baseline.append("|Classes at start| = " + g.GetVertices(AnnotatedVertex.VertexType.CLASS).size());
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
	public static StatDataPoint getOriginalGraphQMOOD() {
		return originalGraphStat;
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
	public static float getDpRewardCoefficient() {
		return dpRewardCoefficient;
	}
	public static void setDpRewardCoefficient(float dpRewardCoefficient) {
		SourceGraph.dpRewardCoefficient = dpRewardCoefficient;
	}
	public static void addStatMapEntry(StatDataPoint s) {
		StatMap.add(s);
	}
	public static Vector<StatDataPoint> getStatMap() {
		return StatMap;
	}
}
