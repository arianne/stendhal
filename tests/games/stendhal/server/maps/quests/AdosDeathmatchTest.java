/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static utilities.SpeakerNPCTestHelper.getReply;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.DeathMatchCreature;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.ados.swamp.DeathmatchRecruiterNPC;
import games.stendhal.server.maps.deathmatch.DeathmatchEngine;
import games.stendhal.server.util.Area;
import marauroa.common.Log4J;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;

public class AdosDeathmatchTest {

	public static final StendhalRPZone ados_wall_n = new StendhalRPZone("0_ados_wall_n", 200, 200);
	public static final StendhalRPZone zone = new StendhalRPZone("dmTestZone");

	private static AdosDeathmatch deathmatch;

	private static final String questSlot = "deathmatch";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		new DatabaseFactory().initializeDatabase();
		PlayerTestHelper.generateNPCRPClasses();
		MockStendlRPWorld.get();
		MockStendlRPWorld.get().addRPZone(ados_wall_n);
		// zone to teleport player after leaving deathmatch
		MockStendlRPWorld.get().addRPZone(new StendhalRPZone("0_semos_plains_n", 200, 200));
		MockStendhalRPRuleProcessor.get();
		final DeathmatchRecruiterNPC configurator = new DeathmatchRecruiterNPC();
		configurator.configureZone(zone, null);
		// some of the recruiter responses are defined in the quest not the configurator

		deathmatch = new AdosDeathmatch(ados_wall_n,
				new Area(ados_wall_n, new Rectangle2D.Double(0, 0, ados_wall_n.getWidth(), ados_wall_n.getHeight())));
		deathmatch.createNPC("Thanatos", 1, 1);
		deathmatch.addToWorld();

