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
 * validates that the given parameter is provided has a minimum length.
 *
 * @author hendrik
 */
public class MinLengthValidator implements AccountParameterValidator {
	private final String parameterValue;
	private final int minLength;

	/**
	 * create a new MinLengthValidator.
	 *
	 * @param parameterValue
	 *            value to validate
	 * @param minLength
	 *            minimum required length
	 */
	public MinLengthValidator(final String parameterValue, final int minLength) {
		this.parameterValue = parameterValue;
		this.minLength = minLength;
	}

	@Override
	public Result validate() {
		if (parameterValue.length() < minLength) {
			return Result.FAILED_STRING_TOO_SHORT;
		}

		return null;
	}

}
