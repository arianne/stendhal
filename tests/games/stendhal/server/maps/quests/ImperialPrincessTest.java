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
import games.stendhal.server.maps.kalavan.castle.KingNPC;
import games.stendhal.server.maps.kalavan.castle.PrincessNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class ImperialPrincessTest {


	private static String questSlot = "imperial_princess";

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		MockStendlRPWorld.get();

		final StendhalRPZone zone = new StendhalRPZone("admin_test");

		new PrincessNPC().configureZone(zone, null);
		new KingNPC().configureZone(zone, null);

		final AbstractQuest quest = new ImperialPrincess();
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
		// try going to king cozart before favour is done for princess
		npc = SingletonRepository.getNPCList().get("King Cozart");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("Leave me! Can't you see I am trying to eat?", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Princess Ylflia");
		en = npc.getEngine();
		// she uses the player level for this, so lets set the player level  to what it was in this test
		player.setLevel(270);

		en.step(player, "hi");
		assertEquals("How do you do?", getReply(npc));
		en.step(player, "help");
		assertEquals("Watch out for mad scientists. My father allowed them liberty to do some work in the basement and I am afraid things have got rather out of hand.", getReply(npc));
		en.step(player, "offer");
		assertEquals("Sorry, but I do not have anything to offer you. You could do me a #favour, though...", getReply(npc));
		en.step(player, "favour");
		assertEquals("I cannot free the captives in the basement but I could do one thing: ease their pain. I need #herbs for this.", getReply(npc));
		en.step(player, "herbs");
		assertEquals("I need 7 arandula, 1 kokuda, 1 sclaria, 1 kekik, 28 potions and 14 antidotes. Will you get these items?", getReply(npc));
		en.step(player, "no");
		assertEquals("So you'll just let them suffer! How despicable.", getReply(npc));
		assertThat(player.getQuest(questSlot), is("rejected"));
		en.step(player, "bye");
		assertEquals("Goodbye, and good luck.", getReply(npc));

		en.step(player, "hi");
		assertEquals("How do you do?", getReply(npc));
		en.step(player, "task");
		assertEquals("I cannot free the captives in the basement but I could do one thing: ease their pain. I need #herbs for this.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, and good luck.", getReply(npc));

		en.step(player, "hi");
		assertEquals("How do you do?", getReply(npc));
		en.step(player, "herbs");
		assertEquals("I need 7 arandula, 1 kokuda, 1 sclaria, 1 kekik, 28 potions and 14 antidotes. Will you get these items?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you! We must be subtle about this, I do not want the scientists suspecting I interfere. When you return with the items, please say codeword #herbs.", getReply(npc));
		en.step(player, "herbs");
		assertEquals("Shh! Don't say it till you have the 7 arandula, 1 #kokuda, 1 #sclaria, 1 #kekik, 28 potions and 14 antidotes. I don't want anyone suspecting our code.", getReply(npc));
		en.step(player, "kokuda");
		assertEquals("I believe that herb can only be found on Athor, though they guard their secrets closely over there.", getReply(npc));
		en.step(player, "kekik");
		assertEquals("My maid's friend Jenny has a source not far from her. The wooded areas at the eastern end of Nalwor river may have it. too.", getReply(npc));
		en.step(player, "sclaria");
		assertEquals("Healers who use sclaria gather it in all sorts of places - around Or'ril, in Nalwor forest, I am sure you will find that without trouble.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, and good luck.", getReply(npc));
		// she should have stored the player level in the quest slot.
		assertTrue(player.getQuest(questSlot).equals(Integer.toString(player.getLevel())));


		Item item = ItemTestHelper.createItem("arandula", 7);
		player.getSlot("bag").add(item);
		item = ItemTestHelper.createItem("kokuda", 1);
		player.getSlot("bag").add(item);
		item = ItemTestHelper.createItem("sclaria", 1);
		player.getSlot("bag").add(item);
		item = ItemTestHelper.createItem("kekik", 1);
		player.getSlot("bag").add(item);
		item = ItemTestHelper.createItem("antidote", 14);
		player.getSlot("bag").add(item);
		item = ItemTestHelper.createItem("potion", 28);
		player.getSlot("bag").add(item);

		final int xp = player.getXP();
		en.step(player, "hi");
		assertEquals("How do you do?", getReply(npc));
		en.step(player, "task");
		assertEquals("I'm sure I asked you to do something for me, already.", getReply(npc));
		// note the typos here, i should have said herbs but she understood herbd anyway.
		en.step(player, "herbd");
		assertEquals("Perfect! I will recommend you to my father, as a fine, helpful person. He will certainly agree you are eligible for citizenship of Kalavan.", getReply(npc));
		// [22:21] kymara earns 110400 experience points.
		assertFalse(player.isEquipped("potion"));
		assertFalse(player.isEquipped("antidote"));
		assertFalse(player.isEquipped("arandula"));
		assertFalse(player.isEquipped("sclaria"));
		assertFalse(player.isEquipped("kekik"));
		assertFalse(player.isEquipped("kokoda"));
		assertThat(player.getXP(), greaterThan(xp));
		assertThat(player.getQuest(questSlot), is("recommended"));
		en.step(player, "bye");
		assertEquals("Goodbye, and good luck.", getReply(npc));

		en.step(player, "hi");
		assertEquals("How do you do?", getReply(npc));
		en.step(player, "task");
		assertEquals("Speak to my father, the King. I have asked him to grant you citizenship of Kalavan, to express my gratitude to you.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, and good luck.", getReply(npc));

		npc = SingletonRepository.getNPCList().get("King Cozart");
		en = npc.getEngine();

		final int xp2 = player.getXP();
		en.step(player, "hi");
		// [22:22] kymara earns 500 experience points.
		assertEquals("Greetings! My wonderful daughter requests that I grant you citizenship of Kalavan City. Consider it done. Now, forgive me while I go back to my meal. Goodbye.", getReply(npc));
		assertThat(player.getXP(), greaterThan(xp2));
		assertTrue(player.isQuestCompleted(questSlot));

		// try going after quest is done
		en.step(player, "hi");
		assertEquals("Leave me! Can't you see I am trying to eat?", getReply(npc));

		npc = SingletonRepository.getNPCList().get("Princess Ylflia");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("How do you do?", getReply(npc));
		en.step(player, "task");
		assertEquals("The trapped creatures looked much better last time I dared venture down to the basement, thank you!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Goodbye, and good luck.", getReply(npc));



	}
}
