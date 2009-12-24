package games.stendhal.server.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.BeforeClass;
import org.junit.Test;

public class BloodTest {

	@BeforeClass
	public static void setUp() throws Exception {
		MockStendlRPWorld.get();
		
	}

	/**
	 * Tests for describe.
	 */
	@Test
	public final void testDescribe() {
		final Blood bl = new Blood();
		assertEquals("You see a pool of blood.", bl.describe());
	}

	/**
	 * Tests for bloodStringInt.
	 */
	@Test
	public final void testBloodStringInt() {
		final Blood bl = new Blood("blabla", 1);
		assertEquals("blabla", bl.get("class"));
		assertEquals("1", bl.get("amount"));
	}

	/**
	 * Tests for onTurnReached.
	 */
	@Test
	public final void testOnTurnReached() {
		final StendhalRPZone zone = new StendhalRPZone("testzone");
		final Blood bl = new Blood();
		zone.add(bl);
		assertNotNull(zone.getBlood(0, 0));
		bl.onTurnReached(1);
		assertNull(zone.getBlood(0, 0));
	}

}
