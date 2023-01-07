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

import org.apache.log4j.Logger;

import marauroa.common.game.Result;
import marauroa.server.db.DBTransaction;
import marauroa.server.db.TransactionPool;
import marauroa.server.game.db.AccountDAO;
import marauroa.server.game.db.DAORegister;

/**
 * validates the new character name is not an account name, unless player owns that account
 *
 * @author kymara
 */
public class IsNotOtherAccountNameValidator implements AccountParameterValidator {

	private static Logger logger = Logger.getLogger(IsNotOtherAccountNameValidator.class);

	private final String charname;
	private final String username;
	/**
	 * creates an IsNotOtherAccountNameValidator.
	 *
	 * @param charname
	 *            value to validate
     * @param username
	 *             account username of character creator
	 */
	public IsNotOtherAccountNameValidator(final String charname, final String username) {
		this.charname = charname;
		this.username = username;
	}

	@Override
	public Result validate() {
		if (charname.equals(username)) {
			return null;
		}
		final DBTransaction transaction = TransactionPool.get().beginWork();
		try {
			if(DAORegister.get().get(AccountDAO.class).hasPlayer(transaction, charname)) {
				return Result.FAILED_PLAYER_EXISTS;
			 }
		} catch (SQLException e) {
			logger.error("Error while trying to validate character name", e);
			return Result.FAILED_EXCEPTION;
		} finally {
			try {
				TransactionPool.get().commit(transaction);
			} catch (final SQLException e) {
				logger.error(e);
			}
		}
		return null;
	}
}
