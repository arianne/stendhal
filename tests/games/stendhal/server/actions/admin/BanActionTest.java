package games.stendhal.server.actions.admin;

import static org.junit.Assert.*;

import java.sql.SQLException;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;

import marauroa.common.game.RPAction;
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
		database.addPlayer(database.getTransaction(), player.getName(), new byte[0], "schnubbel");
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
		database.addPlayer(database.getTransaction(), player.getName(), new byte[0], "schnubbel");
		database.addPlayer(database.getTransaction(), admin.getName(), new byte[0], "schnubbel");

		assertEquals("active", database.getAccountStatus(database.getTransaction(), player.getName()));
		assertEquals("active", database.getAccountStatus(database.getTransaction(), admin.getName()));
		assertFalse(CommandCenter.execute(admin , action));
		
		admin.setAdminLevel(5000);
		assertTrue(CommandCenter.execute(admin , action));
		assertEquals("banned", database.getAccountStatus(database.getTransaction(), player.getName()));
		assertEquals("active", database.getAccountStatus(database.getTransaction(), admin.getName()));
		
	}
}
