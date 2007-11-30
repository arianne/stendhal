package games.stendhal.server.entity.mapstuff.portal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.portal.OneWayPortalDestination;
import games.stendhal.server.entity.mapstuff.portal.Portal;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class OneWayPortalDestinationTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Entity.generateRPClass();
		Portal.generateRPClass();
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

	@Test (expected = IllegalArgumentException.class)
	public final void testSetDestination() {
		OneWayPortalDestination owp = new OneWayPortalDestination();
		owp.setDestination("bla", new Object());

	}

	@Test
	public final void testLoaded() {
		assertTrue(new OneWayPortalDestination().loaded());
	}

	@Test
	public final void testOnUsed() {
		assertFalse(new OneWayPortalDestination().onUsed(null));
	}


}
