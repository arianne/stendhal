package utilities.RPClass;

import static org.junit.Assert.assertTrue;
import marauroa.common.game.RPClass;

import org.junit.Test;

public class PortalTestHelperTest {

	@Test
	public void testGenerateRPClasses() {
		PortalTestHelper.generateRPClasses();
		assertTrue(RPClass.hasRPClass("portal"));
	}

}
