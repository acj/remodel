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
    private Random rand;
    
    public AnnotatedGraph(EdgeFactory<V, E> ef)
    {
        super(ef);
        vertexHash = new HashMap<String, V>();
        vertexList = new Vector<V>();
        rand = new Random();
    }
    public AnnotatedGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<V, E>(edgeClass));
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
			sb.append(v.toString());
			if (v.getType() == AnnotatedVertex.VertexType.CLASS) {
				sb.append("[shape=folder]");
			} else {
				sb.append("[shape=tab]");
			}
			sb.append(";\n");
		}
		Iterator<E> it_e = edgeSet().iterator();
		AnnotatedEdge e;
		while (it_e.hasNext()) {
			e = (AnnotatedEdge)it_e.next();
			sb.append(e.getSourceVertex().toString()).append(" -> ").append(e.getSinkVertex().toString()).append("[label=\"").append(e.getLabel().toString()).append("\"];\n");
		}
		// For each edge, create an edge
		sb.append("}\n");
		return sb.toString();
	}
	
	public V GetRandomVertex() {
		return getVertex(rand.nextInt(this.getSize()));
	}
}
