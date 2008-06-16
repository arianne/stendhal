package games.stendhal.server.core.engine;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import marauroa.common.Configuration;
import marauroa.common.game.RPObject;
import marauroa.server.game.db.IDatabase;
import marauroa.server.game.db.JDBCDatabase;
import marauroa.server.game.db.JDBCSQLHelper;
import marauroa.server.game.db.NoDatabaseConfException;
import marauroa.server.game.db.StringChecker;
import marauroa.server.game.db.Transaction;

import org.apache.log4j.Logger;

public class StendhalPlayerDatabase extends JDBCDatabase implements
		Iterable<RPObject> {

	private static final Logger logger = Logger.getLogger(StendhalPlayerDatabase.class);
	private static final String ATTR_ITEM_LOGID = "logid";
	
	
	/**
	 * This method returns an instance of PlayerDatabase.
	 * 
	 * @return A shared instance of PlayerDatabase
	 */
	public static IDatabase getDatabase() {
		try {
			if (database == null) {
				logger.info("Starting Stendhal JDBC Database");
				database = newConnection();
			}

			return database;
		} catch (Exception e) {
			logger.error("cannot get database connection", e);
			throw new NoDatabaseConfException(e);
		}
	}
	
	protected StendhalPlayerDatabase(Properties connInfo) {
		super(connInfo);
		try {
			configureDatabase();
		} catch (SQLException e) {
			throw new NoDatabaseConfException(e);
		}
	}

	protected void configureDatabase() throws SQLException {
		Transaction trans = getTransaction();
		JDBCSQLHelper.get().runDBScript(trans,
				"games/stendhal/server/stendhal_init.sql");
		trans.commit();
	}

	public static IDatabase newConnection() throws IOException {
		Configuration conf = Configuration.getConfiguration();
		Properties props = new Properties();

		props.put("jdbc_url", conf.get("jdbc_url"));
		props.put("jdbc_class", conf.get("jdbc_class"));
		props.put("jdbc_user", conf.get("jdbc_user"));
		props.put("jdbc_pwd", conf.get("jdbc_pwd"));

		return new StendhalPlayerDatabase(props);
	}

	public void clearOnlineStatus() {
		try {
			Transaction transaction = getTransaction();
			Connection connection = transaction.getConnection();
			Statement stmt = connection.createStatement();
			String query = "UPDATE character_stats SET online=0";

			logger.debug("clearOnlineStatus is running: " + query);
			stmt.executeUpdate(query);
			stmt.close();
		} catch (SQLException sqle) {
			logger.info("error storing character", sqle);
		}
	}

	public void setOnlineStatus(Player player, boolean online) {
		try {
			Transaction transaction =  getTransaction();
			Connection connection = transaction.getConnection();
			Statement stmt = connection.createStatement();

			// first try an update
			String query = "UPDATE character_stats SET online="
					+ (online ? 1 : 0) + " WHERE name='"
					+ StringChecker.escapeSQLString(player.get("name")) + "'";
			logger.debug("setOnlineStatus is running: " + query);
			stmt.executeUpdate(query);
			stmt.close();
		} catch (SQLException sqle) {
			logger.info("error storing character", sqle);
		}
	}

	@Override
	public void addCharacter(Transaction transaction, String username,
			String character, RPObject player) throws SQLException, IOException {
		super.addCharacter(transaction, username, character, player);

		/*
		 * Here goes the stendhal specific code.
		 */
		try {
			Player instance = (Player) player;
			Connection connection =  transaction.getConnection();
			insertIntoCharStats(instance, connection);
		} catch (SQLException sqle) {
			logger.warn("error storing character", sqle);
			throw sqle;
		}
	}

	
	@Override
	public void storeCharacter(Transaction transaction, String username,
			String character, RPObject player) throws SQLException, IOException {
		super.storeCharacter(transaction, username, character, player);

		/*
		 * Here goes the stendhal specific code.
		 */
		try {
			Connection connection = transaction.getConnection();
			Statement stmt = connection.createStatement();

			Player instance = (Player) player;

		

			
			int count = updateCharStats(connection, instance);
			if (count == 0) {
				instance = (Player) player;
				insertIntoCharStats(instance, connection);
			}
			stmt.close();
		} catch (SQLException sqle) {
			logger.warn("error storing character", sqle);
			throw sqle;
		}
	}

	private int updateCharStats(Connection connection, Player instance) throws SQLException {
		String updateTemplate = "UPDATE character_stats SET "
			+ "admin=?,sentence=?,age=?,level=?,"
			+ "outfit=?,xp=?,money=?,married=?,atk=?,def=?,hp=?,karma=?,head=?,armor=?,lhand=?,rhand=?,legs=?,feet=?,cloak=? WHERE name=?";
		PreparedStatement updateCharStatsStatement = connection.prepareStatement(updateTemplate);
		updateCharStatsStatement.setInt(1, instance.getAdminLevel());
		updateCharStatsStatement.setString(2, instance.getSentence());
		updateCharStatsStatement.setInt(3, instance.getAge());
		updateCharStatsStatement.setInt(4, instance.getLevel());
		updateCharStatsStatement.setInt(5, instance.getOutfit().getCode());	
		updateCharStatsStatement.setInt(6, instance.getXP());
		updateCharStatsStatement.setInt(7, instance.getNumberOfEquipped("money"));
		//married
		updateCharStatsStatement.setString(8, null);
		updateCharStatsStatement.setInt(9, instance.getATK());
		updateCharStatsStatement.setInt(10, instance.getDEF());
		updateCharStatsStatement.setInt(11, instance.getHP());
		updateCharStatsStatement.setDouble(12, instance.getKarma());
		updateCharStatsStatement.setString(13, extractName(instance.getHelmet()));
		updateCharStatsStatement.setString(14, extractName(instance.getArmor()));
		updateCharStatsStatement.setString(15, extractName(instance.getShield()));
		updateCharStatsStatement.setString(16, extractRhandName(instance));
		updateCharStatsStatement.setString(17, extractName(instance.getLegs()));
		updateCharStatsStatement.setString(18, extractName(instance.getBoots()));
		updateCharStatsStatement.setString(19, extractName(instance.getCloak()));
		updateCharStatsStatement.setString(20, instance.getName());
		logger.debug("storeCharacter is running: " + updateCharStatsStatement.toString());
		int count = updateCharStatsStatement.executeUpdate();
		return count;
	}

	private void insertIntoCharStats(Player instance, Connection connection) throws SQLException {
		String insertTemplate = "INSERT INTO character_stats (name, online, admin, sentence, age, level, outfit, xp, money, atk, def, hp, karma, "
			+ "head, armor, lhand, rhand, legs, feet, cloak) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement insertStatement = connection.prepareStatement(insertTemplate);

		insertStatement.setString(1, instance.getName());
		/*
		 * TODO: Check if this should be true or false.
		 */
		insertStatement.setBoolean(2, false);
		insertStatement.setInt(3, instance.getAdminLevel());
		insertStatement.setString(4, instance.getSentence());
		insertStatement.setInt(5, instance.getAge());
		insertStatement.setInt(6, instance.getLevel());
		insertStatement.setInt(7, instance.getOutfit().getCode());	
		insertStatement.setInt(8, instance.getXP());
		insertStatement.setInt(9, instance.getNumberOfEquipped("money"));
		insertStatement.setInt(10, instance.getATK());
		insertStatement.setInt(11, instance.getDEF());
		insertStatement.setInt(12, instance.getHP());
		insertStatement.setDouble(13, instance.getKarma());
		insertStatement.setString(14, extractName(instance.getHelmet()));
		insertStatement.setString(15, extractName(instance.getArmor()));
		insertStatement.setString(16, extractName(instance.getShield()));
		insertStatement.setString(17, extractRhandName(instance));
		insertStatement.setString(18, extractName(instance.getLegs()));
		insertStatement.setString(19, extractName(instance.getBoots()));
		insertStatement.setString(20, extractName(instance.getCloak()));

		logger.debug("storeCharacter is running: " + insertStatement.toString());
		insertStatement.executeUpdate();
		
		connection.commit();
		insertStatement.close();
	}

	private String extractRhandName(Player instance) {
		String rhand = null;
		List<Item> items = instance.getWeapons();
		if (items.size() > 0) {
			rhand = items.get(0).getName();
		}
		return rhand;
	}

	private String extractName(Item item) {
		if (item != null) {
			return item.getName();
		}
		return null;
	}



	/**
	 * close the database connection 
	 * 
	 * <p>TODO This function is not yet used, it
	 * should be called for clean shutdown of the game server.
	 */
	public static void closeDatabase() {
		try {
			if (database != null) {
				logger.info("closing Stendhal JDBC Database");
				database.close();
			}

			database = null;
		} catch (Exception e) {
			logger.error("cannot close database connection", e);
		}
	}

	class PlayerIterator implements Iterator<RPObject> {
		private ResultSet result;

		private Transaction trans;

		public PlayerIterator() throws SQLException {
			trans = getTransaction();
			Connection connection = trans.getConnection();
			Statement stmt = connection.createStatement();
			String query = "select object_id from characters";

			logger.debug("iterator is executing query " + query);
			result = stmt.executeQuery(query);
		}

		public boolean hasNext() {
			try {
				return result.next();
			} catch (SQLException e) {
				logger.error(e, e);
				return false;
			}
		}

		public RPObject next() {
			try {
				int objectid = result.getInt("object_id");
				return loadRPObject(trans, objectid);
			} catch (Exception e) {
				logger.warn(e, e);
				return null;
			}
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	public Iterator<RPObject> iterator() {
		try {
			return new PlayerIterator();
		} catch (SQLException e) {
			logger.warn(e, e);
			return null;
		}
	}

	/**
	 * Returns the points in the specified hall of fame.
	 * 
	 * @param trans
	 *            Transaction
	 * @param playername
	 *            name of the player
	 * @param fametype
	 *            type of the hall of fame
	 * @return points or 0 in case there is no entry
	 */
	public int getHallOfFamePoints(Transaction trans, String playername,
			String fametype) {
		int res = 0;
		try {
			Connection connection =  trans.getConnection();
			Statement stmt = connection.createStatement();

			String query = "SELECT points FROM halloffame WHERE charname='"
					+ StringChecker.escapeSQLString(playername)
					+ "' AND fametype='"
					+ StringChecker.escapeSQLString(fametype) + "'";
			ResultSet result = stmt.executeQuery(query);
			if (result.next()) {
				res = result.getInt("points");
			}
			result.close();
			stmt.close();
		} catch (Exception sqle) {
			logger.warn("Error reading hall of fame", sqle);
		}

		return res;
	}

	/**
	 * Stores an entry in the hall of fame.
	 * 
	 * @param trans
	 *            Transaction
	 * @param playername
	 *            name of the player
	 * @param fametype
	 *            type of the hall of fame
	 * @param points
	 *            points to store
	 * @throws SQLException
	 *             in case of an database error
	 */
	public void setHallOfFamePoints(Transaction trans, String playername,
			String fametype, int points) throws SQLException {
		try {
			Connection connection = trans.getConnection();
			Statement stmt = connection.createStatement();

			// first try an update
			String query = "UPDATE halloffame SET points='"
					+ StringChecker.escapeSQLString(Integer.toString(points))
					+ "' WHERE charname='"
					+ StringChecker.escapeSQLString(playername)
					+ "' AND fametype='"
					+ StringChecker.escapeSQLString(fametype) + "';";
			int count = stmt.executeUpdate(query);

			if (count == 0) {
				// no row was modified, so we need to do an insert
				query = "INSERT INTO halloffame (charname, fametype, points) VALUES ('"
						+ StringChecker.escapeSQLString(playername)
						+ "','"
						+ StringChecker.escapeSQLString(fametype)
						+ "','"
						+ StringChecker.escapeSQLString(Integer.toString(points))
						+ "');";
				stmt.executeUpdate(query);
			}
			stmt.close();
		} catch (SQLException sqle) {
			logger.warn("error adding game event", sqle);
			throw sqle;
		}
	}

	public void addGameEvent(String source, String event, String[] params)
			throws SQLException {
		Transaction transaction = getTransaction();
		addGameEvent(transaction, source, event, params);
		transaction.commit();
	}

	public void itemLog(RPObject item, RPEntity player, String event, String param1, String param2, String param3, String param4) {
		if (!item.getRPClass().subclassOf("item")) {
			return;
		}

		Transaction transaction =  SingletonRepository.getPlayerDatabase().getTransaction();
		try {

			itemLogAssignIDIfNotPresent(transaction, item);
			itemLogWriteEntry(transaction, item, player, event, param1, param2, param3, param4);

			transaction.commit();
		} catch (SQLException e) {
			logger.error(e, e);
			try {
				transaction.rollback();
			} catch (SQLException e1) {
				logger.error(e1, e1);
			}
		}
	}

	/**
	 * Assigns the next logid to the specified item in case it does not already have one.
	 *
	 * @param items item
	 */
	public void itemLogAssignIDIfNotPresent(RPObject... items) {
		Transaction transaction =  SingletonRepository.getPlayerDatabase().getTransaction();
		try {
			for (RPObject item : items) {
				if (item.getRPClass().subclassOf("item")) {
					itemLogAssignIDIfNotPresent(transaction, item);
				}
			}

			transaction.commit();
		} catch (SQLException e) {
			logger.error(e, e);
			try {
				transaction.rollback();
			} catch (SQLException e1) {
				logger.error(e1, e1);
			}
		}
	}
	
	/**
	 * Assigns the next logid to the specified item in case it does not already have one.
	 *
	 * @param transaction database transaction
	 * @param item item
	 * @throws SQLException in case of a database error
	 */
	private void itemLogAssignIDIfNotPresent(Transaction transaction, RPObject item) throws SQLException {
		if (item.has(ATTR_ITEM_LOGID)) {
			return;
		}

		// increment the last_id value (or initialize it in case that table has 0 rows).
		int count = transaction.getAccessor().execute("UPDATE itemid SET last_id = last_id+1;");
		if (count < 0) {
			logger.error("Unexpected return value of execute method: " + count);
		} else if (count == 0) {
			// Note: This is just a workaround in case the itemid table is empty.
			// In case itemlog was emptied, too; this workaround does not work because
			// there are still items with higher ids out there.
			logger.warn("Initializing itemid table, this may take a few minutes in case this database is not empty.");
			transaction.getAccessor().execute("INSERT INTO itemid (last_id) SELECT max(itemid) + 1 FROM itemlog;");
			logger.warn("itemid initialized.");
		}

		// read last_id from database
		int id = transaction.getAccessor().querySingleCellInt("SELECT last_id FROM itemid");
		item.put(ATTR_ITEM_LOGID, id);
	}

	private void itemLogWriteEntry(Transaction transaction, RPObject item, RPEntity player, String event, String param1, String param2, String param3, String param4) throws SQLException {
		String playerName = null;
		if (player != null) {
			playerName = player.getName();
		}
		String query = "INSERT INTO itemlog (itemid, source, event, " 
			+ "param1, param2, param3, param4) VALUES (" 
			+ item.getInt(ATTR_ITEM_LOGID) + ", '" 
			+ StringChecker.trimAndEscapeSQLString(playerName, 64) + "', '" 
			+ StringChecker.trimAndEscapeSQLString(event, 64) + "', '" 
			+ StringChecker.trimAndEscapeSQLString(param1, 64) + "', '" 
			+ StringChecker.trimAndEscapeSQLString(param2, 64) + "', '" 
			+ StringChecker.trimAndEscapeSQLString(param3, 64) + "', '" 
			+ StringChecker.trimAndEscapeSQLString(param4, 64) + "');";

		transaction.getAccessor().execute(query);
	}



	/**
	 * Creates a one letter type string based on the class of the entity.
	 *
	 * @param entity Entity
	 * @return P for players, C for creatures, E for other entities
	 */
	private String entityToType(Entity entity) {
		if (entity instanceof Player) {
			return "P";
		} else if (entity instanceof Creature) {
			return "C";
		} else {
			return "E";
		}
	}

}
