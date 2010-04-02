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
		out.println("import games.stendhal.server.core.engine.SingletonRepository;");
		out.println("import games.stendhal.server.core.engine.StendhalRPZone;");
		out.println("import games.stendhal.server.entity.npc.SpeakerNPC;");
		out.println("import games.stendhal.server.entity.npc.fsm.Engine;");
		out.println("import games.stendhal.server.entity.player.Player;");
		out.println();
		out.println("import org.junit.Before;");
		out.println("import org.junit.BeforeClass;");
		out.println("import org.junit.Test;");
		out.println();
		out.println("import utilities.PlayerTestHelper;");
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
		out.println("\t\tfinal StendhalRPZone zone = new StendhalRPZone(\"admin_test\");");
		out.println("\t\tnew TODO_NPC().configureZone(zone, null);	");
		out.println("\t\t");
		out.println();
		out.println("\t\tAbstractQuest quest = new TODO_Quest();");
		out.println("\t\tquest.addToWorld();");
		out.println();
		out.println("\t\tplayer = PlayerTestHelper.createPlayer(\"bob\");");
		out.println("\t}");
		out.println();
		out.println("\t@Test");
		out.println("\tpublic void testQuest() {");
		out.println("\t\t");
		out.println("\t\tnpc = SingletonRepository.getNPCList().get(TODO_Name);");
		out.println("\t\ten = npc.getEngine();");
		out.println();
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
