/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
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

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.Level;
import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.maps.deniran.cityoutside.CloverHunterNPC;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;


public class LuckyFourLeafCloverTest extends ZonePlayerAndNPCTestImpl {

	private static String questSlot = "lucky_four_leaf_clover";


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Override
	@Before
	public void setUp() throws Exception {
		setupZone("test_zone", new CloverHunterNPC());
		setNpcNames("Maple");
		setZoneForPlayer("test_zone");

		super.setUp();

		assertThat(player, notNullValue());
		assertThat(player.hasQuest(questSlot), is(false));
		assertThat(getNPC("Maple"), notNullValue());

		QuestHelper.loadQuest(new LuckyFourLeafClover());
		assertThat(QuestHelper.isLoaded(questSlot), is(true));

		// set player to high level to prevent leveling up from interfering with HP reward check
		int lvl = 500;
		int xp = Level.getXP(lvl);
		player.setXP(xp);
		player.setLevel(lvl);
		assertThat(player.getLevel(), is(lvl));
		assertThat(player.getXP(), is(xp));
	}

	@Override
	@After
	public void tearDown() throws Exception {
		QuestHelper.unloadQuests(questSlot);
		assertThat(QuestHelper.isLoaded(questSlot), is(false));
		super.tearDown();
	}

	@Test
	public void testQuest() {
		SpeakerNPC npc = getNPC("Maple");
		Engine en = npc.getEngine();

		int playerXP = player.getXP();
		int playerAtkXP = player.getAtkXP();
		int playerDefXP = player.getDefXP();
		double playerKarma = player.getKarma();
		int playerBaseHP = player.getBaseHP();
		int playerPotions = player.getNumberOfEquipped("greater potion");

		int completions = MathHelper.parseIntDefault(player.getQuest(questSlot, 1), 0);
		String[] responses = new String[] {
				"I'm looking for a rare four-leaf clover. Do you want to help me find one?",
				"Thanks so much! I'm going to add this to my collection. Here are some potions to show my"
						+ " gratitude."
		};
		if (completions > 0) {
			responses = new String[] {
					"I want another four-leaf clover. Do you want to help again?",
					"Thanks again! I'm going to have so much luck! Here are some more potions to show my"
							+ " gratitude."
			};
		}

		if (player.isEquipped("four-leaf clover")) {
			player.drop("four-leaf clover", player.getNumberOfEquipped("four-leaf clover"));
		}
		assertThat(player.getNumberOfEquipped("four-leaf clover"), is(0));

		en.step(player, "hi");
		assertThat(getReply(npc), is("Hello fellow clover hunter!"));
		en.step(player, "task");
		assertThat(getReply(npc), is(responses[0]));
		en.step(player, "no");
		assertThat(getReply(npc), is("That's okay, I can find one on my own anyway."));
		assertThat(player.getQuest(questSlot, 0), is("rejected"));
		assertThat(player.getKarma(), is(playerKarma - 20));
		playerKarma = player.getKarma();
		// NPC stops attending player when quest is rejected
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
		en.step(player, "hi");
		en.step(player, "task");
		assertThat(getReply(npc), is(responses[0]));
		en.step(player, "yes");
		assertThat(getReply(npc), is("Great! Bring it here if you find one. And if you want some tips"
				+ " on #clover hunting, just ask."));
		assertThat(player.getQuest(questSlot, 0), is ("start"));
		en.step(player, "task");
		assertThat(getReply(npc), is("I already asked you to find me a four-leaf clover."));
		en.step(player, "bye");
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));

		// player does not have clover yet
		en.step(player, "hi");
		assertThat(getReply(npc), is("Hello fellow clover hunter!"));
		en.step(player, "bye");
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));

		// player has clover
		PlayerTestHelper.equipWithStackableItem(player, "four-leaf clover", 2);
		en.step(player, "hi");
		assertThat(getReply(npc), is("Oh my! You found a clover. It has four leaves! Can I have it?"
				+ " Please, please, please ..."));
		assertThat(en.getCurrentState(), is(ConversationStates.QUEST_ITEM_BROUGHT));
		en.step(player, "no");
		assertThat(getReply(npc), is("Hmph! I can find a better one anyway."));
		assertThat(player.getKarma(), is(playerKarma - 25));
		playerKarma = player.getKarma();
		// NPC stops attending player when denying giving clover
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
		en.step(player, "hi");
		en.step(player, "complete");
		assertThat(getReply(npc), is("Oh my! You found a clover. It has four leaves! Can I have it?"
				+ " Please, please, please ..."));
		assertThat(en.getCurrentState(), is(ConversationStates.QUEST_ITEM_BROUGHT));
		// player dropped clover
		player.drop("four-leaf clover", player.getNumberOfEquipped("four-leaf clover"));
		en.step(player, "yes");
		assertThat(getReply(npc), is("What are you trying to pull!? I saw you drop it."));
		assertThat(player.getKarma(), is(playerKarma - 25));
		playerKarma = player.getKarma();
		// NPC stops attending player when trying to trick her
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
		en.step(player, "hi");
		PlayerTestHelper.equipWithStackableItem(player, "four-leaf clover", 2);
		en.step(player, "complete");
		assertThat(getReply(npc), is("Oh my! You found a clover. It has four leaves! Can I have it?"
				+ " Please, please, please ..."));
		assertThat(en.getCurrentState(), is(ConversationStates.QUEST_ITEM_BROUGHT));
		en.step(player, "yes");
		assertThat(getReply(npc), is(responses[1]));
		assertThat(player.getQuest(questSlot, 0), is("done"));
		assertThat(MathHelper.parseIntDefault(player.getQuest(questSlot, 1), 0), is(completions + 1));
		// check that she only took 1 clover
		assertThat(player.getNumberOfEquipped("four-leaf clover"), is(1));

		// reward
		assertThat(player.getNumberOfEquipped("greater potion"), is(playerPotions + 20));
		assertThat(player.getXP(), is(playerXP + 1000));
		assertThat(player.getKarma(), is(playerKarma + 50));
		if (completions == 0) {
			assertThat(player.getAtkXP(), is(playerAtkXP + 10000));
			assertThat(player.getDefXP(), is(playerDefXP + 10000));
			assertThat(player.getBaseHP(), is(playerBaseHP + 150));
		} else {
			assertThat(player.getAtkXP(), is(playerAtkXP));
			assertThat(player.getDefXP(), is(playerDefXP));
			assertThat(player.getBaseHP(), is(playerBaseHP));
		}

		// immediately repeatable
		en.step(player, "hi");
		en.step(player, "task");
		assertThat(getReply(npc), is("I want another four-leaf clover. Do you want to help again?"));
		assertThat(en.getCurrentState(), is(ConversationStates.QUEST_OFFERED));
		en.step(player, "bye");
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
	}

	@Test
	public void testCompletions() {
		for (int count = 0; count < 5; count++) {
			assertEquals(count, MathHelper.parseIntDefault(player.getQuest(questSlot, 1), 0));
			testQuest();
		}
		assertEquals("5", player.getQuest(questSlot, 1));

		Engine en = getNPC("Maple").getEngine();

		// check that completions count is retained after quest is rejected & started
		en.step(player, "hi");
		en.step(player, "task");
		en.step(player, "no");
		assertThat(player.getQuest(questSlot, 0), is("rejected"));
		assertThat(player.getQuest(questSlot, 1), is("5"));
		en.step(player, "hi");
		en.step(player, "task");
		en.step(player, "yes");
		assertThat(player.getQuest(questSlot, 0), is("start"));
		assertThat(player.getQuest(questSlot, 1), is("5"));
		en.step(player, "bye");
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
	}
}
