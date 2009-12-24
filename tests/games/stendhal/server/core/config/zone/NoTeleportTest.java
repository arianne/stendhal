package games.stendhal.server.core.config.zone;


import static org.junit.Assert.assertFalse;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NoTeleportTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	 * Tests for configureZone.
	 */
	@Test
	public void testConfigureZone() {
		StendhalRPZone zone = new StendhalRPZone("testzone",  20, 20);
		ZoneConfigurator conf = new NoTeleport();
		conf.configureZone(zone, null);
		assertFalse(zone.isTeleportInAllowed());
		assertFalse(zone.isTeleportOutAllowed());
		assertFalse(zone.isTeleportAllowed());
		
	}

}
