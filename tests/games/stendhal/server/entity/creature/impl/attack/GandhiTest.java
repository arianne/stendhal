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

import static org.junit.Assert.assertFalse;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.maps.MockStendlRPWorld;

public class GandhiTest {
	@BeforeClass
	public static void beforeClass() {
		MockStendlRPWorld.get();
	}

	/**
	 * Tests for attack.
	 */
	@Test
	public void testAttack() {
		final Gandhi g = new Gandhi();
		final Creature c = new Creature();
		g.attack(null);
		g.attack(c);
		assertFalse(c.isAttacking());
	}

	/**
	 * Tests for canAttackNow.
	 */
	@Test
	public void testCanAttackNow() {
		final Gandhi g = new Gandhi();
		assertFalse(g.canAttackNow(null));
	}



	/**
	 * Tests for hasValidTarget.
	 */
	@Test
	public void testHasValidTarget() {
		final Gandhi g = new Gandhi();
		assertFalse(g.hasValidTarget(null));
	}

}
