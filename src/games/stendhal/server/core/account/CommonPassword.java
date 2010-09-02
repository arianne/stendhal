package games.stendhal.server.core.account;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import marauroa.common.game.Result;

/**
 * checks the password against a list of common passwords
 *
 * @author hendrik
 */
public class CommonPassword implements AccountParameterValidator {
	private List<String> commonPasswords = Arrays.asList(
			"stendhal", "stendhal1", 
			"password", "password1", 
			"passwort", "passwort1", 
			"arianne", "marauroa",
			"123123", "123456", "12345678", "1234567890",
			"jesus", "love", "game", "letmein", 
			"qwerty", "qwertz",	"monkey", "test", "master", "killer",
			"abc123", "fuckyou"
		);

	private String parameterValue;

	public CommonPassword(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public Result validate() {
		if (commonPasswords.contains(parameterValue.toLowerCase(Locale.ENGLISH))) {
			return Result.FAILED_PASSWORD_TO_WEAK;
		}
		return null;
	}

}
