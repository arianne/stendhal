package games.stendhal.server.actions.admin;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPAction;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;
import marauroa.server.game.db.DatabaseFactory;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class BanActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		new DatabaseFactory().initializeDatabase();
		MockStendlRPWorld.get();
	}

	/**
	 * Tests for perform.
	 */
	@Test
	public void testPerform() throws Throwable {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			AccountDAO accountDAO = DAORegister.get().get(AccountDAO.class);
	
			BanAction ban = new BanAction();
			Player player = PlayerTestHelper.createPlayer("bob");
			RPAction action = new RPAction();
			action.put("target", player.getName());
			if (!accountDAO.hasPlayer(transaction, player.getName())) {
				accountDAO.addPlayer(transaction, player.getName(), new byte[0], "schnubbel");
			}
			accountDAO.setAccountStatus(transaction, player.getName(), "active");
			assertEquals("active", accountDAO.getAccountStatus(transaction, player.getName()));

			TransactionPool.get().commit(transaction);

			ban.perform(player , action);

			transaction = TransactionPool.get().beginWork();

			assertEquals("banned", accountDAO.getAccountStatus(transaction, player.getName()));
	
			// just undo the changes so the next test starts clean
			TransactionPool.get().rollback(transaction);
		} catch (Throwable e) {
			TransactionPool.get().rollback(transaction);
			throw e;
		}
	}
	
	/**
	 * Tests for commandCenterPerform.
	 */
	@Test
	public void testCommandCenterPerform() throws Throwable {
		DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			AccountDAO accountDAO = DAORegister.get().get(AccountDAO.class);
	
			Player player = PlayerTestHelper.createPlayer("bobby");
			Player admin = PlayerTestHelper.createPlayer("admin");
			RPAction action = new RPAction();
			action.put("type", "ban");
			action.put("target", player.getName());
			action.put("reason", "whynot");
			if (!accountDAO.hasPlayer(transaction, player.getName())) {
				accountDAO.addPlayer(transaction, player.getName(), new byte[0], "schnubbel");
			}
			accountDAO.setAccountStatus(transaction, player.getName(), "active");
			if (!accountDAO.hasPlayer(transaction, admin.getName())) {
				accountDAO.addPlayer(transaction, admin.getName(), new byte[0], "schnubbel");
			}
			accountDAO.setAccountStatus(transaction, admin.getName(), "active");
	
			assertEquals("active", accountDAO.getAccountStatus(transaction, player.getName()));
			assertEquals("active", accountDAO.getAccountStatus(transaction, admin.getName()));

			TransactionPool.get().commit(transaction);

			assertFalse(CommandCenter.execute(admin , action));
			admin.clearEvents();
			admin.setAdminLevel(5000);
			assertTrue(CommandCenter.execute(admin , action));
			
			transaction = TransactionPool.get().beginWork();
			
			assertEquals("banned", accountDAO.getAccountStatus(transaction, player.getName()));
			assertEquals("active", accountDAO.getAccountStatus(transaction, admin.getName()));
			assertFalse(admin.events().isEmpty());
			assertThat(admin.events().get(0).toString(), containsString("RPEvent private_text with Attributes of Class(private_text): ")); 
			assertThat(admin.events().get(0).toString(), containsString("[text=You have banned bobby for: whynot]"));
			assertThat(admin.events().get(0).toString(), containsString("[texttype=PRIVMSG]"));
	
			// just undo the changes so the next test starts clean
			TransactionPool.get().rollback(transaction);
		} catch (Throwable e) {
			TransactionPool.get().rollback(transaction);
			throw e;
		}
	}
}
