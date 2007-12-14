package games.stendhal.server.account;

import marauroa.common.game.Result;

/**
 * validates that the given parameter is neither null nor the empty string.
 * 
 * @author hendrik
 */
public class NotEmptyValidator implements AccountParameterValidator {
	private String parameterValue;

	/**
	 * create a new NotEmptyValidator.
	 * 
	 * @param parameterValue
	 *            value to validate
	 */
	public NotEmptyValidator(final String parameterValue) {
		this.parameterValue = parameterValue;
	}

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
