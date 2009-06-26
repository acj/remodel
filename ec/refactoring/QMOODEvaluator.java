package ec.refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;

import ec.refactoring.AnnotatedEdge.Label;

public class QMOODEvaluator {
	public static float EvaluateGraph(AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> g) {
		// QMOOD evaluation
		float designSizeInClasses = DesignSizeInClasses(g); 	// Design size
		float avgNumberOfAncestors = AvgNumberOfAncestors(g); 	// Abstraction
		float dataAccessMetric = DataAccessMetric(g); 			// Encapsulation
		float directClassCoupling = DirectClassCoupling(g);		// Coupling
		float numberOfMethods = NumberOfMethods(g);				// Complexity
		float numberOfPolyMethods = NumberOfPolymorphicMethods(g); // Polymorphism
		float classInterfaceSize = ClassInterfaceSize(g);		// Messaging
		float measureOfAggregation = MeasureOfAggregation(g);	// Composition
		float measureOfFunctionalAbstraction = MeasureOfFunctionalAbstraction(g); // Inheritance
		float numberOfHierarchies = NumberOfHierarchies(g);		// Functionality
		
		// Currently no support for method parameters
		float cohesionAmongMethods = 0.0F; // Cohesion
		
		/*
		System.out.println("DSIC: " + designSizeInClasses);
		System.out.println("ANOA: " + avgNumberOfAncestors);
		System.out.println("DAM: " + dataAccessMetric);
		System.out.println("NOM: " + numberOfMethods);
		System.out.println("NOPM: " + numberOfPolyMethods);
		System.out.println("CIF: " + classInterfaceSize);
		System.out.println("MOA: " + measureOfAggregation);
		System.out.println("MOFA: " + measureOfFunctionalAbstraction);
		System.out.println("NOH: " + numberOfHierarchies);
		*/
		
		/**
		 * Reusability: -0.25*Coupling + 0.25*Cohesion + 0.5*Messaging + 0.5*DesignSize
		 * Flexibility: 0.25*Encapsulation - 0.25*Coupling + 0.5*Composition + 0.5*Polymorphism
		 * Understandability: -0.33*Abstraction + 0.33*Encapsulation - 0.33*Coupling +
		 * 					0.33*Cohesion - 0.33*Polymorphism - 0.33*Complexity -
		 * 					0.33*DesignSize
		 * Functionality: 0.12*Cohesion + 0.22*Polymorphism + 0.22*Messaging +
		 * 					0.22*DesignSize + 0.22*Hierarchies
		 * Extendibility: 0.5*Abstraction - 0.5*Coupling + 0.5*Inheritance +
		 * 					0.5*Polymorphism
		 * Effectiveness: 0.2*Abstraction + 0.2*Encapsulation + 0.2*Composition +
		 * 					0.2*Inheritance + 0.2*Polymorphism
		 */
		final float reusability = 0.5F*designSizeInClasses -
							0.25F*directClassCoupling + 
							0.5F*classInterfaceSize;
		final float flexibility = 0.25F*dataAccessMetric - 
							0.25F*directClassCoupling + 
							0.5F*measureOfAggregation + 
							0.5F*numberOfPolyMethods;
		final float understandability = -0.33F*designSizeInClasses -
							0.33F*avgNumberOfAncestors +
							0.33F*dataAccessMetric -
							0.33F*directClassCoupling -
							0.33F*numberOfPolyMethods -
							0.33F*numberOfMethods;
		final float functionality = -0.12F*cohesionAmongMethods +
							0.22F*numberOfPolyMethods +
							0.22F*classInterfaceSize +
							0.22F*designSizeInClasses +
							0.22F*numberOfHierarchies;
		final float extendibility = 0.5F*avgNumberOfAncestors -
							0.5F*directClassCoupling +
							0.5F*measureOfFunctionalAbstraction +
							0.5F*numberOfPolyMethods;
		final float effectiveness = 0.2F*avgNumberOfAncestors +
							0.2F*dataAccessMetric +
							0.2F*measureOfAggregation +
							0.2F*measureOfFunctionalAbstraction +
							0.2F*numberOfPolyMethods;
		
		// In order: flexibility, reusability, understandability
		//
		// These coefficients determine the relative importance of each
		// non-functional property.
		float[] preferenceMatrix = new float[] { 
				0.1666F, // reusability 
				0.1666F, // flexibility
				0.1666F, // understandability
				0.1666F, // functionality
				0.1666F, // extendibility
				0.1666F  // effectiveness
		};
		
		float QMOOD_value = flexibility * preferenceMatrix[0] +
							reusability * preferenceMatrix[1] +
							understandability * preferenceMatrix[2] +
							functionality * preferenceMatrix[3] +
							extendibility * preferenceMatrix[4] +
							effectiveness * preferenceMatrix[5];
		return QMOOD_value;
	}
	
