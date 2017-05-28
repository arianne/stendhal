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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.semos.library.LibrarianNPC;
import games.stendhal.server.maps.wofol.blacksmith.BlacksmithNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class ObsidianKnifeTest {


	private static String questSlot = "obsidian_knife";

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();

		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		new BlacksmithNPC().configureZone(zone, null);
		new LibrarianNPC().configureZone(zone, null);

		final AbstractQuest quest = new ObsidianKnife();
		quest.addToWorld();

	}
	@Before
	public void setUp() {
		player = PlayerTestHelper.createPlayer("player");
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {

		npc = SingletonRepository.getNPCList().get("Alrak");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("How did you get down here? I usually only see #kobolds.", getReply(npc));
		en.step(player, "task");
		assertEquals("You know, it's hard to get food round here. I don't have any #supplies for next year.", getReply(npc));
		en.step(player, "supplies");
		assertTrue(getReply(npc).startsWith("If you could get me 100 pieces of "));
		en.step(player, "yes");
		assertTrue(getReply(npc).startsWith("Thank you! I hope it doesn't take too long to collect. Don't forget to say "));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// alrak might have asked for meat, ham or cheese. we choose a definite one for testing.
		player.setQuest(questSlot, "meat");

		Item item = ItemTestHelper.createItem("meat", 100);
		player.getSlot("bag").add(item);
		final int xp = player.getXP();
		final double oldKarma = player.getKarma();

		en.step(player, "hi");
		assertEquals("How did you get down here? I usually only see #kobolds.", getReply(npc));
		en.step(player, "meat");
		assertFalse(player.isEquipped("meat"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getKarma(), greaterThan(oldKarma));
		assertThat(player.getQuest(questSlot), is("food_brought"));
		// [15:17] kymara earns 1000 experience points.
		assertEquals("Great! You brought the meat!", getReply(npc));
		en.step(player, "task");
		assertEquals("Now I'm less worried about food I've realised I'm bored. There's a #book I'd love to read.", getReply(npc));
		en.step(player, "book");
		assertEquals("It's about gems and minerals. I doubt you'd be interested ... but do you think you could get it somehow?", getReply(npc));
		en.step(player, "no");
		assertEquals("Shame, I would really like to learn more about precious stones. Ah well, good bye.", getReply(npc));

		en.step(player, "hi");
		assertEquals("How did you get down here? I usually only see #kobolds.", getReply(npc));
		en.step(player, "task");
		assertEquals("Now I'm less worried about food I've realised I'm bored. There's a #book I'd love to read.", getReply(npc));
		en.step(player, "book");
		assertEquals("It's about gems and minerals. I doubt you'd be interested ... but do you think you could get it somehow?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thanks. Try asking at a library for a 'gem book'.", getReply(npc));
		assertThat(player.getQuest(questSlot), is("seeking_book"));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));


		en.step(player, "hi");
		assertEquals("Hello again. I hope you haven't forgotten about the gem book I wanted.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Ceryl");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Greetings! How may I help you?", getReply(npc));
		en.step(player, "gem book");
		assertEquals("You're in luck! Ognir brought it back just last week. Now, who is it for?", getReply(npc));
		en.step(player, "me");
		assertEquals("Hm, you better check who it's really for.", getReply(npc));
		en.step(player, "alrak");
		assertEquals("Ah, the mountain dwarf! Hope he enjoys the gem book.", getReply(npc));
		assertTrue(player.isEquipped("blue book"));
		assertThat(player.getQuest(questSlot), is("got_book"));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Alrak");

		en = npc.getEngine();
		final int xp2 = player.getXP();

		en.step(player, "hi");
		// [09:28] kymara earns 500 experience points.
		assertEquals("Great! I think I'll read this for a while. Bye!", getReply(npc));
		assertFalse(player.isEquipped("blue book"));
		assertThat(player.getXP(), greaterThan(xp2));
		assertTrue(player.getQuest(questSlot).startsWith("reading"));

		en.step(player, "hi");
		assertEquals("I haven't finished reading that book. Maybe I'll be done in 3 days.", getReply(npc));

		// -----------------------------------------------

		player.setQuest(questSlot, "reading;0");
		// [09:29] Changed the state of quest 'obsidian_knife' from 'reading;1219829318495' to 'reading;0'
		en.step(player, "hi");
		assertEquals("I've finished reading! That was really interesting. I learned how to make a special #knife from #obsidian.", getReply(npc));
		en.step(player, "knife");
		assertEquals("Well, I don't think you're quite ready for such a dangerous weapon yet. How about you come back when you're above level 50?", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// player was too low level last time. make them above level 50
		player.addXP(1263600);
		assertThat(player.getLevel(), greaterThan(50));

		en.step(player, "hi");
		assertEquals("Hi! Perhaps you have come to ask about that #knife again ... ", getReply(npc));
		en.step(player, "knife");
		assertEquals("I'll make an obsidian knife if you can slay a black dragon and get the gem which makes the blade. Bring a cod so that I can make the bone handle, too.", getReply(npc));
		assertThat(player.getQuest(questSlot), is("knife_offered"));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Hello again. Don't forget I offered to make that obsidian knife, if you bring me a cod and a piece of obsidian from a black dragon you killed. In the meantime if I can #help you, just say the word.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------
		item = ItemTestHelper.createItem("obsidian");
		player.getSlot("bag").add(item);
		// add one item
		en.step(player, "hi");
		assertEquals("Hello again. Don't forget I offered to make that obsidian knife, if you bring me a cod and a piece of obsidian from a black dragon you killed. In the meantime if I can #help you, just say the word.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		// add the next item
		item = ItemTestHelper.createItem("cod");
		player.getSlot("bag").add(item);

		// they haven't killed a dragon
		en.step(player, "hi");
		assertEquals("Didn't you hear me properly? I told you to go slay a black dragon for the obsidian, not buy it! How do I know this isn't a fake gem? *grumble* I'm not making a special knife for someone who is scared to face a dragon.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		player.setSharedKill("black dragon");
		en.step(player, "hi");
		assertEquals("You found the gem for the blade and the fish bone to make the handle! I'll start work right away. Come back in 10 minutes.", getReply(npc));
		assertFalse(player.isEquipped("cod"));
		assertFalse(player.isEquipped("obsidian"));
		// -----------------------------------------------
		en.step(player, "hi");
		assertEquals("I haven't finished making the knife. Please check back in 10 minutes.", getReply(npc));

		// [09:33] Changed the state of quest 'obsidian_knife' from 'forging;1219829551665' to 'forging;0'
		player.setQuest(questSlot, "forging;0");
		final int xp3 = player.getXP();

		en.step(player, "hi");
		// [09:35] kymara earns 10000 experience points.
		assertEquals("The knife is ready! You know, that was enjoyable. I think I'll start making things again. Thanks!", getReply(npc));
		assertTrue(player.isEquipped("obsidian knife"));
		assertThat(player.getXP(), greaterThan(xp3));
		assertThat(player.getQuest(questSlot), is("done"));
	}
}
