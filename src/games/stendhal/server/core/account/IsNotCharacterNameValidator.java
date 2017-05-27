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
import marauroa.server.game.db.CharacterDAO;
import marauroa.server.game.db.DAORegister;

/**
 * validates the new account name is not a character name
 *
 * @author kymara
 */
public class IsNotCharacterNameValidator implements AccountParameterValidator {

	private static Logger logger = Logger.getLogger(IsNotCharacterNameValidator.class);


	private final String username;
	/**
	 * creates an IsNotCharacterNameValidator.
	 *
     * @param username
	 *             account username of character creator
	 */
	public IsNotCharacterNameValidator(final String username) {
		this.username = username;
	}

	@Override
	public Result validate() {
		 try {
			 if(DAORegister.get().get(CharacterDAO.class).getAccountName(username) != null) {
				 return Result.FAILED_CHARACTER_EXISTS;
			 }
		} catch (SQLException e) {
			logger.error("Error while trying to validate username", e);
			return Result.FAILED_EXCEPTION;
		}
		 return null;
	}
}
