/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff.area;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.rp.StendhalRPAction;
import games.stendhal.server.entity.ActiveEntity;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

/**
 * Tests for OnePlayerArea.
 */
public class OnePlayerAreaTest {
	@BeforeClass
	public static void setupBeforeClass() {
		// Ensure the RPClasses are generated
		MockStendlRPWorld.get();
	}
	
	/**
	 * Test setting and getting the occupant.
	 */
	@Test
	public void testSetAndGet() {
		OnePlayerArea area = new OnePlayerArea(1, 1);
		Player player = PlayerTestHelper.createPlayer("Romulus");
		
		assertNull(area.getOccupant());
		area.setOccupant(player);
		assertSame(player, area.getOccupant());
	}
	
	/**
	 * Test clearing the area.
	 */
	@Test
	public void testClearOccupant() {
		OnePlayerArea area = new OnePlayerArea(1, 1);
		Player player = PlayerTestHelper.createPlayer("Romulus");
		
		area.setOccupant(player);
		assertSame(player, area.getOccupant());
		area.clearOccupant();
		assertNull(area.getOccupant());
	}
	
	/**
	 * Test checking if the area contains an entity.
	 */
	@Test
	public void testContains() {
		OnePlayerArea area = new OnePlayerArea(3, 3);
		Player player = PlayerTestHelper.createPlayer("Romulus");
		
		// Extents of the insides 
		assertTrue(area.contains(player));
		player.setPosition(2, 2);
		assertTrue(area.contains(player));
		// try over-/underflowing any of the coordinates
		player.setPosition(2, 3);
		assertFalse(area.contains(player));
		player.setPosition(3, 2);
		assertFalse(area.contains(player));
		player.setPosition(-1, 0);
		assertFalse(area.contains(player));
		player.setPosition(0, -1);
		assertFalse(area.contains(player));
		// or both of the coordinates
		player.setPosition(-1, -1);
		assertFalse(area.contains(player));
		player.setPosition(3, 3);
		assertFalse(area.contains(player));
		
		// checks with a fat player
		player.setSize(2, 2);
		assertFalse(area.contains(player));
		player.setPosition(-1, -1);
		assertTrue(area.contains(player));
	}
	
	/**
	 * Test collision behavior.
	 */
	@Test
	public void testIsObstacle() {
		OnePlayerArea area = new OnePlayerArea(3, 3);
		Player player = PlayerTestHelper.createPlayer("Romulus");
		Entity entity = new Entity() {};
		
		assertFalse("Empty area collides with a player", area.isObstacle(player));
		assertFalse("Empty area collides with an entity", area.isObstacle(entity));
		area.setOccupant(player);
		assertFalse("Area occupied by a player collides with the player himself", area.isObstacle(player));
		assertFalse("Area occupied by a player collides with any entity", area.isObstacle(entity));
		
		Player player2 = PlayerTestHelper.createPlayer("Remus");
		player2.setPosition(1, 1);
		// Entering is possible in ghostmode, so there's a valid way to get two
		// players inside at the same time. These should always be allowed to leave.
		assertFalse("Area prevents extra player from leaving", area.isObstacle(player2));
		// but entering should be prohibited
		player2.setPosition(3, 3);
		assertTrue("Area allows more than one player", area.isObstacle(player2));
		player2.setGhost(true);
		assertFalse("A ghost can not enter the area", area.isObstacle(player2));
	}
	
	/**
	 * Test entering the area in various ways.
	 */
	@Test
	public void testOnEntered() {
		OnePlayerArea area = new OnePlayerArea(3, 3);
		Player player = PlayerTestHelper.createPlayer("Romulus");
		
		area.onEntered(player, null, 0, 0);
		assertSame(player, area.getOccupant());
		area.clearOccupant();
		// try as a ghost
		player.setGhost(true);
		area.onEntered(player, null, 0, 0);
		assertNull(area.getOccupant());
		player.setGhost(false);
		
		// These test largely functionality *outside* the OnePlayerArea code,
		// but there is not a very good place for these elsewhere.
		area = new AreaWrapper() {
			@Override
			public void onEntered(ActiveEntity player, StendhalRPZone zone, int x, int y) {
				throw new MethodCalledException(); 
			}
		};
		StendhalRPZone zone = new StendhalRPZone("test zone", 5, 5);
		zone.add(area);
	
		try {
			zone.add(player);
			fail("onEntered() not called when a player is added to the zone");
		} catch (MethodCalledException e) {
			// Player was added to the zone, within the area
		}
		player.setPosition(3, 3);
		try {
			player.setPosition(2, 2);
			fail("onEntered() not called when a player moves to the area");
		} catch (MethodCalledException e) {
			// Player moved to the area
		}
		zone.remove(player);
		zone.remove(area);
		try {
			zone.add(player);
		} catch (MethodCalledException e) {
			fail("onEntered() called after the area should have been removed");
			// Player was added to the zone, within the area
		}
		zone.remove(player);
	}
	
