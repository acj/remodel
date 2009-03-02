package ec.refactor;

public class SourceAttribute {
	public enum Visibility {
		PUBLIC,
		PROTECTED,
		PRIVATE,
	}
	private String dataType;
	private String name;
	private Visibility visibility; 
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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