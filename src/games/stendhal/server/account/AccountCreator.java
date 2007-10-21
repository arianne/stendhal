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
		validators.add(new NotEmptyValidator(username));
		validators.add(new MinLengthValidator(username, 4));
		validators.add(new MaxLengthValidator(username, 20));

		validators.add(new LowerCaseValidator(username));
		validators.add(new NameCharacterValidator(username));
		validators.add(new ReservedSubStringValidator(username));
	}

	private void setupValidatorsForPassword() {
		validators.add(new NotEmptyValidator(password));
		validators.add(new MinLengthValidator(password, 4));
		validators.add(new MaxLengthValidator(password, 100));
	}

	private void setupValidatorsForEMail() {
		validators.add(new NotEmptyValidator(email));
		validators.add(new MinLengthValidator(email, 6));
		validators.add(new MaxLengthValidator(email, 100));
	}

	private void setupAllValidators() {
		setupValidatorsForUsername();
		setupValidatorsForPassword();
		setupValidatorsForEMail();
	}

	private Result runValidators() {
		Result result = null;
		for (AccountParameterValidator validator : validators) {
			result = validator.validate();
			if (result != null) {
				break;
			}
		}
		return result;
	}

	/**
	 * tries to create this account
	 *
	 * @return AccountResult
	 */
	public AccountResult create() {
		setupAllValidators();

		Result result = runValidators();
		if (result != null) {
			return new AccountResult(result, username);
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
