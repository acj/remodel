package ec.refactor;

import java.util.ArrayList;
import java.util.Set;

public class SourceVertex {
	public enum VertexType {
		CLASS,
		OPERATION,
	}
	private enum Visibility {
		PUBLIC,
		PROTECTED,
		PRIVATE,
	}
	private ArrayList<SourceAttribute> attributes;
	private String name;
	private VertexType type;
	private Visibility visibility;

	/**
	 * Default constructor.
	 * @param The name of the vertex.
	 * @param The type of the vertex.
	 */
	public SourceVertex(String n, VertexType t) {
		name = n;
		type = t;
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
	public void addAttribute(SourceAttribute sa) {
		attributes.add(sa);
	}
	/**
	 * Get the attributes associated with this vertex.
	 * @return An array of attributes.
	 */
	public ArrayList<SourceAttribute> getAttributes() {
		return attributes;
	}
	/**
	 * Replace the current set of attributes.
	 * @param attributes A new set of attributes that will replace the old set.
	 */
	public void setAttributes(ArrayList<SourceAttribute> attributes) {
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