package games.stendhal.server.entity.mapstuff.portal;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.HouseKey;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertEquals;

public class HousePortalTest {
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		HousePortal.generateRPClass();
	}
	
	/**
	 * Tests for creation.
	 */
	@Test
	public void testCreation() {
		final HousePortal portal = new HousePortal("henhouse");
		assertNotNull(portal);
		assertTrue(portal.isStorable());
		assertEquals("henhouse", portal.getDoorId());
		assertEquals("house_portal", portal.get("type"));
	}
	
	/**
	 * Tests for owner.
	 */
	@Test
	public void testOwner() {
		final HousePortal portal = new HousePortal("henhouse");
		assertEquals("", portal.getOwner());
		portal.setOwner("Mr Taxman");
		assertEquals("Mr Taxman", portal.getOwner());
	}
	
	/**
	 * Tests for describe.
	 */
	@Test
	public void testDescribe() {
		final HousePortal portal = new HousePortal("henhouse");
		assertEquals("For sale!", portal.describe());
		portal.setOwner("Mr Taxman");
		assertEquals("Here lives Mr Taxman.", portal.describe());
	}
	
	/**
	 * Tests for isAllowed.
	 */
	@Test
	public void testIsAllowed() {
		final HousePortal portal = new HousePortal("henhouse");
		final Player player = PlayerTestHelper.createPlayer("player");
		
		// should not be allowed in without a key
		assertFalse(portal.isAllowed(player));
		
		// or with some strrange key
		Item key = SingletonRepository.getEntityManager().getItem("dungeon silver key");
		player.equipToInventoryOnly(key);
		assertFalse(portal.isAllowed(player));
		
		// wrong key. should not be allowed in
		key = SingletonRepository.getEntityManager().getItem("house key");
		player.equipToInventoryOnly(key);
		assertFalse(portal.isAllowed(player));
		
		// add a new key with the right qualities
		// adding a new one on purpose rather than changing the old one
		// to ensure that a wrong key is not enough to deny entrance
		key = SingletonRepository.getEntityManager().getItem("house key");
		((HouseKey) key).setup("henhouse", 0, null);
		player.equipToInventoryOnly(key);
		assertTrue(portal.isAllowed(player));
		
		// after changing the lock the player should not be allowed in
		portal.changeLock();
		assertFalse(portal.isAllowed(player));
		
		// ...until the key is updated to match the portal
		((HouseKey) key).setup("henhouse", portal.getLockNumber(), null);
		assertTrue(portal.isAllowed(player));
	}
	
	/**
	 * Tests for changeLock.
	 */
	@Test
	public void testChangeLock() {
		final HousePortal portal = new HousePortal("henhouse");
		for (int i = 0; i <= 10; i++) {
			assertEquals(i, portal.getLockNumber());
			portal.changeLock();
		}
	}
	
	/**
	 * Tests for expiryTime.
	 */
	@Test
	public void testExpiryTime() {
		final HousePortal portal = new HousePortal("henhouse");
		portal.setExpireTime(49584);
		assertEquals(49584, portal.getExpireTime());
		portal.setExpireTime(-912234223);
		assertEquals(-912234223, portal.getExpireTime());
	}
	
	/**
	 * Tests for getPortalNumber.
	 */
	@Test
	public void testGetPortalNumber() {
		HousePortal portal = new HousePortal("henhouse");
		assertEquals(0, portal.getPortalNumber());
		
		portal = new HousePortal("henhouse 2");
		assertEquals(0, portal.getPortalNumber());
		
		portal = new HousePortal("mental institution");
		assertEquals(0, portal.getPortalNumber());
		
		portal = new HousePortal("mental institution 0");
		assertEquals(0, portal.getPortalNumber());
		
		portal = new HousePortal("some other house");
		assertEquals(0, portal.getPortalNumber());
		
		portal = new HousePortal("real house 1");
		assertEquals(1, portal.getPortalNumber());
		
		portal = new HousePortal("real house 13");
		assertEquals(13, portal.getPortalNumber());
		
		portal = new HousePortal("another real house 13");
		assertEquals(0, portal.getPortalNumber());
	}
}
