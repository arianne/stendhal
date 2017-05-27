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

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Corpse;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPObject.ID;
import utilities.PlayerTestHelper;
import utilities.RPClass.CreatureTestHelper;

/**
 * Tests for HandToHand
 */
public class HandToHandTest {

	/**
	 * initialisation
	 */
	@BeforeClass
	public static void setUpbeforeClass() {
		MockStendlRPWorld.get();
		CreatureTestHelper.generateRPClasses();
	}


	/**
	 * Tests for attack.
	 */
	@Test
	public void testAttack() {
		MockStendhalRPRuleProcessor.get();
		final HandToHand hth = new HandToHand();
		final Creature creature = createMock(Creature.class);
		expect(creature.isAttackTurn(0)).andReturn(true);
		expect(creature.attack()).andReturn(true);
		replay(creature);
		hth.attack(creature);
		verify(creature);
	}

	/**
	 * Tests for notAttackTurnAttack.
	 */
	@Test
	public void testNotAttackTurnAttack() {
		MockStendhalRPRuleProcessor.get();
		final HandToHand hth = new HandToHand();
		final Creature creature = createMock(Creature.class);
		expect(creature.isAttackTurn(0)).andReturn(false);
		replay(creature);
		hth.attack(creature);
		verify(creature);
	}

	/**
	 * Tests for canAttackNow.
	 */
	@Test
	public void testCanAttackNow() {
		final HandToHand hth = new HandToHand();
		final Creature creature = new Creature();
		assertFalse("no target yet", hth.canAttackNow(creature));
		final RPEntity victim = new RPEntity() {

			@Override
			protected void dropItemsOn(final Corpse corpse) {
				// empty
			}

			@Override
			public void logic() {
				// empty
			}
		};
		victim.put("id", 1);
		creature.setTarget(victim);
		assertTrue("new ones stand on same positon", hth.canAttackNow(creature));
		victim.setPosition(10, 10);
		assertFalse("too far away", hth.canAttackNow(creature));

	}

	/**
	 * Tests for canAttackNowBigCreature.
	 */
	@Test
	public void testCanAttackNowBigCreature() {
		final StendhalRPZone zone = new StendhalRPZone("hthtest");
		final HandToHand hth = new HandToHand();
		final Creature creature = SingletonRepository.getEntityManager().getCreature("balrog");
		assertNotNull(creature);
		assertThat(creature.getWidth(), is(6.0));
		assertThat(creature.getHeight(), is(6.0));
		creature.setPosition(10, 10);
		assertFalse("no target yet", hth.canAttackNow(creature));
		final RPEntity victim = PlayerTestHelper.createPlayer("bob");
		victim.setHP(1);
		zone.add(creature);
		zone.add(victim);
		creature.setTarget(victim);

		for (int i = 9; i < 12; i++) {
			for (int j = 9; j < 13; j++) {
				victim.setPosition(i, j);
				assertTrue(creature.nextTo(victim));
				assertTrue(victim.nextTo(creature));
				assertTrue("can attack now (" + i + "," + j + ")", hth.canAttackNow(creature));
			}
		}

		victim.setPosition(8, 13);
		assertFalse(creature.nextTo(victim));
		assertFalse(victim.nextTo(creature));
		assertFalse("can attack now ", hth.canAttackNow(creature));

	}

	private static boolean mockinvisible;

	/**
	 * Tests for hasValidTargetNonAttacker.
	 */
	@Test
	public void testhasValidTargetNonAttacker() {
		HandToHand hth = new HandToHand();

		Creature nonAttacker = createMock(Creature.class);
		expect(nonAttacker.isAttacking()).andReturn(false);
		replay(nonAttacker);

		assertFalse("attacker has no target", hth.hasValidTarget(nonAttacker));
		verify(nonAttacker);
	}

	/**
	 * Tests for hasValidTargetInvisibleVictim.
	 */
	@Test
	public void testhasValidTargetInvisibleVictim() {

		Creature victim = createMock(Creature.class);
		expect(victim.isInvisibleToCreatures()).andReturn(true);

		Creature attacker = createMock(Creature.class);
		expect(attacker.isAttacking()).andReturn(true);
		expect(attacker.getAttackTarget()).andReturn(victim);

		replay(victim);
		replay(attacker);

		HandToHand hth = new HandToHand();
		assertFalse("victim is invisible to attacker", hth
				.hasValidTarget(attacker));

	}

