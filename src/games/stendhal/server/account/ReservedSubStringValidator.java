package games.stendhal.server.account;

import marauroa.common.game.Result;

/**
 * validates that reserved names (like admin) are not used as substrings
 *
 * @author hendrik
 */
public class ReservedSubStringValidator implements AccountParameterValidator {
	private String parameterValue;

	/**
	 * creates a ReservedSubStringValidator
	 *
	 * @param parameterValue value to validate
	 */
	public ReservedSubStringValidator(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public Result validate() {
		if (parameterValue.toLowerCase().contains("admin")) {
			return Result.FAILED_INVALID_CHARACTER_USED;
		}

		return null;
	}
}
