package utilities.RPClass;

import static org.junit.Assert.assertTrue;
import marauroa.common.game.RPClass;

import org.junit.Test;

public class CatTestHelperTest {

	@Test
	public void testGenerateRPClasses() {
		CatTestHelper.generateRPClasses();
		assertTrue(RPClass.hasRPClass("cat"));
	}

}
