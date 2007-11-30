package games.stendhal.tools.test;

import java.io.PrintStream;

/**
 * writes parts of the java file for a chat test case
 *
 * @author hendrik
 */
public class JavaWriter {
	private PrintStream out;
	
	JavaWriter(PrintStream out) {
		this.out = out;
	}

	public void emptyLine() {
		out.println();
		out.println("\t\t// -----------------------------------------------");
		out.println();
	}

	public void comment(String line) {
		out.println("\t\t// " + line);
	}
}
