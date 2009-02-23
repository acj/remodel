package ec.refactor;

public class SourceVertex {
	public enum VertexType {
		CLASS,
		OPERATION,
	}
	private String name;
	private VertexType type;
	
	/**
	 * Default constructor.
	 * @param The name of the vertex.
	 * @param The type of the vertex.
	 */
	public SourceVertex(String n, VertexType t) {
		name = n;
		type = t;
	}
	public VertexType getType() {
		return type;
	}
	public String toString() {
		return name;
	}
}
