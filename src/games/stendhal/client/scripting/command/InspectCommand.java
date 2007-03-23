package games.stendhal.client.scripting.command;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

/**
 * Inspect an entity.
 */
class InspectCommand implements SlashCommand {
	/**
	 * Execute a chat command.
	 *
	 * @param	params		The formal parameters.
	 * @param	remainder	Line content after parameters.
	 *
	 * @return	<code>true</code> if  was handled.
	 */
	public boolean execute(String [] params, String remainder) {
		RPAction action = new RPAction();

		action.put("type", "inspect");
		action.put("target", params[0]);

		StendhalClient.get().send(action);

		return true;
	}


	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMaximumParameters() {
		return 1;
	}


	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMinimumParameters() {
		return 1;
	}
}
