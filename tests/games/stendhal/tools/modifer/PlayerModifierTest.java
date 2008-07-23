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

import org.junit.Ignore;
import org.junit.Test;

import utilities.PlayerTestHelper;

public class PlayerModifierTest {

	private final class ThrowSQLdb implements IDatabase {
		public void addCharacter(final Transaction arg0, final String arg1, final String arg2, final RPObject arg3) throws SQLException,
				IOException {

		}

		public void addGameEvent(final Transaction arg0, final String arg1, final String arg2, final String... arg3) {

		}

		public void addLoginEvent(final Transaction arg0, final String arg1, final InetAddress arg2, final boolean arg3) throws SQLException {

		}

		public void addPlayer(final Transaction arg0, final String arg1, final byte[] arg2, final String arg3) throws SQLException {

		}

		public void addStatisticsEvent(final Transaction arg0, final Variables arg1) {

		}

		public void changeEmail(final Transaction arg0, final String arg1, final String arg2) throws SQLException {

		}

		public void changePassword(final Transaction arg0, final String arg1, final String arg2) throws SQLException {

		}

		public void close() {

		}

		public String generatePlayer(final Transaction arg0, final String arg1) throws SQLException {

			return null;
		}

		public String getAccountStatus(final Transaction arg0, final String arg1) throws SQLException {

			return null;
		}

		public List<InetAddressMask> getBannedAddresses(final Transaction arg0) throws SQLException {

			return null;
		}

		public List<String> getCharacters(final Transaction arg0, final String arg1) throws SQLException {

			return null;
		}

		public List<String> getLoginEvents(final Transaction arg0, final String arg1, final int arg2) throws SQLException {

			return null;
		}

		public Transaction getTransaction() {

			return null;
		}

		public boolean hasCharacter(final Transaction arg0, final String arg1, final String arg2) throws SQLException {

			return false;
		}

		public boolean hasPlayer(final Transaction arg0, final String arg1) throws SQLException {

			return false;
		}

		public boolean isAccountBlocked(final Transaction arg0, final String arg1) throws SQLException {

			return false;
		}

		public RPObject loadCharacter(final Transaction arg0, final String arg1, final String arg2) throws SQLException, IOException {
			throw new SQLException();

		}

		public void loadRPZone(final Transaction arg0, final IRPZone arg1) throws SQLException, IOException {

		}

		public boolean removeCharacter(final Transaction arg0, final String arg1, final String arg2) throws SQLException {

			return false;
		}

		public boolean removePlayer(final Transaction arg0, final String arg1) throws SQLException {

			return false;
		}

		public void setAccountStatus(final Transaction arg0, final String arg1, final String arg2) throws SQLException {

		}

		public void storeCharacter(final Transaction arg0, final String arg1, final String arg2, final RPObject arg3) throws SQLException,
				IOException {
			throw new SQLException();

		}

		public void storeRPZone(final Transaction arg0, final IRPZone arg1) throws IOException, SQLException {

		}

		public boolean verify(final Transaction arg0, final SecuredLoginInfo arg1) throws SQLException {

			return false;
		}
	}

	private final class ThrowIOdb implements IDatabase {
		public void addCharacter(final Transaction arg0, final String arg1, final String arg2, final RPObject arg3) throws SQLException,
				IOException {

		}

		public void addGameEvent(final Transaction arg0, final String arg1, final String arg2, final String... arg3) {

		}

		public void addLoginEvent(final Transaction arg0, final String arg1, final InetAddress arg2, final boolean arg3) throws SQLException {

		}

		public void addPlayer(final Transaction arg0, final String arg1, final byte[] arg2, final String arg3) throws SQLException {

		}

		public void addStatisticsEvent(final Transaction arg0, final Variables arg1) {

		}

		public void changeEmail(final Transaction arg0, final String arg1, final String arg2) throws SQLException {

		}

		public void changePassword(final Transaction arg0, final String arg1, final String arg2) throws SQLException {

		}

		public void close() {

		}

		public String generatePlayer(final Transaction arg0, final String arg1) throws SQLException {

			return null;
		}

		public String getAccountStatus(final Transaction arg0, final String arg1) throws SQLException {

			return null;
		}

		public List<InetAddressMask> getBannedAddresses(final Transaction arg0) throws SQLException {

			return null;
		}

		public List<String> getCharacters(final Transaction arg0, final String arg1) throws SQLException {

			return null;
		}

		public List<String> getLoginEvents(final Transaction arg0, final String arg1, final int arg2) throws SQLException {

			return null;
		}

		public Transaction getTransaction() {

			return null;
		}

		public boolean hasCharacter(final Transaction arg0, final String arg1, final String arg2) throws SQLException {

			return false;
		}

		public boolean hasPlayer(final Transaction arg0, final String arg1) throws SQLException {

			return false;
		}

		public boolean isAccountBlocked(final Transaction arg0, final String arg1) throws SQLException {

			return false;
		}

		public RPObject loadCharacter(final Transaction arg0, final String arg1, final String arg2) throws SQLException, IOException {
			throw new IOException();

		}

		public void loadRPZone(final Transaction arg0, final IRPZone arg1) throws SQLException, IOException {

		}

		public boolean removeCharacter(final Transaction arg0, final String arg1, final String arg2) throws SQLException {

			return false;
		}

		public boolean removePlayer(final Transaction arg0, final String arg1) throws SQLException {

			return false;
		}

		public void setAccountStatus(final Transaction arg0, final String arg1, final String arg2) throws SQLException {

		}

		public void storeCharacter(final Transaction arg0, final String arg1, final String arg2, final RPObject arg3) throws SQLException,
				IOException {
			throw new IOException();

		}

		public void storeRPZone(final Transaction arg0, final IRPZone arg1) throws IOException, SQLException {

		}

		public boolean verify(final Transaction arg0, final SecuredLoginInfo arg1) throws SQLException {

			return false;
		}
	}

	@Ignore
	@Test
	public void testLoadPlayer() {
		MockStendlRPWorld.get();
		final String characterName = "modifyme";
		final PlayerModifier mod = new PlayerModifier();
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
		final PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(SingletonRepository.getPlayerDatabase());
		final Player player = mod.loadPlayer(null);
		assertThat(player, nullValue());
	}

	@Ignore
	@Test
	public void testmodifyPlayer() {
		MockStendlRPWorld.get();
		final String characterName = "modifyme";
		final PlayerModifier mod = new PlayerModifier();
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
		final PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(null);
		mod.loadPlayer("any");
	}

	@Test
	public void testLoadPlayerSQLEXCEPTION() {
		final PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(new ThrowSQLdb());
		assertNull(mod.loadPlayer("any"));
	}

	@Test
	public void testLoadPlayerIOException() {
		final PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(new ThrowIOdb());
		assertNull(mod.loadPlayer("any"));
	}

	@Test
	public void testSavePlayerSQLEXCEPTION() {
		final PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(new ThrowSQLdb());
		assertFalse(mod.savePlayer(PlayerTestHelper.createPlayer("bob")));
	}

	@Test
	public void testSavePlayerIOException() {
		final PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(new ThrowIOdb());
		assertFalse(mod.savePlayer(PlayerTestHelper.createPlayer("bob")));
	}
	
	@Test(expected = IllegalStateException.class)
	public void testSavePlayerNoDatabase() {
		final PlayerModifier mod = new PlayerModifier();
		mod.setDatabase(null);
		mod.savePlayer(null);
	}

}
