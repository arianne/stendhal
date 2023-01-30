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

	public static void doQuestElfPrincess(final Player player) {
		final String questSlot = "elf_princess";
		if (player.getQuest(questSlot, 1) != null) {
			// reset timestamp to do quest again
			player.setQuest(questSlot, 1, "0");
		}
		final SpeakerNPC princess = getSpeakerNPC("Tywysoga");
		final SpeakerNPC rose = getSpeakerNPC("Rose Leigh");
		assertEquals(ConversationStates.IDLE, princess.getEngine().getCurrentState());
		Engine en = princess.getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		assertEquals("start", player.getQuest(questSlot, 0));
		en.step(player, "bye");
		en = rose.getEngine();
		en.step(player, "hi");
		assertEquals(1, player.getNumberOfEquipped("rhosyd"));
		en = princess.getEngine();
		en.step(player, "hi");
		en.step(player, "flower");
		assertEquals(0, player.getNumberOfEquipped("rhosyd"));
		en.step(player, "bye");
	}

	public static void doQuestRestockFlowerShop(final Player player) {
		final String questSlot = "restock_flowershop";
		if (player.getQuest(questSlot, 1) != null) {
			// reset timestamp to do quest again
			player.setQuest(questSlot, 1, "0");
		}
		final SpeakerNPC seremela = getSpeakerNPC("Seremela");
		final Engine en = seremela.getEngine();
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
