package utilities.RPClass;

import static org.junit.Assert.assertTrue;
import marauroa.common.game.RPClass;

import org.junit.Test;

public class EntityTestHelperTest {

	@Test
	public void testGenerateRPClasses() {
		EntityTestHelper.generateRPClasses();
		assertTrue(RPClass.hasRPClass("entity"));
	}
}
