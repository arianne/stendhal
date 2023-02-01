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
import static utilities.PlayerTestHelper.equipWithItem;
import static utilities.PlayerTestHelper.equipWithStackableItem;
import static utilities.SpeakerNPCTestHelper.getSpeakerNPC;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import utilities.quest_runner.ChildrensFriendStub;
import utilities.quest_runner.PrivateDetectiveStub;
import utilities.quest_runner.StillBelievingStub;


public class QuestRunner {

	// for debugging
	private static String getReply(final SpeakerNPC npc) {
		return utilities.SpeakerNPCTestHelper.getReply(npc);
	}

	public static void doQuestBeerForHayunn(final Player player) {
		final String questSlot = "beer_hayunn";
		Engine en = getSpeakerNPC("Hayunn Naratha").getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");
		equipWithItem(player, "beer");
		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestBowsForOuchit(final Player player) {
		final String questSlot = "bows_ouchit";
		final Engine en = getSpeakerNPC("Ouchit").getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "yes");
		equipWithStackableItem(player, "wood", 10);
		en.step(player, "wood");
		equipWithItem(player, "horse hair");
		en.step(player, "horse hair");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestCampfire(final Player player) {
		ChildrensFriendStub.doQuestSally(player);
	}

	public static void doQuestChocolateForElisabeth(final Player player) {
		ChildrensFriendStub.doQuestElisabeth(player);
	}

	public static void doQuestCodedMessage(final Player player) {
		ChildrensFriendStub.doQuestFinn(player);
	}

	public static void doQuestDailyMonster(final Player player) {
		final String questSlot = "daily";
		if (player.getQuest(questSlot, 1) != null) {
			// reset timestamp to do quest again
			player.setQuest(questSlot, 1, "0");
		}
		final Engine en = getSpeakerNPC("Mayor Sakhs").getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		player.incSoloKillCount(player.getQuest(questSlot, 0).split(",")[0]);
		en.step(player, "done");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestEggsForMarianne(final Player player) {
		ChildrensFriendStub.doQuestMarianne(player);
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

	public static void doQuestFindGhosts(final Player player) {
		PrivateDetectiveStub.doQuestCarena(player);
	}

	public static void doQuestFindJefsMom(final Player player) {
		ChildrensFriendStub.doQuestJef(player);
	}

	public static void doQuestFishSoupForHughie(final Player player) {
		ChildrensFriendStub.doQuestHughie(player);
	}

	public static void doQuestFindRatKids(final Player player) {
		PrivateDetectiveStub.doQuestAgnus(player);
	}

	public static void doQuestGoodiesForRudolph(final Player player) {
		final String questSlot = "goodies_rudolph";
		final Engine en = getSpeakerNPC("Rudolph").getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");
		equipWithStackableItem(player, "apple", 10);
		equipWithStackableItem(player, "carrot", 10);
		for (int idx = 0; idx < 5; idx++) {
			equipWithItem(player, "reindeer moss");
		}
		assertEquals(5, player.getNumberOfEquipped("reindeer moss"));
		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestGrandfathersWish(final Player player) {
		PrivateDetectiveStub.doQuestNiall(player);
	}

	public static void doQuestHatForMonogenes(final Player player) {
		final String questSlot = "hat_monogenes";
		final Engine en = getSpeakerNPC("Monogenes").getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");
		equipWithItem(player, "leather helmet");
		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestHerbsForCarmen(final Player player) {
		final String questSlot = "herbs_for_carmen";
		final Engine en = getSpeakerNPC("Carmen").getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "no");
		en.step(player, "ingredients");
		en.step(player, "yes");
		en.step(player, "ingredients");
		en.step(player, "yes");
		equipWithItem(player, "porcini");
		en.step(player, "porcini");
		equipWithItem(player, "button mushroom");
		en.step(player, "button mushroom");
		equipWithStackableItem(player, "arandula", 5);
		en.step(player, "arandula");
		equipWithStackableItem(player, "apple", 3);
		en.step(player, "apple");
		equipWithStackableItem(player, "wood", 2);
		en.step(player, "wood");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestHungryJoshua(final Player player) {
		final String questSlot = "hungry_joshua";
		final SpeakerNPC xoderos = getSpeakerNPC("Xoderos");
		Engine en = xoderos.getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "food");
		en.step(player, "yes");
		en.step(player, "bye");
		equipWithStackableItem(player, "sandwich", 5);
		en = getSpeakerNPC("Joshua").getEngine();
		en.step(player, "hi");
		en.step(player, "sandwich");
		en.step(player, "yes");
		en.step(player, "bye");
		en = xoderos.getEngine();
		en.step(player, "hi");
		en.step(player, "Joshua");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestIcecreamForAnnie(final Player player) {
		ChildrensFriendStub.doQuestAnnie(player);
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

	public static void doQuestLearnAboutOrbs(final Player player) {
		final String questSlot = "learn_scrying";
		if (player.getLevel() < 11) {
			player.setLevel(11);
		}
		final Engine en = getSpeakerNPC("Ilisa").getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "use");
		en.step(player, "yes");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestLookBookforCeryl(final Player player) {
		final String questSlot = "ceryl_book";
		final SpeakerNPC ceryl = getSpeakerNPC("Ceryl");
		Engine en = ceryl.getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "book");
		en.step(player, "yes");
		en.step(player, "bye");
		en = getSpeakerNPC("Jynath").getEngine();
		en.step(player, "hi");
		en.step(player, "book");
		en.step(player, "bye");
		en = ceryl.getEngine();
		en.step(player, "hi");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot, 0));
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

	public static void doQuestMedicineForTad(final Player player) {
		ChildrensFriendStub.doQuestTad(player);
	}

	public static void doQuestMeetBunny(final Player player) {
		StillBelievingStub.doQuestBunny(player);
	}

	public static void doQuestMeetHackim(final Player player) {
		final String questSlot = "meet_hackim";
		final Engine en = getSpeakerNPC("Hackim Easso").getEngine();
		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "yes");
		en.step(player, "yes");
		en.step(player, "offer");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestMeetHayunn(final Player player) {
		final String questSlot = "meet_hayunn";
		final Engine en = getSpeakerNPC("Hayunn Naratha").getEngine();
		en.step(player, "hi");
		en.step(player, "bye");
		player.incSoloKillCount("rat");
		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "yes");
		en.step(player, "yes");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestMeetIo(final Player player) {
		final String questSlot = "meet_io";
		final Engine en = getSpeakerNPC("Io Flotto").getEngine();
		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "yes");
		en.step(player, "yes");
		en.step(player, "yes");
		en.step(player, "yes");
		en.step(player, "yes");
		en.step(player, "yes");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestMeetKetteh(final Player player) {
		final String questSlot = "Ketteh";
		final Engine en = getSpeakerNPC("Ketteh Wehoh").getEngine();
		en.step(player, "hi");
		en.step(player, "manners");
		en.step(player, "bye");
		assertEquals("learnt_manners", player.getQuest(questSlot, 0));
	}

	public static void doQuestMeetMonogenes(final Player player) {
		final String questSlot = "Monogenes";
		final Engine en = getSpeakerNPC(questSlot).getEngine();
		en.step(player, "hi");
		en.step(player, "yes");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestPlinksToy(final Player player) {
		ChildrensFriendStub.doQuestPlink(player);
	}

	public static void doQuestPizzaDelivery(final Player player) {
		final String questSlot = "pizza_delivery";
		Engine en = getSpeakerNPC("Leander").getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");
		en = getSpeakerNPC(player.getQuest(questSlot, 0)).getEngine();
		en.step(player, "hi");
		en.step(player, "pizza");
		en.step(player, "bye");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestMeetSanta(final Player player) {
		StillBelievingStub.doQuestSanta(player);
	}

	public static void doQuestNewsFromHackim(final Player player) {
		final String questSlot = "news_hackim";
		Engine en = getSpeakerNPC("Hackim Easso").getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		en.step(player, "bye");
		en = getSpeakerNPC("Xin Blanca").getEngine();
		en.step(player, "hi");
		en.step(player, "bye");
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

	public static void doQuestSevenCherubs(final Player player) {
		PrivateDetectiveStub.doQuestCherubs(player);
	}

	public static void doQuestSheepGrowing(final Player player) {
		final String questSlot = "sheep_growing";
		final Engine en = getSpeakerNPC("Nishiya").getEngine();
		en.step(player, "hi");
		en.step(player, "quest");
		en.step(player, "yes");
		// StendhalRPAction         (653 ) - Unable to place sheep at testzone[33,45]
		player.setQuest(questSlot, 0, "handed_over");
		en.step(player, "hi");
		en.step(player, "yes");
		assertEquals("done", player.getQuest(questSlot, 0));
	}

	public static void doQuestSusi(final Player player) {
		ChildrensFriendStub.doQuestSusi(player);
	}

	public static void doQuestToysCollector(final Player player) {
		ChildrensFriendStub.doQuestAnna(player);
	}
}
