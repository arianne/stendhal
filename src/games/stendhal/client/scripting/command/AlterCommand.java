package games.stendhal.client.scripting.command;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

/**
 * Alter an entity's attributes.
 */
class AlterCommand implements SlashCommand {
	/**
	 * Execute a chat command.
	 *
	 * @param	params		The formal parameters.
	 * @param	remainder	Line content after parameters.
	 *
	 * @return	<code>true</code> if  was handled.
	 */
	public boolean execute(String [] params, String remainder) {
		RPAction alter = new RPAction();

		alter.put("type", "alter");
		alter.put("target", params[0]);
		alter.put("stat", params[1]);
		alter.put("mode", params[2]);
		alter.put("value", params[3]);
		StendhalClient.get().send(alter);

		return true;
	}


	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMaximumParameters() {
		return 4;
	}


	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMinimumParameters() {
		return 4;
	}
}
