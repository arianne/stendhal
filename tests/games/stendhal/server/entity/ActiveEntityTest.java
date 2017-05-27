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
package games.stendhal.server.entity;

import static org.junit.Assert.assertEquals;

import java.awt.Rectangle;

import org.junit.Test;

import games.stendhal.common.Direction;

public class ActiveEntityTest {

	/**
	 * Tests for getDirectionTowardArea.
	 */
	@Test
	public final void testGetDirectionTowardArea() {
		final ActiveEntity ae = new ActiveEntity() {
		};
		ae.setPosition(5, 5);
		Rectangle area;

		assertEquals(5, ae.getX());
		assertEquals(5, ae.getY());
		area = new Rectangle(4, 4, 1, 1);
		assertEquals(Direction.UP, ae.getDirectionToward(area));
		area = new Rectangle(5, 4, 1, 1);
		assertEquals(Direction.UP, ae.getDirectionToward(area));
		area = new Rectangle(6, 4, 1, 1);
		assertEquals(Direction.UP, ae.getDirectionToward(area));

		area = new Rectangle(4, 5, 1, 1);
		assertEquals(Direction.LEFT, ae.getDirectionToward(area));
		// ae.faceto(5,5); assertEquals(Direction.DOWN, ae.getDirection());
		area = new Rectangle(6, 5, 1, 1);
		assertEquals(Direction.RIGHT, ae.getDirectionToward(area));

		area = new Rectangle(4, 6, 1, 1);
		assertEquals(Direction.DOWN, ae.getDirectionToward(area));
		area = new Rectangle(5, 6, 1, 1);
		assertEquals(Direction.DOWN, ae.getDirectionToward(area));
		area = new Rectangle(6, 6, 1, 1);
		assertEquals(Direction.DOWN, ae.getDirectionToward(area));
	}

	/**
	 * Tests for faceto.
	 */
	@Test
	public final void testFaceto() {
		final ActiveEntity ae = new ActiveEntity() {
		};
		ae.setPosition(5, 5);

		assertEquals(5, ae.getX());
		assertEquals(5, ae.getY());
		ae.faceto(4, 4);
		assertEquals(Direction.UP, ae.getDirection());
		ae.faceto(5, 4);
		assertEquals(Direction.UP, ae.getDirection());
		ae.faceto(6, 4);
		assertEquals(Direction.UP, ae.getDirection());

		ae.faceto(4, 5);
		assertEquals(Direction.LEFT, ae.getDirection());
		// ae.faceto(5,5); assertEquals(Direction.DOWN, ae.getDirection());
		ae.faceto(6, 5);
		assertEquals(Direction.RIGHT, ae.getDirection());

		ae.faceto(4, 6);
		assertEquals(Direction.DOWN, ae.getDirection());
		ae.faceto(5, 6);
		assertEquals(Direction.DOWN, ae.getDirection());
		ae.faceto(6, 6);
		assertEquals(Direction.DOWN, ae.getDirection());
	}

}
