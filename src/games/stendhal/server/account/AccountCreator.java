package games.stendhal.server.account;

import java.sql.SQLException;

import marauroa.common.crypto.Hash;
import marauroa.common.game.AccountResult;
import marauroa.common.game.Result;
import marauroa.server.game.db.DatabaseFactory;
import marauroa.server.game.db.JDBCDatabase;
import marauroa.server.game.db.Transaction;

import org.apache.log4j.Logger;

/**
 * Creates a new account as requested by a client.
 */
public class AccountCreator {
	private static Logger logger = Logger.getLogger(AccountCreator.class);
	private String username;
	private String password;
	private String email;

	/**
	 * creates a new AccountCreator.
	 * 
	 * @param username
	 *            name of the user
	 * @param password
	 *            password for this account
	 * @param email
	 *            email contact
	 */
	public AccountCreator(String username, String password, String email) {
		this.username = username.trim();
		this.password = password.trim();
		this.email = email.trim();
	}

	/**
	 * tries to create this account.
	 * 
	 * @return AccountResult
	 */
	public AccountResult create() {
		Result result = validate();
		if (result != null) {
			return new AccountResult(result, username);
		}

		return insertIntoDatabase();
	}

	/**
	 * Checks the user provide parameters.
	 * 
	 * @return null in case everything is ok, a Resul in case some validator
	 *         failed
	 */
	private Result validate() {
		AccountCreationRules rules = new AccountCreationRules(username,
				password, email);
		ValidatorList validators = rules.getAllRules();
		Result result = validators.runValidators();
		return result;
	}

	/**
	 * tries to create the player in the database.
	 * 
	 * @return Result.OK_CREATED on success
	 */
	private AccountResult insertIntoDatabase() {
		JDBCDatabase database = (JDBCDatabase) DatabaseFactory.getDatabase();
		Transaction trans = database.getTransaction();
		try {
			trans.begin();

			if (database.hasPlayer(trans, username)) {
				logger.warn("Account already exist: " + username);
				return new AccountResult(Result.FAILED_PLAYER_EXISTS, username);
			}

			database.addPlayer(trans, username, Hash.hash(password), email);

			trans.commit();
			return new AccountResult(Result.OK_CREATED, username);
		} catch (SQLException e) {
			logger.warn("SQL exception while trying to create a new account", e);
			try {
				trans.rollback();
			} catch (SQLException rollbackException) {
				logger.error("Rollback failed: ", rollbackException);
			}
			return new AccountResult(Result.FAILED_EXCEPTION, username);
		}
	}
}
