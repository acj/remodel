package ec.refactoring;

import java.util.ArrayList;
import java.util.Iterator;

import ec.refactoring.AnnotatedEdge.Label;

public class Helpers {
	/**
	 * abstractClass
	 * 
	 * This helper function constructs and returns a new interface that reflects
	 * all of the public methods of the class represented by its child node.
	 *
	 * Child nodes:
	 *  - Class c
	 *  - String newName (TODO: Need requirements doc vocabulary for this)
	 *  
	 * @author acj
	 *
	 */
	public static AnnotatedVertex abstractClass(
							AnnotatedVertex c,
							String n,
							AnnotatedGraph<AnnotatedVertex,AnnotatedEdge> ag) {
		int size_start = ag.getSize();
		int verts_added = 0;
		// Construct a new "interface" vertex along with a set of operation
		// vertices to represent the interface methods.
		AnnotatedVertex iface_v = new AnnotatedVertex("NewInterface", 
										AnnotatedVertex.VertexType.INTERFACE,
										AnnotatedVertex.Visibility.PUBLIC);
		ArrayList<AnnotatedEdge> edges = ag.GetEdges(c, AnnotatedEdge.Label.OWN);
		Iterator<AnnotatedEdge> it = edges.iterator();
		ag.addVertex(iface_v);
		++verts_added;
		while (it.hasNext()) {
			AnnotatedEdge next_e = it.next();
			AnnotatedVertex next_v = next_e.getSinkVertex();
			if (next_v.getVisibility() != AnnotatedVertex.Visibility.PUBLIC) {
				continue;
			}
			AnnotatedVertex oper_v  = new AnnotatedVertex(next_v.toString(), 
										next_v.getType(),
										AnnotatedVertex.Visibility.PUBLIC);
			
			ag.addVertex(oper_v);
			++verts_added;
			AnnotatedEdge oper_e = new AnnotatedEdge(AnnotatedEdge.Label.OWN);
			ag.addEdge(iface_v, oper_v, oper_e);
		}
		assert (size_start + verts_added) == ag.getSize();
		return iface_v;
	}
	
	/**
	 * abstractMethod
	 * 
	 * This helper constructs and returns an abstract method that has the same
	 * name and signature as the parameter method.
	 * 
	 * Child nodes:
	 *  - Method m
	 * 
	 * @author acj
	 *
	 */
	/*
	public static AnnotatedVertex abstractMethod(AnnotatedVertex v) {
		AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag =
			SourceGraph.GetCurrentClone();
		
		// Construct an abstract (TODO: how?) method that has the same name
		// and signature as the child method.
		AnnotatedVertex abstract_v = new AnnotatedVertex("NewAbstractMethod",
										AnnotatedVertex.VertexType.OPERATION,
										v.getVisibility());
		ag.addVertex(abstract_v);
	}
	*/
	
	/**
	 * createEmptyClass
	 * 
	 * This helper method creates a new, empty class with the given name,
	 * adds it to the graph, and returns the vertex representing the new class.
	 * 
	 * @param The name of the empty class.
	 * @return The vertex representing the new, empty class.
	 */
	public static AnnotatedVertex createEmptyClass(String newName,
						AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag) {
		AnnotatedVertex empty_v = new AnnotatedVertex(newName,
									AnnotatedVertex.VertexType.CLASS,
									AnnotatedVertex.Visibility.PUBLIC);
		ag.addVertex(empty_v);
		return empty_v;
	}
	
	/**
	 * createWrapperClass
	 * 
	 * This helper function creates a new class that provides the same interface
	 * as its first parameter and implements all of its methods by delegating them
	 * to a private field (named by its third parameter) of the type of that
	 * interface.
	 * 
	 * In other words, it creates a wrapper class so that the "wrapped" object
	 * can be dynamically replaced without affecting classes that use the wrapper.
	 * 
	 * Child nodes:
	 *  - Interface iface
	 *  - String wrapperName
	 *  - String fieldName
	 *  
	 * @author acj
	 */
	public static AnnotatedVertex createWrapperClass(
							AnnotatedVertex iface_v,
							String wrapperName,
							String fieldName,
							AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag) {
		// TODO: Ensure that the child is an interface.
		
		// Construct a new "wrapper" vertex along with a set of operation
		// vertices to represent the wrapped methods.
		AnnotatedVertex wrapper_v = new AnnotatedVertex("NewWrapper", 
										AnnotatedVertex.VertexType.CLASS,
										AnnotatedVertex.Visibility.PUBLIC);
		AnnotatedVertex wrapped_field = new AnnotatedVertex("NewWrappedField",
										AnnotatedVertex.VertexType.FIELD,
										AnnotatedVertex.Visibility.PRIVATE);
		AnnotatedEdge wrapped_e = new AnnotatedEdge(AnnotatedEdge.Label.OWN);
		ag.addEdge(wrapper_v, wrapped_field, wrapped_e);
		
		ArrayList<AnnotatedEdge> edges = ag.GetEdges(iface_v, AnnotatedEdge.Label.OWN);
		Iterator<AnnotatedEdge> it = edges.iterator();
		while (it.hasNext()) {
			AnnotatedEdge next_e = it.next();
			AnnotatedVertex next_v = next_e.getSinkVertex();
			if (next_v.getVisibility() != AnnotatedVertex.Visibility.PUBLIC) {
				continue;
			}
			AnnotatedVertex oper_v  = new AnnotatedVertex(next_v.toString(), 
										next_v.getType(),
										AnnotatedVertex.Visibility.PUBLIC);
			ag.addVertex(oper_v);
			AnnotatedEdge oper_e = new AnnotatedEdge(AnnotatedEdge.Label.OWN);
			ag.addEdge(wrapper_v, oper_v, oper_e);
		}
		return wrapper_v;
	}
	
	/**
	 * This helper function returns a method that, given the same arguments,
	 * will create the same object as the constructor parameter.
	 *
	 * Child nodes:
	 *  - Constructor c
	 *  - String newName
	 *  
	 * @author acj
	 */
	public static AnnotatedVertex makeAbstract(
						AnnotatedVertex c,
						String n,
						AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> ag) {
		
		// Construct an abstract (TODO: how?) method that has the same name
		// and signature as the child method (constructor)
		AnnotatedVertex abstract_v = new AnnotatedVertex(n,
										AnnotatedVertex.VertexType.OPERATION,
										c.getVisibility());
		ag.addVertex(abstract_v);
		AnnotatedEdge e = new AnnotatedEdge(Label.CALL);
		ag.addEdge(abstract_v, c, e);
		return abstract_v;
	}
}