    /**
     * Metrics
     */
    
    /**
     * Computes the size of the software design by counting the number of
     * classes.
     * 
     * @param g A graph.
     * @return Number of classes in the graph.
     */
	private static float DesignSizeInClasses(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
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
	/**
	 * Computes the number of inheritance hierarchies in the graph.
	 * 
	 * Basic idea: look for classes that are inherited but do not inherit
	 * anything themselves.  In other words, find vertices that have incoming
	 * inheritance edges but do not have outgoing inheritance edges.
	 * 
	 * @param g A graph.
	 * @return Number of inheritance hierarchies in the graph.
	 */
	private static float NumberOfHierarchies(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		float numOfHierarchies = 0F;
		Set<AnnotatedVertex> vertices = g.vertexSet();
		Iterator<AnnotatedVertex> it = vertices.iterator();
		AnnotatedVertex v;
		while (it.hasNext()) {
			v = it.next();
			if (v.getType() == AnnotatedVertex.VertexType.CLASS) {
				ArrayList<AnnotatedEdge> edges = g.GetEdges(v, Label.INHERIT);
				// If this class doesn't inherit, then skip it
				if (edges.size() == 0) {
					continue;
				}
				// Look for outgoing inheritance edges.  If we find any,
				// then we skip this vertex.  Otherwise, increment
				// the number of hierarchies by 1.
				Boolean foundOutgoingInheritance = false;
				Iterator<AnnotatedEdge> inherit_it = edges.iterator();
				while (inherit_it.hasNext()) {
					AnnotatedEdge e = inherit_it.next();
					if (e.getSourceVertex() == v) {
						foundOutgoingInheritance = true;
						break;
					}
				}
				if (foundOutgoingInheritance) {
					// Nothing to do.  This is not an inheritance root.
				} else {
					numOfHierarchies += 1F;
				}
			}
		}
		return numOfHierarchies;
	}
	/**
	 * Computes the average number of ancestors between a given class and the
	 * "root" class.
	 * @param g A graph.
	 * @return Average number of ancestors.
	 */
	private static float AvgNumberOfAncestors(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
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
	private static float DataAccessMetric(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		Vector<Float> dam_ratios = new Vector<Float>();
		float num_private_vars, num_total_vars;
		Set<AnnotatedVertex> vertices = g.vertexSet();
		Iterator<AnnotatedVertex> it = vertices.iterator();
		while (it.hasNext()) {
			num_private_vars = 0.0F;
			num_total_vars = 0.0F;
			AnnotatedVertex v = it.next();
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
			System.out.println(data_access_metric / (float)dam_ratios.size());
			return data_access_metric / (float)dam_ratios.size();
		}
	}
	private static float DirectClassCoupling(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		Set<AnnotatedVertex> vertices = g.vertexSet();
		Iterator<AnnotatedVertex> it_v = vertices.iterator();
		AnnotatedVertex v;
		int classCount = 0; // Number of classes in the system
		int vertexClassCount = 0; // Number of classes the vertex is related to
		float dacTotal = 0.0F;
		while(it_v.hasNext()) {
			v = it_v.next();
			if (v.getType() != AnnotatedVertex.VertexType.CLASS) {
				continue;
			}
			
			++classCount;
			vertexClassCount = 0;
			Set<AnnotatedEdge> edges = g.edgesOf(v);
			Iterator<AnnotatedEdge> it_e = edges.iterator();
			AnnotatedEdge e;
			while (it_e.hasNext()) {
				e = it_e.next();
				// We don't know whether this vertex is the source or the sink
				// (or neither).
				if (e.getSourceVertex().equals(v) &&
						e.getSinkVertex().getType() == AnnotatedVertex.VertexType.CLASS) {
					++vertexClassCount;
				} else if (e.getSinkVertex().equals(v) &&
						e.getSourceVertex().getType() == AnnotatedVertex.VertexType.CLASS) {
					++vertexClassCount;
				}
			}
			dacTotal += vertexClassCount;
		}
		return dacTotal / (float)classCount;
	}
	private float CohesionAmongClassMethods(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		return 0.0F;
	}
	private static float MeasureOfAggregation(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
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
	private static float MeasureOfFunctionalAbstraction(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
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
	private static float NumberOfPolymorphicMethods(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
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
	private static float ClassInterfaceSize(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
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
	private static float NumberOfMethods(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
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
	private static float CountInheritedMethods(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g,
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

	private static Integer ComputeAncestors(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g, 
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
