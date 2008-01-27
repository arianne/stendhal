package utilities.RPClass;

import static org.junit.Assert.assertTrue;
import marauroa.common.game.RPClass;

import org.junit.Test;

public class PassiveEntityRespawnPointTestHelperTest {

	@Test
	public void testGenerateRPClasses() {

		PassiveEntityRespawnPointTestHelper.generateRPClasses();
		assertTrue(RPClass.hasRPClass("plant_grower"));

	}

}