	/**
	 * Tests for hasValidTargetDifferentZones.
	 */
	@Test
	public void testhasValidTargetDifferentZones() {

		StendhalRPZone zoneA = new StendhalRPZone("A");
		StendhalRPZone zoneB = new StendhalRPZone("B");

		RPEntity victim = createMock(RPEntity.class);
		expect(victim.isInvisibleToCreatures()).andReturn(false);
		expect(victim.getZone()).andReturn(zoneA);

		Creature attacker = createMock(Creature.class);
		expect(attacker.isAttacking()).andReturn(true);
		expect(attacker.getAttackTarget()).andReturn(victim);

		expect(attacker.getZone()).andReturn(zoneB);

		replay(victim);
		replay(attacker);

		HandToHand hth = new HandToHand();
		assertFalse("attacker and victim are in different zones", hth.hasValidTarget(attacker));

	}

	/**
	 * Tests for hasValidTargetvisibleVictim.
	 */
	@Test
	public void testhasValidTargetvisibleVictim() {
		ID id = new RPObject.ID(1, "zone");
		StendhalRPZone zone = createMock(StendhalRPZone.class);
		expect(zone.has(id)).andReturn(false);

		RPEntity victim = createMock(RPEntity.class);
		expect(victim.isInvisibleToCreatures()).andReturn(false);
		expect(victim.getZone()).andReturn(zone);
		expect(victim.getID()).andReturn(id);

		Creature attacker = createMock(Creature.class);
		expect(attacker.isAttacking()).andReturn(true);
		expect(attacker.getAttackTarget()).andReturn(victim);

		expect(attacker.getZone()).andReturn(zone).times(2);

		replay(victim);
		replay(attacker);
		replay(zone);

		HandToHand hth = new HandToHand();
		assertFalse("victims id is not in attacker's zone", hth
				.hasValidTarget(attacker));

	}

	/**
	 * Tests for hasValidTarget.
	 */
	@Test
	public void testHasValidTarget() {
		final StendhalRPZone zone = new StendhalRPZone("hthtest");

		final HandToHand hth = new HandToHand();
		final Creature creature = new Creature();
		assertFalse("is not attacking", hth.hasValidTarget(creature));
		final RPEntity victim = new RPEntity() {

			@Override
			public boolean isInvisibleToCreatures() {
				return mockinvisible;
			}

			@Override
			protected void dropItemsOn(final Corpse corpse) {
				// empty
			}

			@Override
			public void logic() {
				// empty
			}
		};
		victim.put("id", 1);
		creature.setTarget(victim);
		mockinvisible = true;
		assertTrue(victim.isInvisibleToCreatures());
		assertFalse("victim is invisible", hth.hasValidTarget(creature));
		mockinvisible = false;
		assertFalse(victim.isInvisibleToCreatures());
		zone.add(victim);
		assertFalse("not in same zone", hth.hasValidTarget(creature));
		zone.add(creature);
		assertFalse("in same zone, on same spot and dead", hth.hasValidTarget(creature));

		creature.setTarget(victim);
		victim.setBaseHP(10);
		victim.setHP(1);
		assertTrue("in same zone, on same spot", hth.hasValidTarget(creature));

		victim.setPosition(12, 0);
		assertTrue("in same zone, not too far away", hth.hasValidTarget(creature));
		victim.setPosition(13, 0);
		assertFalse("in same zone, too far away", hth.hasValidTarget(creature));
	}

	/**
	 * Tests for findNewtarget.
	 */
	@Test
	public void testFindNewtarget() {
		MockStendhalRPRuleProcessor.get();
		final HandToHand hth = new HandToHand();
		final Creature lonesomeCreature = new Creature();
		assertFalse(lonesomeCreature.isAttacking());
		hth.findNewTarget(lonesomeCreature);
		assertFalse(lonesomeCreature.isAttacking());

		final Creature creature = createMock(Creature.class);
		expect(creature.getPerceptionRange()).andReturn(5);
		expect(creature.getNearestEnemy(7)).andReturn(creature);
		creature.setTarget(creature);
		replay(creature);

		hth.findNewTarget(creature);

		verify(creature);
	}

}
