package utilities.RPClass;

import static org.junit.Assert.assertTrue;
import marauroa.common.game.RPClass;

import org.junit.Test;

public class FishSourceTestHelperTest {

	@Test
	public void testGenerateRPClasses() {
		FishSourceTestHelper.generateRPClasses();
		assertTrue(RPClass.hasRPClass("fish_source"));
	}

}
