package games.stendhal.server.core.account;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;

import marauroa.common.Log4J;
import marauroa.common.game.Result;
import marauroa.server.game.db.DatabaseFactory;
import marauroa.server.game.db.NoDatabaseConfException;
import marauroa.server.game.db.Transaction;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class CharacterCreatorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		PlayerTestHelper.generatePlayerRPClasses();
		PlayerTestHelper.generateItemRPClasses();
	}

	@Test
	public void testCreate() {
		try {
			Transaction trans = DatabaseFactory.getDatabase().getTransaction();

			if (!DatabaseFactory.getDatabase().getCharacters(trans, "user").isEmpty()) {

				DatabaseFactory.getDatabase().removeCharacter(trans, "user", "char");

			}
			CharacterCreator cc = new CharacterCreator("user", "char", null);

			assertEquals(Result.OK_CREATED, cc.create().getResult());
			assertEquals(Result.FAILED_PLAYER_EXISTS, cc.create().getResult());
			if (!DatabaseFactory.getDatabase().getCharacters(trans, "user").isEmpty()) {

				DatabaseFactory.getDatabase().removeCharacter(trans, "user", "char");

			}
		} catch (NoDatabaseConfException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
