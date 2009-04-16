package ec.refactoring;

import java.io.*;
import java.util.*;

import ec.*;
import ec.gp.*;
import ec.simple.*;
import ec.util.Parameter;

public class RefactorProblem extends GPProblem implements SimpleProblemForm {
	private static final long serialVersionUID = 4425245889654977390L;
	public RefactorData input;
	
	public RefactorProblem clone() {
		RefactorProblem rp = (RefactorProblem) (super.clone());
		rp.input = (RefactorData) (input.clone());
		return rp;
	}
	
	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		if (!ind.evaluated) {	// Don't reevaluate
			((GPIndividual)ind).trees[0].child.eval(
					state,threadnum,input,stack,((GPIndividual)ind), this);
		}
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g = 
			((RefactorIndividual)ind).GetGraph();
		
		SimpleFitness fit = new SimpleFitness();
		Float fitness_value = 0F;
		
		// QMOOD evaluation
		float designSizeInClasses = -DesignSizeInClasses(g);
		float avgNumberOfAncestors = AvgNumberOfAncestors(g);
		float dataAccessMetric = DataAccessMetric(g);
		float numberOfMethods = -NumberOfMethods(g);
		float numberOfPolyMethods = NumberOfPolymorphicMethods(g);
		float classInterfaceSize = ClassInterfaceSize(g);
		float measureOfAggregation = MeasureOfAggregation(g);
		float measureOfFunctionalAbstraction = MeasureOfFunctionalAbstraction(g);
		
		/*
		System.out.println("DSIC: " + designSizeInClasses);
		System.out.println("ANOA: " + avgNumberOfAncestors);
		System.out.println("DAM: " + dataAccessMetric);
		System.out.println("NOM: " + numberOfMethods);
		System.out.println("NOPM: " + numberOfPolyMethods);
		System.out.println("CIF: " + classInterfaceSize);
		System.out.println("MOA: " + measureOfAggregation);
		System.out.println("MOFA: " + measureOfFunctionalAbstraction);
		*/

		// Design pattern detection
		int patternsFound = QLWrapper.EvaluateGraph(g.ToFacts());
		
		fitness_value = 100 + designSizeInClasses + avgNumberOfAncestors +
			dataAccessMetric + numberOfMethods + numberOfPolyMethods +
			classInterfaceSize + measureOfAggregation +
			measureOfFunctionalAbstraction + 100*patternsFound;
		
		fit.setFitness(state, fitness_value, false);
		//System.err.println("Fitness: " + fitness_value);
		ind.fitness = fit;
		
