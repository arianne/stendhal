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

import org.apache.log4j.Logger;

import marauroa.common.game.Result;

/**
 * checks that the password is not closly related to the username.
 *
 * @author timothyb89
 */
public class PasswordDiffersFromUsernameValidator implements
		AccountParameterValidator {
	private static Logger logger = Logger.getLogger(PasswordDiffersFromUsernameValidator.class);

	private final String username;
	private final String password;

	/**
	 * Creates a new PasswordDiffersFromUsernameValidator validator.
	 *
	 * @param username
	 *            name of user
	 * @param password
	 *            password
	 */
	public PasswordDiffersFromUsernameValidator(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	@Override
	public Result validate() {
		// check for username
		boolean hasUsername = false;
		if (password.contains(username)) {
			hasUsername = true;
		}

		if (!hasUsername) {
			// now we'll do some more checks to see if the password
			// contains more than three letters of the username
			logger.debug("Checking if password contains a derivative of the username, trimming from the back...");
			final int min_user_length = 3;
			for (int i = 1; i < username.length(); i++) {
				final String subuser = username.substring(0, username.length() - i);
				logger.debug("\tchecking for \"" + subuser + "\"...");
				if (subuser.length() <= min_user_length) {
					break;
				}

				if (password.contains(subuser)) {
					hasUsername = true;
					logger.debug("Password contians username!");
					break;
				}
			}

			if (!hasUsername) {
				// now from the end of the password..
				logger.debug("Checking if password contains a derivative of the username, trimming from the front...");
				for (int i = 0; i < username.length(); i++) {
					final String subuser = username.substring(i);
					logger.debug("\tchecking for \"" + subuser + "\"...");
					if (subuser.length() <= min_user_length) {
						break;
					}
					if (password.contains(subuser)) {
						hasUsername = true;
						logger.debug("Password contains username!");
						break;
					}
				}
			}
		}

		if (hasUsername) {
			return Result.FAILED_PASSWORD_TOO_CLOSE_TO_USERNAME;
		}

		return null;
	}

}
