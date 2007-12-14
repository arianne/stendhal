package games.stendhal.server.account;

import marauroa.common.game.Result;

/**
 * validates the character used for the character name.
 * 
 * @author hendrik
 */
public class NameCharacterValidator implements AccountParameterValidator {
	private String parameterValue;

	/**
	 * creates a NameCharacterValidator.
	 * 
	 * @param parameterValue
	 *            value to validate
	 */
	public NameCharacterValidator(final String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public Result validate() {
		// only letters are allowed (and numbers :-/)
		for (int i = parameterValue.length() - 1; i >= 0; i--) {
			char chr = parameterValue.charAt(i);
			if ((chr < 'a' || chr > 'z') && (chr < '0' || chr > '9')) {
				return Result.FAILED_INVALID_CHARACTER_USED;
			}
		}

		// at lest the first character must be a letter
		char chr = parameterValue.charAt(0);
		if ((chr < 'a' || chr > 'z')) {
			return Result.FAILED_INVALID_CHARACTER_USED;
		}

		return null;
	}
}
