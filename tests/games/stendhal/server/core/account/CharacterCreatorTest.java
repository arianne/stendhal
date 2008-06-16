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
import utilities.RPClass.ItemTestHelper;

public class CharacterCreatorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Log4J.init();
		PlayerTestHelper.generatePlayerRPClasses();
		ItemTestHelper.generateRPClasses();
	}

	@Test
	public void testCreate() {
		cleanDB();

		CharacterCreator cc = new CharacterCreator("user", "player", null);
		assertEquals(Result.OK_CREATED, cc.create().getResult());
		assertEquals(Result.FAILED_PLAYER_EXISTS, cc.create().getResult());

		cleanDB();
	}

	private void cleanDB() {
		Transaction trans = DatabaseFactory.getDatabase().getTransaction();
		try {
			trans.getAccessor().execute("DELETE FROM character_stats where name='player';");
			trans.commit();
			trans.getAccessor().execute("DELETE rpobject , characters from rpobject , characters where characters.charname = 'player' and characters.object_id = rpobject.object_id;");
			trans.commit();
		} catch (NoDatabaseConfException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
