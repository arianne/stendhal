package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
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
	public boolean execute(final String[] params, final String remainder) {
		lastPlayerTell = params[0];

		final RPAction tell = new RPAction();

		tell.put("type", "tell");
		tell.put("target", lastPlayerTell);
		tell.put("text", remainder);

		ClientSingletonRepository.getClientFramework().send(tell);

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
	 * Gets the last player we have sent something using /tell.
	 * 
	 * @return player name
	 */
	String getLastPlayerTell() {
		return lastPlayerTell;
	}
}
