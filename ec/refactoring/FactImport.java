package ec.refactoring;

import java.io.*;
import java.util.ArrayList;

public class FactImport implements IModelImport {
	public void Import(AnnotatedGraph<AnnotatedVertex, AnnotatedEdge> g,
						String Filename) throws FileNotFoundException {
		try {
			ArrayList<String> classes = new ArrayList<String>();
			ArrayList<String> operations = new ArrayList<String>();
			ArrayList<String> inherits = new ArrayList<String>();
			ArrayList<String> instantiates = new ArrayList<String>();
			ArrayList<String> calls = new ArrayList<String>();
			FileInputStream fstream = new FileInputStream(Filename);
			DataInputStream in = new DataInputStream(fstream);
	        BufferedReader br = new BufferedReader(new InputStreamReader(in));
	        
	        // Read fact file line by line
	        String strLine;
	        while ((strLine = br.readLine()) != null) {
	        	String[] tokens = strLine.split(" ");
	        	if (tokens[0].equals("class")) {
	        		classes.add(tokens[1]);
	        	} else if (tokens[0].equals("operations")) {
	        		operations.add(tokens[1]);
	        	} else if (tokens[0].equals("inherits")) {
	        		inherits.add(tokens[1]);
	        	} else if (tokens[0].equals("instantiates")) {
	        		instantiates.add(tokens[1]);
	        	} else if (tokens[0].equals("calls")) {
	        		calls.add(tokens[1]);
	        	}
	        }
	        //Close the input stream
	        in.close();
        } catch (Exception e){
          System.err.println("Error: " + e.getMessage());
        }
	}

}