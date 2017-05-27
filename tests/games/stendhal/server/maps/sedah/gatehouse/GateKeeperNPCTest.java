/***************************************************************************
 *                (C) Copyright 2005-2015 - Faiumoni e. V.                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.sedah.gatehouse;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
import utilities.QuestHelper;
import utilities.ZonePlayerAndNPCTestImpl;

public class GateKeeperNPCTest extends ZonePlayerAndNPCTestImpl {

	private static final String ZONE_NAME = "-1_fado_great_cave_w2";
	private static final String NPC_NAME = "Revi Borak";

	private static final String ITEM_MONEY = "money";
	private static final String ITEM_KEY = "sedah gate key";

	private static final String REPLY_HELLO = "What do you want?";
	private static final String REPLY_BRIBE_NOT_ENOUGH = "You think that amount will persuade me?! That's more than my job is worth!";
	private static final String REPLY_BRIBE_NOT_AVAILABLE = "Criminal! You don't have 300 money!";
	private static final String REPLY_BRIBE_ACCEPTED = "Ok, I got your money, here's the key.";
	private static final String REPLY_BRIBE_NO_MONEY = "A bribe of no money is no bribe! Bribe me with some amount!";
	private static final String REPLY_BRIBE_NOT_MONEY = "You can't bribe me with anything but money!";

	private Player player;
	private SpeakerNPC npc;
	private Engine engine;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
		setupZone(ZONE_NAME);
	}

	public GateKeeperNPCTest() {
		setNpcNames(NPC_NAME);
		setZoneForPlayer(ZONE_NAME);
		addZoneConfigurator(new GateKeeperNPC(), ZONE_NAME);
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();

		player = createPlayer("player");
		npc = SingletonRepository.getNPCList().get(NPC_NAME);
		engine = npc.getEngine();
	}

	@Test
	public void testBribeNotEnough() {
		startConversation();
		checkReply("bribe 299", REPLY_BRIBE_NOT_ENOUGH);
		assertFalse(player.isEquipped(ITEM_KEY));
	}

	@Test
	public void testBribeNotAvailable() {
		startConversation();
		checkReply("bribe 300", REPLY_BRIBE_NOT_AVAILABLE);
		assertFalse(player.isEquipped(ITEM_KEY));
	}

	@Test
	public void testBribeEnough() {
		PlayerTestHelper.equipWithStackableItem(player, ITEM_MONEY, 300);
		startConversation();
		checkReply("bribe 300", REPLY_BRIBE_ACCEPTED);
		assertTrue(player.isEquipped(ITEM_KEY));
		assertMoneyLeft(0);
	}

	@Test
	public void testBribeMoreThanEnough() {
		PlayerTestHelper.equipWithStackableItem(player, ITEM_MONEY, 1500);
		startConversation();
		checkReply("bribe 1000", REPLY_BRIBE_ACCEPTED);
		assertTrue(player.isEquipped(ITEM_KEY));
		assertMoneyLeft(500);
	}

	@Test
	public void testBribeNoMoney() {
		startConversation();
		checkReply("bribe", REPLY_BRIBE_NO_MONEY);
		assertFalse(player.isEquipped(ITEM_KEY));
	}

	@Test
	public void testBribeNotANumber() {
		startConversation();
		checkReply("bribe nan", REPLY_BRIBE_NOT_MONEY);
		assertFalse(player.isEquipped(ITEM_KEY));
	}

	@Test
	public void testBribeNotInteger() {
		PlayerTestHelper.equipWithStackableItem(player, ITEM_MONEY, 300);
		startConversation();
		checkReply("bribe 299,5", REPLY_BRIBE_ACCEPTED);
		assertTrue(player.isEquipped(ITEM_KEY));
		assertMoneyLeft(0);
	}

	private void startConversation() {
		engine.step(player, ConversationPhrases.GREETING_MESSAGES.get(0));
		assertTrue(npc.isTalking());
		assertEquals(REPLY_HELLO, getReply(npc));
	}

	private void checkReply(String question, String expectedReply) {
		engine.step(player, question);
		assertTrue(npc.isTalking());
		assertEquals(expectedReply, getReply(npc));
	}

	/**
	 * Check for exact amount of money on the player.
	 *
	 * @param exactAmount
	 */
	private void assertMoneyLeft(int exactAmount) {
		// Testing for 0 items is always false
		if (exactAmount > 0) {
			assertTrue(player.isEquipped(ITEM_MONEY, exactAmount));
		}
		assertFalse(player.isEquipped(ITEM_MONEY, exactAmount + 1));
	}
}
