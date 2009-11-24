package ec.refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.*;
import org.jgrapht.graph.*;

import ec.refactoring.AnnotatedVertex.VertexType;
import ec.refactoring.AnnotatedVertex.Visibility;

public class AnnotatedGraph<V, E> extends DirectedMultigraph<V, E> {
    @Override
	public String toString() {
    	StringBuilder stb = new StringBuilder();
		Iterator<V> it_v = vertexSet().iterator();
		while (it_v.hasNext()) {
			stb.append(it_v.next() + "\n");
		}
		Iterator<E> it_e = edgeSet().iterator();
		while (it_e.hasNext()) {
			AnnotatedEdge e = (AnnotatedEdge)it_e.next();
			stb.append(e.getSourceVertex() + " --(" + e.getLabel() + ")--> " + e.getSinkVertex() + "\n");
		}
		return stb.toString();
	}

	private static final long serialVersionUID = 4984872244526792365L;
    private HashMap<String, V> vertexHash;
    private Vector<V> vertexList;
    private int graphId; // Unique identifier for this graph instance
    
    public AnnotatedGraph(EdgeFactory<V, E> ef)
    {
        super(ef);
        vertexHash = new HashMap<String, V>();
        vertexList = new Vector<V>();
        graphId = SourceGraph.GetNextGraphId();
    }
    public AnnotatedGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<V, E>(edgeClass));
        vertexHash = new HashMap<String, V>();
        vertexList = new Vector<V>();
        graphId = SourceGraph.GetNextGraphId();
    }
    /**
     * Removes all objects associated with the graph instance.
     */
    public void clear() {
    	vertexHash.clear();
    	vertexList.clear();
    }

    public int getSize() {
    	return vertexList.size();
    }
    /*
	public boolean addEdge(V arg0, V arg1, E arg2) {
		return super.addEdge(arg0, arg1, arg2);
	}
	*/
    public V getVertex(String name) {
    	return vertexHash.get(name);
    }
    public V getVertex(int index) {
    	return vertexList.get(index);
    }
	public boolean addVertex(V arg0) {
		vertexHash.put(arg0.toString(), arg0);
		vertexList.add(arg0);
		return super.addVertex(arg0);
	}
	public boolean removeVertex(String arg0) {
		V v = vertexHash.get(arg0);
		vertexHash.remove(arg0);
		vertexList.remove(v);
		return super.removeVertex(v);
	}
	@Override
	public boolean addEdge(V arg0, V arg1, E arg2) {
		// Ensure that we are not duplicating edges
		Set<E> edges = getAllEdges(arg0, arg1);
		Iterator<E> edge_it = edges.iterator();
		AnnotatedEdge new_e = (AnnotatedEdge)arg2;
		AnnotatedEdge e;
		while (edge_it.hasNext()) {
			e = (AnnotatedEdge)edge_it.next();
			if (e.getSourceVertex() == arg0 &&
					e.getSinkVertex() == arg1 &&
					e.getLabel() == new_e.getLabel()) {
				return false;
			}
		}
		return super.addEdge(arg0, arg1, arg2);
	}
	public ArrayList<E> GetEdges(V v, AnnotatedEdge.Label l) {
		ArrayList<E> edges = new ArrayList<E>();
		Set<E> candidates = edgesOf(v);
		Iterator<E> it = candidates.iterator();
		E e;
		while (it.hasNext()) {
			e = it.next();
			if (((AnnotatedEdge)e).getLabel().equals(l)) {
				edges.add(e);
			}
		}
		return edges;
	}
	public ArrayList<V> GetVertices(AnnotatedVertex.VertexType vt) {
		ArrayList<V> vertices = new ArrayList<V>();
		Set<V> candidates = vertexSet();
		Iterator<V> it = candidates.iterator();
		V v;
		while (it.hasNext()) {
			v = it.next();
			if (((AnnotatedVertex)v).getType() == vt) {
				vertices.add(v);
			}
		}
		return vertices;
	}
	/**
	 * Export the graph into the dot language used by graphviz.
	 * @return String representation suitable for graphviz.
	 */
	public String ToGraphViz(String graphTitle) {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph G {\nlabel=\"" + graphTitle + "\"\n");
		// For each vertex, create a node
		Iterator<V> it_v = vertexSet().iterator();
		AnnotatedVertex v;
		while (it_v.hasNext()) {
			v = (AnnotatedVertex)it_v.next();
			sb.append("\"" + v.toString() + "\" [shape=");
			if (v.getType() == AnnotatedVertex.VertexType.CLASS) {
				sb.append("folder");
			} else {
				sb.append("tab");
			}
			if (v.getAddedByEvolution()) {
				sb.append(",color=red");
			}
			sb.append("];\n");
		}
		Iterator<E> it_e = edgeSet().iterator();
		AnnotatedEdge e;
		while (it_e.hasNext()) {
			e = (AnnotatedEdge)it_e.next();
			sb.append("\"" + e.getSourceVertex().toString() + "\"");
			sb.append(" -> ").append("\"" + e.getSinkVertex().toString() + "\"");
			sb.append("[label=\"").append(e.getLabel().toString() + "\"");
			if (e.getAddedByEvolution()) {
				sb.append(",color=red");
			}
			sb.append("];\n");
		}
		// For each edge, create an edge
		sb.append("}\n");
		return sb.toString();
	}
	
	/**
	 * Exports the graph instance as a collection of facts.
	 * @return Fact representation of the graph.
	 */
	public String ToFacts() {
		StringBuilder sb = new StringBuilder();
		
		Iterator<V> it_v = vertexSet().iterator();
		while (it_v.hasNext()) {
			AnnotatedVertex v = (AnnotatedVertex)it_v.next();
			if (v.getType() == AnnotatedVertex.VertexType.CLASS) {
				sb.append("classes " + v.toString() + "\n");
			} else if (v.getType() == AnnotatedVertex.VertexType.FIELD) {
				sb.append("fields " + v.toString() + "\n");
			} else if (v.getType() == AnnotatedVertex.VertexType.INTERFACE) {
				sb.append("interfaces " + v.toString() + "\n");
			} else if (v.getType() == AnnotatedVertex.VertexType.OPERATION) {
				sb.append("opers " + v.toString() + "\n");
			}
		}
		Iterator<E> it_e = edgeSet().iterator();
		while (it_e.hasNext()) {
			AnnotatedEdge e = (AnnotatedEdge)it_e.next();
			if (e.getLabel() == AnnotatedEdge.Label.AGGREGATE) {
				sb.append("aggregates " + e.getSourceVertex().toString() +
						" " + e.getSinkVertex().toString() + "\n");
			} else if (e.getLabel() == AnnotatedEdge.Label.ASSOCIATE) {
				sb.append("associates " + e.getSourceVertex().toString() +
						" " + e.getSinkVertex().toString() + "\n");
			} else if (e.getLabel() == AnnotatedEdge.Label.CALL) {
				sb.append("calls " + e.getSourceVertex().toString() +
						" " + e.getSinkVertex().toString() + "\n");
			} else if (e.getLabel() == AnnotatedEdge.Label.COMPOSE) {
				sb.append("composes " + e.getSourceVertex().toString() +
						" " + e.getSinkVertex().toString() + "\n");
			} else if (e.getLabel() == AnnotatedEdge.Label.IMPLEMENT) {
				sb.append("implements " + e.getSourceVertex().toString() +
						" " + e.getSinkVertex().toString() + "\n");
			} else if (e.getLabel() == AnnotatedEdge.Label.INHERIT) {
				sb.append("inherits " + e.getSourceVertex().toString() +
						" " + e.getSinkVertex().toString() + "\n");
			} else if (e.getLabel() == AnnotatedEdge.Label.INSTANTIATE) {
				sb.append("instantiates " + e.getSourceVertex().toString() +
						" " + e.getSinkVertex().toString() + "\n");
			} else if (e.getLabel() == AnnotatedEdge.Label.OWN) {
				sb.append("owns " + e.getSourceVertex().toString() +
						" " + e.getSinkVertex().toString() + "\n");
			} else if (e.getLabel() == AnnotatedEdge.Label.REFERENCE) {
				sb.append("references " + e.getSourceVertex().toString() +
						" " + e.getSinkVertex().toString() + "\n");
			}
		}
		
		return sb.toString();
	}
	
	public AnnotatedVertex GetRandomVertex(AnnotatedVertex.VertexType t) {
		// This is expensive, but it guarantees that we choose among vertices
		// of the correct type.  This is important for proper tree growing.
		Iterator<V> it = vertexSet().iterator();
		ArrayList<AnnotatedVertex> vertices = new ArrayList<AnnotatedVertex>();
		while (it.hasNext()) {
			AnnotatedVertex v = (AnnotatedVertex)it.next();
			if (v.getType() == t) {
				vertices.add(v);
			}
		}
		
		if (vertices.size() == 0 && t == VertexType.INTERFACE) {
			AnnotatedVertex v = new AnnotatedVertex("IDummy", VertexType.INTERFACE, Visibility.PRIVATE);
			addVertex((V)v);
			vertices.add(v);
		}
		assert vertices.size() > 0;
		return vertices.get(SourceGraph.GetRandom().nextInt(vertices.size()));
	}
	
	public AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> GetPatternSubgraph(String patternData, Boolean includeNeighbors) {
		AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> subgraph =
			new AnnotatedGraph<AnnotatedVertex,AnnotatedEdge>(AnnotatedEdge.class);
		
		String[] patternRoles = patternData.split(" ");
		for (int ndx=1; ndx<patternRoles.length; ++ndx) {
			AnnotatedVertex v = (AnnotatedVertex)getVertex(patternRoles[ndx]);
			AnnotatedVertex v_new = new AnnotatedVertex(v.toString() + "<role>", v.getType(), v.getVisibility());
			subgraph.addVertex(v_new);
			
			if (includeNeighbors) {
				Set<E> edges = outgoingEdgesOf((V)v);
				Iterator<E> edge_it = edges.iterator();
				AnnotatedEdge e;
				while (edge_it.hasNext()) {
					e = (AnnotatedEdge) edge_it.next();
					AnnotatedEdge e_new = new AnnotatedEdge(e.getLabel());
					// Look for one of the role-playing classes first
					AnnotatedVertex v_neighbor = subgraph.getVertex(e.getSinkVertex().toString() + "<role>");
					if (v_neighbor == null) {
						v_neighbor = new AnnotatedVertex(e.getSinkVertex().toString(), e.getSinkVertex().getType(), e.getSinkVertex().getVisibility());
					}
					subgraph.addVertex(v_neighbor);
					subgraph.addEdge(v_new, v_neighbor, e_new);
				}
				edges = incomingEdgesOf((V)v);
				edge_it = edges.iterator();
				while (edge_it.hasNext()) {
					e = (AnnotatedEdge) edge_it.next();
					AnnotatedEdge e_new = new AnnotatedEdge(e.getLabel());
					// Look for one of the role-playing classes first
					AnnotatedVertex v_neighbor = subgraph.getVertex(e.getSourceVertex().toString() + "<role>");
					if (v_neighbor == null) {
						v_neighbor = new AnnotatedVertex(e.getSourceVertex().toString(), e.getSourceVertex().getType(), e.getSourceVertex().getVisibility());
					}
					subgraph.addVertex(v_neighbor);
					subgraph.addEdge(v_neighbor, v_new, e_new);
				}
			}
		}
		
		// If we're not including all neighbors of role-playing vertices in
		// this subgraph, then we only add the neighbors that are ALSO
		// role-playing vertices.
		if (!includeNeighbors) {
			String patternDataPadded = " " + patternData + " ";
			Set<E> edges = edgeSet();
			Iterator<E> edge_it = edges.iterator();
			while (edge_it.hasNext()) {
				AnnotatedEdge e = (AnnotatedEdge) edge_it.next();
				if (patternDataPadded.contains(" " + e.getSourceVertex().toString() + " ") &&
						patternDataPadded.contains(" " + e.getSinkVertex().toString() + " ")) {
					// Both source and sink are role-players.  Add the edge.
					subgraph.addEdge(subgraph.getVertex(e.getSourceVertex().toString() + "<role>"),
							subgraph.getVertex(e.getSinkVertex().toString() + "<role>"),
							new AnnotatedEdge(e.getLabel()));
				}
			}
		}
		
		return subgraph;
	}
	
	public int GetGraphId() { return graphId; }
}
