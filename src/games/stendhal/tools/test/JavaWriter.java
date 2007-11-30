package games.stendhal.tools.test;

import java.io.PrintStream;

import marauroa.common.Log4J;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.BeforeClass;

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

	public void header() {
		out.println("package games.stendhal.server.maps.quests;");
		out.println();
		out.println("import static org.junit.Assert.assertEquals;");
		out.println("import games.stendhal.server.entity.npc.SpeakerNPC;");
		out.println("import games.stendhal.server.entity.npc.fsm.Engine;");
		out.println("import games.stendhal.server.entity.player.Player;");
		out.println();
		out.println("import org.junit.BeforeClass;");
		out.println();
		out.println("import utilities.QuestHelper;");
		out.println("public class TODO_Test {");
		out.println();
		out.println("\t@BeforeClass");
		out.println("\tpublic static void setUpBeforeClass() throws Exception {");
		out.println("\t\tQuestHelper.setUpBeforeClass();");
		out.println("\t}");
		out.println();
		out.println("\tpublic void testQuest() {");
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
	
	public void footer() {
		out.println("\t}");
		out.println("}");
	}
}
