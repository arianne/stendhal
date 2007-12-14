package games.stendhal.client.actions;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

/**
 * Send a message to a player.
 */
class MessageAction implements SlashAction {

	private String lastPlayerTell;

	/**
	 * Execute a chat command.
	 * 
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 * 
	 * @return <code>true</code> if command was handled.
	 */
	public boolean execute(String[] params, String remainder) {
		lastPlayerTell = params[0];

		RPAction tell = new RPAction();

		tell.put("type", "tell");
		tell.put("target", lastPlayerTell);
		tell.put("text", remainder);

		StendhalClient.get().send(tell);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 1;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 1;
	}

	/**
	 * get the last player we have send something using /tell
	 * 
	 * @return player name
	 */
	String getLastPlayerTell() {
		return lastPlayerTell;
	}
}
