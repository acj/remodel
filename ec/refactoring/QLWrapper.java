package ec.refactoring;

import java.io.*;
import java.nio.CharBuffer;

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
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("graph.facts"));
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
			//System.out.println("1");
			qlWriter.write("getdb(\"graph.facts\")\n");
			qlWriter.flush();
			Thread.sleep(30,0);
			//while (qlReader.ready()) {
			//	System.out.println("STUFF: " + (char)qlReader.read());
			//}
			qlReader.skip(3);
			qlWriter.write("DP[c,conC,conP,p,methC] = {inherits[conC,c]; owns[conC,methC]; instantiates[methC,conP]; inherits[conP,p]}\n");
			qlWriter.flush();
			Thread.sleep(30,0);
			qlReader.skip(3);
			qlWriter.write("DP\n");
			qlWriter.flush();
			Thread.sleep(30,0);
			
			String buf = "";
			while (qlReader.ready()) {
				buf += (char)qlReader.read();
			}
			buf = buf.replaceAll(">> ", "");
			if (!buf.equals("")) {
				++patternsFound;
				//System.out.println("Candidate: \"" + buf + "\"");
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
}
