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

import static org.junit.Assert.assertEquals;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ados.outside.CloaksCollectorNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;

public class CloaksForBarioTest {

	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	public void setUp() {
		final ZoneConfigurator zoneConf = new CloaksCollectorNPC();
		zoneConf.configureZone(new StendhalRPZone("admin_test"), null);
		npc = SingletonRepository.getNPCList().get("Bario");

		final AbstractQuest quest = new CloaksForBario();
		quest.addToWorld();
		en = npc.getEngine();

		player = PlayerTestHelper.createPlayer("player");
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		en.step(player, "hi");
		assertEquals("Hey! How did you get down here? You did what? Huh. Well, I'm Bario. I don't suppose you could do a #task for me.", getReply(npc));
		en.step(player, "task");
		assertEquals("I don't dare go upstairs anymore because I stole a beer barrel from the dwarves. But it is so cold down here... Can you help me?", getReply(npc));
		en.step(player, "no");
		assertEquals("Oh dear... I'm going to be in trouble...", getReply(npc));
		en.step(player, "task");
		assertEquals("I don't dare go upstairs anymore because I stole a beer barrel from the dwarves. But it is so cold down here... Can you help me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("I need some blue elven cloaks if I'm to survive the winter. Bring me ten of them, and I will give you a reward.", getReply(npc));
		en.step(player, "ok");
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------
		equipCloaks(4);

		en.step(player, "hi");
		assertEquals("Hi again! I still need 10 blue elven cloaks. Do you have any for me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 9 cloaks.", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 8 cloaks.", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 7 cloaks.", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 6 cloaks.", getReply(npc));
		en.step(player, "no");
		assertEquals("Too bad.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
		en.step(player, "hi");
		assertEquals("Hi again! I still need 6 blue elven cloaks. Do you have any for me?", getReply(npc));
		en.step(player, "yes");
		// was lying
		assertEquals("Really? I don't see any...", getReply(npc));
		en.step(player, "no");
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------
		equipCloaks(6);

		en.step(player, "hi");
		assertEquals("Hi again! I still need 6 blue elven cloaks. Do you have any for me?", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 5 cloaks.", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 4 cloaks.", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 3 cloaks.", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need 2 cloaks.", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you very much! Do you have another one? I still need one cloak.", getReply(npc));
		en.step(player, "yes");
		assertEquals("Thank you very much! Now I have enough cloaks to survive the winter. Here, take this golden shield as a reward.", getReply(npc));
		// [23:48] superkym earns 1500 experience points.
		en.step(player, "task");
		assertEquals("I don't have anything for you to do, really.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("Welcome! Thanks again for those cloaks.", getReply(npc));
		en.step(player, "task");
		assertEquals("I don't have anything for you to do, really.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}

	private void equipCloaks(final int quantity) {
		for (int i = 0; i < quantity; i++) {
			final Item item = ItemTestHelper.createItem("blue elf cloak");
			player.getSlot("bag").add(item);
		}
	}
}
