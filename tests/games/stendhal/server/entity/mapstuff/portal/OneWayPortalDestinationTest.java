package games.stendhal.server.entity.mapstuff.portal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.RPClass.PortalTestHelper;

public class OneWayPortalDestinationTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PortalTestHelper.generateRPClasses();
	}

	@Test (expected = IllegalArgumentException.class)
	public final void testSetDestination() {
		final OneWayPortalDestination owp = new OneWayPortalDestination();
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
