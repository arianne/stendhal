/***************************************************************************
 *                    Copyright © 2020-2023 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package utilities.quest_runner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static utilities.SpeakerNPCTestHelper.getSpeakerNPC;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;


public class PrivateDetectiveStub {

	public static void doQuestAgnus(final Player player) {
		final String questSlot = "find_rat_kids";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC agnus = getSpeakerNPC("Agnus");
		final SpeakerNPC opal = getSpeakerNPC("Opal");
		final SpeakerNPC mariel = getSpeakerNPC("Mariel");
		final SpeakerNPC cody = getSpeakerNPC("Cody");
		final SpeakerNPC avalon = getSpeakerNPC("Avalon");
		assertNotNull(agnus);
		assertNotNull(opal);
		assertNotNull(mariel);
		assertNotNull(cody);
		assertNotNull(avalon);

		Engine en = agnus.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "children");
		en.step(player, "yes");
		en.step(player, "bye");

		for (final SpeakerNPC ratchild: new SpeakerNPC[]{opal, mariel, cody, avalon}) {
			en = ratchild.getEngine();

			en.step(player, "hi");

			// don't need to say "bye"
			assertEquals(ConversationStates.IDLE, en.getCurrentState());
		}

		en = agnus.getEngine();

		en.step(player, "hi");
		en.step(player, opal.getName());
		en.step(player, mariel.getName());
		en.step(player, cody.getName());
		en.step(player, avalon.getName());
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestCarena(final Player player) {
		final String questSlot = "find_ghosts";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC carena = getSpeakerNPC("Carena");
		final SpeakerNPC mary = getSpeakerNPC("Mary");
		final SpeakerNPC ben = getSpeakerNPC("Ben");
		final SpeakerNPC zak = getSpeakerNPC("Zak");
		final SpeakerNPC goran = getSpeakerNPC("Goran");
		assertNotNull(carena);
		assertNotNull(mary);
		assertNotNull(ben);
		assertNotNull(zak);
		assertNotNull(goran);

		Engine en = carena.getEngine();

		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "spirits");
		en.step(player, "yes");
		en.step(player, "bye");

		for (final SpeakerNPC ghost: new SpeakerNPC[]{mary, ben, zak, goran}) {
			en = ghost.getEngine();

			en.step(player, "hi");

			// don't need to say "bye"
			assertEquals(ConversationStates.IDLE, en.getCurrentState());
		}

		en = carena.getEngine();

		en.step(player, "hi");
		en.step(player, mary.getName());
		en.step(player, ben.getName());
		en.step(player, zak.getName());
		en.step(player, goran.getName());
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestCherubs(final Player player) {
		final String questSlot = "seven_cherubs";
		assertNull(player.getQuest(questSlot));

		SpeakerNPC cherub;
		Engine en;
		final String[] nameList = {
				"Cherubiel", "Gabriel", "Ophaniel", "Raphael", "Uriel",
				"Zophiel", "Azazel"};

		final StringBuilder sb = new StringBuilder();

		for (final String name: nameList) {
			cherub = getSpeakerNPC(name);
			en = cherub.getEngine();

			en.step(player, "hi");

			// don't need to say "bye"
			assertEquals(ConversationStates.IDLE, en.getCurrentState());

			sb.append(";" + name);
		}

		assertEquals(sb.toString(), player.getQuest(questSlot));
	}

	public static void doQuestJef(final Player player) {
		ChildrensFriendStub.doQuestJef(player);
	}

	public static void doQuestNiall(final Player player) {
		final String questSlot = "a_grandfathers_wish";
		assertNull(player.getQuest(questSlot));

		final SpeakerNPC niall = getSpeakerNPC("Niall Breland");
		assertNotNull(niall);

		// set quest to final step
		player.setQuest(questSlot, "investigate;find_myling:done;holy_water:done;cure_myling:done");

		final Engine en = niall.getEngine();
		en.step(player, "hi");
		en.step(player, "bye");

		assertEquals("done", player.getQuest(questSlot, 0));
	}
}
