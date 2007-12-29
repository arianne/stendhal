package games.stendhal.server.core.account;

import static org.junit.Assert.assertEquals;
import marauroa.common.Log4J;
import marauroa.common.game.Result;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class CharacterCreatorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();

		// setup RP classes
		PlayerTestHelper.generatePlayerRPClasses();
		PlayerTestHelper.generateItemRPClasses();
	}

	@Test
	public void testCreate() {
		CharacterCreator cc = new CharacterCreator("user", "char", null);

		Result result = cc.create().getResult();

		// repeat creation after success in the first run
		if (result == Result.OK_CREATED) {
			result = cc.create().getResult();
		}

		// failure in the second run
		assertEquals(Result.FAILED_PLAYER_EXISTS, result);
	}

}