		stack.reset();
		ind.evaluated = true;
		/*
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("model.out"));
			out.write(g.ToGraphViz());
			out.close();
		} catch (IOException e) {
			System.err.println("Could not export graphviz data!");
		}
		*/
	}
    public void setup(final EvolutionState state, final Parameter base)
	{
		// very important, remember this
		super.setup(state,base);
		
		StringFactory.Setup();
		
		input = new RefactorData();
		input.name = "";
	}
    
    /**
     * Metrics
     */
	private float DesignSizeInClasses(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		Set<AnnotatedVertex> vertices = g.vertexSet();
		Iterator<AnnotatedVertex> it = vertices.iterator();
		int class_count = 0;
		AnnotatedVertex v;
		while (it.hasNext()) {
			v = it.next();
			if (v.getType() == AnnotatedVertex.VertexType.CLASS) {
				++class_count;
			}
		}
		return (float)class_count;
	}
	private float NumberOfHierarchies(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		return 0.0F;
	}
	/**
	 * Computes the average number of ancestors between a given class and the
	 * "root" class.
	 * @param g A graph.
	 * @return Average number of ancestors.
	 */
	private float AvgNumberOfAncestors(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		// Does not account for multiple inheritance.  A path to the "root" will be
		// arbitrarily chosen in such cases.
		Set<AnnotatedVertex> vertices = g.vertexSet();
		HashMap<String, Integer> vertexmap = new HashMap<String, Integer>();
		Iterator<AnnotatedVertex> it = vertices.iterator();
		AnnotatedVertex v;
		while (it.hasNext()) {
			v = it.next();
			if (v.getType() != AnnotatedVertex.VertexType.CLASS) {
				continue;
			}
			ComputeAncestors(g, v, vertexmap);
		}
		// Reset and compute the average number of ancestors this time
		float num_ancestors = 0.0F;
		it = vertices.iterator();
		while (it.hasNext()) {
			v = it.next();
			if (v.getType() != AnnotatedVertex.VertexType.CLASS) {
				continue;
			}
			num_ancestors += vertexmap.get(v.toString()).floatValue();
		}
		return num_ancestors / vertices.size();
	}
	private float DataAccessMetric(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		Set<AnnotatedVertex> vertices = g.vertexSet();
		Iterator<AnnotatedVertex> it = vertices.iterator();
		Vector<Float> dam_ratios = new Vector<Float>();
		float num_private_vars, num_total_vars;
		AnnotatedVertex v;
		while (it.hasNext()) {
			num_private_vars = 0.0F;
			num_total_vars = 0.0F;
			v = it.next();
			if (v.getType() != AnnotatedVertex.VertexType.CLASS ||
					v.getAttributes().isEmpty()) {
				continue;
			}
			ArrayList<AnnotatedAttribute> attribs = v.getAttributes();
			Iterator<AnnotatedAttribute> it_attribs = attribs.iterator();
			while (it_attribs.hasNext()) {
				if (it_attribs.next().getVisibility() == AnnotatedAttribute.Visibility.PRIVATE) {
					++num_private_vars;
					++num_total_vars;
				} else {
					++num_total_vars;
				}
			}

			dam_ratios.add(num_private_vars / num_total_vars);
		}
		// Compute the simple average of the ratios
		float data_access_metric = 0.0F;
		Iterator<Float> ratio_it = dam_ratios.iterator();
		while (ratio_it.hasNext()) {
			data_access_metric += ratio_it.next();
		}
		if (dam_ratios.isEmpty()) {
			return 0.0F;
		} else {
			return data_access_metric / (float)dam_ratios.size();
		}
	}
	private float DirectClassCoupling(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		return 0.0F;
	}
	private float CohesionAmongClassMethods(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		return 0.0F;
	}
	private float MeasureOfAggregation(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		// First, get a list of user-defined types (class names)
		Set<AnnotatedVertex> vertices = g.vertexSet();
		Set<AnnotatedVertex> class_vertices = new HashSet<AnnotatedVertex>();
		Set<String> class_names = new HashSet<String>();
		Iterator<AnnotatedVertex> it_v = vertices.iterator();
		AnnotatedVertex v;
		while (it_v.hasNext()) {
			v = it_v.next();
			if (v.getType() == AnnotatedVertex.VertexType.CLASS) {
				class_vertices.add(v);
				class_names.add(v.toString());
			}
		}
		// Look through the classes' attributes to find these user-defined
		// data types.
		int aggregate_count = 0; // Num. of attribs with user-defined type
		it_v = class_vertices.iterator();
		while (it_v.hasNext()) { 
			v = it_v.next();
			ArrayList<AnnotatedAttribute> attribs = v.getAttributes();
			Iterator<AnnotatedAttribute> it_a = attribs.iterator();
			AnnotatedAttribute a;
			while (it_a.hasNext()) {
				a = it_a.next();
				if (class_vertices.contains(a.getDataType())) {
					++aggregate_count;
				}
			}
		}
		return (float)aggregate_count / (float)class_vertices.size();
	}
	private float MeasureOfFunctionalAbstraction(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		Set<AnnotatedVertex> vertices = g.vertexSet();
		Iterator<AnnotatedVertex> it_v = vertices.iterator();
		float abstraction_tally = 0.0F;
		AnnotatedVertex v;
		while (it_v.hasNext()) {
			v = it_v.next();
			if (v.getType() == AnnotatedVertex.VertexType.CLASS) {
				abstraction_tally += CountInheritedMethods(g, v);
			}
		}
		return abstraction_tally / (float)vertices.size();
	}
	private float NumberOfPolymorphicMethods(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		// For each class, look through its methods and determine how many
		// of them are polymorphic.
		ArrayList<AnnotatedVertex> polymorphic_methods = new ArrayList<AnnotatedVertex>();
		Set<AnnotatedVertex> vertices = g.vertexSet();
		Iterator<AnnotatedVertex> it_v = vertices.iterator();
		int class_count = 0;
		AnnotatedVertex v;
		while (it_v.hasNext()) {
			v = it_v.next();
			if (v.getType() == AnnotatedVertex.VertexType.CLASS) {
				++class_count;
				// TODO: Is there a constant-lookup structure that we could
				// use without needing to store the object reference?  We
				// only look at the string name here.
				HashMap<String,AnnotatedVertex> vertex_hash = new HashMap<String,AnnotatedVertex>();
				Set<AnnotatedEdge> edges = g.edgesOf(v);
				AnnotatedEdge e;
				Iterator<AnnotatedEdge> it_e = edges.iterator();
				// Look for ownership edges
				while (it_e.hasNext()) {
					e = it_e.next();
					if (e.getSourceVertex().equals(v) &&
							e.getLabel() == AnnotatedEdge.Label.OWN) {
						if (vertex_hash.containsKey(v.toString())) {
							polymorphic_methods.add(v); // Polymorphic!
						} else {
							vertex_hash.put(v.toString(), v);
						}
					}
				}
			}
		}
		
		return (float)polymorphic_methods.size() / (float)class_count;
	}
	private float ClassInterfaceSize(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		ArrayList<Integer> vertexMethodCounts = new ArrayList<Integer>();
		Set<AnnotatedVertex> vertices = g.vertexSet();
		Iterator<AnnotatedVertex> it_v = vertices.iterator();
		AnnotatedVertex v;
		while (it_v.hasNext()) {
			int vertexCount = 0;
			v = it_v.next();
			Set<AnnotatedEdge> edges = g.edgesOf(v);
			AnnotatedEdge e;
			Iterator<AnnotatedEdge> it_e = edges.iterator();
			while (it_e.hasNext()) {
				e = it_e.next();
				// We don't know whether this vertex is the source or the sink
				// (or neither).
				if (e.getSourceVertex().equals(v) &&
						e.getSinkVertex().getType() == AnnotatedVertex.VertexType.OPERATION &&
						e.getSinkVertex().getVisibility() == AnnotatedVertex.Visibility.PUBLIC) {
					++vertexCount;
				} else if (e.getSinkVertex().equals(v) &&
						e.getSourceVertex().getType() == AnnotatedVertex.VertexType.OPERATION &&
						e.getSourceVertex().getVisibility() == AnnotatedVertex.Visibility.PUBLIC) {
					++vertexCount;
				}
				vertexMethodCounts.add(vertexCount);
			}
		}
		Iterator<Integer> it_count = vertexMethodCounts.iterator();
		float average_num_methods = 0.0F;
		while (it_count.hasNext()) {
			average_num_methods += it_count.next();
		}
		return average_num_methods / (float)vertexMethodCounts.size();
	}
	private float NumberOfMethods(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		ArrayList<Integer> vertexMethodCounts = new ArrayList<Integer>();
		Set<AnnotatedVertex> vertices = g.vertexSet();
		Iterator<AnnotatedVertex> it_v = vertices.iterator();
		AnnotatedVertex v;
		while (it_v.hasNext()) {
			int vertexCount = 0;
			v = it_v.next();
			Set<AnnotatedEdge> edges = g.edgesOf(v);
			AnnotatedEdge e;
			Iterator<AnnotatedEdge> it_e = edges.iterator();
			while (it_e.hasNext()) {
				e = it_e.next();
				// We don't know whether this vertex is the source or the sink
				// (or neither).
				if (e.getSourceVertex().equals(v) &&
						e.getSinkVertex().getType() == AnnotatedVertex.VertexType.OPERATION) {
					++vertexCount;
				} else if (e.getSinkVertex().equals(v) &&
						e.getSourceVertex().getType() == AnnotatedVertex.VertexType.OPERATION) {
					++vertexCount;
				}
				vertexMethodCounts.add(vertexCount);
			}
		}
		Iterator<Integer> it_count = vertexMethodCounts.iterator();
		float average_num_methods = 0.0F;
		while (it_count.hasNext()) {
			average_num_methods += it_count.next();
		}
		return average_num_methods/((float)vertexMethodCounts.size());
	}
	/**
	 * Computes the number of inherited methods in a class.
	 * graph.
	 * @param g A graph.
	 * @param v A vertex representing a class.
	 * @return The number of inherited methods accessible by the class.
	 */
	private float CountInheritedMethods(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g,
									AnnotatedVertex v)
	{
		// FIXME: This could be made recursive and use memoization so that
		// we don't duplicate a bunch of work.  Currently implemented
		// the simple way for readability.  We probably don't even need to
		// use a stack.
		Set<AnnotatedVertex> visited_vertices = new HashSet<AnnotatedVertex>();
		Stack<AnnotatedVertex> inherited_vertices = new Stack<AnnotatedVertex>();
		ArrayList<AnnotatedEdge> inherit_edges;
		ArrayList<AnnotatedEdge> own_edges;
		AnnotatedEdge temp_edge;
		AnnotatedVertex temp_vert;
		int inherited_methods = 0;
		int start_vertex_methods = 0;
		inherited_vertices.push(v);
		while (!inherited_vertices.empty()) {
			temp_vert = inherited_vertices.pop();
			visited_vertices.add(temp_vert);
			inherit_edges = g.GetEdges(temp_vert, AnnotatedEdge.Label.INHERIT);
			own_edges = g.GetEdges(temp_vert, AnnotatedEdge.Label.OWN);
			// Push the next inherited class onto the stack
			if (!inherit_edges.isEmpty()) {
				// Assumption: single inheritance
				temp_edge = inherit_edges.get(0);
				if (temp_edge.getSourceVertex() == v &&
						!visited_vertices.contains(temp_edge.getSinkVertex())) {
					inherited_vertices.push(temp_edge.getSinkVertex());
				}
			}
			// Don't double-count the methods attached to the starting class
			if (temp_vert == v) {
				start_vertex_methods = own_edges.size();
			} else {
				inherited_methods += own_edges.size();
			}
		}
		if (start_vertex_methods == 0 && inherited_methods == 0) {
			return 0.0F;
		} else {
			return (float)inherited_methods / (float)(start_vertex_methods + inherited_methods);
		}
	}

	private Integer ComputeAncestors(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g, 
								  AnnotatedVertex v,
								  HashMap<String, Integer> hash) {
		ArrayList<AnnotatedEdge> edges = g.GetEdges(v, AnnotatedEdge.Label.INHERIT);
		AnnotatedEdge out_edge = null;
		AnnotatedEdge temp_edge;
		Iterator<AnnotatedEdge> it = edges.iterator();
		while (it.hasNext()) {
			temp_edge = it.next();
			// This vertex must be the edge's "source" vertex since we
			// are following inheritance.
			if (temp_edge.getSourceVertex() == v) {
				out_edge = temp_edge;
				break;
			}
		}
		// Determine whether we've found a root class
		if (out_edge == null) {
			hash.put(v.toString(), 0);
			return 0;
		} else {
			// Has this vertex been processed already?
			if (!hash.containsKey(v.toString())) {
				// Mark this vertex as "visited" to break cycles
				// FIXME
				hash.put(v.toString(), -1);
				// Update the hash
				hash.put(v.toString(), 1 + ComputeAncestors(g, out_edge.getSinkVertex(), hash));
			} else {
			}
			return hash.get(v.toString());
		}
	}
}
