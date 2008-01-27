package utilities.RPClass;

import static org.junit.Assert.assertTrue;
import marauroa.common.game.RPClass;

import org.junit.Test;

public class SheepTestHelperTest {
	@Test
	public void testGenerateRPClasses() {
		CreatureTestHelper.generateRPClasses();
		SheepTestHelper.generateRPClasses();
		assertTrue(RPClass.hasRPClass("sheep"));
	}

}
