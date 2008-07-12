package conf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestPortalTest {

	@Test
	public void isDestinationOf() throws Exception {
		final PortalTestObject emptyPortal = new PortalTestObject();

		assertFalse(emptyPortal.isDestinationOf(null));
		assertFalse(emptyPortal.isDestinationOf(emptyPortal));
		final PortalTestObject source = new PortalTestObject("1", "source", "1", "target");
		final PortalTestObject target = new PortalTestObject("1", "target", "", "");
		assertFalse(emptyPortal.isDestinationOf(target));
		assertTrue(target.isDestinationOf(source));
		assertFalse(source.isDestinationOf(target));

	}

	@Test
	public void testHasdestinationTest() {

		assertFalse(new PortalTestObject().hasDestination());
		assertTrue(new PortalTestObject("1", "target", "any", "").hasDestination());
		assertTrue(new PortalTestObject("1", "target", "", "any").hasDestination());
		assertFalse(new PortalTestObject("1", "target", "", "").hasDestination());
		assertTrue(new PortalTestObject("1", "source", "1", "target")
				.hasDestination());

	}

}
