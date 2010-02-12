package games.stendhal.server.core.account;

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

	public Result validate() {
		if (parameterValue.toLowerCase().contains("admin")) {
			return Result.FAILED_RESERVED_NAME;
		}


		// name must not be equal to "gm". We do not use a substring filter
		// here, because these to letters may be part of normal names.
		// Since neither spaces (and other special characters) nor uppercase
		// letters are allowed, it should not be possible to "highlight" the
		// "GM" in any way within the name.
		if (parameterValue.toLowerCase().equals("gm")) {
			return Result.FAILED_RESERVED_NAME;
		}

		// the official server is hosted in Germany
		if (parameterValue.toLowerCase().contains("hitler")) {
			return Result.FAILED_RESERVED_NAME;
		}

		return null;
	}
}
