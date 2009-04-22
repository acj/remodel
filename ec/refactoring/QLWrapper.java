package ec.refactoring;

import java.io.*;
import java.nio.CharBuffer;

/**
 * This class provides a convenient wrapper around a QL process that is
 * used to query sets of facts for patterns (in the general sense of
 * the word).  It currently looks for patterns of facts that seem
 * to represent design pattern instances.
 * @author acj
 *
 */
public class QLWrapper {
	private static Process qlProcess;
	private static BufferedReader qlError;
	private static BufferedReader qlReader;
	private static BufferedWriter qlWriter;
	
	// Set up a QL instance for pattern detection
	public static void SetupQL() {
		try {
			qlProcess = Runtime.getRuntime().exec("java -Xms128M -Xmx128M -classpath QLDX/lib/jar/ql.jar:QLDX/lib/jar/java_readline.jar ca.uwaterloo.cs.ql.Main");
			qlReader = new BufferedReader(
				new InputStreamReader(qlProcess.getInputStream()));
			qlError = new BufferedReader(
					new InputStreamReader(qlProcess.getErrorStream()));
			qlWriter = new BufferedWriter(
				new OutputStreamWriter(qlProcess.getOutputStream()));
			
			// Flush the input buffer
			System.out.println(qlReader.readLine());
			System.out.println(qlError.readLine());
		} catch (IOException e) {
			System.err.println("Couldn't execute QL");
			e.printStackTrace();
		}
	}
	
	public static int EvaluateGraph(String facts) {
		int patternsFound = 0;
		
		// TODO: This "write to disk and then read it back in" thing is very
		// inefficient.  A ramdisk might be useful here to cut down on disk
		// delay.
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("graph.facts"));
			/*
			System.out.println("============ Writing facts ================\n" + facts + 
					"\n============= Done ============\n\n");
			*/
			out.write(facts);
			out.flush();
			out.close();
		} catch (IOException e) {
			System.err.println("Could not export graph facts!");
		}
		try {
			if (qlError.ready()) {
				System.out.println("ERROR! : " + qlError.readLine());
				System.exit(-1);
			}
			qlWriter.write("getdb(\"graph.facts\")\n");
			qlWriter.flush();
			Thread.sleep(30,0);
			//while (qlReader.ready()) {
			//	System.out.println("STUFF: " + (char)qlReader.read());
			//}
			qlReader.skip(3);
			qlWriter.write("DP[c,conC,conP,p,methC] = {classes[conC]; classes[c]; classes[conP]; classes[p]; opers[methC]; inherits[conC,c]; owns[conC,methC]; instantiates[methC,conP]; inherits[conP,p]}\n");
			qlWriter.flush();
			Thread.sleep(30,0);
			qlReader.skip(3);
			qlWriter.write("DP\n");
			qlWriter.flush();
			Thread.sleep(30,0);
			
			String buf = readAvailableData();
			//System.out.println("Read " + buf.length());
			buf = buf.replaceAll(">> ", "");
			if (buf.length() > 0 && !buf.contains("unresolvable")) {
				// TODO: Need to count the number of instances here
				System.out.println("Stuff: \"" + buf + "\"");
				
				++patternsFound;
			}
			
			if (qlError.ready()) {
				System.out.println("ERROR! : " + qlError.readLine());
				System.exit(-1);
			}
			
			// Reset for the next evaluation
			qlWriter.write("reset\n");
			qlWriter.flush();
			qlReader.skip(3);
			
			while (qlReader.ready()) {
				System.out.println("Skipping leftovers: " + (char)qlReader.read());
			}

		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return patternsFound;
	}
	
	/**
	 * Convenience function that retrieves all available data in the
	 * process's output buffer.
	 * @return
	 */
	private static String readAvailableData() {
		StringBuilder sb = new StringBuilder();
		try {
			while (qlReader.ready()) {
				char[] buf = new char[16384];
				qlReader.read(buf, 0, buf.length);
				sb.append(buf);
				Thread.sleep(50); // Let the buffer have a chance to update
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sb.substring(0, sb.indexOf("\0"));
	}
}
