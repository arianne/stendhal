/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.server.maps.quests;

import static org.junit.Assert.assertEquals;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.ados.abandonedkeep.OrcKillGiantDwarfNPC;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

/**
 * JUnit test for the KillDhohrNuggetcutter quest.
 * @author bluelads, M. Fuchs
 */
public class KillDhohrNuggetcutterTest extends ZonePlayerAndNPCTestImpl {

	private SpeakerNPC npc = null;
	private Engine en = null;

	private String questSlot;
	private static final String ZONE_NAME = "-1_ados_abandoned_keep";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public KillDhohrNuggetcutterTest() {
		super(ZONE_NAME, "Zogfang");
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		new OrcKillGiantDwarfNPC().configureZone(zone, null);

		quest = new KillDhohrNuggetcutter();
		quest.addToWorld();

		questSlot = quest.getSlotName();
	}

	@Test
	public void testQuest() {
		npc = SingletonRepository.getNPCList().get("Zogfang");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello my fine fellow. Welcome to Ados Abandoned Keep, our humble dwelling!", getReply(npc));
		en.step(player, "task");
		assertEquals("We are unable to rid our area of dwarves. Especially one mighty one named Dhohr Nuggetcutter. Would you please kill them?", getReply(npc));
		en.step(player, "no");
		assertEquals("Ok, I will await someone having the guts to have the job done.", getReply(npc));
		assertEquals("rejected", player.getQuest(questSlot));
		en.step(player, "bye");
		assertEquals("I wish you well on your journeys.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Hello my fine fellow. Welcome to Ados Abandoned Keep, our humble dwelling!", getReply(npc));
		en.step(player, "task");
		assertEquals("We are unable to rid our area of dwarves. Especially one mighty one named Dhohr Nuggetcutter. Would you please kill them?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Great! Please find all wandering #dwarves somewhere in this level of the keep and make them pay for their tresspassing!", getReply(npc));
		assertEquals("start", player.getQuest(questSlot, 0));
		en.step(player, "bye");
		assertEquals("I wish you well on your journeys.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Just go kill Dhohr Nuggetcutter and his minions; the mountain leader, hero and elder dwarves. Even the simple mountain dwarves are a danger to us, kill them too.", getReply(npc));
		en.step(player, "task");
		assertEquals("I already asked you to kill Dhohr Nuggetcutter!", getReply(npc));
		en.step(player, "bye");
		assertEquals("I wish you well on your journeys.", getReply(npc));

		// kill Dhohr Nuggetcutter
		player.setSoloKill("Dhohr Nuggetcutter");
		en.step(player, "hi");
		assertEquals("Just go kill Dhohr Nuggetcutter and his minions; the mountain leader, hero and elder dwarves. Even the simple mountain dwarves are a danger to us, kill them too.", getReply(npc));
		en.step(player, "task");
		assertEquals("I already asked you to kill Dhohr Nuggetcutter!", getReply(npc));
		en.step(player, "done");
		en.step(player, "bye");
		assertEquals("I wish you well on your journeys.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Just go kill Dhohr Nuggetcutter and his minions; the mountain leader, hero and elder dwarves. Even the simple mountain dwarves are a danger to us, kill them too.", getReply(npc));
		en.step(player, "task");
		assertEquals("I already asked you to kill Dhohr Nuggetcutter!", getReply(npc));
		en.step(player, "bye");
		assertEquals("I wish you well on your journeys.", getReply(npc));

		// kill some other of the enemies
		player.setSoloKill("mountain leader dwarf");
		player.setSoloKill("mountain leader dwarf");
		en.step(player, "hi");
		assertEquals("Just go kill Dhohr Nuggetcutter and his minions; the mountain leader, hero and elder dwarves. Even the simple mountain dwarves are a danger to us, kill them too.", getReply(npc));
		en.step(player, "task");
		assertEquals("I already asked you to kill Dhohr Nuggetcutter!", getReply(npc));
		player.setSoloKill("mountain orc");
		player.setSoloKill("mountain orc");
		en.step(player, "bye");
		assertEquals("I wish you well on your journeys.", getReply(npc));

		// now kill all remaining creatures
		player.setSoloKill("orc hunter");
		player.setSoloKill("mountain orc warrior");
		player.setSoloKill("mountain orc");
		player.setSoloKill("mountain hero dwarf");
		player.setSoloKill("mountain hero dwarf");
		player.setSoloKill("mountain elder dwarf");
		player.setSoloKill("mountain elder dwarf");
		player.setSoloKill("mountain leader dwarf");
		player.setSoloKill("mountain leader dwarf");
		player.setSoloKill("mountain dwarf");
		player.setSoloKill("mountain dwarf");

		en.step(player, "hi");
		assertEquals("Thank you so much. You are a warrior, indeed! Here, have one of these. We have found them scattered about. We have no idea what they are.", getReply(npc));
		assertEquals(4000, player.getXP());
		assertEquals("killed", player.getQuest(questSlot, 0));

		en.step(player, "task");
		assertEquals("Thank you for helping us. Maybe you could come back later. The dwarves might return. Try coming back in 2 weeks.", getReply(npc));
		en.step(player, "bye");
		assertEquals("I wish you well on your journeys.", getReply(npc));
	}
}
