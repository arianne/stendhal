package games.stendhal.tools.loganalyser.login;

import games.stendhal.server.core.engine.SingletonRepository;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Log4J;
import marauroa.server.game.db.StringChecker;
import marauroa.server.game.db.Transaction;

/**
 * Extracts timeframe of logins to accounts from a specified ip address.
 *
 * @author hendrik
 */
public class Analyser {

	private static final String SQL_FROM_IP = "SELECT username, address, loginEvent.timedate As timedate"
		+ " FROM loginEvent, account "
		+ " WHERE account.id = loginEvent.player_id AND result=1"
		+ " AND loginEvent.address = '%1$s' AND loginEvent.timedate >= '%2$s'"
		+ " ORDER BY loginEvent.timedate";
	
	private static final String SQL_NEXT_LOGIN = "SELECT username, address, loginEvent.timedate As timedate" 
		+ " FROM loginEvent, account"
		+ " WHERE account.id = loginEvent.player_id AND result=1"
		+ " AND username='%1$s' AND loginEvent.timedate > '%2$s'"
		+ " ORDER BY loginEvent.timedate LIMIT 1;";
	
	private LoginEventIterator readLoginsFromAddress(String address, String timedate) throws SQLException {
		Transaction transaction =  SingletonRepository.getPlayerDatabase().getTransaction();
		Connection connection = transaction.getConnection();
		Statement stmt = connection.createStatement();
		String select = String.format(SQL_FROM_IP, StringChecker.escapeSQLString(address), StringChecker.escapeSQLString(timedate));
		ResultSet resultSet = stmt.executeQuery(select);
		return new LoginEventIterator(stmt, resultSet);
	}
	
	private LoginEvent getNextLoginEvent(LoginEvent event) throws SQLException {
		LoginEvent nextEvent = null;
		Transaction transaction =  SingletonRepository.getPlayerDatabase().getTransaction();
		Connection connection = transaction.getConnection();
		Statement stmt = connection.createStatement();
		String select = String.format(SQL_NEXT_LOGIN, StringChecker.escapeSQLString(event.getUsername()), StringChecker.escapeSQLString(event.getTimestamp()));
		ResultSet resultSet = stmt.executeQuery(select);
		Iterator<LoginEvent> itr = new LoginEventIterator(stmt, resultSet);
		if (itr.hasNext()) {
			nextEvent = itr.next();
		}
		return nextEvent;
	}

	private static <T> List<T> iterableToList(Iterable<T> itr) {
		List<T> list = new LinkedList<T>();
		for (T t : itr) {
			list.add(t);
		}
		return list;
	}

	private String generatelSQLPart(LoginEvent event, LoginEvent nextEvent) {
		StringBuilder sb = new StringBuilder();
		sb.append("OR (source=\"'");
		sb.append(StringChecker.escapeSQLString(event.getUsername()));
		sb.append("' AND timedate >= '");
		sb.append(StringChecker.escapeSQLString(event.getTimestamp()));
		sb.append("'");
		if (nextEvent != null) {
			sb.append(" AND timedate < '");
			sb.append(StringChecker.escapeSQLString(nextEvent.getTimestamp()));
			sb.append("'");
		}
		sb.append(")");
		return sb.toString();
	}

	private void analyse(String address, String timedate) throws SQLException {
		LoginEventIterator iterator = readLoginsFromAddress(address, timedate);
		List<LoginEvent> events = iterableToList(iterator);
		for (LoginEvent event : events) {
			LoginEvent nextEvent = getNextLoginEvent(event);
			System.out.println(generatelSQLPart(event, nextEvent));
		}
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
