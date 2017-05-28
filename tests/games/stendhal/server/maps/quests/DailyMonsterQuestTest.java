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

import static games.stendhal.server.entity.npc.ConversationStates.ATTENDING;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.LinkedList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.LevelBasedComparator;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import games.stendhal.server.maps.quests.DailyMonsterQuest.DailyQuestAction;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;
import utilities.SpeakerNPCTestHelper;
import utilities.RPClass.CreatureTestHelper;

public class DailyMonsterQuestTest {

	private static SpeakerNPC mayor;
	private static DailyMonsterQuest dmq;
	private static Engine en;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		mayor = SpeakerNPCTestHelper.createSpeakerNPC("Mayor Sakhs");
		NPCList.get().add(mayor);
		dmq = new DailyMonsterQuest();

		dmq.addToWorld();
		en = mayor.getEngine();
		final StendhalRPWorld world = MockStendlRPWorld.get();
		final StendhalRPZone zone = new StendhalRPZone("int_semos_guard_house");
		world.addRPZone(zone);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for fire.
	 */
	@Test
	public void testfire() {

		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));
		final Player bob = PlayerTestHelper.createPlayer("bob");
		assertFalse(en.step(bob, ""));
		assertThat(en.getCurrentState(), is(ConversationStates.IDLE));

		en.setCurrentState(ATTENDING);
		CreatureTestHelper.generateRPClasses();
		SingletonRepository.getEntityManager().getCreature("rat");
		assertThat(en.getCurrentState(), is(ATTENDING));
		assertTrue(en.step(bob, "quest"));
		assertThat(en.getCurrentState(), is(ATTENDING));
		assertTrue(bob.hasQuest("daily"));
	}
	/**
	 * Tests for claimDone.
	 */
	@Test
	public void testClaimDone() {

		final Player bob = PlayerTestHelper.createPlayer("bob");
		en.setCurrentState(ATTENDING);
		CreatureTestHelper.generateRPClasses();
		SingletonRepository.getEntityManager().getCreature("rat");
		assertThat(en.getCurrentState(), is(ATTENDING));
		assertTrue(en.step(bob, "quest"));
		assertThat(en.getCurrentState(), is(ATTENDING));
		assertTrue(bob.hasQuest("daily"));
		assertTrue(en.step(bob, "complete"));
		assertTrue(bob.events().isEmpty());
	}

	/**
	 * Tests for pickIdealCreature.
	 */
	@Test
	public void testPickIdealCreature() {
		//final DailyMonsterQuest dmqp = new DailyMonsterQuest();
		final DailyMonsterQuest.DailyQuestAction dmqpick = new DailyQuestAction();
		CreatureTestHelper.generateRPClasses();
		assertNull("empty list", dmqpick.pickIdealCreature(-1, false, new LinkedList<Creature>()));
		final LinkedList<Creature> creatureList = new LinkedList<Creature>();
		creatureList.add(SingletonRepository.getEntityManager().getCreature("rat"));
		assertThat("1 rat in list", dmqpick.pickIdealCreature(-1, false, creatureList).getName(), is("rat"));
		assertThat("1 rat in list", dmqpick.pickIdealCreature(1000, false, creatureList).getName(), is("rat"));
		creatureList.add(SingletonRepository.getEntityManager().getCreature("balrog"));
		assertThat("rat and balrog in list", dmqpick.pickIdealCreature(-1, false, creatureList).getName(), is("rat"));

	}

	/**
	 * Tests for pickIdealCreatureratLONGLIST.
	 */
	@Test
	public void testPickIdealCreatureratLONGLIST() {
		//final DailyMonsterQuest dmqp = new DailyMonsterQuest();
		final DailyMonsterQuest.DailyQuestAction dmqpick = new DailyQuestAction();
		CreatureTestHelper.generateRPClasses();
		final LinkedList<Creature> creatureList = new LinkedList<Creature>();
		Creature creat;
		for (int i = 0; i < 3; i++) {
			creat = new Creature();
			creat.setLevel(i);
			creatureList.add(creat);
		}

		for (int i = 10; i < 50; i++) {
			creat = new Creature();
			creat.setLevel(i);
			creatureList.add(creat);
		}
		for (int i = 10; i < 20; i++) {
			creat = new Creature();
			creat.setLevel(i);
			creatureList.add(creat);
		}


		for (int i = 80; i < 100; i++) {
			creat = new Creature();
			creat.setLevel(i);
			creatureList.add(creat);
		}
		Collections.sort(creatureList, new LevelBasedComparator());
		for (int level = 0; level < 120; level++) {
			assertThat("1 rat in list", dmqpick.pickIdealCreature(level, false, creatureList).getLevel(),
					lessThanOrEqualTo(level + 5));
		}

	}

}
