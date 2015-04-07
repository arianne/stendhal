package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.common.StringHelper;
import marauroa.common.game.RPAction;

/**
 * Stops player's movement.
 * 
 * @author
 *         AntumDeluge
 */
public class StopWalkAction implements SlashAction {
	/**
	 * Execute a chat command.
	 * 
	 * @param params
	 *        The formal parameters.
	 * @param remainder
	 *        Line content after parameters.
	 * @return
	 *         <code>true</code> if command was handled.
	 */
	@Override
	public boolean execute(String[] params, String remainder) {
		final RPAction stop = new RPAction();

		stop.put("type", "walk");
		stop.put("target", StringHelper.unquote(remainder));
		stop.put("mode", "stop");

		ClientSingletonRepository.getClientFramework().send(stop);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return
	 *         Parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 0;
	}

	/**
	 * Get the minimum number of formal parameters.
	 * 
	 * @return
	 *         Parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 0;
	}
}
