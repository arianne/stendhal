package games.stendhal.tools.test;

import java.io.PrintStream;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * Writes parts of the java file for a chat test case.
 * 
 * @author hendrik
 */
class JavaWriter {
	private final PrintStream out;

	JavaWriter(final PrintStream out) {
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
		out.println("import org.junit.Before;");
		out.println("import org.junit.BeforeClass;");
		out.println("import org.junit.Test;");
		out.println();
		out.println("import utilities.QuestHelper;");
		out.println("import static utilities.SpeakerNPCTestHelper.getReply;");
		out.println();
		out.println("public class TODO_Test {");
		out.println();
		out.println("\tprivate Player player = null;");
		out.println("\tprivate SpeakerNPC npc = null;");
		out.println("\tprivate Engine en = null;");
		out.println();
		out.println("\t@BeforeClass");
		out.println("\tpublic static void setUpBeforeClass() throws Exception {");
		out.println("\t\tQuestHelper.setUpBeforeClass();");
		out.println("\t}");
		out.println();
		out.println("\t@Before");
		out.println("\tpublic void setUp() {");
		out.println("\t\tnpc = new SpeakerNPC(TODO);");
		out.println("\t\tNPCList.get().add(npc);");
		out.println("\t\tSpeakerNPCFactory npcConf = new TODONPC();");
		out.println("\t\tnpcConf.createDialog(npc);");
		out.println();
		out.println("\t\tAbstractQuest quest = new TODO();");
		out.println("\t\tquest.addToWorld();");
		out.println("\t\ten = npc.getEngine();");
		out.println();
		out.println("\t\tplayer = PlayerTestHelper.createPlayer();");
		out.println("\t}");
		out.println();
		out.println("\t@Test");
		out.println("\tpublic void testQuest() {");
	}

	public void emptyLine() {
		out.println();
		out.println("\t\t// -----------------------------------------------");
		out.println();
	}

	public void comment(final String line) {
		out.println("\t\t// " + line);
	}

	public void player(final String protagonist, final String text) {
		out.println("\t\ten.step(player, \""
				+ StringEscapeUtils.escapeJava(text) + "\");");
	}

	public void npc(final String protagonist, final String text) {
		out.println("\t\tassertEquals(\"" + StringEscapeUtils.escapeJava(text)
				+ "\", getReply(npc));");
	}

	public void footer() {
		out.println("\t}");
		out.println("}");
	}
}
