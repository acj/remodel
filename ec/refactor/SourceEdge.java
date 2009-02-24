package ec.refactor;

import org.jgrapht.graph.DefaultEdge;

public class SourceEdge extends DefaultEdge {
    private static final long serialVersionUID = 765973795445L;
    private Label label;
    public static enum Label {
    	AGGREGATE,
    	ASSOCIATE,
    	CALL,
    	COMPOSE,
    	INHERIT,
    	OWN,
    	REFERENCE,
    }
    public SourceEdge(Label l) {
    	label = l;
    }
    public Label getLabel() {
    	return label;
    }
    public void setLabel(Label l) {
    	label = l;
    }
}
