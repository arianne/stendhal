package games.stendhal.tools.modifer;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.List;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;
import marauroa.server.game.Statistics.Variables;
import marauroa.server.game.container.PlayerEntry.SecuredLoginInfo;
import marauroa.server.game.db.IDatabase;
import marauroa.server.game.db.Transaction;
import marauroa.server.net.validator.InetAddressMask;

import org.junit.Test;

import utilities.PlayerTestHelper;

public class PlayerModifierTest {

	private final class ThrowSQLdb implements IDatabase {
		public void addCharacter(Transaction arg0, String arg1, String arg2, RPObject arg3) throws SQLException,
				IOException {

		}

		public void addGameEvent(Transaction arg0, String arg1, String arg2, String... arg3) {

		}

		public void addLoginEvent(Transaction arg0, String arg1, InetAddress arg2, boolean arg3) throws SQLException {

		}

		public void addPlayer(Transaction arg0, String arg1, byte[] arg2, String arg3) throws SQLException {

		}

		public void addStatisticsEvent(Transaction arg0, Variables arg1) {

		}

		public void changeEmail(Transaction arg0, String arg1, String arg2) throws SQLException {

		}

		public void changePassword(Transaction arg0, String arg1, String arg2) throws SQLException {

		}

		public void close() {

		}

		public String generatePlayer(Transaction arg0, String arg1) throws SQLException {

			return null;
		}

		public String getAccountStatus(Transaction arg0, String arg1) throws SQLException {

			return null;
		}

		public List<InetAddressMask> getBannedAddresses(Transaction arg0) throws SQLException {

			return null;
		}

		public List<String> getCharacters(Transaction arg0, String arg1) throws SQLException {

			return null;
		}

		public List<String> getLoginEvents(Transaction arg0, String arg1, int arg2) throws SQLException {

			return null;
		}

		public Transaction getTransaction() {

			return null;
		}

		public boolean hasCharacter(Transaction arg0, String arg1, String arg2) throws SQLException {

			return false;
		}

		public boolean hasPlayer(Transaction arg0, String arg1) throws SQLException {

			return false;
		}

		public boolean isAccountBlocked(Transaction arg0, String arg1) throws SQLException {

			return false;
		}

		public RPObject loadCharacter(Transaction arg0, String arg1, String arg2) throws SQLException, IOException {
			throw new SQLException();

		}

		public void loadRPZone(Transaction arg0, IRPZone arg1) throws SQLException, IOException {

		}

		public boolean removeCharacter(Transaction arg0, String arg1, String arg2) throws SQLException {

			return false;
		}

		public boolean removePlayer(Transaction arg0, String arg1) throws SQLException {

			return false;
		}

		public void setAccountStatus(Transaction arg0, String arg1, String arg2) throws SQLException {

		}

		public void storeCharacter(Transaction arg0, String arg1, String arg2, RPObject arg3) throws SQLException,
				IOException {
			throw new SQLException();

		}

		public void storeRPZone(Transaction arg0, IRPZone arg1) throws IOException, SQLException {

		}

		public boolean verify(Transaction arg0, SecuredLoginInfo arg1) throws SQLException {

			return false;
		}
	}

	private final class ThrowIOdb implements IDatabase {
		public void addCharacter(Transaction arg0, String arg1, String arg2, RPObject arg3) throws SQLException,
				IOException {

		}

		public void addGameEvent(Transaction arg0, String arg1, String arg2, String... arg3) {

		}

		public void addLoginEvent(Transaction arg0, String arg1, InetAddress arg2, boolean arg3) throws SQLException {

		}

		public void addPlayer(Transaction arg0, String arg1, byte[] arg2, String arg3) throws SQLException {

		}

		public void addStatisticsEvent(Transaction arg0, Variables arg1) {

		}

		public void changeEmail(Transaction arg0, String arg1, String arg2) throws SQLException {

		}

		public void changePassword(Transaction arg0, String arg1, String arg2) throws SQLException {

		}

		public void close() {

		}

		public String generatePlayer(Transaction arg0, String arg1) throws SQLException {

			return null;
		}

		public String getAccountStatus(Transaction arg0, String arg1) throws SQLException {

			return null;
		}

