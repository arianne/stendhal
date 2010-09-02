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

	public Result validate() {
		if (!parameterValue.toLowerCase(Locale.ENGLISH).equals(parameterValue)) {
			return Result.FAILED_INVALID_CHARACTER_USED;
		}

		return null;
	}

}
