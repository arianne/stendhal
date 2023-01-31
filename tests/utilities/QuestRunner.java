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
package utilities;

import static org.junit.Assert.assertEquals;
import static utilities.PlayerTestHelper.equipWithStackableItem;
import static utilities.SpeakerNPCTestHelper.getSpeakerNPC;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;


public class QuestRunner {

	// for debugging
	private static String getReply(final SpeakerNPC npc) {
		return utilities.SpeakerNPCTestHelper.getReply(npc);
	}

	public static void doQuestElfPrincess(final Player player) {
		final String questSlot = "elf_princess";
		if (player.getQuest(questSlot, 1) != null) {
			// reset timestamp to do quest again
			player.setQuest(questSlot, 1, "0");
		}
		final SpeakerNPC princess = getSpeakerNPC("Tywysoga");
		assertEquals(ConversationStates.IDLE, princess.getEngine().getCurrentState());
		Engine en = princess.getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		assertEquals("start", player.getQuest(questSlot, 0));
		en.step(player, "bye");
		en = getSpeakerNPC("Rose Leigh").getEngine();
		en.step(player, "hi");
		assertEquals(1, player.getNumberOfEquipped("rhosyd"));
		en = princess.getEngine();
		en.step(player, "hi");
		en.step(player, "flower");
		assertEquals(0, player.getNumberOfEquipped("rhosyd"));
		en.step(player, "bye");
	}

	public static void doQuestKillMonks(final Player player) {
		final String questSlot = "kill_monks";
		if (player.getQuest(questSlot, 1) != null) {
			// reset timestamp to do quest again
			player.setQuest(questSlot, 1, "0");
		}
		final Engine en = getSpeakerNPC("Andy").getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		player.setSoloKillCount("monk", player.getSoloKillCount("monk") + 25);
		player.setSoloKillCount("darkmonk", player.getSoloKillCount("darkmonk") + 25);
		en.step(player, "done");
		en.step(player, "bye");
	}

	private static void groongoEquipIngredients(final Player player, final String questSlot) {
		String[] tmp = player.getQuest(questSlot).split(";");
		final String state = tmp[0];
		String[] ingredients;
		if ("fetch_maindish".equals(state)) {
			ingredients = tmp[3].split(",");
		} else {
			ingredients = tmp[5].split(",");
		}
		for (final String ig: ingredients) {
			tmp = ig.split("=");
			final String name = tmp[0];
			final int count = Integer.valueOf(tmp[1]);
			assertEquals(0, player.getNumberOfEquipped(name));
			equipWithStackableItem(player, name, count);
			assertEquals(count, player.getNumberOfEquipped(name));
		}
	}

	public static void doQuestMealForGroongo(final Player player) {
		final String questSlot = "meal_for_groongo";
		if (player.getQuest(questSlot, 6) != null) {
			// reset timestamp to do quest again
			player.setQuest(questSlot, 6, "0");
		}
		final SpeakerNPC groongo = getSpeakerNPC("Groongo Rahnnt");
		final SpeakerNPC stefan = getSpeakerNPC("Stefan");
		Engine en = groongo.getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");
		groongoEquipIngredients(player, questSlot);
		en = stefan.getEngine();
		en.step(player, "hi");
		en.step(player, "meal");
		en.step(player, "yes");
		en.step(player, "bye");
		en = groongo.getEngine();
		en.step(player, "hi");
		en.step(player, "dessert");
		en.step(player, "bye");
		en = stefan.getEngine();
		en.step(player, "hi");
		en.step(player, "dessert");
		groongoEquipIngredients(player, questSlot);
		en.step(player, "yes");
		player.setQuest(questSlot, 6, "0");
		en.step(player, "hi");
		en = groongo.getEngine();
		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestRestockFlowerShop(final Player player) {
		final String questSlot = "restock_flowershop";
		if (player.getQuest(questSlot, 1) != null) {
			// reset timestamp to do quest again
			player.setQuest(questSlot, 1, "0");
		}
		final Engine en = getSpeakerNPC("Seremela").getEngine();
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		assertEquals("start", player.getQuest(questSlot, 0));
		en.step(player, "bye");
		en.step(player, "hi");
		for (final String s: player.getQuest(questSlot).split(";")) {
			if (!s.contains("=")) {
				continue;
			}
			final String[] item = s.split("=");
			final String name = item[0];
			final int quant = Integer.parseInt(item[1]);
			equipWithStackableItem(player, name, quant);
			assertEquals(quant, player.getNumberOfEquipped(name));
			en.step(player, name);
			assertEquals(0, player.getNumberOfEquipped(name));
		}
		assertEquals("done", player.getQuest(questSlot, 0));
		en.step(player, "bye");
	}
}
