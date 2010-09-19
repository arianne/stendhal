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
package games.stendhal.server.maps.semos.city;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;
import games.stendhal.server.entity.mapstuff.area.FertileGround;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import marauroa.common.game.RPClass;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.RPClass.EntityTestHelper;

public class FertileGroundsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		Log4J.init();
		EntityTestHelper.generateRPClasses();
		if (!RPClass.hasRPClass("area")) {
			AreaEntity.generateRPClass();
		}
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
	 * Tests for configureZoneNullNull.
	 */
	@Test
	public void testConfigureZoneNullNull() {
		final FertileGrounds fg = new FertileGrounds();
		fg.configureZone(null, null);
	}

	/**
	 * Tests for configureZoneNullvalues.
	 */
	@Test
	public void testConfigureZoneNullvalues() {
		final FertileGrounds fg = new FertileGrounds();
		final StendhalRPZone zone = new StendhalRPZone("zone");
		
		final Map<String, String> attribs = new HashMap<String, String>();
		attribs.put("x", null);
		attribs.put("y", null);
		attribs.put("width", null);
		attribs.put("height", null);
		
		fg.configureZone(zone, attribs);
	}
	/**
	 * Tests for configureZone.
	 */
	@Test
	public void testConfigureZone() {
		final FertileGrounds fg = new FertileGrounds();
		final StendhalRPZone zone = new StendhalRPZone("zone");
		
		final Map<String, String> attribs = new HashMap<String, String>();
		attribs.put("x", "1");
		attribs.put("y", "1");
		attribs.put("width", "3");
		attribs.put("height", "3");
		
		fg.configureZone(zone, attribs);
		assertFalse(0 + ":" + 0,
				zone.getEntityAt(0, 0) instanceof FertileGround);
		for (int x = 1; x < 4; x++) {
			for (int y = 1; y < 4; y++) {
				assertTrue(x + ":" + y,
						zone.getEntityAt(x, y) instanceof FertileGround);
			}
		} 
		
		
	}
}
