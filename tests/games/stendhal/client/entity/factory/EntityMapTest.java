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
package games.stendhal.client.entity.factory;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import games.stendhal.client.entity.Gate;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.Player;
import games.stendhal.client.entity.StatefulEntity;

public class EntityMapTest {

	/**
	 * Tests for getClassStringString.
	 */
	@Test
	public final void testGetClassStringString() {
		Class< ? extends IEntity> entClass = EntityMap.getClass("player", null,
				null);
		assertEquals(Player.class, entClass);
		entClass = EntityMap.getClass(null, null, null);
		assertEquals(null, entClass);
	}

	/**
	 * Tests for getClassGoldsource.
	 */
	@Test
	public final void testGetClassGoldsource() {
		Class< ? extends IEntity> entClass = EntityMap.getClass("gold_source",
				null, null);
		assertEquals(StatefulEntity.class, entClass);
		entClass = EntityMap.getClass(null, null, null);
		assertEquals(null, entClass);
	}

	/**
	 * Tests for getSeed.
	 */
	@Test
	public final void testGetSeed() {
		Class< ? extends IEntity> entClass = EntityMap.getClass("gold_source",
				null, null);
		assertEquals(StatefulEntity.class, entClass);
		entClass = EntityMap.getClass(null, null, null);
		assertEquals(null, entClass);
	}
	/**
	 * Tests for getGate.
	 */
	@Test
	public final void testGetGate() {
		Class< ? extends IEntity> entClass = EntityMap.getClass("gate",
				null, null);
		assertEquals(Gate.class, entClass);
		entClass = EntityMap.getClass(null, null, null);
		assertEquals(null, entClass);
	}
}
