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
package games.stendhal.client.entity;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.client.entity.RPEntity.Resolution;
import games.stendhal.client.events.AttackEvent;
import games.stendhal.client.events.Event;
import games.stendhal.client.util.UserInterfaceTestHelper;
import marauroa.common.game.RPEvent;

public class RPEntityTest {

	private RPEntity defender;
	private RPEntity attacker;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		UserInterfaceTestHelper.initUserInterface();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		defender = new RPEntity() {
		};
		attacker = new RPEntity() {
		};
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for evaluateAttackEmptyObject.
	 */
	@Test
	public void testEvaluateAttackEmptyObject() {
		assertNull(attacker.getResolution());
	}

	/**
	 * Tests for AttackEvents without "hit" being missed.
	 */
	@Test
	public void testEvaluateAttackNoHit() {
		RPEvent obj = new RPEvent();
		obj.put("type", 0);

		Event<RPEntity> ev = new AttackEvent();
		ev.init(attacker, obj);

		attacker.attackTarget = defender;
		ev.execute();
		assertThat(defender.getResolution(), is(Resolution.MISSED));
	}

	/**
	 * Tests for AttackEvents without "hit" being missed
	 */
	@Test
	public void testEvaluateAttackNoHitdamage0() {
		RPEvent obj = new RPEvent();
		obj.put("type", 0);
		obj.put("damage", "0");

		Event<RPEntity> ev = new AttackEvent();
		ev.init(attacker, obj);

		attacker.attackTarget = defender;
		ev.execute();
		assertThat(defender.getResolution(), is(Resolution.MISSED));
	}

	/**
	 * Tests for a blocked attack.
	 */
	@Test
	public void testEvaluateAttackHitDamage0() {
		RPEvent obj = new RPEvent();
		obj.put("type", 0);
		obj.put("hit", "");
		obj.put("damage", "0");

		Event<RPEntity> ev = new AttackEvent();
		ev.init(attacker, obj);

		attacker.attackTarget = defender;
		ev.execute();

		assertThat(defender.getResolution(), is(Resolution.BLOCKED));
	}

	/**
	 * Tests for a damaging attack
	 */
	@Test
	public void testEvaluateAttackRisk1Damage1() {
		RPEvent obj = new RPEvent();
		obj.put("type", 0);
		obj.put("hit", "");
		obj.put("damage", "1");

		Event<RPEntity> ev = new AttackEvent();
		ev.init(attacker, obj);

		attacker.attackTarget = defender;
		ev.execute();

		assertThat(defender.getResolution(), is(Resolution.HIT));
	}
}
