package ec.refactor;

import java.util.HashMap;

import org.jgrapht.*;
import org.jgrapht.graph.*;

public class AnnotatedGraph<V, E> extends Pseudograph<V, E> {
    private static final long serialVersionUID = 4984872244526792365L;
    
    HashMap<String, V> vertexHash;
    
    public AnnotatedGraph(EdgeFactory<V, E> ef)
    {
        super(ef);
        vertexHash = new HashMap<String, V>();
    }
    public AnnotatedGraph(Class<? extends E> edgeClass)
    {
        this(new ClassBasedEdgeFactory<V, E>(edgeClass));
    }
    /*
	public boolean addEdge(V arg0, V arg1, E arg2) {
		return super.addEdge(arg0, arg1, arg2);
	}
	*/
    public V getVertex(String name) {
    	return vertexHash.get(name);
    }
	public boolean addVertex(V arg0) {
		vertexHash.put(arg0.toString(), arg0);
		return super.addVertex(arg0);
	}
	public boolean removeVertex(String arg0) {
		V v = vertexHash.get(arg0);
		vertexHash.remove(arg0);
		return super.removeVertex(v);
	}
}
