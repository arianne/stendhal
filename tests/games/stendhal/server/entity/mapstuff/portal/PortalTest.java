package games.stendhal.server.entity.mapstuff.portal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class PortalTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		Log4J.init();
		Entity.generateRPClass();
		Portal.generateRPClass();
	}

	@Test
	public final void testToString() {

		Portal port = new Portal();
		assertEquals("Portal[0,0]", port.toString());
	}

	@Test
	public final void testIsHidden() {
		Portal port = new Portal();
		assertFalse(port.isHidden());
		port.put("hidden", "You don't see this object");
		assertTrue(port.isHidden());
	}

	@Test
	public final void testSetGetIdentifier() {

		Portal port = new Portal();
		Object o = new Object();
		port.setIdentifier(o);
		assertSame(o, port.getIdentifier());
	}

	@Test
	public final void testDestination() {

		Portal port = new Portal();
		Object ref = new Object();
		port.setDestination("zonename", ref);
		assertTrue(port.loaded());
		assertSame(ref, port.getDestinationReference());
		assertEquals("zonename", port.getDestinationZone());
	}

	@Test
	public final void testUsePortalWithNoDestination() {

		Portal port = new Portal();
		Player player = PlayerTestHelper.createPlayer("player");
		assertFalse("port has no destination", port.usePortal(player));
	}

	@Test
	public final void testUsePortalNotNextToPlayer() {

		Portal port = new Portal();
		Player player = PlayerTestHelper.createPlayer("player");
		player.setPosition(5, 5);
		assertFalse("port is not nextto player", port.usePortal(player));
	}

	@Test
	public final void testUsePortalHasInvalidDestination() {

		Portal port = new Portal();
		Player player = PlayerTestHelper.createPlayer("player");
		Object ref = new Object();
		port.setDestination("zonename", ref);
		assertFalse("port has invalid destination", port.usePortal(player));
	}

	@Test
	public final void testUsePortalHasInvalidDestinationReference() {

		Portal port = new Portal();
		Player player = PlayerTestHelper.createPlayer("player");
		Object ref = new Object();
		port.setDestination("zonename", ref);
		StendhalRPZone zone = new StendhalRPZone("zonename");
		MockStendlRPWorld.get().addRPZone(zone);
		assertFalse("port has invalid destination", port.usePortal(player));
	}

	@Test
	public final void testUsePortal() {

		Portal port = new Portal();
		Player player = PlayerTestHelper.createPlayer("player");
		Object ref = new Object();
		port.setDestination("zonename", ref);
		Portal destPort = new Portal();
		destPort.setIdentifier(ref);
		StendhalRPZone zone = new StendhalRPZone("zonename");
		zone.add(destPort);
		MockStendlRPWorld.get().addRPZone(zone);
		assertTrue("all things are nice", port.usePortal(player));
	}

	@Test
	public final void testOnUsed() {

		Portal port = new Portal() {

			@Override
			protected boolean usePortal(Player player) {
				player.setName("works");
				return false;
			}
		};
		Player bob = PlayerTestHelper.createPlayer("player");
		port.usePortal(bob);
		assertEquals("works", bob.getName());

	}

	@Test
	public final void testOnUsedBackwards() {

		Portal port = new Portal();
		Player player = PlayerTestHelper.createPlayer("player");
		port.onUsedBackwards(player);
	}

}
