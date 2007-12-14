package games.stendhal.server.account;

import marauroa.common.game.Result;

/**
 * validates that the given parameter is provided has a minimum length.
 * 
 * @author hendrik
 */
public class MinLengthValidator implements AccountParameterValidator {
	private String parameterValue;
	private int minLength;

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

	public Result validate() {
		if (parameterValue.length() < minLength) {
			return Result.FAILED_STRING_TOO_SHORT;
		}

		return null;
	}

}
