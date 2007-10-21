package games.stendhal.server.account;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

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
	private List<AccountParameterValidator> validators = new LinkedList<AccountParameterValidator>();

	private String username;
	private String password;
	private String email;

	private AccountResult accountResult;

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

	private void setupValidatorsForUsername() {
		validators.add(new MinLengthValidator(username, 4));
	}

	private void setupValidatorsForPassword() {
		validators.add(new MinLengthValidator(password, 4));
	}

	private void setupValidatorsForEMail() {
		validators.add(new MinLengthValidator(email, 6));
	}

	private void setupAllValidators() {
		setupValidatorsForUsername();
		setupValidatorsForPassword();
		setupValidatorsForEMail();
	}

	private void checkValidUsername() {
		/** TODO: Complete this. Should read the list from XML file */
		if (username.indexOf(' ') != -1) {
			accountResult = new AccountResult(Result.FAILED_INVALID_CHARACTER_USED, username);
		}
		// TODO: Fix bug [ 1672627 ] 'admin' not allowed in username but GM_ and
		// _GM are
		// TODO: Refactor Invalid patterns for username should be stored in a
		// text file or XML file.
		if (username.toLowerCase().contains("admin")) {
			accountResult = new AccountResult(Result.FAILED_INVALID_CHARACTER_USED, username);
		}

		// only lower case usernames are allowed
		if (!username.toLowerCase().equals(username)) {
			accountResult = new AccountResult(Result.FAILED_INVALID_CHARACTER_USED, username);
		}

		// only letters are allowed (and numbers :-/)
		for (int i = username.length() - 1; i >= 0; i--) {
			char chr = username.charAt(i);
			if ((chr < 'a' || chr > 'z') && (chr < '0' || chr > '9')) {
				accountResult = new AccountResult(Result.FAILED_INVALID_CHARACTER_USED, username);
			}
		}
	}

	private void runValidators() {
		Result result = null;
		for (AccountParameterValidator validator : validators) {
			result = validator.validate();
			if (result != null) {
				this.accountResult = new AccountResult(result, username);
				break;
			}
		}
	}

	/**
	 * tries to create this account
	 *
	 * @return AccountResult
	 */
	public AccountResult create() {
		setupAllValidators();

		checkValidUsername();
		if (accountResult != null) {
			return accountResult;
		}

		runValidators();
		if (accountResult != null) {
			return accountResult;
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
