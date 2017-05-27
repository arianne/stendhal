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

import java.util.Locale;

import marauroa.common.game.Result;

/**
 * checks that only lower case letters are used.
 *
 * @author hendrik
 */
public class LowerCaseValidator implements AccountParameterValidator {
	private final String parameterValue;

	/**
	 * creates a LowerCaseValidator.
	 *
	 * @param parameterValue
	 *            value to validate
	 */
	public LowerCaseValidator(final String parameterValue) {
		this.parameterValue = parameterValue;
	}

	@Override
	public Result validate() {
		if (!parameterValue.toLowerCase(Locale.ENGLISH).equals(parameterValue)) {
			return Result.FAILED_INVALID_CHARACTER_USED;
		}

		return null;
	}

}
