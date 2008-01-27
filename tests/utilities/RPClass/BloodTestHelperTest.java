package utilities.RPClass;

import static org.junit.Assert.assertTrue;
import marauroa.common.game.RPClass;

import org.junit.Test;

public class BloodTestHelperTest {

	@Test
	public void testGenerateRPClasses() {
		BloodTestHelper.generateRPClasses();
		assertTrue(RPClass.hasRPClass("blood"));
	}

}
