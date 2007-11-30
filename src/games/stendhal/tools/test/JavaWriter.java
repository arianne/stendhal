package games.stendhal.tools.test;

import java.io.PrintStream;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * writes parts of the java file for a chat test case
 *
 * @author hendrik
 */
class JavaWriter {
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

	public void player(String protagonist, String text) {
		out.println("\t\ten.step(player, \"" + StringEscapeUtils.escapeJava(text) + "\");");
	}

	public void npc(String protagonist, String text) {
		out.println("\t\tassertEquals(\""
			+ StringEscapeUtils.escapeJava(text)
			+ "\", " + protagonist + ".get(\"text\"));");
	}
}
