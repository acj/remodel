package ec.refactoring;

import java.util.ArrayList;

public class AnnotatedVertex {
	public enum VertexType {
		CLASS,
		FIELD,
		INTERFACE,
		OPERATION,
	}
	public enum Visibility {
		PUBLIC,
		PROTECTED,
		PRIVATE,
	}
	private ArrayList<AnnotatedAttribute> attributes;
	private String name;
	private VertexType type;
	private Visibility visibility;

	/**
	 * Default constructor.
	 * @param The name of the vertex.
	 * @param The type of the vertex.
	 */
	public AnnotatedVertex(String n, VertexType t, Visibility v) {
		name = n;
		type = t;
		visibility = v;
		attributes = new ArrayList<AnnotatedAttribute>();
	}
	/**
	 * Get the type (class, operation, etc.) of the vertex.
	 * @return The type of the vertex.
	 */
	public VertexType getType() {
		return type;
	}
	/**
	 * Get the name (data type) of the vertex.
	 * @return The name (data type) of the vertex.
	 */
	public String toString() {
		return name;
	}
	public void addAttribute(AnnotatedAttribute sa) {
		attributes.add(sa);
	}
	/**
	 * Get the attributes associated with this vertex.
	 * @return An array of attributes.
	 */
	public ArrayList<AnnotatedAttribute> getAttributes() {
		return attributes;
	}
	/**
	 * Replace the current set of attributes.
	 * @param attributes A new set of attributes that will replace the old set.
	 */
	public void setAttributes(ArrayList<AnnotatedAttribute> attributes) {
		this.attributes = attributes;
	}
	/**
	 * Get the visibility of the vertex.
	 * @return Visibility of the vertex.
	 */
	public Visibility getVisibility() {
		return visibility;
	}
	/**
	 * Set the visibility of the vertex.
	 * @param visibility The new visibility of the vertex.
	 */
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
	}
}