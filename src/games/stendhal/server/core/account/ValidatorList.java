package games.stendhal.server.core.account;

import java.util.LinkedList;

import marauroa.common.game.Result;

/**
 * Manages a list of validators.
 * 
 * @author hendrik
 */
public class ValidatorList extends LinkedList<AccountParameterValidator> {
	private static final long serialVersionUID = 4267126954814325760L;

	/**
	 * Executes all validators until one fails or all are completed.
	 * 
	 * @return Result in case of an error, null in case of success
	 */
	public Result runValidators() {
		Result result = null;
		for (AccountParameterValidator validator : this) {
			result = validator.validate();
			if (result != null) {
				break;
			}
		}
		return result;
	}
}