	/**
	 * Test exiting the area in various ways.
	 */
	@Test
	public void testOnExited() {
		OnePlayerArea area = new OnePlayerArea(3, 3);
		Player player = PlayerTestHelper.createPlayer("Romulus");
		area.setOccupant(player);
		area.onExited(player, null, 0, 0);
		assertNull(area.getOccupant());
		
		// These test largely functionality *outside* the OnePlayerArea code,
		// but there is not a very good place for these elsewhere.
		area = new AreaWrapper() {
			@Override
			public void onExited(ActiveEntity player, StendhalRPZone zone, int x, int y) {
				throw new MethodCalledException(); 
			}
		};
		StendhalRPZone zone = new StendhalRPZone("test zone", 5, 5);
		zone.add(area);
		try {
			zone.add(player);
		} catch (MethodCalledException e) {
			fail("onExited() called when a player is added to the zone");
			// Player was added to the zone, within the area
		}
		try {
			player.setPosition(3, 3);
			fail("onExited() not called when a player moves outside the area");
		} catch (MethodCalledException e) {
			// Player moved outside the area
		}	
		player.setPosition(2, 2);
		try {
			zone.remove(player);
			fail("onExited() not called when a player is removed from the zone");
		} catch (MethodCalledException e) {
			// Player was removed from the zone
		}
		zone.remove(area);
		// The previous remove was interrupted
		zone.remove(player);
		zone.add(player);
		
		try {
			zone.remove(player);
		} catch (MethodCalledException e) {
			fail("onExited() called after the area should have been removed");
		}
	}
	
	/**
	 * Check that the appropriate methods get called at zone change
	 */
	@Test
	public void testZoneChange() {
		Player player = PlayerTestHelper.createPlayer("Romulus");
		
		StendhalRPZone zone1 = new StendhalRPZone("test zone 1", 5, 5);
		StendhalRPZone zone2 = new StendhalRPZone("test zone 2", 5, 5);
		// These test largely functionality *outside* the OnePlayerArea code,
		// but there is not a very good place for these elsewhere.
		
		// *** exit checks ***
		OnePlayerArea area = new AreaWrapper() {
			@Override
			public void onExited(ActiveEntity player, StendhalRPZone zone, int x, int y) {
				throw new MethodCalledException(); 
			}
		};
		zone1.add(area);
		zone1.add(player);
		// Change zones
		try {
			StendhalRPAction.placeat(zone2, player, 1, 1);
			fail("onExited() at source area not called when a player changes zone");
		} catch (MethodCalledException e) {
		}
		
		// Interrupted move; ensure that the player is at zone2 for the next test
		zone1.remove(area);
		area.clearOccupant();
		StendhalRPAction.placeat(zone2, player, 1, 1);
		zone1.add(area);
		
		try {
			StendhalRPAction.placeat(zone1, player, 3, 3);
		} catch (MethodCalledException e) {
			fail("onExited() at destination area called when a player changes zone");
		}
		zone1.remove(area);
		
		// *** enter checks ***
		area = new AreaWrapper() {
			@Override
			public void onEntered(ActiveEntity player, StendhalRPZone zone, int x, int y) {
				throw new MethodCalledException(); 
			}
		};
		zone2.add(area);
		// Change zones
		try {
			StendhalRPAction.placeat(zone2, player, 1, 1);
			fail("onEntered() at destination area not called when a player changes zone");
		} catch (MethodCalledException e) {
		}
		
		// Interrupted move; ensure that the player is at zone2, and in the area
		// for the next test.
		zone2.remove(area);
		// place the player *outside*
		StendhalRPAction.placeat(zone2, player, 3, 3);
		zone2.add(area);
		area.setOccupant(player);
	
		try {
			StendhalRPAction.placeat(zone1, player, 0, 0);
		} catch (MethodCalledException e) {
			// Triggers an area that the player does not occupy. This can happen
			// if the movement is done in the source area before the player
			// arrives at the new zone. (it should happen between the zones)
			fail("onEntered() at source area called when a player changes zone");
		}
	}
	
	/**
	 * A helper class to allow attaching hooks to onEntered() and friends.
	 */
	private static class AreaWrapper extends OnePlayerArea {
		AreaWrapper() {
			super(3, 3);
		}
	}
	
	/**
	 * An exception to be thrown when a method is called.
	 */
	private static class MethodCalledException extends RuntimeException {
	}
}
