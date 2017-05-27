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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.Arrays;

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
import games.stendhal.server.maps.semos.plains.LittleBoyNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.RPClass.ItemTestHelper;
import utilities.RPClass.PassiveEntityRespawnPointTestHelper;

public class PlinksToyTest {
	private Player player = null;
	private SpeakerNPC npc = null;
	private Engine en = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		PassiveEntityRespawnPointTestHelper.generateRPClasses();
	}

	@Before
	public void setUp() {
		final StendhalRPZone zone = new StendhalRPZone("0_semos_plains_n");
		MockStendlRPWorld.get().addRPZone(zone);
		new LittleBoyNPC().configureZone(zone, null);

		final PlinksToy quest = new PlinksToy();
		quest.addToWorld();

		player = PlayerTestHelper.createPlayer("player");
	}

	/**
	 * Tests for quest.
	 */
	@Test
	public void testQuest() {
		npc = SingletonRepository.getNPCList().get("Plink");
		en = npc.getEngine();

		en.step(player, "hi");
		assertEquals("*cries* There were wolves in the #park! *sniff* I ran away, but I dropped my #teddy! Please will you get it for me? *sniff* Please?", getReply(npc));
		en.step(player, "park!");
		assertEquals("My parents told me not to go to the park by myself, but I got lost when I was playing... Please don't tell them! Can you bring my #teddy back?", getReply(npc));
		en.step(player, "yes");
		assertEquals("*sniff* Thanks a lot! *smile*", getReply(npc));

		// -----------------------------------------------

		en.step(player, "hi");
		assertEquals("*cries* There were wolves in the #park! *sniff* I ran away, but I dropped my #teddy! Please will you get it for me? *sniff* Please?", getReply(npc));
		en.step(player, "teddy");
		assertEquals("Teddy is my favourite toy! Please will you bring him back?", getReply(npc));
		en.step(player, "no");
		assertEquals("*sniff* But... but... PLEASE! *cries*", getReply(npc));

		en.step(player, "teddy bear");
		assertEquals("Teddy is my favourite toy! Please will you bring him back?", getReply(npc));
		en.step(player, "yes");
		assertEquals("*sniff* Thanks a lot! *smile*", getReply(npc));

		// -----------------------------------------------

		final Item teddy = ItemTestHelper.createItem("teddy");
		teddy.setEquipableSlots(Arrays.asList("bag"));
		player.equipToInventoryOnly(teddy);
		assertTrue(player.isEquipped("teddy"));

		System.out.println(player.getSlot("!quests"));
		System.out.println(player.getSlot("lhand"));
		System.out.println(player.getSlot("rhand"));

		en.step(player, "hi");
		// [21:25] player earns 10 experience points.
		assertEquals("You found him! *hugs teddy* Thank you, thank you! *smile*", getReply(npc));

		assertFalse(player.isEquipped("teddy"));

		en.step(player, "help");
		assertEquals("Be careful out east, there are wolves about!", getReply(npc));
		en.step(player, "job");
		assertEquals("I play all day.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Bye.", getReply(npc));
	}
}
