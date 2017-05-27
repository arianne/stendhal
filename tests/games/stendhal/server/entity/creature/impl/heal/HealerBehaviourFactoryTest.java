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
package games.stendhal.server.entity.creature.impl.heal;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.RPClass.CreatureTestHelper;

public class HealerBehaviourFactoryTest {
	 boolean called;

	/**
	 * Tests for healNonHealer.
	 */
	@Test
	public void testHealNonHealer() {
		called = false;
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get();
		final StendhalRPZone zone = new StendhalRPZone("blabl");
		CreatureTestHelper.generateRPClasses();
		final Creature creature = new Creature() {
			@Override
			public int heal(final int amount) {
				called = true;
				return amount;
			}
		};
		zone.add(creature);
		creature.setBaseHP(100);
		creature.setHP(50);
		creature.setHealer(null);
		creature.logic();
		assertThat(creature.getHP(), is(50));
		creature.setHealer("10,1");
		creature.logic();
		assertTrue(called);

	}

	/**
	 * Tests for get.
	 */
	@Test
	public void testGet() {
		assertThat(HealerBehaviourFactory.get(null), instanceOf(NonHealingBehaviour.class));
		assertThat(HealerBehaviourFactory.get("5,5"), instanceOf(Healer.class));
	}

	/**
	 * Tests for getEmptyString.
	 */
	@Test(expected = NumberFormatException.class)
	public void testGetEmptyString() {
		assertThat(HealerBehaviourFactory.get(""), instanceOf(NonHealingBehaviour.class));
	}

}
