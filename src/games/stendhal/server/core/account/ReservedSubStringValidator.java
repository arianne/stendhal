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
 * validates that reserved names (like admin) are not used as substrings.
 *
 * @author hendrik
 */
public class ReservedSubStringValidator implements AccountParameterValidator {
	private final String parameterValue;

	/**
	 * creates a ReservedSubStringValidator.
	 *
	 * @param parameterValue
	 *            value to validate
	 */
	public ReservedSubStringValidator(final String parameterValue) {
		this.parameterValue = parameterValue;
	}

	@Override
	public Result validate() {
		if (parameterValue.toLowerCase(Locale.ENGLISH).contains("admin")) {
			return Result.FAILED_RESERVED_NAME;
		}
		if (parameterValue.toLowerCase(Locale.ENGLISH).contains("arianne")) {
			return Result.FAILED_RESERVED_NAME;
		}
		if (parameterValue.toLowerCase(Locale.ENGLISH).equals("help")) {
			return Result.FAILED_RESERVED_NAME;
		}
		if (parameterValue.toLowerCase(Locale.ENGLISH).contains("marauroa")) {
			return Result.FAILED_RESERVED_NAME;
		}
		if (parameterValue.toLowerCase(Locale.ENGLISH).equals("null")) {
			return Result.FAILED_RESERVED_NAME;
		}
		if (parameterValue.toLowerCase(Locale.ENGLISH).contains("support")) {
			return Result.FAILED_RESERVED_NAME;
		}
		if (parameterValue.toLowerCase(Locale.ENGLISH).equals("server")) {
			return Result.FAILED_RESERVED_NAME;
		}
		if (parameterValue.toLowerCase(Locale.ENGLISH).contains("stendhal")) {
			return Result.FAILED_RESERVED_NAME;
		}

		// name must not be equal to "gm". We do not use a substring filter
		// here, because these to letters may be part of normal names.
		// Since neither spaces (and other special characters) nor uppercase
		// letters are allowed, it should not be possible to "highlight" the
		// "GM" in any way within the name.
		if (parameterValue.toLowerCase(Locale.ENGLISH).equals("gm")) {
			return Result.FAILED_RESERVED_NAME;
		}

		// the official server is hosted in Germany
		if (parameterValue.toLowerCase(Locale.ENGLISH).contains("hitler")) {
			return Result.FAILED_RESERVED_NAME;
		}

		return null;
	}
}
