package ec.refactoring;

import java.io.*;
import java.util.*;

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
		ArrayList<String> interfaces = new ArrayList<String>();
		ArrayList<String> operations = new ArrayList<String>();
		Map<String, ArrayList<String>> has_oper = new HashMap<String, ArrayList<String>>();
		Map<String, ArrayList<String>> inherits = new HashMap<String, ArrayList<String>>();
		Map<String, ArrayList<String>> instantiates = new HashMap<String, ArrayList<String>>();
		HashMap<String, ArrayList<String>> calls = new HashMap<String, ArrayList<String>>();
		// Set up file I/O
		FileInputStream fstream = new FileInputStream(Filename);
		DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
		try {
	        // Read fact file line by line
	        String strLine;
	        while ((strLine = br.readLine()) != null) {
	        	// We ignore the following:
	        	// 	* global fields ('$' in the java name)
	        	// 	* java.lang.Object instances
	        	if (strLine.contains("$") || strLine.contains("java")) {
	        		continue;
	        	}

	        	String[] tokens = strLine.split(" ");

	        	if (tokens[0].equals("classes")) {
	        		classes.add(tokens[1]);
	        	} else if (tokens[0].equals("interfaces")) {
	        		interfaces.add(tokens[1]);
	        	} else if (tokens[0].equals("opers")) {
	        		operations.add(tokens[1]);
	        	} else if (tokens[0].equals("has_oper")) {
	        		if (!has_oper.containsKey(tokens[1])) has_oper.put(tokens[1], new ArrayList<String>());
	        		has_oper.get(tokens[1]).add(tokens[2]);
	        	} else if (tokens[0].equals("inherits")) {
	        		if (!inherits.containsKey(tokens[1])) inherits.put(tokens[1], new ArrayList<String>());
	        		inherits.get(tokens[1]).add(tokens[2]);
	        	} else if (tokens[0].equals("instantiates")) {
	        		if (!instantiates.containsKey(tokens[1])) instantiates.put(tokens[1], new ArrayList<String>());
	        		instantiates.get(tokens[1]).add(tokens[2]);
	        	} else if (tokens[0].equals("calls")) {
	        		if (!calls.containsKey(tokens[1])) calls.put(tokens[1], new ArrayList<String>());
	        		calls.get(tokens[1]).add(tokens[2]);
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
        Iterator<String> it_interfaces = interfaces.iterator();
        while (it_interfaces.hasNext()) {
        	g.addVertex(new AnnotatedVertex(it_interfaces.next(),
        			AnnotatedVertex.VertexType.INTERFACE,
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
        	ArrayList<String> snk_v = has_oper.get(src_v); // Value
        	Iterator<String> it = snk_v.iterator();
        	while (it.hasNext()) {
	        	AnnotatedEdge e = new AnnotatedEdge(AnnotatedEdge.Label.OWN);
	        	String s = it.next();
	        	// The following is a hack to get around some odd values
	        	// that get lumped in with has_oper.  TODO: Figure out
	        	// why grok lumps them in.
	        	AnnotatedVertex real_src_v = g.getVertex(src_v);
	        	AnnotatedVertex real_snk_v = g.getVertex(s);
	        	if (!g.containsVertex(real_src_v) ||
	        			!g.containsVertex(real_snk_v)) {
	        		System.err.println("Skipping 'has_oper " + src_v + " " + s + "'");
	        		continue;
	        	}
	        	g.addEdge(real_src_v, real_snk_v, e);
        	}
        }
        Iterator<String> it_inherits = inherits.keySet().iterator();
        while (it_inherits.hasNext()) {
        	String src_v = it_inherits.next();
        	ArrayList<String> snk_v = inherits.get(src_v);
        	Iterator<String> it = snk_v.iterator();
        	while (it.hasNext()) {
	        	AnnotatedEdge e = new AnnotatedEdge(AnnotatedEdge.Label.INHERIT);
	        	g.addEdge(g.getVertex(src_v), g.getVertex(it.next()), e);
        	}
        }
        Iterator<String> it_instantiates = instantiates.keySet().iterator();
        while (it_instantiates.hasNext()) {
        	String src_v = it_instantiates.next();
        	ArrayList<String> snk_v = instantiates.get(src_v);
        	Iterator<String> it = snk_v.iterator();
        	while (it.hasNext()) {
	        	AnnotatedEdge e = new AnnotatedEdge(AnnotatedEdge.Label.INSTANTIATE);
	        	g.addEdge(g.getVertex(src_v), g.getVertex(it.next()), e);
        	}
        }
        Iterator<String> it_calls = calls.keySet().iterator();
        while (it_calls.hasNext()) {
        	String src_v = it_calls.next();
        	ArrayList<String> snk_v = calls.get(src_v);
        	Iterator<String> it = snk_v.iterator();
        	while (it.hasNext()) {
	        	AnnotatedEdge e = new AnnotatedEdge(AnnotatedEdge.Label.CALL);
	        	g.addEdge(g.getVertex(src_v), g.getVertex(it.next()), e);
        	}
        }
	}
}