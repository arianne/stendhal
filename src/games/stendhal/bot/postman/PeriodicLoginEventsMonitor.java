package games.stendhal.bot.postman;

import java.sql.ResultSet;
import java.sql.SQLException;

import marauroa.server.game.db.Accessor;
import marauroa.server.game.db.JDBCDatabase;

import org.apache.log4j.Logger;

/**
 * This class periodically monitors the loginEvent table for
 * failed logins to admin accounts
 *
 * @author hendrik
 */
public class PeriodicLoginEventsMonitor implements Runnable {
	
	private static final String SQL =
		"SELECT loginEvent.id, loginEvent.timedate, " +
		"account.username, loginEvent.player_id, loginEvent.address " +
		"FROM loginEvent, account, character_stats " +
		"WHERE account.id=loginEvent.player_id " +
		"AND account.username = character_stats.name " +
		"AND admin > 0 " +
		"AND loginEvent.timedate>'2008-05-15' AND result=0 " +
		"ORDER BY loginEvent.timedate";
 
	
	private static Logger logger = Logger.getLogger(PeriodicLoginEventsMonitor.class);
	private PostmanIRC postmanIRC;
	private JDBCDatabase database;

	/**
	 * creates a new PeriodicLoginEventsMonitor
	 *
	 * @param postmanIRC PostmanIRC
	 */
	public PeriodicLoginEventsMonitor(PostmanIRC postmanIRC) {
		this.postmanIRC = postmanIRC;
		database = (JDBCDatabase) JDBCDatabase.getDatabase();
	}

	/**
	 * Starts the /who thread.
	 */
	public void startThread() {
		Thread t = new Thread(this, "LoginEventsMonitor");
		t.setPriority(Thread.MIN_PRIORITY);
		t.setDaemon(true);
		t.start();
	}

	public void run() {
		while (true) {
			checkLogins();
			try {
				Thread.sleep(10 * 60 * 1000);
			} catch (InterruptedException e) {
				logger.error(e, e);
			}
		}
	}

	private void checkLogins() {
		try {
			queryDatabase();
			// TODO
			throw new RuntimeException("Not implemented yet"); // TODO: implement
		} catch (SQLException e) {
			logger.error(e, e);
		}

	}

	/**
	 * Queries the database for any failed admins login during the last time.
	 *
	 * @throws SQLException in case of an database error
	 */
	private void queryDatabase() throws SQLException {
		Accessor accessor = database.getTransaction().getAccessor();
		ResultSet rs = accessor.query(SQL);
		// TODO
		accessor.close();
		throw new RuntimeException("Not implemented yet"); // TODO: implement
	}
}
