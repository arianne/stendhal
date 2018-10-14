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
package games.stendhal.server.maps.quests;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.plains.MillerNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

/**
 * Tests for Jenny's quest to kill gnomes
 *
 * @author IschBing, hendrik
 */
public class KillGnomesTest {
	private static final String QUEST_VALUE_STARTED = "start;cavalryman gnome,0,1,0,0,gnome,0,1,0,0,infantry gnome,0,1,0,0";

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;
	private static String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new MillerNPC().configureZone(zone, null);
		AbstractQuest quest = new KillGnomes();
		questSlot = quest.getSlotName();
		quest.addToWorld();
	}


	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("bob");
		npc = SingletonRepository.getNPCList().get("Jenny");
		en = npc.getEngine();
	}


	/**
	 * ask for the quest.
	 */
	@Test
	public void testAskForQuest() {

		// Ask for the quest
		en.step(player, "hi");
		assertEquals("Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.", getReply(npc));
		en.step(player, "task");
		assertEquals("Some gnomes have been stealing carrots from the farms North of Semos. They need to be taught a lesson, will you help?", getReply(npc));

		// Accept quest
		en.step(player, "yes");
		assertEquals("Excellent. You'll find the gnomes camped out, north west of Semos. Make sure you kill some of the ringleaders, too, at least one infantryman and one cavalryman.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		assertThat(player.getQuest(questSlot), equalTo(QUEST_VALUE_STARTED));
	}


	/**
	 * return without having killed the gomes but trying to cheat Jenny
	 */
	@Test
	public void returnWithoutCompleting() {
		player.setQuest(questSlot, QUEST_VALUE_STARTED);
		en.step(player, "hi");
		assertEquals("Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.", getReply(npc));
		en.step(player, "done");
		assertEquals("You need to teach those pesky gnomes a lesson, by killing some as an example! Make sure you get the leaders, too, at least one infantryman and one cavalryman.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		assertThat(player.getQuest(questSlot), equalTo(QUEST_VALUE_STARTED));
	}


	/**
	 * Now kills the gnomes and complete the quest correctly.
	 */
	@Test
	public void returnAfterCompleting() {
		player.setQuest(questSlot, QUEST_VALUE_STARTED);

		// kill gnomes
		player.setSoloKill("gnome");
		player.setSoloKill("infantry gnome");
		player.setSoloKill("cavalryman gnome");

		// complete quest
		en.step(player, "hi");
		assertEquals("Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.", getReply(npc));
		en.step(player, "done");
		assertEquals("I see you have killed the gnomes as I asked. I hope they will stay away from the carrots for a while! Please take these potions as a reward.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		assertThat(player.getQuest(questSlot, 0), equalTo("killed"));
	}


	/**
	 * Ask again directly after the quest was completed
	 */
	@Test
	public void askForQuestAgain() {
		player.setQuest(questSlot, "killed;" + System.currentTimeMillis());

		// ask for quest again
		en.step(player, "hi");
		assertEquals("Greetings! I am Jenny, the local miller. If you bring me some #grain, I can #mill it into flour for you.", getReply(npc));
		en.step(player, "task");
		assertEquals("The gnomes haven't made any trouble since you last taught them a lesson.", getReply(npc));

		// help should still work
		en.step(player, "help");
		assertEquals("Do you know the bakery in Semos? I'm proud to say they use my flour. But the wolves ate my delivery boy again recently... they're probably running out.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
}
