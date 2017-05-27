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
 * validates that the given parameter is provided is short than a specified
 * maximum length.
 *
 * @author hendrik
 */
public class MaxLengthValidator implements AccountParameterValidator {
	private final String parameterValue;
	private final int maxLength;

	/**
	 * creates a new MaxLengthValidator.
	 *
	 * @param parameterValue
	 *            value to validate
	 * @param maxLength
	 *            maximum required length
	 */
	public MaxLengthValidator(final String parameterValue, final int maxLength) {
		this.parameterValue = parameterValue;
		this.maxLength = maxLength;
	}

	@Override
	public Result validate() {
		if (parameterValue.length() > maxLength) {
			return Result.FAILED_STRING_TOO_LONG;
		}

		return null;
	}

}
