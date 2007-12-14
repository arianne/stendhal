package games.stendhal.client.actions;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

/**
 * Summon an item (presumably) into an entity's slot.
 */
class SummonAtAction implements SlashAction {

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

		summon.put("type", "summonat");
		summon.put("target", params[0]);
		summon.put("slot", params[1]);
		summon.put("item", params[2]);

		if (params[3] != null) {
			summon.put("amount", params[3]);
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
		return 4;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 3;
	}
}
