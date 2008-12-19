package games.stendhal.server.actions.admin;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.sql.SQLException;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

import marauroa.common.game.RPAction;
import marauroa.common.game.RPEvent;
import marauroa.server.game.db.IDatabase;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class BanActionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPerform() throws SQLException {
		IDatabase database = SingletonRepository.getPlayerDatabase();
	
		BanAction ban = new BanAction();
		Player player = PlayerTestHelper.createPlayer("bob");
		RPAction action = new RPAction();
		action.put("target", player.getName());
		if (!database.hasPlayer(database.getTransaction(), player.getName())) {
			database.addPlayer(database.getTransaction(), player.getName(), new byte[0], "schnubbel");
		}
		database.setAccountStatus(database.getTransaction(), player.getName(), "active");
		assertEquals("active", database.getAccountStatus(database.getTransaction(), player.getName()));
		ban.perform(player , action);
		assertEquals("banned", database.getAccountStatus(database.getTransaction(), player.getName()));
		
	}
	
	@Test
	public void testCommandCenterPerform() throws SQLException {
		IDatabase database = SingletonRepository.getPlayerDatabase();
	
		Player player = PlayerTestHelper.createPlayer("bobby");
		Player admin = PlayerTestHelper.createPlayer("admin");
		RPAction action = new RPAction();
		action.put("type", "ban");
		action.put("target", player.getName());
		action.put("reason", "whynot");
		if (!database.hasPlayer(database.getTransaction(), player.getName())) {
			database.addPlayer(database.getTransaction(), player.getName(), new byte[0], "schnubbel");
		}
		database.setAccountStatus(database.getTransaction(), player.getName(), "active");
		if (!database.hasPlayer(database.getTransaction(), admin.getName())) {
			database.addPlayer(database.getTransaction(), admin.getName(), new byte[0], "schnubbel");
		}
		database.setAccountStatus(database.getTransaction(), admin.getName(), "active");

		assertEquals("active", database.getAccountStatus(database.getTransaction(), player.getName()));
		assertEquals("active", database.getAccountStatus(database.getTransaction(), admin.getName()));
		assertFalse(CommandCenter.execute(admin , action));
		admin.clearEvents();
		admin.setAdminLevel(5000);
		assertTrue(CommandCenter.execute(admin , action));
		assertEquals("banned", database.getAccountStatus(database.getTransaction(), player.getName()));
		assertEquals("active", database.getAccountStatus(database.getTransaction(), admin.getName()));
		assertFalse(admin.events().isEmpty());
		assertThat(admin.events().get(0).toString(), is("[private_text=Attributes of Class(): [text=You have banned bobby for: whynot][texttype=PRIVMSG]]"));

	}
}
