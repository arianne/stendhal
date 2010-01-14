package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

/**
 * Teleport a player.
 */
class TeleportAction implements SlashAction {

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
	public boolean execute(final String[] params, final String remainder) {
		final RPAction teleport = new RPAction();

		teleport.put("type", "teleport");
		teleport.put("target", params[0]);
		teleport.put("zone", params[1]);
		teleport.put("x", params[2]);
		teleport.put("y", params[3]);

		ClientSingletonRepository.getClientFramework().send(teleport);

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
		return 4;
	}
}
