package utilities.RPClass;


import static org.junit.Assert.assertTrue;
import marauroa.common.game.RPClass;

import org.junit.Test;

public class CorpseTestHelperTest {

	@Test
	public void testGenerateRPClasses() {
		CorpseTestHelper.generateRPClasses();
		assertTrue(RPClass.hasRPClass("corpse"));
	}
}
