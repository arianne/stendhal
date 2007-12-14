package games.stendhal.client.actions;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.User;
import marauroa.common.game.RPAction;

/**
 * Summon an entity.
 */
class SummonAction implements SlashAction {

	/**
	 * Execute a chat command.
	 * 
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 * 
	 * @return <code>true</code> if was handled.
	 */
	public boolean execute(String[] params, String remainder) {
		RPAction summon = new RPAction();

		summon.put("type", "summon");
		summon.put("creature", params[0]);

		if (params[2] != null) {
			summon.put("x", params[1]);
			summon.put("y", params[2]);
		} else if (params[1] != null) {
			return false;
		} else {
			summon.put("x", (int) User.get().getX());
			summon.put("y", (int) User.get().getY());
		}
		StendhalClient.get().send(summon);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 3;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 1;
	}
}
