/***************************************************************************
 *                     Copyright © 2023 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.deniran.cityoutside;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import utilities.ZonePlayerAndNPCTestImpl;


public class FletcherNPCTest extends ZonePlayerAndNPCTestImpl {

	public FletcherNPCTest() {
		super("testzone", "Fletcher");
		addZoneConfigurator(new FletcherNPC(), "testzone");
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		utilities.QuestHelper.setUpBeforeClass();
		setupZone("testzone");
	}

	@Test
	public void initTest() {
		assertNotNull(player);
		final SpeakerNPC fletcher = getNPC("Fletcher");

		final String slotName = "fletcher_soak_arrows";
		assertNull(player.getQuest(slotName));
		final String[] ingredients = {"money", "wooden arrow", "poison"};
		for (final String itemName: ingredients) {
			assertEquals(0, player.getNumberOfEquipped(itemName));
		}

		final Engine en = fletcher.getEngine();
		en.step(player, "hi");
		en.step(player, "soak");
		assertEquals(
			"I can only soak poison arrows in quantities divisible by 10.",
			getReply(fletcher));
		en.step(player, "soak 9");
		assertEquals(
			"I can only soak poison arrows in quantities divisible by 10.",
			getReply(fletcher));
		en.step(player, "soak 101");
		assertEquals(
			"I can only soak poison arrows in quantities divisible by 10.",
			getReply(fletcher));
		en.step(player, "soak 10");
		assertEquals(
			"I can only soak 10 poison arrows if you bring me 10 #'wooden"
				+ " arrows', 500 #money, and a #'bottle of poison'.",
			getReply(fletcher));
		equipWithStackableItem(player, "money", 500);
		equipWithStackableItem(player, "wooden arrow", 10);
		equipWithStackableItem(player, "poison", 1);
		en.step(player, "soak 10");
		assertEquals(
			"I need you to fetch me 10 #'wooden arrows', 500 #money, and"
				+ " a #'bottle of poison' for this job, which will take 1"
				+ " minute. Do you have what I need?",
			getReply(fletcher));
		en.step(player, "yes");
		assertEquals("10", player.getQuest(slotName, 0));
		assertEquals("poison arrow", player.getQuest(slotName, 1));
		for (final String itemName: ingredients) {
			assertEquals(0, player.getNumberOfEquipped(itemName));
		}
		en.step(player, "bye");
		en.step(player, "hi");
		assertEquals(
			"Welcome back! I'm still busy with your order to soak 10 poison"
				+ " arrows for you",
			getReply(fletcher).split("\\.")[0]);
		en.step(player, "bye");
		assertEquals(0, player.getNumberOfEquipped("poison arrow"));
		player.setQuest(slotName, 2, "0");
		en.step(player, "hi");
		assertEquals(
			"Welcome back! I'm done with your order. Here you have 10 poison"
				+ " arrows.",
			getReply(fletcher));
		assertEquals(10, player.getNumberOfEquipped("poison arrow"));
		assertEquals("done", player.getQuest(slotName, 0));
		en.step(player, "bye");
		equipWithStackableItem(player, "money", 5000);
		equipWithStackableItem(player, "wooden arrow", 100);
		equipWithStackableItem(player, "poison", 10);
		en.step(player, "hi");
		en.step(player, "soak 100");
		assertEquals(
			"I need you to fetch me 10 #'bottles of poison', 100 #'wooden"
				+ " arrows', and 5000 #money for this job, which will take 10"
				+ " minutes. Do you have what I need?",
			getReply(fletcher));
		en.step(player, "yes");
		assertEquals("100", player.getQuest(slotName, 0));
		assertEquals("poison arrow", player.getQuest(slotName, 1));
		for (final String itemName: ingredients) {
			assertEquals(0, player.getNumberOfEquipped(itemName));
		}
		en.step(player, "bye");
		player.setQuest(slotName, 2, "0");
		en.step(player, "hi");
		assertEquals(
			"Welcome back! I'm done with your order. Here you have 100 poison"
				+ " arrows.",
			getReply(fletcher));
		assertEquals(110, player.getNumberOfEquipped("poison arrow"));
		assertEquals("done", player.getQuest(slotName, 0));
		en.step(player, "bye");
	}
}
