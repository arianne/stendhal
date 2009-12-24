package games.stendhal.server.entity.mapstuff.portal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

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

		final Portal port = new Portal();
		final Player player = PlayerTestHelper.createPlayer("player");
		player.setPosition(5, 5);
		assertFalse("port is not nextto player", port.usePortal(player));
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
		port.onUsedBackwards(player);
	}

}
