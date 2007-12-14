package games.stendhal.server.core.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import games.stendhal.server.core.account.LowerCaseValidator;
import marauroa.common.game.Result;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LowerCaseValidatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testLowerCaseValidator() {
		LowerCaseValidator lcv = new LowerCaseValidator("A");
		assertEquals(Result.FAILED_INVALID_CHARACTER_USED, lcv.validate());
		lcv = new LowerCaseValidator("a");
		assertNull(lcv.validate());

		lcv = new LowerCaseValidator("Ü");
		assertEquals(Result.FAILED_INVALID_CHARACTER_USED, lcv.validate());
		lcv = new LowerCaseValidator("ü");
		assertNull(lcv.validate());

	}

}
