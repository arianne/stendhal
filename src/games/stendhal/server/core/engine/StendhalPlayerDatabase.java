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
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import marauroa.common.Configuration;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;
import marauroa.server.game.db.IDatabase;
import marauroa.server.game.db.JDBCDatabase;
import marauroa.server.game.db.JDBCSQLHelper;
import marauroa.server.game.db.NoDatabaseConfException;
import marauroa.server.game.db.StringChecker;
import marauroa.server.game.db.Transaction;

import org.apache.log4j.Logger;

public class StendhalPlayerDatabase extends JDBCDatabase implements
		Iterable<RPObject> {

	private static boolean shouldStop;
	
	private static final Logger logger = Logger.getLogger(StendhalPlayerDatabase.class);
	private static TimerTask task = new TimerTask() {

		ItemLogger itemLogger = new ItemLogger();

		@Override
		public void run() {
			
			try {
				((StendhalPlayerDatabase) StendhalPlayerDatabase.database).processGameEvents();
				itemLogger.processEntries();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
			if (shouldStop && GameEventQueue.queue.isEmpty()) {
				this.cancel();
			}
		}
	};
	
	protected StendhalPlayerDatabase(final Properties connInfo) {
		super(connInfo);
		try {
			configureDatabase();
		} catch (final SQLException e) {
			throw new NoDatabaseConfException(e);
		}
	}
	
	
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
				new Timer().schedule(task , 2000, 300);
			}

			return database;
		} catch (final Exception e) {
			logger.error("cannot get database connection", e);
			throw new NoDatabaseConfException(e);
		}
	}
	


	protected void configureDatabase() throws SQLException {
		final Transaction trans = getTransaction();
		JDBCSQLHelper.get().runDBScript(trans,
				"games/stendhal/server/stendhal_init.sql");
		trans.commit();
	}

	public static IDatabase newConnection() throws IOException {
		final Configuration conf = Configuration.getConfiguration();
		final Properties props = new Properties();

		props.put("jdbc_url", conf.get("jdbc_url"));
		props.put("jdbc_class", conf.get("jdbc_class"));
		props.put("jdbc_user", conf.get("jdbc_user"));
		props.put("jdbc_pwd", conf.get("jdbc_pwd"));

		return new StendhalPlayerDatabase(props);
	}

	public void clearOnlineStatus() {
		try {
			final Transaction transaction = getTransaction();
			final Connection connection = transaction.getConnection();
			final Statement stmt = connection.createStatement();
			final String query = "UPDATE character_stats SET online=0";

			logger.debug("clearOnlineStatus is running: " + query);
			stmt.executeUpdate(query);
			stmt.close();
		} catch (final SQLException sqle) {
			logger.info("error storing character", sqle);
		}
	}

	public void setOnlineStatus(final Player player, final boolean online) {
		try {
			final Transaction transaction =  getTransaction();
			final Connection connection = transaction.getConnection();
			final Statement stmt = connection.createStatement();

			
			String onlinestate;
			if (online) {
				onlinestate = "1";
			} else {
				onlinestate = "0";
			}
			// first try an update
			final String query = "UPDATE character_stats SET online="
					+ onlinestate + " WHERE name='"
					+ StringChecker.escapeSQLString(player.get("name")) + "'";
			logger.debug("setOnlineStatus is running: " + query);
			stmt.executeUpdate(query);
			stmt.close();
		} catch (final SQLException sqle) {
			logger.info("error storing character", sqle);
		}
	}

	@Override
	public void addCharacter(final Transaction transaction, final String username,
			final String character, final RPObject player) throws SQLException, IOException {
		super.addCharacter(transaction, username, character, player);

		/*
		 * Here goes the stendhal specific code.
		 */
		try {
			final Player instance = (Player) player;
			final Connection connection =  transaction.getConnection();
			insertIntoCharStats(instance, connection);
		} catch (final SQLException sqle) {
			logger.warn("error storing character", sqle);
			throw sqle;
		}
	}

	
	@Override
	public void storeCharacter(final Transaction transaction, final String username,
			final String character, final RPObject player) throws SQLException, IOException {
		super.storeCharacter(transaction, username, character, player);

		/*
		 * Here goes the stendhal specific code.
		 */
		try {
			final Connection connection = transaction.getConnection();
			final Statement stmt = connection.createStatement();

			Player instance = (Player) player;

		

			
			final int count = updateCharStats(connection, instance);
			if (count == 0) {
				instance = (Player) player;
				insertIntoCharStats(instance, connection);
			}
			stmt.close();
		} catch (final SQLException sqle) {
			logger.warn("error storing character", sqle);
			throw sqle;
		}
	}

	private int updateCharStats(final Connection connection, final Player instance) throws SQLException {
		final String updateTemplate = "UPDATE character_stats SET "
			+ "admin=?,sentence=?,age=?,level=?,"
			+ "outfit=?,xp=?,money=?,married=?,atk=?,def=?,hp=?,karma=?,head=?,armor=?,lhand=?,rhand=?,legs=?,feet=?,cloak=? WHERE name=?";
		final PreparedStatement updateCharStatsStatement = connection.prepareStatement(updateTemplate);
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
		updateCharStatsStatement.setString(15, extractHandName(instance, "lhand"));			
		updateCharStatsStatement.setString(16, extractHandName(instance, "rhand"));
		updateCharStatsStatement.setString(17, extractName(instance.getLegs()));
		updateCharStatsStatement.setString(18, extractName(instance.getBoots()));
		updateCharStatsStatement.setString(19, extractName(instance.getCloak()));
		updateCharStatsStatement.setString(20, instance.getName());
		logger.debug("storeCharacter is running: " + updateCharStatsStatement.toString());
		final int count = updateCharStatsStatement.executeUpdate();
		return count;
	}

	private void insertIntoCharStats(final Player instance, final Connection connection) throws SQLException {
		final String insertTemplate = "INSERT INTO character_stats (name, online, admin, sentence, age, level, outfit, xp, money, atk, def, hp, karma, "
			+ "head, armor, lhand, rhand, legs, feet, cloak) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		final PreparedStatement insertStatement = connection.prepareStatement(insertTemplate);

		insertStatement.setString(1, instance.getName());
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
		insertStatement.setString(16, extractHandName(instance, "lhand"));
		insertStatement.setString(17, extractHandName(instance, "rhand"));
		insertStatement.setString(18, extractName(instance.getLegs()));
		insertStatement.setString(19, extractName(instance.getBoots()));
		insertStatement.setString(20, extractName(instance.getCloak()));

		logger.debug("storeCharacter is running: " + insertStatement.toString());
		insertStatement.executeUpdate();
		
		connection.commit();
		insertStatement.close();
	}
	
	// Used to get the items in the hands container, as they can be different to weapons or shields...
	// Could also be done using getEquippedItemClass and using all posibble classes for
	// the objects that can be used in hands.
	private String extractHandName(final Player instance, final String handSlot) {		
		if (instance != null && handSlot != null) {		
			if (instance.hasSlot(handSlot)) {
				final RPSlot rpslot = instance.getSlot(handSlot);
					// traverse all slot items
					for (final RPObject object : rpslot) {
						// is it the right type
						if (object instanceof Item) {
							final Item item = (Item) object;
							return item.getName();
						}
					}
					return null;
			}	
			return null;
		}
		return null;
	}

	private String extractName(final Item item) {
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
		} catch (final Exception e) {
			logger.error("cannot close database connection", e);
		}
	}

	class PlayerIterator implements Iterator<RPObject> {
		private final ResultSet result;

		private final Transaction trans;

		public PlayerIterator() throws SQLException {
			trans = getTransaction();
			final Connection connection = trans.getConnection();
			final Statement stmt = connection.createStatement();
			final String query = "select object_id from characters";

			logger.debug("iterator is executing query " + query);
			result = stmt.executeQuery(query);
		}

		public boolean hasNext() {
			try {
				return result.next();
			} catch (final SQLException e) {
				logger.error(e, e);
				return false;
			}
		}

		public RPObject next() {
			try {
				final int objectid = result.getInt("object_id");
				return loadRPObject(trans, objectid);
			} catch (final Exception e) {
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
		} catch (final SQLException e) {
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
	public int getHallOfFamePoints(final Transaction trans, final String playername,
			final String fametype) {
		int res = 0;
		try {
			final Connection connection =  trans.getConnection();
			final Statement stmt = connection.createStatement();

			final String query = "SELECT points FROM halloffame WHERE charname='"
					+ StringChecker.escapeSQLString(playername)
					+ "' AND fametype='"
					+ StringChecker.escapeSQLString(fametype) + "'";
			final ResultSet result = stmt.executeQuery(query);
			if (result.next()) {
				res = result.getInt("points");
			}
			result.close();
			stmt.close();
		} catch (final Exception sqle) {
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
	public void setHallOfFamePoints(final Transaction trans, final String playername,
			final String fametype, final int points) throws SQLException {
		try {
			final Connection connection = trans.getConnection();
			final Statement stmt = connection.createStatement();

			// first try an update
			String query = "UPDATE halloffame SET points='"
					+ StringChecker.escapeSQLString(Integer.toString(points))
					+ "' WHERE charname='"
					+ StringChecker.escapeSQLString(playername)
					+ "' AND fametype='"
					+ StringChecker.escapeSQLString(fametype) + "';";
			final int count = stmt.executeUpdate(query);

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
		} catch (final SQLException sqle) {
			logger.warn("error adding game event", sqle);
			throw sqle;
		}
	}


	private void processGameEvents() throws SQLException {
		
		final Transaction transaction = getTransaction();
		for (GameEvent current = GameEventQueue.getGameEvents().poll();
			current != null; current = GameEventQueue.getGameEvents().poll()) {
			addGameEvent(transaction, current.source, current.event, current.params);
			if ("server system".equals(current.source) && "shutdown".equals(current.event)) {
				shouldStop = true;
			}
		}
		
		transaction.commit();
	}

	/**
	 * Logs a kill.
	 *
	 * @param killed killed entity
	 * @param killer killer
	 */
	public void logKill(final Entity killed, final Entity killer) {
		final Transaction transaction =  SingletonRepository.getPlayerDatabase().getTransaction();
		try {

			// try update in case we already have this combination
			final String update = "UPDATE kills SET cnt = cnt+1 WHERE "
				+ "killed = '" + StringChecker.trimAndEscapeSQLString(getEntityName(killed), 64)
				+ "' AND killed_type = '" + entityToType(killed)
				+ "' AND killer = '" + StringChecker.trimAndEscapeSQLString(getEntityName(killer), 64)
				+ "' AND killer_type = '" + entityToType(killer) + "';";
			final int rowCount = transaction.getAccessor().execute(update);
			
			// in case we did not have this combination yet, make an insert
			if (rowCount == 0) {
				final String insert = "INSERT INTO kills (killed, killed_type, "
					+ "killer, killer_type, cnt) VALUES ('" 
					+ StringChecker.trimAndEscapeSQLString(getEntityName(killed), 64) + "', '" 
					+ entityToType(killed) + "', '" 
					+ StringChecker.trimAndEscapeSQLString(getEntityName(killer), 64) + "', '" 
					+ entityToType(killer) + "', 1);";
				transaction.getAccessor().execute(insert);
			}

			transaction.commit();
		} catch (final SQLException e) {
			logger.error(e, e);
			try {
				transaction.rollback();
			} catch (final SQLException e1) {
				logger.error(e1, e1);
			}
		}
	}

	/**
	 * Creates a one letter type string based on the class of the entity.
	 *
	 * @param entity Entity
	 * @return P for players, C for creatures, E for other entities
	 */
	private String entityToType(final Entity entity) {
		if (entity instanceof Player) {
			return "P";
		} else if (entity instanceof Creature) {
			return "C";
		} else {
			return "E";
		}
	}

	/**
	 * gets the real name of an entity (not the changeable title). 
	 *
	 * @param entity Entity
	 * @return name of entity
	 */
	private String getEntityName(final RPObject entity) {
		if (entity instanceof RPEntity) {
			return ((RPEntity) entity).getName();
		} else {
			return entity.getClass().getName();
		}
	}
	
}
