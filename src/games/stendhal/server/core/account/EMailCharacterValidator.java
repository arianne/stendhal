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

import marauroa.common.game.Result;

/**
 * validates the character used in the email address.
 *
 * @author hendrik
 */
public class EMailCharacterValidator implements AccountParameterValidator {
	private final String parameterValue;
	private char[] badCharacters = new char[]{'\"', '\'', '\\', ' ', ';', '<', '>'};

	/**
	 * creates a EMailValidator.
	 *
	 * @param parameterValue
	 *            value to validate
	 */
	public EMailCharacterValidator(final String parameterValue) {
		this.parameterValue = parameterValue;
	}

	@Override
	public Result validate() {
		// only letters are allowed
		for (int i = parameterValue.length() - 1; i >= 0; i--) {
			final char chr = parameterValue.charAt(i);
			for (char bad : badCharacters) {
				if (chr == bad) {
					return Result.FAILED_INVALID_CHARACTER_USED;
				}
			}
		}

		return null;
	}
}
