package games.stendhal.tools.loganalyser.login;

import games.stendhal.tools.loganalyser.util.ResultSetIterator;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;


/**
 * Iterates over the log entries returned by a database query.
 *
 * @author hendrik
 */
public class LoginEventIterator extends ResultSetIterator<LoginEvent> {
	private static Logger logger = Logger.getLogger(LoginEventIterator.class);

	/**
	 * Creates a new LogEntryIterator.
	 *
	 * @param statement statement
	 * @param resultSet resultSet
	 */
	public LoginEventIterator(Statement statement, ResultSet resultSet) {
	    super(statement, resultSet);
    }

	@Override
    protected LoginEvent createObject() {
		try {
			return new LoginEvent(
				resultSet.getString("address"),
				resultSet.getString("timedate"),
	    		resultSet.getString("username"));
		} catch (SQLException e) {
			logger.error(e, e);
			return null;
		}
    }

}
