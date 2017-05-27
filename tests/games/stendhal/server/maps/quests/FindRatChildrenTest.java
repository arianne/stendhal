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

import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.orril.dungeon.RatChild1NPC;
import games.stendhal.server.maps.orril.dungeon.RatChild2NPC;
import games.stendhal.server.maps.orril.dungeon.RatChildBoy1NPC;
import games.stendhal.server.maps.orril.dungeon.RatChildBoy2NPC;
import games.stendhal.server.maps.ratcity.house1.OldRatWomanNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class FindRatChildrenTest {

	private static final String QUEST_SLOT = "find_rat_kids";

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {

		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		new OldRatWomanNPC().configureZone(zone, null);
		new RatChildBoy1NPC().configureZone(zone, null);
		new RatChildBoy2NPC().configureZone(zone, null);
		new RatChild1NPC().configureZone(zone, null);
		new RatChild2NPC().configureZone(zone, null);

		AbstractQuest quest = new FindRatChildren();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@After
	public void tearDown() {
		PlayerTestHelper.removeNPC("Agnus");
		PlayerTestHelper.removeNPC("Avalon");
		PlayerTestHelper.removeNPC("Cody");
		PlayerTestHelper.removeNPC("Mariel");
		PlayerTestHelper.removeNPC("Opal");
	}

	@Test
	public void testMeetingKidBeforeQuestStarted() {

		// haven't started quest yet
		assertNull(player.getQuest(QUEST_SLOT));

		npc = SingletonRepository.getNPCList().get("Cody");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Mother says I mustn't talk to strangers.", getReply(npc));
		en.step(player, "bye");
	}
		// -----------------------------------------------

	@Test
	public void testStartQuest() {

		npc = SingletonRepository.getNPCList().get("Agnus");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "help");
		assertEquals("I have no help to offer you.", getReply(npc));
		en.step(player, "job");
		assertEquals("Leave it to my children to not check in once in a while.", getReply(npc));
		en.step(player, "task");
		assertEquals("I feel so worried. If I only knew my #children were safe I would feel better.", getReply(npc));
		en.step(player, "children");
		assertEquals("My children have gone to play in the sewers. They have been gone for a long time. Will you find them and see if they are ok?", getReply(npc));
		en.step(player, "no");
		assertEquals("Oh. Never mind. I'm sure someone else would be glad to help me.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye", getReply(npc));

		// check quest slot
		assertEquals(player.getQuest(QUEST_SLOT),"rejected");

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "task");
		assertEquals("I feel so worried. If I only knew my #children were safe I would feel better.", getReply(npc));
		en.step(player, "children");
		assertEquals("My children have gone to play in the sewers. They have been gone for a long time. Will you find them and see if they are ok?", getReply(npc));
		en.step(player, "yes");
		assertEquals("That's so nice of you. Good luck searching for them.", getReply(npc));
		en.step(player, "task");
		assertEquals("Why must my children stay out so long? Please find them and tell me who is ok.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye", getReply(npc));

		// check quest slot
		assertEquals(player.getQuest(QUEST_SLOT),"looking:said");
	}

	@Test
	public void testNamingKidsThatDontExistOrNotMet() {

		npc = SingletonRepository.getNPCList().get("Agnus");
		en = npc.getEngine();

		// the state wasn't remembered across the new test method so we need to set it to what it was when we ended the last
		player.setQuest(QUEST_SLOT, "looking:said");

		en.step(player, "hi");
		assertEquals("If you found any of my #children, please tell me their name.", getReply(npc));
		en.step(player, "children");
		assertEquals("I wish to know that my children are ok. Please tell me who is ok.", getReply(npc));
		en.step(player, "unknownchild");
		assertEquals("Sorry, I don't understand you. What name are you trying to say?", getReply(npc));
		en.step(player, "banana");
		assertEquals("Sorry, I don't understand you. What name are you trying to say?", getReply(npc));
		en.step(player, "Cody");
		assertEquals("I don't think you actually checked if they were ok. If you have seen any of my other children, please tell me who.", getReply(npc));
		en.step(player, "bye");
		assertEquals("No problem, come back later.", getReply(npc));

		// check quest slot
		assertEquals(player.getQuest(QUEST_SLOT),"looking:said");
	}

	@Test
	public void testMeetingCodyAfterQuestStarted() {
		npc = SingletonRepository.getNPCList().get("Cody");
		en = npc.getEngine();

		// the state wasn't remembered across the new test method so we need to set it to what it was when we ended the last
		player.setQuest(QUEST_SLOT, "looking:said");

		// remember the xp and karma, did it go up?
		final int xp = player.getXP();

		en.step(player, "hi");
		assertEquals("Hello my name is Cody. Please tell mother that I am ok.", getReply(npc));
		// [11:49] kymara earns 500 experience points.

		assertThat(player.getXP(), greaterThan(xp));
		// check quest slot
		assertEquals(player.getQuest(QUEST_SLOT),"looking;cody:said");

		// return after having met in this quest run
		en.step(player, "hi");
		assertEquals("Oh hello again.", getReply(npc));

		// check quest slot
		assertEquals(player.getQuest(QUEST_SLOT),"looking;cody:said");
	}

	@Test
	public void testNamingKidsMet() {

		npc = SingletonRepository.getNPCList().get("Agnus");
		en = npc.getEngine();

		// the state wasn't remembered across the new test method so we need to set it to what it was when we ended the last
		player.setQuest(QUEST_SLOT, "looking;cody:said");

		en.step(player, "hi");
		assertEquals("If you found any of my #children, please tell me their name.", getReply(npc));
		en.step(player, "children");
		assertEquals("I wish to know that my children are ok. Please tell me who is ok.", getReply(npc));
		en.step(player, "CODY");
		assertEquals("Thank you. If you have seen any of my other children, please tell me who.", getReply(npc));
		en.step(player, "mariel");
		assertEquals("I don't think you actually checked if they were ok. If you have seen any of my other children, please tell me who.", getReply(npc));
		en.step(player, "bye");
		assertEquals("No problem, come back later.", getReply(npc));

		// check quest slot
		assertEquals(player.getQuest(QUEST_SLOT),"looking;cody:said;cody");
	}

	@Test
	public void testMeetingRemainingKids() {
		npc = SingletonRepository.getNPCList().get("Mariel");
		en = npc.getEngine();

		// the state wasn't remembered across the new test method so we need to set it to what it was when we ended the last
		player.setQuest(QUEST_SLOT, "looking;cody:said;cody");

		en.step(player, "hi");
		assertEquals("Hello my name is Mariel. Please tell mother that I am ok.", getReply(npc));
		// [11:49] kymara earns 500 experience points.

		// -----------------------------------------------

		npc = SingletonRepository.getNPCList().get("Opal");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello my name is Opal. Please tell mother that I am ok.", getReply(npc));
		// [11:50] kymara earns 500 experience points.

		// -----------------------------------------------
		npc = SingletonRepository.getNPCList().get("Avalon");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello my name is Avalon. Please tell mother that I am ok.", getReply(npc));
		// [11:50] kymara earns 500 experience points.

		// check quest slot
		assertEquals(player.getQuest(QUEST_SLOT),"looking;cody;mariel;opal;avalon:said;cody");

	}
	@Test
	public void testNamingRemainingKids() {

		npc = SingletonRepository.getNPCList().get("Agnus");
		en = npc.getEngine();

		// the state wasn't remembered across the new test method so we need to set it to what it was when we ended the last
		player.setQuest(QUEST_SLOT, "looking;cody;mariel;opal;avalon:said;cody");

		en.step(player, "hi");
		assertEquals("If you found any of my #children, please tell me their name.", getReply(npc));
		en.step(player, "mariel");
		assertEquals("Thank you. If you have seen any of my other children, please tell me who.", getReply(npc));
		en.step(player, "bye");
		assertEquals("No problem, come back later.", getReply(npc));

		// check quest slot
		assertEquals(player.getQuest(QUEST_SLOT),"looking;cody;mariel;opal;avalon:said;cody;mariel");

		// remember the xp and karma, did it go up?
		final int xp = player.getXP();
		final double karma = player.getKarma();

		en.step(player, "hi");
		assertEquals("If you found any of my #children, please tell me their name.", getReply(npc));
		en.step(player, "avalon");
		assertEquals("Thank you. If you have seen any of my other children, please tell me who.", getReply(npc));
		// test saying a name we already had given
		en.step(player, "Cody");
		assertEquals("Yes you told me that they were ok already, thanks. If you have seen any of my other children, please tell me who.", getReply(npc));
		en.step(player, "Opal");
		assertEquals("Thank you. Now that I know my kids are safe, I can set my mind at rest.", getReply(npc));
		// [11:50] kymara earns 5000 experience points.
		en.step(player, "bye");
		assertEquals("Bye", getReply(npc));

		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getKarma(), greaterThan(karma));

		// check quest slot
		assertTrue(player.isQuestCompleted(QUEST_SLOT));
	}

	@Test
	public void testReturningBeforeTimePassed() {

		npc = SingletonRepository.getNPCList().get("Agnus");
		en = npc.getEngine();

		// the state wasn't remembered across the new test method so we need to set it to what it was when we ended the last
		player.setQuest(QUEST_SLOT, "done;"+System.currentTimeMillis());

		en.step(player, "hi");
		assertEquals("Hello there.", getReply(npc));
		en.step(player, "task");
		assertEquals("Thank you! I feel better now knowing my kids are safe.", getReply(npc));
		en.step(player, "yes");
		en.step(player, "bye");
		assertEquals("Bye", getReply(npc));
	}

    @Test
	public void testReturningAfterTimePassed() {

		// [11:51] Admin kymara changed your state of the quest 'find_rat_kids' from 'done;1270205441630' to 'done;1'
		// [11:51] Changed the state of quest 'find_rat_kids' from 'done;1270205441630' to 'done;1'

		// the state wasn't remembered across the new test method so we need to set it to what it was when we ended the last
		player.setQuest(QUEST_SLOT, "done;1");

		npc = SingletonRepository.getNPCList().get("Agnus");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Do you think you could find my children again?", getReply(npc));
		en.step(player, "yes");
		assertEquals("That's so nice of you. Good luck searching for them.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye", getReply(npc));

		// -----------------------------------------------

		npc = SingletonRepository.getNPCList().get("Cody");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Hello my name is Cody. Please tell mother that I am ok.", getReply(npc));
		// [11:51] kymara earns 500 experience points.
		en.step(player, "bye");
	}
}
