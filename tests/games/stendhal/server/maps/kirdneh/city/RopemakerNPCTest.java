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
package games.stendhal.server.maps.kirdneh.city;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import utilities.ZonePlayerAndNPCTestImpl;


public class RopemakerNPCTest extends ZonePlayerAndNPCTestImpl {

	public RopemakerNPCTest() {
		super("testzone", "Giles");
		addZoneConfigurator(new RopemakerNPC(), "testzone");
	}

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		utilities.QuestHelper.setUpBeforeClass();
		setupZone("testzone");
	}

	@Test
	public void initTest() {
		assertNotNull(player);
		final SpeakerNPC giles = getNPC("Giles");

		final String slotName = "ropemaker_braid_rope";
		assertNull(player.getQuest(slotName));
		final String[] ingredients = {"money", "horse hair"};
		for (final String itemName: ingredients) {
			assertEquals(0, player.getNumberOfEquipped(itemName));
		}

		final Engine en = giles.getEngine();
		en.step(player, "hi");
		en.step(player, "braid");
		assertEquals(
			"I can only braid a rope if you bring me 200 #money and 6 #'horse"
				+ " hairs'.",
			getReply(giles));
		equipWithStackableItem(player, "money", 200);
		equipWithStackableItem(player, "horse hair", 6);
		en.step(player, "braid");
		assertEquals(
			"I need you to fetch me 200 #money and 6 #'horse hairs' for this"
				+ " job, which will take 15 minutes. Do you have what I need?",
			getReply(giles));
		en.step(player, "yes");
		assertEquals("1", player.getQuest(slotName, 0));
		assertEquals("rope", player.getQuest(slotName, 1));
		for (final String itemName: ingredients) {
			assertEquals(0, player.getNumberOfEquipped(itemName));
		}
		en.step(player, "bye");
		en.step(player, "hi");
		assertEquals(
			"Welcome back! I'm still busy with your order to braid a rope"
				+ " for you",
			getReply(giles).split("\\.")[0]);
		en.step(player, "bye");
		assertEquals(0, player.getNumberOfEquipped("rope"));
		player.setQuest(slotName, 2, "0");
		en.step(player, "hi");
		assertEquals(
			"Welcome back! I'm done with your order. Here you have the rope.",
			getReply(giles));
		assertEquals(1, player.getNumberOfEquipped("rope"));
		assertEquals("done", player.getQuest(slotName, 0));
		en.step(player, "bye");
		equipWithStackableItem(player, "money", 2000);
		equipWithStackableItem(player, "horse hair", 60);
		en.step(player, "hi");
		en.step(player, "braid 10");
		assertEquals(
			"I need you to fetch me 2000 #money and 60 #'horse hairs' for"
				+ " this job, which will take about 2 and a half hours. Do you"
				+ " have what I need?",
			getReply(giles));
		en.step(player, "yes");
		assertEquals("10", player.getQuest(slotName, 0));
		assertEquals("rope", player.getQuest(slotName, 1));
		for (final String itemName: ingredients) {
			assertEquals(0, player.getNumberOfEquipped(itemName));
		}
		en.step(player, "bye");
		player.setQuest(slotName, 2, "0");
		en.step(player, "hi");
		assertEquals(
			"Welcome back! I'm done with your order. Here you have 10 ropes.",
			getReply(giles));
		assertEquals(11, player.getNumberOfEquipped("rope"));
		assertEquals("done", player.getQuest(slotName, 0));
		en.step(player, "bye");
	}
}
