package games.stendhal.client.actions;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

/**
 * Send a message to the last player messaged.
 */
class RemessageAction implements SlashAction {
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
		MessageAction messageCommand = (MessageAction) SlashActionRepository.get("msg");

		if (messageCommand == null
				|| messageCommand.getLastPlayerTell() == null) {
			return false;
		}

		RPAction tell = new RPAction();

		tell.put("type", "tell");
		tell.put("target", messageCommand.getLastPlayerTell());
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
		return 0;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMinimumParameters() {
		return 0;
	}
}
