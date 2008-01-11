package games.stendhal.tools.loganalyser.login;

import games.stendhal.server.core.engine.StendhalPlayerDatabase;
import games.stendhal.tools.loganalyser.itemlog.consistency.LogEntryIterator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.common.Pair;
import marauroa.server.game.db.StringChecker;
import marauroa.server.game.db.Transaction;

/**
 * Extracts timeframe of logins to accounts from a specified ip address
 *
 * @author hendrik
 */
public class Analyser {

	private static final String SQL = "SELECT timedate, username, address "
		+ " FROM loginEvent, account "
		+ " WHERE account.id = loginEvent.player_id"
		+ " AND loginEvent.address = '%0$s' AND loginEvent.timedate >= '%1$s'"
		+ " ORDER BY loginEvent.timedate";

	private void analyse(String address, String timedate) throws SQLException {
		LoginEventIterator iterator = readLoginsFromAddress(address, timedate);
	}

	private LoginEventIterator readLoginsFromAddress(String address, String timedate) throws SQLException {
		Transaction transaction =  StendhalPlayerDatabase.getDatabase().getTransaction();
		Connection connection = transaction.getConnection();
		Statement stmt = connection.createStatement();
		ResultSet resultSet = stmt.executeQuery(String.format(SQL, StringChecker.escapeSQLString(address), StringChecker.escapeSQLString(timedate)));
		return new LoginEventIterator(stmt, resultSet);
	}

	/**
	 * Entry point.
	 *
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		Log4J.init();
		String address = args[0];
		String timedate = "1900-01-01";
		if (args.length > 1) {
			timedate = args[1];
		}
		Analyser analyser = new Analyser();
		analyser.analyse(address, timedate);
	}


}
