package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

/**
 * Alter an entity's attributes.
 */
class AlterCreatureAction implements SlashAction {

	/**
	 * Alters an entity's attributes.
	 * 
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 * 
	 * @return <code>true</code> if was handled.
	 */
	public boolean execute(final String[] params, final String remainder) {
		if ((params == null) || (params.length < getMinimumParameters())) {
			return false;
		}
		final RPAction alter = new RPAction();

		alter.put("type", "altercreature");
		alter.put("target", params[0]);
		alter.put("text", params[1]);
		ClientSingletonRepository.getClientFramework().send(alter);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 2;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 2;
	}
}
