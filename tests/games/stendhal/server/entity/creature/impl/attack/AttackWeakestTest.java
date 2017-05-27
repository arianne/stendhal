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
package games.stendhal.server.entity.creature.impl.attack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.AttackableCreature;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.server.game.db.DatabaseFactory;
import utilities.PlayerTestHelper;
import utilities.RPClass.CreatureTestHelper;

public class AttackWeakestTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		MockStendlRPWorld.get();
		CreatureTestHelper.generateRPClasses();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		PlayerTestHelper.removePlayer("ghost");
		PlayerTestHelper.removePlayer("elvis");
	}

	/**
	 * Tests that a new creature does not have a target.
	 */
	@Test
	public void testhasValidTargetNonAttacker() {
		AttackStrategy strat = new AttackWeakest();
		Creature loner = new Creature();

		assertFalse("attacker has no target", strat.hasValidTarget(loner));
	}

	/**
	 * Tests that hasValidTarget succeeds for a normal,
	 * and fails for invisible target.
	 */
	@Test
	public void testhasValidTargetInvisibleVictim() {
		AttackStrategy strat = new AttackWeakest();

		Creature creature = new Creature();
		Player player = PlayerTestHelper.createPlayer("ghost");
		StendhalRPZone arena = new StendhalRPZone("arena");
		arena.add(creature);
		arena.add(player);
		creature.setTarget(player);
		assertTrue("has a valid target", strat.hasValidTarget(creature));
		player.setInvisible(true);
		assertFalse("has a valid target", strat.hasValidTarget(creature));
	}

	/**
	 * Tests that hasValidTarget fails when the target
	 * is not in the same zone.
	 */
	@Test
	public void testhasValidTargetDifferentZones() {
		AttackStrategy strat = new AttackWeakest();

		StendhalRPZone jacuzzi = new StendhalRPZone("jacuzzi");
		Player player = PlayerTestHelper.createPlayer("elvis");
		PlayerTestHelper.registerPlayer(player, jacuzzi);

		Creature creature = new Creature();
		StendhalRPZone arena = new StendhalRPZone("arena");
		arena.add(creature);

		creature.setTarget(player);
		assertFalse("has a valid target", strat.hasValidTarget(creature));
	}

	/**
	 * Tests finding a new target
	 */
	@Test
	public void testFindNewTarget() {
		AttackStrategy strat = new AttackWeakest();

		/*
		 *  Need to use a real creature, because the
		 *  creature needs to be offensive to see the
		 *  targets.
		 */
		final Creature creature = SingletonRepository.getEntityManager().getCreature("rat");

		Player veteran = PlayerTestHelper.createPlayer("test dummy");
		Player newbie = PlayerTestHelper.createPlayer("test dummy2");

		StendhalRPZone arena = new StendhalRPZone("arena");
		arena.add(creature);
		assertFalse("is not attacking", strat.hasValidTarget(creature));
		arena.add(veteran);
		arena.add(newbie);

		creature.setPosition(3, 3);
		veteran.setPosition(3, 4);

		// Should pick the nearest: veteran
		strat.findNewTarget(creature);
		assertTrue("has a valid target", strat.hasValidTarget(creature));
		assertEquals("attack nearest", veteran, creature.getAttackTarget());
	}

	/**
	 * Tests finding a new target. No enemies next to the creature.
	 */
	@Test
	public void testFindNewTargetFromDistance() {
		AttackStrategy strat = new AttackWeakest();

		/*
		 *  Need to use a real creature, because the
		 *  creature needs to be offensive to see the
		 *  targets.
		 */
		final Creature creature = SingletonRepository.getEntityManager().getCreature("rat");

		Player veteran = PlayerTestHelper.createPlayer("veteran");
		Player newbie = PlayerTestHelper.createPlayer("newbie");

		// Give the arena a proper size so that pathfinding can work
		StendhalRPZone arena = new StendhalRPZone("arena", 10, 10);
		arena.add(creature);
		assertFalse("is not attacking", strat.hasValidTarget(creature));
		arena.add(veteran);
		arena.add(newbie);

		creature.setPosition(3, 3);
		veteran.setPosition(3, 5);
		newbie.setPosition(1, 5);
		assertFalse("sanity check; target not next to attacker", veteran.nextTo(creature));
		assertFalse("sanity check; target not next to attacker", newbie.nextTo(creature));
		assertTrue("sanity check; veteran is closer than newbie",
				creature.squaredDistance(veteran) < creature.squaredDistance(newbie));

		// Should pick the nearest: veteran
		strat.findNewTarget(creature);
		assertTrue("has a valid target", strat.hasValidTarget(creature));
		assertEquals("attack nearest", veteran, creature.getAttackTarget());
	}

	/**
	 * Tests switching to a weaker target
	 */
	@Test
	public void testSwitchTargets() {
		AttackStrategy strat = new AttackWeakest();

		/*
		 *  Need to use a real creature, because the
		 *  creature needs to be offensive to see the
		 *  targets.
		 */
		final Creature creature = SingletonRepository.getEntityManager().getCreature("rat");

		Player veteran = PlayerTestHelper.createPlayer("veteran");
		Player newbie = PlayerTestHelper.createPlayer("newbie");

		StendhalRPZone arena = new StendhalRPZone("arena");
		arena.add(creature);
		assertFalse("is not attacking", strat.hasValidTarget(creature));
		arena.add(veteran);
		arena.add(newbie);
		veteran.addXP(10000);
		newbie.addXP(100);

		assertTrue("sanity check for player levels", veteran.getLevel() > newbie.getLevel());

		creature.setPosition(3, 3);
		veteran.setPosition(3, 4);

		// Should pick the nearest: veteran
		strat.findNewTarget(creature);
		assertTrue("has a valid target", strat.hasValidTarget(creature));
		assertEquals("attack nearest", veteran, creature.getAttackTarget());

		// move newbie near. this should result in switching targets
		newbie.setPosition(2, 3);
		assertTrue("has a valid target", strat.hasValidTarget(creature));
		assertEquals("attack weakest", newbie, creature.getAttackTarget());

		// move veteran away for a moment
		veteran.setPosition(3, 5);
		// should make no difference
		assertTrue("has a valid target", strat.hasValidTarget(creature));
		assertEquals("attack weakest", newbie, creature.getAttackTarget());
		veteran.setPosition(3, 4);
		// and neither should putting him back (unlike newbie's arrival)
		assertTrue("has a valid target", strat.hasValidTarget(creature));
		assertEquals("attack weakest", newbie, creature.getAttackTarget());
	}

	/**
	 * Tests that switching to a weaker target ignores creatures.
	 * (from summon scrolls)
	 */
	@Test
	public void testSwitchTargetsIgnoreCreatures() {
		AttackStrategy strat = new AttackWeakest();

		/*
		 *  Need to use a real creature, because the
		 *  creature needs to be offensive to see the
		 *  targets.
		 */
		final Creature creature = SingletonRepository.getEntityManager().getCreature("rat");
		final Creature scrollCreature = new AttackableCreature(SingletonRepository.getEntityManager().getCreature("rat"));

		Player veteran = PlayerTestHelper.createPlayer("veteran");

		StendhalRPZone arena = new StendhalRPZone("arena");
		arena.add(creature);
		assertFalse("is not attacking", strat.hasValidTarget(creature));
		arena.add(veteran);
		arena.add(scrollCreature);
		veteran.addXP(10000);

		assertTrue("sanity check for enemy levels", veteran.getLevel() > scrollCreature.getLevel());

		creature.setPosition(3, 3);
		scrollCreature.setPosition(2, 4);

		// Should pick the nearest: scrollCreature
		strat.findNewTarget(creature);
		assertTrue("has a valid target", strat.hasValidTarget(creature));
		assertEquals("attack nearest", scrollCreature, creature.getAttackTarget());

		// move veteran near. this should result in switching targets because players
		// are favored over creatures
		veteran.setPosition(3, 4);
		assertTrue("has a valid target", strat.hasValidTarget(creature));
		assertEquals("attack player", veteran, creature.getAttackTarget());

		// Add a sheep. Pets are nice targets for killing
		final Sheep sheep = new Sheep(veteran);
		sheep.setPosition(4, 4);
		assertTrue("sanity check for enemy levels", veteran.getLevel() > sheep.getLevel());
		assertTrue("has a valid target", strat.hasValidTarget(creature));
		assertEquals("attack sheep", sheep, creature.getAttackTarget());
	}
}
