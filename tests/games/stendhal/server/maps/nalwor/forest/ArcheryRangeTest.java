/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.nalwor.forest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
//import static utilities.PlayerTestHelper.createPlayer;
//import static utilities.PlayerTestHelper.equipWithItem;
//import static utilities.PlayerTestHelper.equipWithMoney;
//import static utilities.PlayerTestHelper.resetNPC;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.sign.ShopSign;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.quests.AbstractQuest;
import utilities.QuestHelper;
import utilities.ZoneAndPlayerTestImpl;


/**
 * Tests for the archery range.
 *
 * FIXME: should test portal functions?
 */
public class ArcheryRangeTest extends ZoneAndPlayerTestImpl {

	private SpeakerNPC npc;
	private Engine en;

	// retrievable objects
	private String questSlot;
	private ShopSign blackboard;
	private int cost;
	private int trainTime;
	private String trainTimeMinutes;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		QuestHelper.setUpBeforeClass();
	}

	@Before
	@Override
	public void setUp() throws Exception {
		// FIXME: misspelled "MockStendlRPWorld"?
		final StendhalRPWorld world = MockStendlRPWorld.get();

		final StendhalRPZone zone = new StendhalRPZone("test_zone");
		world.addRPZone(zone);
		final ArcheryRange range = new ArcheryRange();
		range.configureZone(zone, null);

		final List<Object> objects = range.getJunitObjects();
		questSlot = ((AbstractQuest) objects.get(0)).getSlotName();
		blackboard = ((ShopSign) objects.get(1));
		cost = ((int) objects.get(2));
		trainTime = ((int) objects.get(3));
		trainTimeMinutes = Integer.toString(trainTime / MathHelper.SECONDS_IN_ONE_MINUTE);

		npc = SingletonRepository.getNPCList().get("Chester");
		en = npc.getEngine();
	}

	@Test
	public void testNPC() {
		final Player player = createPlayer("foo");

		assertEquals("You see a man who appears to be a skilled assassin.", npc.getDescription());

		en.step(player, "hi");
		assertEquals("This is the assassins' archery range. Watch yer tongue if ya don't want to get hurt.", getReply(npc));
		en.step(player, "job");
		assertEquals("I manage this here archery range. It belongs to the assassins, so don't go pokin' yer nose where it doesn't belong.", getReply(npc));
		en.step(player, "bye");
		assertEquals("Come back when ya got some cash. Courtesy aint currency 'round here.", getReply(npc));

		// make sure NPC is no longer attending to player
		assertTrue(en.getCurrentState() == ConversationStates.IDLE);
	}

	@Test
	public void testShopWithoutID() {
		final Player player = createPlayer("foo");

		en.step(player, "hi");
		en.step(player, "offer");
		assertEquals("I'm not selling you anything without proof that you can be trusted.", getReply(npc));
		en.step(player, "buy training bow");
		assertEquals("I'm not selling you anything without proof that you can be trusted.", getReply(npc));

		// make sure NPC is no longer attending to player
		resetNPC(npc);

		// look at blackboard
		blackboard.onUsed(player);
		// FIXME: NPC's reply not updating when sign used
		//assertEquals("Get away from my blackboard you mongrel!", getReply(npc));
	}

	@Test
	public void testShopWithID() {
		final Player player = createPlayer("foo");
		equipWithItem(player, "assassins id");

		en.step(player, "hi");
		en.step(player, "offer");
		assertEquals("Don't look at me all dumbfounded! Check my bow and arrow prices on"
				+ " that blackboard over there. But if yer lookin' for a"
				+ " bargain, then scram and find some other witless sucker.", getReply(npc));
		en.step(player, "buy training bow");
		assertEquals("A training bow will cost 4500. Do you want to buy it?", getReply(npc));
		en.step(player, "no");
		assertEquals("Ok, how else may I help you?", getReply(npc));
		en.step(player, "buy training bow");
		en.step(player, "yes");
		assertEquals("Sorry, you don't have enough money!", getReply(npc));

		equipWithMoney(player, 4499);

		en.step(player, "buy training bow");
		en.step(player, "yes");
		assertEquals("Sorry, you don't have enough money!", getReply(npc));

		equipWithMoney(player, 1);

		en.step(player, "buy training bow");
		en.step(player, "yes");
		assertEquals("Congratulations! Here is your training bow!", getReply(npc));

		assertTrue(player.isEquipped("training bow"));

		// make sure NPC is no longer attending to player
		resetNPC(npc);

		// should not respond if player has assassins id
		blackboard.onUsed(player);
		// FIXME: NPC's reply not updating when sign used
		//assertEquals(null, getReply(npc));
	}

	@Test
	public void testTraining() {
		final Player player = createPlayer("foo");

		en.step(player, "hi");
		en.step(player, "quest");
		assertEquals("Do I look like I need any help!? If yer not here to #train then get outta my sight!", getReply(npc));
		en.step(player, "help");
		assertEquals("This is the assassins' archery range. I can let you #train here for a #fee"
				+ " if you're in good with HQ. If you haven't quite got the range, try the targets"
				+ " on the end. The ninjas seem to like those. I recommend using a #'training bow'.", getReply(npc));
		en.step(player, "fee");
		assertEquals("The fee to #train is " + Integer.toString(cost) + " money.", getReply(npc));

		// make sure NPC is no longer attending to player
		resetNPC(npc);
	}

	@Test
	public void testTrainingWithoutID() {
		final Player player = createPlayer("foo");

		// player is new to archery range & does not have assassin id
		assertNull(player.getQuest(questSlot));

		en.step(player, "hi");
		en.step(player, "train");
		assertEquals("You can't train here without permission from the assassins' HQ. Now git, before I sic the dogs on you!", getReply(npc));

		// make sure NPC is no longer attending to player
		resetNPC(npc);
	}

	@Test
	public void testTrainingWithID() {
		final Player player = createPlayer("foo");

		// player is new to archery range & has assassin id
		assertNull(player.getQuest(questSlot));
		equipWithItem(player, "assassins id");

		en.step(player, "hi");

		// player is new to archery range
		en.step(player, "train");
		assertEquals("Hmmm, I haven't seen you around here before."
				+ " But you have the proper credentials. Do you want me to"
				+ " open up the archery range? It'll cost ya "
				+ Integer.toString(cost) + " money.", getReply(npc));
		assertTrue(en.getCurrentState() == ConversationStates.QUESTION_1);
		en.step(player, "no");
		assertEquals("Then quit wasting my time!", getReply(npc));
		en.step(player, "train");
		equipWithMoney(player, cost - 1);
		en.step(player, "yes");
		// player doesn't have enough money
		assertEquals("What's this? You don't even have enough money for the #fee. Be gone with ya!", getReply(npc));
		en.step(player, "train");
		equipWithMoney(player, 1);
		en.step(player, "yes");
		assertEquals("You can train for up to " + trainTimeMinutes + " minutes. So make good use of yer time.", getReply(npc));

		// make sure NPC is no longer attending to player
		assertTrue(en.getCurrentState() == ConversationStates.IDLE);

		// quest state
		assertEquals("training;" + Integer.toString(trainTime), player.getQuest(questSlot));
	}

	@Test
	public void testTrainingCooldownWithoutID() {

	}

	@Test
	public void testTrainingCooldownWithID() {

	}
}