		public List<InetAddressMask> getBannedAddresses(Transaction arg0) throws SQLException {

			return null;
		}

		public List<String> getCharacters(Transaction arg0, String arg1) throws SQLException {

			return null;
		}

		public List<String> getLoginEvents(Transaction arg0, String arg1, int arg2) throws SQLException {

			return null;
		}

		public Transaction getTransaction() {

			return null;
		}

		public boolean hasCharacter(Transaction arg0, String arg1, String arg2) throws SQLException {

			return false;
		}

		public boolean hasPlayer(Transaction arg0, String arg1) throws SQLException {

			return false;
		}

		public boolean isAccountBlocked(Transaction arg0, String arg1) throws SQLException {

			return false;
		}

		public RPObject loadCharacter(Transaction arg0, String arg1, String arg2) throws SQLException, IOException {
			throw new IOException();

		}

		public void loadRPZone(Transaction arg0, IRPZone arg1) throws SQLException, IOException {

		}

		public boolean removeCharacter(Transaction arg0, String arg1, String arg2) throws SQLException {

			return false;
		}

		public boolean removePlayer(Transaction arg0, String arg1) throws SQLException {

			return false;
		}

		public void setAccountStatus(Transaction arg0, String arg1, String arg2) throws SQLException {

		}

		public void storeCharacter(Transaction arg0, String arg1, String arg2, RPObject arg3) throws SQLException,
				IOException {
			throw new IOException();

		}

		public void storeRPZone(Transaction arg0, IRPZone arg1) throws IOException, SQLException {

		}

		public boolean verify(Transaction arg0, SecuredLoginInfo arg1) throws SQLException {

			return false;
		}
	}

	@Test
	public void testLoadPlayer() {
		MockStendlRPWorld.get();
		String characterName = "modifyme";
		PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(SingletonRepository.getPlayerDatabase());
		Player player = mod.loadPlayer("");
		assertThat(player, nullValue());

		player = mod.loadPlayer(characterName);
		assertThat(player, not(nullValue()));
		assertThat(player.getName(), is(characterName));
	}

	@Test
	public void testLoadPlayerNameIsNull() {
		MockStendlRPWorld.get();
		PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(SingletonRepository.getPlayerDatabase());
		Player player = mod.loadPlayer(null);
		assertThat(player, nullValue());
	}

	@Test
	public void testmodifyPlayer() {
		MockStendlRPWorld.get();
		String characterName = "modifyme";
		PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(SingletonRepository.getPlayerDatabase());
		Player player = mod.loadPlayer(characterName);
		assertThat(player, not(nullValue()));
		assertThat(player.getName(), is(characterName));
		int adminlevel;
		if (player.getAdminLevel() == 100) {
			adminlevel = 0;

		} else {
			adminlevel = 100;
		}
		assertThat(player.getAdminLevel(), not(is(adminlevel)));
		player.setAdminLevel(adminlevel);

		assertThat(mod.savePlayer(player), is(true));

		player = mod.loadPlayer(characterName);
		assertThat(player, not(nullValue()));
		assertThat(player.getName(), is(characterName));
		assertThat(player.getAdminLevel(), is(adminlevel));
	}

	@Test(expected = IllegalStateException.class)
	public void testLoadPlayerNoDatabase() {
		PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(null);
		mod.loadPlayer("any");
	}

	@Test
	public void testLoadPlayerSQLEXCEPTION() {
		PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(new ThrowSQLdb());
		assertNull(mod.loadPlayer("any"));
	}

	@Test
	public void testLoadPlayerIOException() {
		PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(new ThrowIOdb());
		assertNull(mod.loadPlayer("any"));
	}

	@Test
	public void testSavePlayerSQLEXCEPTION() {
		PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(new ThrowSQLdb());
		assertFalse(mod.savePlayer(PlayerTestHelper.createPlayer("bob")));
	}

	@Test
	public void testSavePlayerIOException() {
		PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(new ThrowIOdb());
		assertFalse(mod.savePlayer(PlayerTestHelper.createPlayer("bob")));
	}
	
	@Test(expected = IllegalStateException.class)
	public void testSavePlayerNoDatabase() {
		PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(null);
		mod.savePlayer(null);
	}

}
