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
	private AccountResult result;

	/**
	 * creates a new AccountCreator
	 *
	 * @param username name of the user
	 * @param password password for this account
	 * @param email email contact
	 */
	public AccountCreator(String username, String password, String email) {
		this.username = username.trim();
		this.password = password.trim();
		this.email = email.trim();
	}

	private void checkValidUsername() {
		/** TODO: Complete this. Should read the list from XML file */
		if (username.indexOf(' ') != -1) {
			result = new AccountResult(Result.FAILED_INVALID_CHARACTER_USED, username);
		}
		// TODO: Fix bug [ 1672627 ] 'admin' not allowed in username but GM_ and
		// _GM are
		// TODO: Refactor Invalid patterns for username should be stored in a
		// text file or XML file.
		if (username.toLowerCase().contains("admin")) {
			result = new AccountResult(Result.FAILED_INVALID_CHARACTER_USED, username);
		}

		if (username.length() == 0)  {
			result = new AccountResult(Result.FAILED_EMPTY_STRING, username);
		}

		// Ensure username is at least 4 characters length.
		if (username.length() < 4)  {
			result = new AccountResult(Result.FAILED_STRING_SIZE, username);
		}

		// only lower case usernames are allowed
		if (!username.toLowerCase().equals(username)) {
			result = new AccountResult(Result.FAILED_INVALID_CHARACTER_USED, username);
		}

		// only letters are allowed (and numbers :-/)
		for (int i = username.length() - 1; i >= 0; i--) {
			char chr = username.charAt(i);
			if ((chr < 'a' || chr > 'z') && (chr < '0' || chr > '9')) {
				result = new AccountResult(Result.FAILED_INVALID_CHARACTER_USED, username);
			}
		}
	}

	private void checkValidPassword() {
		if (username.length() == 0)  {
			result = new AccountResult(Result.FAILED_EMPTY_STRING, username);
		}

		if (password.length() < 4)  {
			result = new AccountResult(Result.FAILED_STRING_SIZE, username);
		}
	}

	private void checkValidEMail() {
		if (email.length() == 0)  {
			result = new AccountResult(Result.FAILED_EMPTY_STRING, username);
		}

		if (email.length() < 4)  {
			result = new AccountResult(Result.FAILED_STRING_SIZE, username);
		}
	}

	/**
	 * tries to create this account
	 *
	 * @return AccountResult
	 */
	public AccountResult create() {

		checkValidUsername();
		if (result != null) {
			return result;
		}

		checkValidPassword();
		if (result != null) {
			return result;
		}

		checkValidEMail();
		if (result != null) {
			return result;
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
