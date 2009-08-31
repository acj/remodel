package ec.refactoring;

import org.jgrapht.graph.DefaultEdge;

public class AnnotatedEdge extends DefaultEdge {
    private static final long serialVersionUID = 765973795445L;
    private Label label;
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
}