		// initialize creatures
		PlayerTestHelper.generateCreatureRPClasses();
		SingletonRepository.getEntityManager().populateCreatureList();
	}

	@AfterClass
	public static void teardownAfterClass() throws Exception {
		MockStendlRPWorld.reset();
	}

	@After
	public void tearDown() {
		// remove players
		final StendhalRPZone semos_plains_n = (StendhalRPZone) MockStendlRPWorld.get().getRPZone("0_semos_plains_n");
		for (final StendhalRPZone z: new StendhalRPZone[] {ados_wall_n, semos_plains_n}) {
			for (final RPObject obj: ados_wall_n) {
				if (obj instanceof Player) {
					z.remove(obj);
				}
			}
			// FIXME: players not removed
			//~ assertEquals(0, z.getPlayers().size());
		}
	}


	/**
	 * Tests for recruiter.
	 */
	@Test
	public void testRecruiter() {
		final SpeakerNPC recruiter = SingletonRepository.getNPCList().get("Thonatus");
		assertNotNull(recruiter);
		assertNotNull(zone);
		assertNotNull(ados_wall_n);
		final Player dmPlayer = PlayerTestHelper.createPlayer("dmPlayer");
		final Engine en = recruiter.getEngine();
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		en.step(dmPlayer, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Hey there. You look like a reasonable fighter.", getReply(recruiter));

		en.step(dmPlayer, "job");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("I'm recruiter for the Ados #deathmatch.", getReply(recruiter));

		en.step(dmPlayer, "deathmatch");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Many dangerous creatures will attack you in the deathmatch arena. It is only for strong #heroes.", getReply(recruiter));


		dmPlayer.setLevel(19);
		en.step(dmPlayer, "challenge");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Sorry, you are too weak for the #deathmatch now, come back when you have at least level 20.", getReply(recruiter));
		recruiter.remove("text");

		en.step(dmPlayer, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("I hope you will enjoy the Ados Deathmatch!", getReply(recruiter));



		dmPlayer.setLevel(20);
		//assertNotNull(dmPlayer.getZone());
		en.step(dmPlayer, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Hey there. You look like a reasonable fighter.", getReply(recruiter));
		recruiter.remove("text");
		en.step(dmPlayer, "challenge");

		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals(null, getReply(recruiter));
		assertNotNull(dmPlayer.getZone());
		// no players already in zone, send straight in
		assertEquals(ados_wall_n, dmPlayer.getZone());
		assertEquals(100, dmPlayer.getX());

		en.setCurrentState(ConversationStates.IDLE);

		final Player joiner = PlayerTestHelper.createPlayer("dmPlayer");
		joiner.setLevel(19);
		en.step(joiner, "hi");
		recruiter.remove("text");
		en.step(joiner, "challenge");
		recruiter.remove("text");
		assertEquals(null, joiner.getZone());
		joiner.setLevel(20);

		en.step(joiner, "challenge");
		assertEquals("There are heroes battling right now in the deathmatch, so it may be dangerous there. Do you want to join dmPlayer?", getReply(recruiter));
		en.step(joiner, "yes");
		assertEquals(ados_wall_n, joiner.getZone());
	}

	@Test
	public void testAssistant() {
		assertTrue(SingletonRepository.getEntityManager().getCreatures().size() > 0);

		final SpeakerNPC assistant = SingletonRepository.getNPCList().get("Thanatos");
		assertNotNull(assistant);
		assertNotNull(zone);
		assertNotNull(ados_wall_n);
		final Player dmPlayer = PlayerTestHelper.createPlayer("dmPlayer");
		dmPlayer.setLevel(55);

		final Engine en = assistant.getEngine();
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		assertEquals(0, getActiveCreatures().size());

		en.step(dmPlayer, "hi");
		assertEquals(ConversationStates.INFORMATION_1, en.getCurrentState());
		assertEquals("Welcome to Ados Deathmatch! Please talk to #Thonatus if you want to join.", getReply(assistant));
		en.step(dmPlayer, "bye");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		// place player in Deathmatch area
		ados_wall_n.add(dmPlayer);
		assertEquals(ados_wall_n, dmPlayer.getZone());

		assertNull(dmPlayer.getQuest(questSlot));

		en.step(dmPlayer, "hi");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals("Welcome to Ados Deathmatch! Do you need #help?", getReply(assistant));
		en.step(dmPlayer, "start");
		assertEquals(ConversationStates.IDLE, en.getCurrentState());
		assertEquals("Have fun!", getReply(assistant));

		assertEquals(0, MathHelper.parseInt(dmPlayer.getQuest(questSlot, 3)));

		final int stateStart = MathHelper.parseInt(dmPlayer.getQuest(questSlot, 1));

		final DeathmatchEngine dmEngine = deathmatch.getDeathmatchInfo().getEngine();

		// spawn first creature
		dmEngine.onTurnReached(0);

		en.step(dmPlayer, "hi");
		en.step(dmPlayer, "leave");
		assertEquals("What are you? A coward?", getReply(assistant));
		en.step(dmPlayer, "bye");

		for (int idx = 1; idx <= 10; idx++) {
			assertEquals(stateStart+idx, MathHelper.parseInt(dmPlayer.getQuest(questSlot, 1)));

			List<DeathMatchCreature> activeCreatures = getActiveCreatures();
			assertEquals(1, activeCreatures.size());

			final DeathMatchCreature creature = activeCreatures.get(0);
			creature.damage(creature.getHP(), dmPlayer);
			assertEquals(0, creature.getHP());
			creature.notifyWorldAboutChanges();
			assertEquals(0, getActiveCreatures().size());

			// spawn next creature or end deathmatch
			dmEngine.onTurnReached(idx);
		}

		assertEquals(0, getActiveCreatures().size());
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		en.step(dmPlayer, "leave");
		assertEquals("I don't think you claimed your #victory yet.", getReply(assistant));
		en.step(dmPlayer, "victory");
		assertEquals(ConversationStates.ATTENDING, en.getCurrentState());
		assertEquals(
				"Here is your special trophy helmet. Keep it, as the defense will increase by 1 "
						+ "for every deathmatch you complete. Now, tell me if you want to #leave.",
				getReply(assistant));
		assertEquals(1, dmPlayer.getNumberOfEquipped("trophy helmet"));
		en.step(dmPlayer, "leave");

		// invoke a turn so NPC stops attending player in different zone
		nextTurn();
		assertEquals(ConversationStates.IDLE, en.getCurrentState());

		final StendhalRPZone playerZone = dmPlayer.getZone();
		assertNotNull(playerZone);
		assertEquals("0_semos_plains_n", playerZone.getName());
	}

	/***
	 * Retrieves the creatures spawned within the deathmatch arena.
	 */
	private List<DeathMatchCreature> getActiveCreatures() {
		final List<DeathMatchCreature> creatures = new ArrayList<>();
		for (final RPObject obj: ados_wall_n) {
			if (obj instanceof DeathMatchCreature) {
				creatures.add((DeathMatchCreature) obj);
			}
		}
		return creatures;
	}

	/**
	 * Simulates cycling a turn.
	 */
	private void nextTurn() {
		MockStendlRPWorld.get().nextTurn();
		MockStendhalRPRuleProcessor.get().beginTurn();
		MockStendhalRPRuleProcessor.get().endTurn();
	}
}
