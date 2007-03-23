package games.stendhal.client.scripting.command;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

/**
 * Send a player to jail.
 */
class JailCommand implements SlashCommand {
	/**
	 * Execute a chat command.
	 *
	 * @param	params		The formal parameters.
	 * @param	remainder	Line content after parameters.
	 *
	 * @return	<code>true</code> if  was handled.
	 */
	public boolean execute(String[] params, String remainder) {
		/*
		 * Reason required
		 */
		if(remainder.length() == 0) {
			return false;
		}


		RPAction add = new RPAction();

		add.put("type", "jail");
		add.put("target", params[0]);
		add.put("minutes", params[1]);
		add.put("reason", remainder);

		StendhalClient.get().send(add);

		return true;
	}


	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMaximumParameters() {
		return 2;
	}


	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return	The parameter count.
	 */
	public int getMinimumParameters() {
		return 2;
	}
}
