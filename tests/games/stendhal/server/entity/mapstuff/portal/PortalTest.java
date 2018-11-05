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
package games.stendhal.server.entity.mapstuff.portal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;
import utilities.RPClass.EntityTestHelper;
import utilities.RPClass.PortalTestHelper;

public class PortalTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		Log4J.init();
		MockStendlRPWorld.get();
		EntityTestHelper.generateRPClasses();
		PortalTestHelper.generateRPClasses();
	}

	/**
	 * Tests for toString.
	 */
	@Test
	public final void testToString() {

		final Portal port = new Portal();
		assertEquals("Portal[0,0]", port.toString());
	}

	/**
	 * Tests for isHidden.
	 */
	@Test
	public final void testIsHidden() {
		final Portal port = new Portal();
		assertFalse(port.isHidden());
		port.put("hidden", "You don't see this object");
		assertTrue(port.isHidden());
	}

	/**
	 * Tests for setGetIdentifier.
	 */
	@Test
	public final void testSetGetIdentifier() {

		final Portal port = new Portal();
		final Object o = new Object();
		port.setIdentifier(o);
		assertSame(o, port.getIdentifier());
	}

	/**
	 * Tests for destination.
	 */
	@Test
	public final void testDestination() {

		final Portal port = new Portal();
		final Object ref = new Object();
		port.setDestination("zonename", ref);
		assertTrue(port.loaded());
		assertSame(ref, port.getDestinationReference());
		assertEquals("zonename", port.getDestinationZone());
	}

	/**
	 * Tests for usePortalWithNoDestination.
	 */
	@Test
	public final void testUsePortalWithNoDestination() {

		final Portal port = new Portal();
		final Player player = PlayerTestHelper.createPlayer("player");
		assertFalse("port has no destination", port.usePortal(player));
	}

	/**
	 * Tests for usePortalNotNextToPlayer.
	 */
	@Test
	public final void testUsePortalNotNextToPlayer() {

		// there is a bit of pathfinding now in the portal code (if you are a distance from it)
		// so we need a 'zone' defined

		final Portal port = new Portal();
		port.setPosition(1, 1);
		final StendhalRPZone testzone = new StendhalRPZone("admin_test");
		testzone.collisionMap.init(10, 10);

		final Player player = PlayerTestHelper.createPlayer("player");

		final Object ref = new Object();
		port.setDestination("zonename", ref);
		final Portal destPort = new Portal();
		destPort.setIdentifier(ref);
		final StendhalRPZone zone = new StendhalRPZone("zonename");
		zone.add(destPort);
		testzone.add(port);
		testzone.add(player);

		MockStendlRPWorld.get().addRPZone(testzone);
		MockStendlRPWorld.get().addRPZone(zone);

		player.setPosition(5, 5);
		assertTrue("player is in original zone now", player.getZone().equals(testzone));
		assertTrue("portal is in original zone now", port.getZone().equals(testzone));
		assertFalse("player is not next to portal", port.nextTo(player));
		assertFalse("portal is not next to player, won't walk through but will set a path", port.usePortal(player));
		assertTrue("player was set on a path", player.hasPath());
		// would be nice to test but we would have to iterate the turns manually
		// and this is rather more part of the pathfinding which makes the player change zone, not the portal
		// assertTrue("player is in destination zone now", player.getZone().equals(zone));
	}

	/**
	 * Tests for usePortalHasInvalidDestination.
	 */
	@Test
	public final void testUsePortalHasInvalidDestination() {

		final Portal port = new Portal();
		final Player player = PlayerTestHelper.createPlayer("player");
		final Object ref = new Object();
		port.setDestination("zonename", ref);
		assertFalse("port has invalid destination", port.usePortal(player));
	}

	/**
	 * Tests for usePortalHasInvalidDestinationReference.
	 */
	@Test
	public final void testUsePortalHasInvalidDestinationReference() {

		final Portal port = new Portal();
		final Player player = PlayerTestHelper.createPlayer("player");
		final Object ref = new Object();
		port.setDestination("zonename", ref);
		final StendhalRPZone zone = new StendhalRPZone("zonename");
		MockStendlRPWorld.get().addRPZone(zone);
		assertFalse("port has invalid destination", port.usePortal(player));
	}

	/**
	 * Tests for usePortal.
	 */
	@Test
	public final void testUsePortal() {

		final Portal port = new Portal();
		final Player player = PlayerTestHelper.createPlayer("player");
		final Object ref = new Object();
		port.setDestination("zonename", ref);
		final Portal destPort = new Portal();
		destPort.setIdentifier(ref);
		final StendhalRPZone zone = new StendhalRPZone("zonename");
		zone.add(destPort);
		MockStendlRPWorld.get().addRPZone(zone);
		assertTrue("all things are nice", port.usePortal(player));
	}

	/**
	 * Tests for onUsed.
	 */
	@Test
	public final void testOnUsed() {
		final Portal port = new Portal() {
			@Override
			protected boolean usePortal(final Player player) {
				player.setName("renamed-" + player.getName());
				return false;
			}
		};
		final Player bob = PlayerTestHelper.createPlayer("bob");
		port.usePortal(bob);
		assertEquals("renamed-bob", bob.getName());
	}

	/**
	 * Tests for onUsedBackwards.
	 */
	@Test
	public final void testOnUsedBackwards() {
		final Portal port = new Portal();
		final Player player = PlayerTestHelper.createPlayer("player");
		port.onUsedBackwards(player, player.hasPath());
	}

}
