package games.stendhal.server.core.account;

import static org.junit.Assert.*;

import marauroa.common.game.Result;

import org.junit.BeforeClass;
import org.junit.Test;

public class CharacterCreatorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testCreate() {
		CharacterCreator cc = new CharacterCreator("user", "char", null);
		assertEquals(Result.FAILED_EXCEPTION, cc.create());
	}

}
