package ec.refactoring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public class AnnotatedGraph<V, E> extends DirectedMultigraph<V, E> {
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
	/**
	 * Export the graph into the dot language used by graphviz.
	 * @return String representation suitable for graphviz.
	 */
	public String ToGraphViz() {
		StringBuilder sb = new StringBuilder();
		sb.append("digraph G {\n");
		// For each vertex, create a node
		Iterator<V> it_v = vertexSet().iterator();
		AnnotatedVertex v;
		while (it_v.hasNext()) {
			v = (AnnotatedVertex)it_v.next();
			sb.append("\"" + v.toString() + "\"");
			if (v.getType() == AnnotatedVertex.VertexType.CLASS) {
				sb.append(" [shape=folder]");
			} else {
				sb.append(" [shape=tab]");
			}
			sb.append(";\n");
		}
		Iterator<E> it_e = edgeSet().iterator();
		AnnotatedEdge e;
		while (it_e.hasNext()) {
			e = (AnnotatedEdge)it_e.next();
			sb.append("\"" + e.getSourceVertex().toString() + "\"");
			sb.append(" -> ").append(e.getSinkVertex().toString());
			sb.append("[label=\"").append(e.getLabel().toString());
			sb.append("\"];\n");
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
	
	public V GetRandomVertex(AnnotatedVertex.VertexType t) {
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
		assert vertices.size() > 0;
		return getVertex(SourceGraph.GetRandom().nextInt(vertices.size()));
	}
	
	public int GetGraphId() { return graphId; }
}
