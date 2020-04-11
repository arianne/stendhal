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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.magic.school.GroundskeeperNPC;
import games.stendhal.server.maps.magic.school.SpidersCreatures;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class KillSpidersTest extends SpidersCreatures {

	private static final String NPC_HELLO = "Hello my friend. Nice day for walking isn't it?";
	private static final String NPC_BYE = "Bye.";
	private static final String NPC_OFFER_QUEST = "Have you ever been to the basement of the school? The room is full of spiders and some could be dangerous, since the students do experiments! Would you like to help me with this 'little' problem?";
	private static final String NPC_ACKNOWLEDGE_QUEST_ACCEPTED = "Fine. Go down to the basement and kill all the creatures there!";
	private static final String NPC_QUEST_ALREADY_OFFERED = "I already asked you to kill all creatures in the basement!";

	private static String questSlot = "kill_all_spiders";

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;
	private final static StendhalRPZone basement = new StendhalRPZone("spiders_zone",100,100);
	private final static AbstractQuest quest = new KillSpiders();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		final StendhalRPWorld world = MockStendlRPWorld.get();
		QuestHelper.setUpBeforeClass();

		final StendhalRPZone zone = new StendhalRPZone("int_semos_guard_house");
		new GroundskeeperNPC().configureZone(zone, null);

		world.addRPZone(zone);
		world.addRPZone(basement);
		quest.addToWorld();
	}

	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
		configureZone(basement, null);
		npc = SingletonRepository.getNPCList().get("Morgrin");
		en = npc.getEngine();
	}

	/**
	 * function for emulating killing of creature by player.
	 * @param name - creature's name
	 */
	private void killSpider(String name) {
		final Creature creature = new Creature();
		creature.put("class", "");
		creature.put("subclass", "");
		creature.setName(name);
		creature.setHP(1);
		creature.setAtkXP(1);
		creature.setDefXP(1);
		creature.setSounds(new LinkedList<String>());
		final Creature spider = new Creature(creature);
		spider.registerObjectsForNotification(observer);
		player.teleport(basement, 5, 5, null, player);
		StendhalRPAction.placeat(basement, spider, 51, 50);
		spider.onDead(player, true);
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		double oldkarma = player.getKarma();
		LinkedList<String> questHistory = new LinkedList<String>();

		assertTrue(quest.getHistory(player).isEmpty());
		en.step(player, "hi");
		assertEquals("Hello my friend. Nice day for walking isn't it?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Fine fine, I hope you enjoy your day.", getReply(npc));

		en.step(player, "task");
		assertEquals("Have you ever been to the basement of the school? The room is full of spiders and some could be dangerous, since the students do experiments! Would you like to help me with this 'little' problem?", getReply(npc));
		en.step(player, "no");
		assertEquals("Ok, I have to find someone else to do this 'little' job!", getReply(npc));
		assertThat(player.getKarma(), lessThan(oldkarma));
		questHistory.add("I do not agree to help Morgrin.");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		questHistory.clear();
		oldkarma = player.getKarma();
		en.step(player, "hi");
		assertEquals("Hello my friend. Nice day for walking isn't it?", getReply(npc));
		en.step(player, "task");
		assertEquals("Have you ever been to the basement of the school? The room is full of spiders and some could be dangerous, since the students do experiments! Would you like to help me with this 'little' problem?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Fine. Go down to the basement and kill all the creatures there!", getReply(npc));
		questHistory.add("I do agree to help Morgrin.");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Go down and kill the creatures, no time left.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		killSpider("spider");
		assertEquals("spider", player.getQuest(questSlot, 1));
		questHistory.add("I have killed a spider in the basement.");
		assertEquals(questHistory, quest.getHistory(player));
		killSpider("poisonous spider");
		assertEquals("poisonous spider",player.getQuest(questSlot, 2));
		questHistory.add("I have killed a poisonous spider in the basement.");
		assertEquals(questHistory, quest.getHistory(player));
		killSpider("giant spider");
		assertEquals("giant spider",player.getQuest(questSlot, 3));
		questHistory.add("I have killed a giant spider in the basement.");
		questHistory.add("I have killed all 3 spiders in the basement. Now I go back to Morgrin to fetch my reward.");
		assertEquals(questHistory, quest.getHistory(player));
		final int xp = player.getXP();
		final double karma = player.getKarma();

		questHistory.clear();
		en.step(player, "hi");
		// [15:13] kymara earns 5000 experience points.
		assertEquals("Oh thank you my friend. Here you have something special, I got it from a Magican. Who he was I do not know. What the egg's good for, I do not know. I only know, it could be useful for you.", getReply(npc));
		assertTrue(player.isEquipped("mythical egg"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getKarma(), greaterThan(karma));
		assertTrue(player.getQuest(questSlot).startsWith("killed"));
		questHistory.add("I have killed all spiders in the magic school basement and got a mythical egg.");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Hello my friend. Nice day for walking isn't it?", getReply(npc));
		en.step(player, "task");
		assertEquals("Sorry there is nothing to do for you yet. But maybe you could come back later. I have to clean the school once a week.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		final double newKarma = player.getKarma();

		// [15:14] Changed the state of quest 'kill_all_spiders' from 'killed;1219677211115' to 'killed;0'
		player.setQuest(questSlot, "killed;0");
		en.step(player, "hi");
		assertEquals("Hello my friend. Nice day for walking isn't it?", getReply(npc));
		en.step(player, "task");
		assertEquals("Would you like to help me again?", getReply(npc));
		en.step(player, "no");
		assertThat(player.getKarma(), lessThan(newKarma));
		assertEquals("Ok, I have to find someone else to do this 'little' job!", getReply(npc));
		assertThat(player.getQuest(questSlot), is("rejected"));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Hello my friend. Nice day for walking isn't it?", getReply(npc));
		en.step(player, "task");
		assertEquals("Have you ever been to the basement of the school? The room is full of spiders and some could be dangerous, since the students do experiments! Would you like to help me with this 'little' problem?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Fine. Go down to the basement and kill all the creatures there!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void testOldQuest() {
		final int xp = player.getXP();
		final double karma = player.getKarma();
		LinkedList<String> questHistory = new LinkedList<String>();

		assertTrue(quest.getHistory(player).isEmpty());
		player.setQuest(questSlot, "start");
		questHistory.add("I do agree to help Morgrin.");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "hi");
		assertEquals("Go down and kill the creatures, no time left.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		player.setSharedKill("spider");
		questHistory.add("I have killed a spider in the basement.");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "hi");
		assertEquals("Go down and kill the creatures, no time left.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		player.setSharedKill("poisonous spider");
		questHistory.add("I have killed a poisonous spider in the basement.");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "hi");
		assertEquals("Go down and kill the creatures, no time left.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		player.setSharedKill("giant spider");
		questHistory.add("I have killed a giant spider in the basement.");
		questHistory.add("I have killed all 3 spiders in the basement. Now I go back to Morgrin to fetch my reward.");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "hi");
		assertEquals("Oh thank you my friend. Here you have something special, I got it from a Magican. Who he was I do not know. What the egg's good for, I do not know. I only know, it could be useful for you.", getReply(npc));
		assertTrue(player.isEquipped("mythical egg"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getKarma(), greaterThan(karma));
		assertTrue(player.getQuest(questSlot).startsWith("killed"));
		questHistory.clear();
		questHistory.add("I have killed all spiders in the magic school basement and got a mythical egg.");
		assertEquals(questHistory, quest.getHistory(player));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	/**
	 * <ul>
	 * <li>given the player has not completed the quest yet</li>
	 * <li>when the player asks again for a quest</li>
	 * <li>then the NPC says the quest was already offered</li>
	 * </ul>
	 */
	@Test
	public void testAskForQuestBeforeCompleting() {
		en.step(player, "hi");
		assertEquals(NPC_HELLO, getReply(npc));

		en.step(player, "task");
		assertEquals(NPC_OFFER_QUEST, getReply(npc));

		en.step(player, "yes");
		assertEquals(NPC_ACKNOWLEDGE_QUEST_ACCEPTED, getReply(npc));

		en.step(player, "task");
		assertEquals(NPC_QUEST_ALREADY_OFFERED, getReply(npc));

		en.step(player, "bye");
		assertEquals(NPC_BYE, getReply(npc));
	}
}
