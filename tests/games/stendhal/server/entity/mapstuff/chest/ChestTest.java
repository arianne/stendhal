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
package games.stendhal.server.entity.mapstuff.chest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.PassiveEntity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.Corpse;
import marauroa.common.game.RPClass;
import marauroa.common.game.SlotIsFullException;

public class ChestTest {

	@Before
	public void setUp() throws Exception {
		if (!RPClass.hasRPClass("entity")) {
			Entity.generateRPClass();
		}

		if (!RPClass.hasRPClass("chest")) {
			Chest.generateRPClass();
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests for size.
	 */
	@Test(expected = SlotIsFullException.class)
	public final void testSize() {
		final Chest ch = new Chest();
		assertEquals(0, ch.size());
		for (int i = 0; i < 30; i++) {
			ch.add(new PassiveEntity() {
			});
		}
		assertEquals(30, ch.size());
		ch.add(new PassiveEntity() {
		});
	}

	/**
	 * Tests for open.
	 */
	@Test
	public final void testOpen() {
		final Chest ch = new Chest();
		assertFalse(ch.isOpen());
		ch.open();

		assertTrue(ch.isOpen());
		ch.close();
		assertFalse(ch.isOpen());
	}

	/**
	 * Tests for onUsed.
	 */
	@Test
	public final void testOnUsed() {
		final Chest ch = new Chest();
		assertFalse(ch.isOpen());
		ch.onUsed(new RPEntity() {

			@Override
			protected void dropItemsOn(final Corpse corpse) {
			}

			@Override
			public void logic() {

			}
		});

		assertTrue(ch.isOpen());
		ch.onUsed(new RPEntity() {

			@Override
			protected void dropItemsOn(final Corpse corpse) {
			}

			@Override
			public void logic() {

			}
		});
		assertFalse(ch.isOpen());
	}

}
