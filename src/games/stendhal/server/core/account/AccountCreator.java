/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.account;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import marauroa.common.crypto.Hash;
import marauroa.common.game.AccountResult;
import marauroa.common.game.Result;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;

/**
 * Creates a new account as requested by a client.
 */
public class AccountCreator {
	private static Logger logger = Logger.getLogger(AccountCreator.class);
	private final String username;
	private final String password;
	private final String email;

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
	public AccountCreator(final String username, final String password, final String email) {
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
		final Result result = validate();
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
		final AccountCreationRules rules = new AccountCreationRules(username,
				password, email);
		final ValidatorList validators = rules.getAllRules();
		final Result result = validators.runValidators();
		return result;
	}

	/**
	 * tries to create the player in the database.
	 *
	 * @return Result.OK_CREATED on success
	 */
	private AccountResult insertIntoDatabase() {
		final TransactionPool transactionPool = SingletonRepository.getTransactionPool();
		final DBTransaction transaction = transactionPool.beginWork();
		final AccountDAO accountDAO = DAORegister.get().get(AccountDAO.class);

		try {
			if (accountDAO.hasPlayer(transaction, username)) {
				logger.warn("Account already exist: " + username);
				transactionPool.commit(transaction);
				return new AccountResult(Result.FAILED_PLAYER_EXISTS, username);
			}

			accountDAO.addPlayer(transaction, username, Hash.hash(password), email, new Timestamp(new Date().getTime()));

			transactionPool.commit(transaction);
			return new AccountResult(Result.OK_CREATED, username);
		} catch (final SQLException e) {
			logger.warn("SQL exception while trying to create a new account", e);
			transactionPool.rollback(transaction);
			return new AccountResult(Result.FAILED_EXCEPTION, username);
		}
	}
}
