package games.stendhal.client.actions;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

/**
 * Alter an entity's attributes.
 */
class AlterAction implements SlashAction {

	/**
	 * Executes a chat command.
	 * 
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 * 
	 * @return <code>true</code> if was handled.
	 */
	public boolean execute(String[] params, String remainder) {
		if (hasInvalidArguments(params, remainder)) {
			return false;
		}
		RPAction alter = new RPAction();

		alter.put("type", "alter");
		alter.put("target", params[0]);
		alter.put("stat", params[1]);
		alter.put("mode", params[2]);
		alter.put("value", remainder);
		StendhalClient.get().send(alter);

		return true;
	}

	/**
	 * Checks whether the arguments passed are valid for execution.
	 * 
	 * @param params
	 * @param remainder
	 * @return
	 */
	private boolean hasInvalidArguments(String[] params, String remainder) {
		return params == null || remainder == null || params.length < getMinimumParameters();
	}

	/**
	 * Gets the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 3;
	}

	/**
	 * Gets the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 3;
	}
}
