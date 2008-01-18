package conf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestPortalTest {

	@Test
	public void isDestinationOf() throws Exception {
		TestPortal emptyPortal = new TestPortal();

		assertFalse(emptyPortal.isDestinationOf(null));
		assertFalse(emptyPortal.isDestinationOf(emptyPortal));
		TestPortal source = new TestPortal("1", "source", "1", "target");
		TestPortal target = new TestPortal("1", "target", "", "");
		assertFalse(emptyPortal.isDestinationOf(target));
		assertTrue(target.isDestinationOf(source));
		assertFalse(source.isDestinationOf(target));

	}

	@Test
	public void testHasdestinationTest() {

		assertFalse(new TestPortal().hasDestination());
		assertTrue(new TestPortal("1", "target", "any", "").hasDestination());
		assertTrue(new TestPortal("1", "target", "", "any").hasDestination());
		assertFalse(new TestPortal("1", "target", "", "").hasDestination());
		assertTrue(new TestPortal("1", "source", "1", "target")
				.hasDestination());

	}

}
