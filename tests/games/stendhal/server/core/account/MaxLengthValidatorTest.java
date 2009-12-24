package games.stendhal.server.core.account;

import static org.junit.Assert.*;

import marauroa.common.game.Result;

import org.junit.BeforeClass;
import org.junit.Test;

public class MaxLengthValidatorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * Tests for maxLengthValidator.
	 */
	@Test
	public void testMaxLengthValidator() {
		MaxLengthValidator validator = new MaxLengthValidator("four", 4);
		assertNull(validator.validate());

		validator = new MaxLengthValidator("four", 5);
		assertNull(validator.validate());
		
		validator = new MaxLengthValidator("four", 3);
		assertEquals(Result.FAILED_STRING_TOO_LONG, validator.validate());
		
	}

	

}
