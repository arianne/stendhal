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
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.Stackable;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;

public class VampireSwordTest {
	private static String questSlot;
	private static final String sickySlotName = "sicky_fill_goblet";
	private static VampireSword vs;

	private static final String DWARF_NPC = "Hogart";
	private static final String VAMPIRE_NPC = "Markovich";

	private final Map<String, Integer> requiredForFilling = new TreeMap<String, Integer>();

	private void fillSlots(Player player) {
		for (Map.Entry<String, Integer> entry : requiredForFilling.entrySet()) {
			int needed = entry.getValue();
			String name = entry.getKey();

			Item item = SingletonRepository.getEntityManager().getItem(name);
			if (needed != 1) {
				assertTrue(item instanceof Stackable<?>);
				((Stackable<?>) item).setQuantity(needed);
			}

			player.equipToInventoryOnly(item);
		}
	}

	@BeforeClass
	public static void setupBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		SingletonRepository.getNPCList().add(new SpeakerNPC(DWARF_NPC));
		SingletonRepository.getNPCList().add(new SpeakerNPC(VAMPIRE_NPC));

		vs = new VampireSword();
		vs.addToWorld();

		questSlot = vs.getSlotName();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		SingletonRepository.getNPCList().clear();
	}

	public VampireSwordTest() {
		requiredForFilling.put("vampirette entrails", 7);
		requiredForFilling.put("bat entrails", 7);
		requiredForFilling.put("skull ring", 1);
		requiredForFilling.put("empty goblet", 1);
	}

	// **** Early quest tests ****
	@Test
	public void requestQuest()  {
		for (String request : ConversationPhrases.QUEST_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

			assertFalse(player.hasQuest(questSlot));
			en.setCurrentState(ConversationStates.ATTENDING);

			en.step(player, request);
			assertEquals(request, "I can forge a powerful life stealing sword for you. You will need to go to the Catacombs below Semos Graveyard and fight the Vampire Lord. Are you interested?", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.QUEST_OFFERED);
		}
	}

	@Test
	public void requestAgainAfterDone() {
		for (String request : ConversationPhrases.QUEST_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

			player.setQuest(questSlot, "done");
			en.setCurrentState(ConversationStates.ATTENDING);

			en.step(player, request);
			assertEquals(request, "What are you bothering me for now? You've got your sword, go and use it!", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
		}
	}

	@Test public void requestWhileQuestActive() {
		for (String request : ConversationPhrases.QUEST_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

			// getting to this state should not be possible while the quest is active,
			// but there's a response for it in the quest. test for a sane answer in
			// case the implementation changes
			for (String state : Arrays.asList("start", "forging")) {
				player.setQuest(questSlot, state);
				en.setCurrentState(ConversationStates.ATTENDING);

				en.step(player, request);
				assertEquals(request, "Why are you bothering me when you haven't completed your quest yet?", getReply(npc));
				assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
			}
		}
	}

	@Test
	public void rejectQuest() {
		final Player player = PlayerTestHelper.createPlayer("me");
		final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
		final Engine en = vs.npcs.get(DWARF_NPC).getEngine();
		final double karma = player.getKarma();

		assertFalse(player.hasQuest(questSlot));
		en.setCurrentState(ConversationStates.QUEST_OFFERED);

		en.step(player, "no");
		assertEquals("Refusing", "Oh, well forget it then. You must have a better sword than I can forge, huh? Bye.", getReply(npc));
		assertEquals("karma penalty", karma - 5.0, player.getKarma(), 0.01);
		assertFalse(player.isEquipped("empty goblet"));
		assertEquals(en.getCurrentState(), ConversationStates.IDLE);
	}

	@Test
	public void acceptQuest() {
		for (String answer : ConversationPhrases.YES_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

			assertFalse(player.hasQuest(questSlot));
			en.setCurrentState(ConversationStates.QUEST_OFFERED);

			en.step(player, answer);
			assertEquals("Then you need this #goblet. Take it to the Semos #Catacombs.", getReply(npc));
			assertTrue("Player is given a goblet", player.isEquipped("empty goblet"));
			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
		}
	}

	/**
	 * Tests for dwarfsExplanations.
	 */
	@Test
	public void testDwarfsExplanations() {
		final Player player = PlayerTestHelper.createPlayer("me");
		final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
		final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

		en.setCurrentState(ConversationStates.ATTENDING);
		en.step(player, "catacombs");
		assertEquals("answer to 'catacombs'", "The Catacombs of north Semos of the ancient #stories.", getReply(npc));
		assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);

		en.step(player, "goblet");
		assertEquals("answer to 'goblet'", "Go fill it with the blood of the enemies you meet in the #Catacombs.", getReply(npc));
	}

	@Test
	public void greetDwarfWithGoblet() {
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

			assertFalse(player.hasQuest(questSlot));
			en.setCurrentState(ConversationStates.IDLE);
			player.setQuest(questSlot, "start");

			final Item goblet = SingletonRepository.getEntityManager().getItem("empty goblet");
			player.equipToInventoryOnly(goblet);

			en.step(player, hello);
			assertEquals(hello, "Did you lose your way? The Catacombs are in North Semos. Don't come back without a full goblet! Bye!", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.IDLE);
		}
	}

	@Test
	public void greetDwarfWithLostGoblet() {
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

			assertFalse(player.hasQuest(questSlot));
			assertFalse(player.isEquipped("empty goblet"));
			assertFalse(player.isEquipped("goblet"));
			en.setCurrentState(ConversationStates.IDLE);
			player.setQuest(questSlot, "start");

			en.step(player, hello);
			assertEquals(hello, "I hope you didn't lose your goblet! Do you need another?", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.QUESTION_1);
		}
	}


	@Test
	public void getAnotherGoblet() {
		for (String answer : ConversationPhrases.YES_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

			assertFalse(player.hasQuest(questSlot));
			assertFalse(player.isEquipped("empty goblet"));
			assertFalse(player.isEquipped("goblet"));
			en.setCurrentState(ConversationStates.QUESTION_1);
			player.setQuest(questSlot, "start");

			en.step(player, answer);
			assertEquals(answer, "You stupid ..... Be more careful next time. Bye!", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.IDLE);
			assertTrue("Player is given a goblet", player.isEquipped("empty goblet"));
		}
	}

	@Test
	public void refuseAnotherGoblet() {
		final Player player = PlayerTestHelper.createPlayer("me");
		final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
		final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

		assertFalse(player.hasQuest(questSlot));
		assertFalse(player.isEquipped("empty goblet"));
		assertFalse(player.isEquipped("goblet"));
		en.setCurrentState(ConversationStates.QUESTION_1);
		player.setQuest(questSlot, "start");

		en.step(player, "no");
		assertEquals("Then why are you back here? Go slay some vampires! Bye!", getReply(npc));
		assertEquals(en.getCurrentState(), ConversationStates.IDLE);
		assertFalse("Player is not given a goblet", player.isEquipped("empty goblet"));
	}

	// **** Goblet filling tests ****
	@Test
	public void sayHelloToVampire() {
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
			final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();

            // Make sure npc is not attending for each pass
            en.step(player, "bye");

			en.step(player, hello);
			assertEquals("vampires greeting", "Please don't try to kill me...I'm just a sick old #vampire. Do you have any #blood I could drink? If you have an #empty goblet I will #fill it with blood for you in my cauldron.", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
			en.setCurrentState(ConversationStates.IDLE);
		}
	}

	@Test
	public void sayByeToVampire() {
		for (String bye : ConversationPhrases.GOODBYE_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
			final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();

			en.setCurrentState(ConversationStates.ATTENDING);
			en.step(player, bye);
			assertEquals("vampires bye message", "*cough* ... farewell ... *cough*", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.IDLE);
		}
	}

	/**
	 * Tests for bloodMaterialDescription.
	 */
	@Test
	public void testBloodMaterialDescription() {
		for (String material : Arrays.asList("blood", "vampirette entrails", "bat entrails")) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
			final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();

			en.setCurrentState(ConversationStates.ATTENDING);

			en.step(player, material);
			assertEquals("answer to '" + material + "'", "I need blood. I can take it from the entrails of the alive and undead. I will mix the bloods together for you and #fill your #goblet, if you let me drink some too. But I'm afraid of the powerful #lord.", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
		}
	}

	/**
	 * Tests for vampireLordDescription.
	 */
	@Test
	public void testVampireLordDescription() {
		for (String word : Arrays.asList("lord", "vampire", "skull ring")) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
			final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();

			en.setCurrentState(ConversationStates.ATTENDING);

			en.step(player, word);
			assertEquals("answer to '" + word + "'", "The Vampire Lord rules these Catacombs! And I'm afraid of him. I can only help you if you kill him and bring me his skull ring with the #goblet.", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
		}
	}

	/**
	 * Tests for vampiresGobletDescription.
	 */
	@Test
	public void testVampiresGobletDescription() {
		for (String word : Arrays.asList("empty goblet", "goblet")) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
			final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();

			en.setCurrentState(ConversationStates.ATTENDING);

			en.step(player, word);
			assertEquals("answer to '" + word + "'", "Only a powerful talisman like this cauldron or a special goblet should contain blood.", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
		}
	}

	@Test
	public void askForFillingWithoutNeededItems() {
		final Player player = PlayerTestHelper.createPlayer("me");
		final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
		final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();

		en.setCurrentState(ConversationStates.ATTENDING);

		en.step(player, "fill");
		// the list of needed items goes through various hashtables before
		// ending in the answer, so the order is very much implementation
		// defined. don't test for it - just test that Markovich wants
		// something
		String answer = getReply(npc);
		assertTrue("answer to 'fill'", answer.startsWith("I can only fill a goblet if you bring me "));
		assertEquals("should not have a '" + sickySlotName + "' slot", null, player.getQuest(sickySlotName));
		assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
	}

	@Test
	public void askForFillingWithIncompleteItems() {
		final Player player = PlayerTestHelper.createPlayer("me");
		final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
		final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();

		Item item = SingletonRepository.getEntityManager().getItem("empty goblet");
		player.equipToInventoryOnly(item);
		item = SingletonRepository.getEntityManager().getItem("skull ring");
		player.equipToInventoryOnly(item);
		item = SingletonRepository.getEntityManager().getItem("bat entrails");
		player.equipToInventoryOnly(item);
		item = SingletonRepository.getEntityManager().getItem("vampirette entrails");
		player.equipToInventoryOnly(item);

		en.step(player, "fill");
		String answer = getReply(npc);
		assertTrue("answer to 'fill'", answer.startsWith("I can only fill a goblet if you bring me "));
		assertEquals("should not have a '" + sickySlotName + "' slot", null, player.getQuest(sickySlotName));
		assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
	}

	@Test
	public void askForFilling() {
		final Player player = PlayerTestHelper.createPlayer("me");
		final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
		final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();

		en.setCurrentState(ConversationStates.ATTENDING);

		fillSlots(player);

		en.step(player, "fill");
		String answer = getReply(npc);
		assertTrue("answer to 'fill'", answer.startsWith("I need you to fetch me "));
		assertTrue("answer to 'fill'", answer.endsWith("Do you have what I need?"));
		assertEquals(en.getCurrentState(), ConversationStates.PRODUCTION_OFFERED);

		en.step(player, "bye");
		assertEquals("*cough* ... farewell ... *cough*", getReply(npc));
	}

	@Test
	public void startFillingGoblet() {
		for (String yes : ConversationPhrases.YES_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
			final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();

			npc.setCurrentState(ConversationStates.IDLE);
			en.step(player, "hi");
			assertEquals("Please don't try to kill me...I'm just a sick old #vampire. Do you have any #blood I could drink? If you have an #empty goblet I will #fill it with blood for you in my cauldron.", getReply(npc));

			for(Map.Entry<String, Integer> it : requiredForFilling.entrySet()) {
				int amount = it.getValue();
				if (amount > 1) {
					PlayerTestHelper.equipWithStackableItem(player, it.getKey(), amount);
				} else {
					PlayerTestHelper.equipWithItem(player, it.getKey());
				}
			}

			en.step(player, "fill");
			assertEquals("I need you to fetch me 7 #'bat entrails', 7 #'vampirette entrails', a #'skull ring', and an #'empty goblet' for this job, which will take 5 minutes. Do you have what I need?", getReply(npc));

			en.step(player, yes);
			assertEquals("answer to '" + yes + "'", "OK, I will fill a goblet for you, but that will take some time. Please come back in 5 minutes.", getReply(npc));

			en.step(player, "bye");
			assertEquals("*cough* ... farewell ... *cough*", getReply(npc));

			// wait 5 minutes
			PlayerTestHelper.setPastTime(player, questSlot, 2, 5*60);

			en.step(player, "hi");
			assertEquals("Welcome back! I'm still busy with your order to fill a goblet for you. Come back in 5 minutes to get it.", getReply(npc));

			assertFalse(player.isEquipped("goblet"));
			for(String item : requiredForFilling.keySet()) {
				assertFalse("vampire took " + item, player.isEquipped(item));
			}

			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);
			assertTrue("player has " + sickySlotName + "slot", player.hasQuest(sickySlotName));

			en.step(player, "bye");
			assertEquals("*cough* ... farewell ... *cough*", getReply(npc));
		}
	}

	@Test
	public void tryGettingGobletTooEarly() {
		String questState = "1;goblet;" + Long.toString(new Date().getTime());
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
			final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();

			en.setCurrentState(ConversationStates.IDLE);
			player.setQuest(sickySlotName, questState);

			en.step(player, hello);
			// This will fail if someone manages to stop the test
			// within the loop and continue later. (Or to run it on a
			// ridiculously slow computer)
			assertEquals("too early '" + hello + "'", "Welcome back! I'm still busy with your order to fill a goblet for you. Come back in 5 minutes to get it.", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);

			// bothering Markovich should not affect the quest state
			// or give the goblet too early
			assertEquals(questState, player.getQuest(sickySlotName));
			assertFalse(player.isEquipped("goblet"));
		}
	}

	// Try dates in the future too. Should not happen, but you never know...
	@Test
	public void tryGettingGobletWayTooEarly() {
		// 1 min in the future
		String questState = "1;goblet;" + Long.toString(new Date().getTime() + 60 * 1000);
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
			final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();

			en.setCurrentState(ConversationStates.IDLE);
			player.setQuest(sickySlotName, questState);

			en.step(player, hello);
			assertTrue("''" + hello + "' in future", getReply(npc).startsWith("Welcome back! I'm still busy with your order to fill a goblet for you. Come back in"));
			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);

			// bothering Markovich should not affect the quest state
			// or give the goblet too early
			assertEquals(questState, player.getQuest(sickySlotName));
			assertFalse(player.isEquipped("goblet"));
		}
	}

	@Test
	public void fillGoblet() {
		final Player player = PlayerTestHelper.createPlayer("me");
		final SpeakerNPC npc = vs.npcs.get(VAMPIRE_NPC);
		final Engine en = vs.npcs.get(VAMPIRE_NPC).getEngine();

		// jump to the past
		String questState = "1;goblet;" + Long.toString(new Date().getTime() - 5 * 60 * 1000);
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			en.setCurrentState(ConversationStates.IDLE);
			player.setQuest(sickySlotName, questState);

			en.step(player, hello);
			assertEquals("''" + hello + "' in past", "Welcome back! I'm done with your order. Here you have the goblet.", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.ATTENDING);

			assertEquals("done", player.getQuest(sickySlotName));
			assertTrue("player got the goblet", player.isEquipped("goblet"));

			final Item goblet = player.getFirstEquipped("goblet");
			assertEquals("The filled goblet is bound", "me", goblet.getBoundTo());

			player.drop("goblet");
		}
	}

	// *** Forging tests ***
	@Test
	public void greetDwarfWithFullGobletNoKill() {
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

			en.setCurrentState(ConversationStates.IDLE);
			player.setQuest(questSlot, "start");
			Item item = SingletonRepository.getEntityManager().getItem("goblet");
			player.equipToInventoryOnly(item);

			en.step(player, hello);
			assertEquals("Hm, that goblet is not filled with vampire blood; it can't be, you have not killed the vampire lord. You must slay him.", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.IDLE);
		}
	}

	@Test
	public void greetDwarfWithFullGobletWithKill() {
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

			en.setCurrentState(ConversationStates.IDLE);
			player.setQuest(questSlot, "start");
			Item item = SingletonRepository.getEntityManager().getItem("goblet");
			player.equipToInventoryOnly(item);
			player.setSharedKill("vampire lord");

			en.step(player, hello);
			assertEquals("You have battled hard to bring that goblet. I will use it to #forge the vampire sword", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.QUEST_ITEM_BROUGHT);
		}
	}
	@Test
	public void askAboutForging() {
		final Player player = PlayerTestHelper.createPlayer("me");
		final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
		final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

		en.setCurrentState(ConversationStates.QUEST_ITEM_BROUGHT);
		player.setQuest(questSlot, "start");

		en.step(player, "forge");
		assertEquals("Bring me 10 #iron bars to forge the sword with. Don't forget to bring the goblet too.", getReply(npc));
		assertEquals(en.getCurrentState(), ConversationStates.QUEST_ITEM_BROUGHT);
	}

	@Test
	public void askAboutIron() {
		final Player player = PlayerTestHelper.createPlayer("me");
		final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
		final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

		en.setCurrentState(ConversationStates.QUEST_ITEM_BROUGHT);
		player.setQuest(questSlot, "start");

		en.step(player, "iron");
		assertEquals("You know, collect the iron ore lying around and get it cast! Bye!", getReply(npc));
		assertEquals(en.getCurrentState(), ConversationStates.IDLE);
	}

	@Test
	public void greetDwarfWithRequiredItems() {
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

			en.setCurrentState(ConversationStates.IDLE);
			player.setQuest(questSlot, "start");

			Item item = SingletonRepository.getEntityManager().getItem("goblet");
			player.equipToInventoryOnly(item);

			PlayerTestHelper.equipWithStackableItem(player, "iron", 10);

			player.setSharedKill("vampire lord");

			en.step(player, hello);
			assertEquals("You've brought everything I need to make the vampire sword. Come back in 10 minutes and it will be ready", getReply(npc));
			assertFalse("dwarf took the goblet", player.isEquipped("goblet"));
			assertFalse("dwarf took the iron", player.isEquipped("iron"));
			assertTrue("in forging state", player.getQuest(questSlot).startsWith("forging;"));
			assertEquals(en.getCurrentState(), ConversationStates.IDLE);
		}
	}

	@Test
	public void tryGettingSwordTooEarly() {
		String questState = "forging;" + Long.toString(new Date().getTime());
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();

			en.setCurrentState(ConversationStates.IDLE);
			player.setQuest(questSlot, questState);

			en.step(player, hello);
			assertEquals("too early '" + hello + "'", "I haven't finished forging the sword. Please check back in 10 minutes.", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.IDLE);

			// should not make any change in quest state, or give the sword
			assertEquals(questState, player.getQuest(questSlot));
			assertFalse(player.isEquipped("vampire sword"));
		}
	}

	@Test
	public void gettingTheSword() {
		String questState = "forging;" + Long.toString(new Date().getTime() - 10 * 60 * 1000);
		for (String hello : ConversationPhrases.GREETING_MESSAGES) {
			final Player player = PlayerTestHelper.createPlayer("me");
			final SpeakerNPC npc = vs.npcs.get(DWARF_NPC);
			final Engine en = vs.npcs.get(DWARF_NPC).getEngine();
			int xp = player.getXP();
			double karma = player.getKarma();

			en.setCurrentState(ConversationStates.IDLE);
			player.setQuest(questSlot, questState);

			en.step(player, hello);
			assertEquals("I have finished forging the mighty Vampire Sword. You deserve this. Now i'm going back to work, goodbye!", getReply(npc));
			assertEquals(en.getCurrentState(), ConversationStates.IDLE);

			assertEquals("done", player.getQuest(questSlot));
			assertTrue("got the sword", player.isEquipped("vampire sword"));

			final Item sword = player.getFirstEquipped("vampire sword");
			assertEquals("The vampire sword is bound", "me", sword.getBoundTo());
			assertEquals("XP bonus", xp + 5000, player.getXP());
			assertEquals("final karma bonus", karma + 15.0, player.getKarma(), 0.01);
		}
	}
}
