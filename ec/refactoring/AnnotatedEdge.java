package ec.refactoring;

import org.jgrapht.graph.DefaultEdge;

public class AnnotatedEdge extends DefaultEdge {
    private static final long serialVersionUID = 765973795445L;
    private Label label;
    private Boolean addedByEvolution = false; // To track newly added elements
    public static enum Label {
    	AGGREGATE,
    	ASSOCIATE,
    	CALL,
    	COMPOSE,
    	IMPLEMENT,
    	INHERIT,
    	INSTANTIATE,
    	OWN,
    	REFERENCE,
    }
    public AnnotatedEdge(Label l) {
    	label = l;
    }
    public Label getLabel() {
    	return label;
    }
    public void setLabel(Label l) {
    	label = l;
    }
    public AnnotatedVertex getSourceVertex() {
    	return (AnnotatedVertex)getSource();
    }
    public AnnotatedVertex getSinkVertex() {
    	return (AnnotatedVertex)getTarget();
    }
	/**
	 * Set whether this edge was added by evolution.  (If it
	 * was not added by evolution, then it was part of the original
	 * graph.)
	 * @param addedByEvolution Was this edge added by evolution?
	 */
	public void setAddedByEvolution(Boolean addedByEvolution) {
		this.addedByEvolution = addedByEvolution;
	}
	/**
	 * Return an answer to the question, "Was this edge added by
	 * evolution?"
	 * @return The answer to the question.
	 */
	public Boolean getAddedByEvolution() {
		return addedByEvolution;
	}
}
