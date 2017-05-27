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
 * validates that the given parameter is neither null nor the empty string.
 *
 * @author hendrik
 */
public class NotEmptyValidator implements AccountParameterValidator {
	private final String parameterValue;

	/**
	 * create a new NotEmptyValidator.
	 *
	 * @param parameterValue
	 *            value to validate
	 */
	public NotEmptyValidator(final String parameterValue) {
		this.parameterValue = parameterValue;
	}

	@Override
	public Result validate() {
		if (parameterValue == null) {
			return Result.FAILED_EMPTY_STRING;
		}

		if (parameterValue.length() == 0) {
			return Result.FAILED_EMPTY_STRING;
		}

		return null;
	}

}
