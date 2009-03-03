package ec.refactor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import ec.EvolutionState;
import ec.Individual;
import ec.vector.IntegerVectorIndividual;
import ec.simple.SimpleProblemForm;
import ec.simple.SimpleFitness;
import ec.Problem;

public class RefactorProblem extends Problem implements SimpleProblemForm {

	public void describe(Individual ind, EvolutionState state,
			int subpopulation, int threadnum, int log, int verbosity) {
		// TODO Auto-generated method stub

	}

	public void evaluate(EvolutionState state, Individual ind,
			int subpopulation, int threadnum) {
		final int[] genome = (int[])((IntegerVectorIndividual)ind).getGenome();
		RefactorCPU cpu = new RefactorCPU();
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g = SourceGraph.GetInstance().clone();
		cpu.SetGenome(genome);
		cpu.SimulateGenome(g);
		SimpleFitness fit = new SimpleFitness();
		Float fitness_value = 0F;
		//fitness_value = 100 - g.edgeSet().size() - (float)g.getSize();
		fitness_value = 100 - AvgNumberOfAncestors(g) + DataAccessMetric(g) - NumberOfMethods(g) - ClassInterfaceSize(g);
		fit.setFitness(state, fitness_value, false);
		ind.fitness = fit;
		
		//g.clear();
		g = null;
		cpu = null;
	}

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
		return 0.0F;
	}
	private float MeasureOfFunctionalAbstraction(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		return 0.0F;
	}
	private float NumberOfPolymorphicMethods(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g) {
		// Look for methods in the same class that have the same name.  Could do this with a hash
		// to make it O(n) in the number of vertices (if you see a collision, it's polymorphic).
		return 0.0F;
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
				hash.put(v.toString(), -1);
				// Update the hash
				hash.put(v.toString(), 1 + ComputeAncestors(g, out_edge.getSinkVertex(), hash));
			} else {
			}
			return hash.get(v.toString());
		}
	}
}
