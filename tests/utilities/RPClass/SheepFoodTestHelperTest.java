package utilities.RPClass;


import static org.junit.Assert.assertTrue;
import marauroa.common.game.RPClass;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SheepFoodTestHelperTest {

	@Test
	public void testGenerateRPClasses() {
		
		SheepFoodTestHelper.generateRPClasses();
		assertTrue(RPClass.hasRPClass("food"));
	}

}
