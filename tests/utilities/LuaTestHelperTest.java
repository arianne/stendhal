/***************************************************************************
 *                   (C) Copyright 2003-2022 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.After;
import org.junit.Test;


public class LuaTestHelperTest extends LuaTestHelper {

	@After
	@Override
	public void tearDown() {
		super.tearDown();

		assertEquals(0, mrp.getOnlinePlayers().size());
		assertNull(world.getRPZone("dummy"));
	}

	@Test
	public void init() {
		testInit();
		testSetUp();
	}

	private void testInit() {
		assertNotNull(world);
		assertNotNull(mrp);
		assertNotNull(qs);
		assertNotNull(luaEngine);
	}

	private void testSetUp() {
		assertNull(zone);
		assertNull(player);

		setUpZone("dummy");
		assertNotNull(zone);
		assertEquals(zone, world.getRPZone("dummy"));

		setUpPlayer();
		assertNotNull(player);
		assertEquals(0, zone.getPlayers().size());

		addPlayerToWorld();
		assertEquals(1, zone.getPlayers().size());
	}
}
