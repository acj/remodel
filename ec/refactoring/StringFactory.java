package ec.refactoring;

import java.util.ArrayList;
import java.util.Random;

/**
 * This class holds the vocabulary that is used to name new classes, operations,
 * and other elements in the annotated graph.
 * @author acj
 *
 */
public class StringFactory {
	private static Random rand = null;
	private static ArrayList<String> strings;
	
	public static void Setup() {
		strings = new ArrayList<String>();
		rand = new Random(SourceGraph.RANDOM_SEED);
		strings.add("These");
		strings.add("Are");
		strings.add("Some");
		strings.add("Words");
		strings.add("Seeding");
		strings.add("Approach");
	}
	/**
	 * Returns a randomly-chosen string from the set of available words.
	 */
	public static String GetRandomString() {
		assert strings.size() > 0;
		return strings.get(rand.nextInt(strings.size()));
	}
}
