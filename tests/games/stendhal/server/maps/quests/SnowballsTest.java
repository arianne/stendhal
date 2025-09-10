/***************************************************************************
 *                 Copyright © 2010-2024 - Faiumoni e. V.                  *
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
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.oneOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.semos.yeticave.MrYetiNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class SnowballsTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	private String questSlot;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("admin_test");
		new MrYetiNPC().configureZone(zone, null);


		AbstractQuest quest = new Snowballs();
		quest.addToWorld();

		questSlot = quest.getSlotName();

		player = PlayerTestHelper.createPlayer("bob");
	}

	@Test
	public void testQuest() {

		npc = SingletonRepository.getNPCList().get("Mr. Yeti");
		en = npc.getEngine();


		// -----------------------------------------------

		final int completions = MathHelper.parseIntDefault(player.getQuest(questSlot, 1), 0);
		String[] responses = new String[] {
				"Greetings stranger! Have you seen my snow sculptures? I need a #favor from someone friendly like you."
		};
		if (completions > 0) {
			responses = new String[] {
					"Greetings again! Have you seen my latest snow sculptures? I need a #favor again ..."
			};
		}

		en.step(player, "hi");
		assertEquals(responses[0], getReply(npc));
		en.step(player, "favor");
		assertEquals("I like to make snow sculptures, but the snow in this cavern is not good enough. Would you help me and get some snowballs? I need twenty five of them.", getReply(npc));
		en.step(player, "no");
		assertEquals("So what are you doing here? Go away!", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		assertEquals(player.getQuest(questSlot, 0), "rejected");

		en.step(player, "hi");
		assertEquals("Greetings stranger! Have you seen my snow sculptures? I need a #favor from someone friendly like you.", getReply(npc));
		en.step(player, "favor");
		assertEquals("I like to make snow sculptures, but the snow in this cavern is not good enough. Would you help me and get some snowballs? I need twenty five of them.", getReply(npc));
		en.step(player, "yes");
		assertEquals("Fine. You can loot the snowballs from the ice golem in this cavern, but be careful there is something huge nearby! Come back when you get twenty five snowballs.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		assertEquals(player.getQuest(questSlot, 0), "start");

		en.step(player, "hi");
		assertEquals("You're back already? Don't forget that you promised to collect a bunch of snowballs for me!", getReply(npc));
		en.step(player, "quest");
		assertEquals("You already promised me to bring some snowballs! Twenty five pieces, remember ...", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		PlayerTestHelper.equipWithStackableItem(player, "snowball", 25);
		assertTrue(player.isEquipped("snowball", 25));

		en.step(player, "hi");
		assertEquals("Greetings stranger! I see you have the snow I asked for. Are these snowballs for me?", getReply(npc));
		en.step(player, "no");
		assertEquals("Oh I hope you bring me them soon! I would like to finish my sculpture!", getReply(npc));
		en.step(player, "quest");
		assertEquals("You already promised me to bring some snowballs! Twenty five pieces, remember ...", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		en.step(player, "hi");
		assertEquals("Greetings stranger! I see you have the snow I asked for. Are these snowballs for me?", getReply(npc));
		// put the snowballs on the ground
		player.drop("snowball", 25);
		assertFalse(player.isEquipped("snowball", 25));

		en.step(player, "yes");
		assertEquals("Hey! Where did you put the snowballs?", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		PlayerTestHelper.equipWithStackableItem(player, "snowball", 25);

		final int xp = player.getXP();

		en.step(player, "hi");
		assertEquals("Greetings stranger! I see you have the snow I asked for. Are these snowballs for me?", getReply(npc));
		en.step(player, "yes");
		assertThat(getReply(npc), is(oneOf("Thank you! Here, take some perch! I do not like to eat them.", "Thank you! Here, take some cod! I do not like to eat them.")));
		// [09:49] kymara earns 50 experience points.
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		assertThat(player.getXP(), greaterThan(xp));
		assertFalse(player.isEquipped("snowballs", 25));
		assertTrue(player.isEquipped("perch", 20) || player.isEquipped("cod", 20) );

		assertNotNull(player.getQuest(questSlot));
		assertFalse(player.getQuest(questSlot, 0).equals("start"));

		en.step(player, "hi");
		assertEquals("I have enough snow for my new sculpture. Thank you for helping! I might start a new one in 2 hours.", getReply(npc));
		en.step(player, "task");
		assertEquals("I have enough snow to finish my sculpture, but thanks for asking.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// [09:49] Admin kymara changed your state of the quest 'snowballs' from '1288518569387' to '0'
		// [09:49] Changed the state of quest 'snowballs' from '1288518569387' to '0'

		player.setQuest(questSlot, 0, "0");

		en.step(player, "hi");
		assertEquals("Greetings again! Have you seen my latest snow sculptures? I need a #favor again ...", getReply(npc));
		en.step(player, "favor");
		assertEquals("I like to make snow sculptures, but the snow in this cavern is not good enough. Would you help me and get some snowballs? I need twenty five of them.", getReply(npc));
		en.step(player, "yes");
		assertEquals("Fine. You can loot the snowballs from the ice golem in this cavern, but be careful there is something huge nearby! Come back when you get twenty five snowballs.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	@Test
	public void testCompletions() {
		for (int count = 0; count < 5; count++) {
			assertEquals(count, MathHelper.parseIntDefault(player.getQuest(questSlot, 1), 0));
			testQuest();
			// reset so can be repeated
			player.setQuest(questSlot, 0, "0");
		}
		assertEquals("5", player.getQuest(questSlot, 1));

		// check that completions count is retained after quest is rejected & started
		en.step(player, "hi");
		en.step(player, "task");
		en.step(player, "no");
		assertEquals("rejected", player.getQuest(questSlot, 0));
		assertEquals("5", player.getQuest(questSlot, 1));
		en.step(player, "task");
		en.step(player, "yes");
		assertEquals("start", player.getQuest(questSlot, 0));
		assertEquals("5", player.getQuest(questSlot, 1));
		en.step(player, "bye");
	}
}
