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
package games.stendhal.server.util;

import static org.junit.Assert.assertFalse;

import java.awt.Rectangle;

import org.junit.Assert;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.creature.Creature;


/**
 * Tests for the area class.
 *
 * @author hendrik
 */
public class AreaTest {
	private static final Rectangle rect = new Rectangle(3, 4, 5, 6);
	private static final StendhalRPZone zone = new StendhalRPZone("int_admin_test");
	private static final StendhalRPZone otherZone = new StendhalRPZone("int_admin_test_2");

	private Area createArea() {
		final Area area = new Area(zone, rect);
		return area;
	}

	/**
	 * Tests for createArea.
	 */
	@Test
	public void testCreateArea() {
		final Area area = createArea();
		Assert.assertEquals(rect, area.getShape());
	}

	/**
	 * Tests for inArea.
	 */
	@Test
	public void testInArea() {
		SingletonRepository.getRPWorld();
		final Area area = createArea();
		final Creature entity = new Creature();
		zone.add(entity);
		entity.setPosition(3, 4);
		Assert.assertTrue(area.contains(entity));
	}

	/**
	 * Tests for notInArea.
	 */
	@Test
	public void testNotInArea() {
		SingletonRepository.getRPWorld();
		final Area area = createArea();

		// other zone
		Creature entity = new Creature();
		otherZone.add(entity);
		entity.setPosition(3, 4);
		Assert.assertFalse(area.contains(entity));

		// right zone but wrong position
		entity = new Creature();
		zone.add(entity);
		entity.setPosition(1, 1);
		Assert.assertFalse(area.contains(entity));
}

	/**
	 * Tests for containsNull.
	 */
	@Test
	public void testContainsNull() {

		final Area area = new Area(null, null);
		final Entity entity = null;
		assertFalse(area.contains(entity));
	}
}
