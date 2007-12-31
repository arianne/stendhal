package games.stendhal.server.core.account;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
		
			Transaction trans = DatabaseFactory.getDatabase().getTransaction();
			try {
				trans.getAccessor().execute("DELETE FROM character_stats where name='char';");
				trans.getAccessor().execute("DELETE rpobject , characters from rpobject , characters where characters.charname = \"char\" and characters.object_id = rpobject.object_id;");
				
				
			} catch (NoDatabaseConfException e) {
				
				e.printStackTrace();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			CharacterCreator cc = new CharacterCreator("user", "char", null);
			assertEquals(Result.OK_CREATED, cc.create().getResult());
			assertEquals(Result.FAILED_PLAYER_EXISTS, cc.create().getResult());

			try {
				trans.getAccessor().execute("DELETE FROM character_stats where name='char';");
				trans.getAccessor().execute("DELETE rpobject , characters from rpobject , characters where characters.charname = \"char\" and characters.object_id = rpobject.object_id;");
				
				
			} catch (NoDatabaseConfException e) {
				
				e.printStackTrace();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
		
	}

}
