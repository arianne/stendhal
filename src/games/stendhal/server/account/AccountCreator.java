package games.stendhal.server.account;

import java.sql.SQLException;

import marauroa.common.crypto.Hash;
import marauroa.common.game.AccountResult;
import marauroa.common.game.Result;
import marauroa.server.game.db.DatabaseFactory;
import marauroa.server.game.db.JDBCDatabase;
import marauroa.server.game.db.Transaction;
import marauroa.test.TestHelper;

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
	 * creates a new AccountCreator
	 *
	 * @param username name of the user
	 * @param password password for this account
	 * @param email email contact
	 */
	public AccountCreator(String username, String password, String email) {
		this.username = username;
		this.password = password;
		this.email = email;
	}

	private boolean isValidUsername(String username) {
		/** TODO: Complete this. Should read the list from XML file */
		if (username.indexOf(' ') != -1) {
			return false;
		}
		// TODO: Fix bug [ 1672627 ] 'admin' not allowed in username but GM_ and
		// _GM are
		if (username.toLowerCase().contains("admin")) {
			return false;
		}
		
		// Ensure username is at least 4 characters length.
		if( username.length()<4)  {
			return false;
		}
		
		return true;
	}

	/**
	 * tries to create this account
	 *
	 * @return AccountResult
	 */
	public AccountResult create() {
		/*
		 * TODO: Refactor Invalid patterns for username should be stored in a
		 * text file or XML file.
		 */
		if (!isValidUsername(username)) {
			return new AccountResult(Result.FAILED_EXCEPTION, username);
		}

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
			try {
				trans.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			TestHelper.fail();
			return new AccountResult(Result.FAILED_EXCEPTION, username);
		}
	}


}
