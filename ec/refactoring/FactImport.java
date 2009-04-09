package ec.refactoring;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class builds an annotated graph based on the object-oriented fact
 * collection in the given file.
 * @author acj
 *
 */
public class FactImport implements IModelImport {
	public void Import(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g,
						String Filename) throws FileNotFoundException {
		ArrayList<String> classes = new ArrayList<String>();
		ArrayList<String> operations = new ArrayList<String>();
		Map<String, String> has_oper = new HashMap<String, String>();
		Map<String, String> inherits = new HashMap<String, String>();
		Map<String, String> instantiates = new HashMap<String, String>();
		Map<String, String> calls = new HashMap<String, String>();
		// Set up file I/O
		FileInputStream fstream = new FileInputStream(Filename);
		DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {
	        // Read fact file line by line
	        String strLine;
	        while ((strLine = br.readLine()) != null) {
	        	if (strLine.contains("$")) {
	        		continue;
	        	}
	        	String[] tokens = strLine.split(" ");
	        	if (tokens[0].equals("classes")) {
	        		classes.add(tokens[1]);
	        	} else if (tokens[0].equals("opers")) {
	        		operations.add(tokens[1]);
	        	} else if (tokens[0].equals("has_oper")) {
	        		has_oper.put(tokens[1], tokens[2]);
	        	} else if (tokens[0].equals("inherits")) {
	        		inherits.put(tokens[1], tokens[2]);
	        	} else if (tokens[0].equals("instantiates")) {
	        		instantiates.put(tokens[1], tokens[2]);
	        	} else if (tokens[0].equals("calls")) {
	        		calls.put(tokens[1], tokens[2]);
	        	}
	        }
	        //Close the input stream
	        in.close();
        } catch (Exception e){
          System.err.println("Error: " + e.getMessage());
        }
        
        Iterator<String> it_classes = classes.iterator();
        while (it_classes.hasNext()) {
        	g.addVertex(new AnnotatedVertex(it_classes.next(),
        			AnnotatedVertex.VertexType.CLASS,
        			AnnotatedVertex.Visibility.PUBLIC));
        }
        Iterator<String> it_opers = operations.iterator();
        while (it_opers.hasNext()) {
        	g.addVertex(new AnnotatedVertex(it_opers.next(),
        			AnnotatedVertex.VertexType.OPERATION,
        			AnnotatedVertex.Visibility.PUBLIC));
        }
        Iterator<String> it_hasoper = has_oper.keySet().iterator();
        while (it_hasoper.hasNext()) {
        	String src_v = it_hasoper.next(); // Key
        	String snk_v = has_oper.get(src_v); // Value
        	AnnotatedEdge e = new AnnotatedEdge(AnnotatedEdge.Label.OWN);
        	g.addEdge(g.getVertex(src_v), g.getVertex(snk_v), e);
        }
        Iterator<String> it_inherits = inherits.keySet().iterator();
        while (it_inherits.hasNext()) {
        	String src_v = it_inherits.next();
        	String snk_v = inherits.get(src_v);
        	AnnotatedEdge e = new AnnotatedEdge(AnnotatedEdge.Label.INHERIT);
        	g.addEdge(g.getVertex(src_v), g.getVertex(snk_v), e);
        }
        Iterator<String> it_instantiates = instantiates.keySet().iterator();
        while (it_instantiates.hasNext()) {
        	String src_v = it_instantiates.next();
        	String snk_v = instantiates.get(src_v);
        	AnnotatedEdge e = new AnnotatedEdge(AnnotatedEdge.Label.INSTANTIATE);
        	g.addEdge(g.getVertex(src_v), g.getVertex(snk_v), e);
        }
        Iterator<String> it_calls = calls.keySet().iterator();
        while (it_calls.hasNext()) {
        	String src_v = it_calls.next();
        	String snk_v = calls.get(src_v);
        	AnnotatedEdge e = new AnnotatedEdge(AnnotatedEdge.Label.CALL);
        	g.addEdge(g.getVertex(src_v), g.getVertex(snk_v), e);
        }
	}

}