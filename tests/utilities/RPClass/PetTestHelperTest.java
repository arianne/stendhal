package utilities.RPClass;

import static org.junit.Assert.assertTrue;
import marauroa.common.game.RPClass;

import org.junit.Test;

public class PetTestHelperTest {

	@Test
	public void testGenerateRPClasses() {
		PetTestHelper.generateRPClasses();
		assertTrue(RPClass.hasRPClass("pet"));
	}

}
