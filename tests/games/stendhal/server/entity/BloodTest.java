package games.stendhal.server.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

public class BloodTest {

	@BeforeClass
	public static void setUp() throws Exception {
		MockStendlRPWorld.get();
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testDescribe() {
		Blood bl = new Blood();
		assertEquals("You see a pool of blood.", bl.describe());
	}

	@Test
	public final void testBloodStringInt() {
		Blood bl = new Blood("blabla", 1);
		assertEquals("blabla", bl.get("class"));
		assertEquals("1", bl.get("amount"));
	}

	@Test
	public final void testOnTurnReached() {
		StendhalRPZone zone = new StendhalRPZone("testzone");
		Blood bl = new Blood();
		zone.add(bl);
		assertNotNull(zone.getBlood(0, 0));
		bl.onTurnReached(1);
		assertNull(zone.getBlood(0, 0));
	}

}
