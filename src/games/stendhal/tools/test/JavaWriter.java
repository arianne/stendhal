/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.tools.test;

import java.io.PrintStream;

import com.google.common.escape.Escaper;
import com.google.common.escape.Escapers;

/**
 * Writes parts of the java file for a chat test case.
 *
 * @author hendrik
 */
class JavaWriter {
	private final PrintStream out;
	private Escaper javaStringEscaper = Escapers.builder().addEscape('"', "\"").addEscape('\\', "\\\\").build();

	/**
	 * creates a JavaWriter
	 *
	 * @param out output stream
	 */
	JavaWriter(final PrintStream out) {
		this.out = out;
	}

	/**
	 * writes the header
	 */
	public void header() {
		out.println("/* $Id$ */");
		out.println("/***************************************************************************");
		out.println(" *                   (C) Copyright 2003-2013 - Stendhal                    *");
		out.println(" ***************************************************************************");
		out.println(" ***************************************************************************");
		out.println(" *                                                                         *");
		out.println(" *   This program is free software; you can redistribute it and/or modify  *");
		out.println(" *   it under the terms of the GNU General Public License as published by  *");
		out.println(" *   the Free Software Foundation; either version 2 of the License, or     *");
		out.println(" *   (at your option) any later version.                                   *");
		out.println(" *                                                                         *");
		out.println(" ***************************************************************************/");
		out.println("");
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
		out.println("import utilities.ZonePlayerAndNPCTestImpl;");
		out.println("import static utilities.SpeakerNPCTestHelper.getReply;");
		out.println();
		out.println("public class TODO_Test extends ZonePlayerAndNPCTestImpl {");
		out.println();
		out.println("\tprivate Player player = null;");
		out.println("\tprivate SpeakerNPC npc = null;");
		out.println("\tprivate Engine en = null;");
		out.println();
		out.println("\tprivate String questSlot;");
		out.println("\tprivate static final String ZONE_NAME = \"admin_test\";");
		out.println();
		out.println("\t@BeforeClass");
		out.println("\tpublic static void setUpBeforeClass() throws Exception {");
		out.println("\t\tQuestHelper.setUpBeforeClass();");
		out.println("\t\tsetupZone(ZONE_NAME);");
		out.println("\t}");
		out.println();
		out.println("\tpublic TODO_Test() {");
		out.println("\t\tsuper(ZONE_NAME, TODO_NPC_Name);");
		out.println("\t}");
		out.println();
		out.println("\t@Before");
		out.println("\tpublic void setUp() {");
		out.println("\t\tfinal StendhalRPZone zone = new StendhalRPZone(ZONE_NAME);");
		out.println("\t\tnew TODO_NPC().configureZone(zone, null);");
		out.println();
		out.println("\t\tAbstractQuest quest = new TODO_Quest();");
		out.println("\t\tquest.addToWorld();");
		out.println();
		out.println("\t\tquestSlot = quest.getSlotName();");
		out.println();
		out.println("\t\tplayer = PlayerTestHelper.createPlayer(\"bob\");");
		out.println("\t}");
		out.println();
		out.println("\t@Test");
		out.println("\tpublic void testQuest() {");
		out.println("\t\tnpc = SingletonRepository.getNPCList().get(TODO_NPC_Name);");
		out.println("\t\ten = npc.getEngine();");
		out.println();
	}

	/**
	 * an empty line
	 */
	public void emptyLine() {
		out.println();
		out.println("\t\t// -----------------------------------------------");
		out.println();
	}

	/**
	 * a coment
	 *
	 * @param line content of comment
	 */
	public void comment(final String line) {
		out.println("\t\t// " + line);
	}

	/**
	 * a player action
	 *
	 * @param protagonist the player name
	 * @param text the text said by the player
	 */
	public void player(final String text) {
		out.println("\t\ten.step(player, \""
				+ javaStringEscaper.escape(text) + "\");");
	}

	/**
	 * an npc action
	 *
	 * @param protagonist name of npc
	 * @param text text said by the npc
	 */
	public void npc(final String text) {
		out.println("\t\tassertEquals(\"" + javaStringEscaper.escape(text)
				+ "\", getReply(npc));");
	}

	/**
	 * writes the footer
	 */
	public void footer() {
		out.println("\t}");
		out.println("}");
	}
}
