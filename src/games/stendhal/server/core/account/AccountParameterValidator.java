package games.stendhal.server.core.account;

import marauroa.common.game.Result;

/**
 * validates a parameter used during account creation.
 * 
 * @author hendrik
 */
public interface AccountParameterValidator {

	/**
	 * validates a parameter provided for account creation.
	 * 
	 * @return <code>null</code> in case the parameter is valid, or an error
	 *         otherwise
	 */
	Result validate();
}
