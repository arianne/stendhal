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

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class WizardBankTest extends ZonePlayerAndNPCTestImpl {

	private static final String GRAFINDLE_QUEST_SLOT = "grafindle_gold";
	private static final String ZARA_QUEST_SLOT = "suntan_cream_zara";
	private static final String QUEST_SLOT = "wizard_bank";
	private static final String ZONE_NAME = "int_magic_bank";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();

		setupZone(ZONE_NAME);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		MockStendhalRPRuleProcessor.get().clearPlayers();
	}

	public WizardBankTest() {
		super(ZONE_NAME, "Javier X");
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		quest = new WizardBank();
		quest.addToWorld();
	}

	/**
	 * Tests for hiAndBye.
	 */
	@Test
	public void testHiAndBye() {
		final SpeakerNPC npc = getNPC("Javier X");
		final Engine en = npc.getEngine();

		 // Test trusted access to the bank.
		assertTrue(en.step(player, "hi Javier"));
		assertEquals("You may not use this bank if you have not gained the right to use the chests at Nalwor, nor if you have not earned the trust of a certain young woman. Goodbye!",
				getReply(npc));

		player.setQuest(GRAFINDLE_QUEST_SLOT, "done");
		assertTrue(en.step(player, "hi Javier"));
		assertEquals("You may not use this bank if you have not gained the right to use the chests at Nalwor, nor if you have not earned the trust of a certain young woman. Goodbye!",
				getReply(npc));

		player.setQuest(ZARA_QUEST_SLOT, "done");
		 // Now we are welcome.
		assertTrue(en.step(player, "hi Javier"));
		assertEquals("Welcome to the Wizard's Bank, player.", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Goodbye.", getReply(npc));

		 // set quest to "start"
		player.setQuest(QUEST_SLOT, "start");

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to the Wizard's Bank, player. You may #leave sooner, if required.", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Goodbye.", getReply(npc));

		 // set quest to "done"
		player.setQuest(QUEST_SLOT, "done");

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to the Wizard's Bank, player. Do you wish to pay to access your chest again?", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "no"));
		assertTrue(npc.isTalking());
		assertEquals("Very well.", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Goodbye.", getReply(npc));

		 // Test second chest access
		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to the Wizard's Bank, player. Do you wish to pay to access your chest again?", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "yes"));
		assertTrue(npc.isTalking());
		assertEquals("You do not have enough money!", getReply(npc));

		assertTrue(en.step(player, "bye"));
		assertFalse(npc.isTalking());
		assertEquals("Goodbye.", getReply(npc));

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to the Wizard's Bank, player. Do you wish to pay to access your chest again?", getReply(npc));
		assertTrue(npc.isTalking());

		 // equip the player with enough money to pay the fee
		assertTrue(equipWithMoney(player, 1000));

		assertTrue(en.step(player, "yes"));
		assertTrue(npc.isTalking());
		assertEquals("Semos, Nalwor and Fado bank chests are to my right. The chests owned by Ados Bank Merchants and your friend Zara are to my left. If you are finished before your time here is done, please say #leave.", getReply(npc));
	}

	/**
	 * Tests for replies.
	 */
	@Test
	public void testReplies() {
		 // A named player name needed to create the name based hash code.
		final Player player = createPlayer("player1");

		final SpeakerNPC npc = getNPC("Javier X");
		final Engine en = npc.getEngine();

		 // set requirements to access the bank
		player.setQuest(GRAFINDLE_QUEST_SLOT, "done");
		player.setQuest(ZARA_QUEST_SLOT, "done");

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to the Wizard's Bank, player1.", getReply(npc));

		assertTrue(en.step(player, "job"));
		assertEquals("I control access to the bank. My spells ensure people cannot simply come and go as they please. We charge a #fee to #enter.", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "deal"));
		assertEquals("I would have thought that the offer of these #fiscal services is enough for you.", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "fiscal"));
		assertEquals("You do not understand the meaning of the word? You should spend more time in libraries, I hear the one in Ados is excellent. Anyhow, to #enter the bank just ask.", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "help me"));
		assertEquals("This bank is suffused with #magic, and as such you may access any vault you own. There will be a #fee to pay for this privilege, as we are not a charity.", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "magic"));
		assertEquals("Have you not heard of magic? It is what makes the grass grow here. Perhaps in time your kind will learn how to use this fine art.", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "task"));
		assertEquals("To #enter this bank you need only ask.", getReply(npc));
		assertTrue(npc.isTalking());
	}

	/**
	 * Tests for doQuest.
	 */
	@Test
	public void testDoQuest() {
		 // A named player name needed to create the name based hash code.
		final Player player = createPlayer("player2");
		registerPlayer(player, ZONE_NAME);

		final SpeakerNPC npc = getNPC("Javier X");
		final Engine en = npc.getEngine();

		 // set requirements to access the bank
		player.setQuest(GRAFINDLE_QUEST_SLOT, "done");
		player.setQuest(ZARA_QUEST_SLOT, "done");

		assertTrue(en.step(player, "hi"));
		assertEquals("Welcome to the Wizard's Bank, player2.", getReply(npc));
		assertFalse(player.hasQuest(QUEST_SLOT));

		assertTrue(en.step(player, "fee"));
		assertEquals("The fee is 1000 money. Do you want to pay?", getReply(npc));
		assertTrue(npc.isTalking());
		assertFalse(player.hasQuest(QUEST_SLOT));

		assertTrue(en.step(player, "yes"));
		assertEquals("You do not have enough money!", getReply(npc));
		assertTrue(npc.isTalking());
		assertFalse(player.hasQuest(QUEST_SLOT));

		assertTrue(en.step(player, "fee"));
		assertEquals("The fee is 1000 money. Do you want to pay?", getReply(npc));
		assertTrue(npc.isTalking());
		assertFalse(player.hasQuest(QUEST_SLOT));

		assertTrue(en.step(player, "no"));
		assertEquals("Very well.", getReply(npc));
		assertTrue(npc.isTalking());
		assertFalse(player.hasQuest(QUEST_SLOT));

		assertTrue(en.step(player, "leave"));
		assertEquals("Leave where?", getReply(npc));
		assertTrue(npc.isTalking());
		assertFalse(player.hasQuest(QUEST_SLOT));

		 // equip the player with enough money to pay the fee
		assertTrue(equipWithMoney(player, 1000));

		assertTrue(en.step(player, "fee"));
		assertEquals("The fee is 1000 money. Do you want to pay?", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "yes"));
		assertEquals("Semos, Nalwor and Fado bank chests are to my right. The chests owned by Ados Bank Merchants and your friend Zara are to my left. If you are finished before your time here is done, please say #leave.", getReply(npc));
		assertTrue(npc.isTalking());
		assertTrue(player.hasQuest(QUEST_SLOT));

		assertTrue(en.step(player, "yes"));
		assertEquals("Hm, I do not understand you. If you wish to #leave, just say", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "no"));
		assertEquals("Hm, I do not understand you. If you wish to #leave, just say", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "fee"));
		assertEquals("As you already know, the fee is 1000 money.", getReply(npc));
		assertTrue(npc.isTalking());

		assertTrue(en.step(player, "leave"));
		assertEquals("Thank you for using the Wizard's Bank", getReply(npc));
		assertTrue(npc.isTalking());

		player.setQuest(QUEST_SLOT, "done");

		assertTrue(en.step(player, "leave"));
		assertEquals("Leave where?", getReply(npc));
		assertTrue(npc.isTalking());
	}
}
