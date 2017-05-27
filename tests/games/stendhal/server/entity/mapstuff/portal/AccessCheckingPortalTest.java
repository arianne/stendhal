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
import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.portal.AccessCheckingPortal.SendMessage;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;
import utilities.PlayerTestHelper;
import utilities.RPClass.PortalTestHelper;

public class AccessCheckingPortalTest extends PlayerTestHelper {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		MockStendlRPWorld.get();
		PortalTestHelper.generateRPClasses();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
		SingletonRepository.getTurnNotifier().getEventListForDebugging().clear();
		assertTrue(SingletonRepository.getTurnNotifier().getEventListForDebugging().isEmpty());
	}

	/**
	 * Tests for accessCheckingPortal.
	 */
	@Test
	public final void testAccessCheckingPortal() {
		new MockAccessCheckingPortal();
	}

	/**
	 * Tests for onUsed.
	 */
	@Test
	public final void testOnUsed() {
		final AccessCheckingPortal port = new MockAccessCheckingPortal();
		final Object ref = new Object();
		port.setDestination("zonename", ref);
		final Portal destPort = new Portal();
		destPort.setIdentifier(ref);
		final StendhalRPZone zone = new StendhalRPZone("zonename");
		zone.add(destPort);
		MockStendlRPWorld.get().addRPZone(zone);

		Player player = createPlayer("mayNot");
		assertFalse(port.onUsed(player));

		player = createPlayer("player-may");
		assertTrue(port.onUsed(player));
	}

	/**
	 * Tests for isAllowed.
	 */
	@Test
	public final void testIsAllowed() {
		final AccessCheckingPortal port = new MockAccessCheckingPortal();
		Player player = createPlayer("player-may");
		assertTrue(port.isAllowed(player));
		player = createPlayer("mayNot");
		assertFalse(port.isAllowed(player));
	}

	/**
	 * Tests for rejected.
	 */
	@Test
	public final void testRejected() {
		final AccessCheckingPortal port = new MockAccessCheckingPortal();
		final Player player = PlayerTestHelper.createPlayer("mayNot");
		port.rejected(player);
		TurnNotifier turnNotifier = SingletonRepository.getTurnNotifier();
		final Set<TurnListener> bla = turnNotifier.getEventListForDebugging().get(
				Integer.valueOf(turnNotifier.getCurrentTurnForDebugging() + 1));
		final TurnListener[] listenerset = new TurnListener[bla.size()];
		bla.toArray(listenerset);
		assertTrue(listenerset[0] instanceof AccessCheckingPortal.SendMessage);
		final SendMessage sm = (SendMessage) listenerset[0];
		sm.onTurnReached(0);
		assertEquals("rejected", player.events().get(0).get("text"));
	}

	/**
	 * Tests for setRejectedMessage.
	 */
	@Test
	public final void testSetRejectedMessage() {
		final AccessCheckingPortal port = new MockAccessCheckingPortal();
		final Player player = PlayerTestHelper.createPlayer("mayNot");
		port.setRejectedMessage("setRejectMessage");
		port.rejected(player);
		final Set<TurnListener> bla = SingletonRepository.getTurnNotifier().getEventListForDebugging().get(
				Integer.valueOf(TurnNotifier.get().getCurrentTurnForDebugging() + 1));
		final TurnListener[] listenerset = new TurnListener[bla.size()];
		bla.toArray(listenerset);
		assertTrue(listenerset[0] instanceof AccessCheckingPortal.SendMessage);
		final SendMessage sm = (SendMessage) listenerset[0];
		sm.onTurnReached(0);
		assertEquals("setRejectMessage", player.events().get(0).get("text"));
	}

	private static final class MockAccessCheckingPortal extends AccessCheckingPortal {

		public MockAccessCheckingPortal() {
			super("rejected");
		}

		@Override
		protected boolean isAllowed(final RPEntity user) {
			return "player-may".equals(user.getName());
		}

	}
}
