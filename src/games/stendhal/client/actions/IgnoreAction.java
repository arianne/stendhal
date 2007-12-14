package games.stendhal.client.actions;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

/**
 * Add a player to the ignore list.
 */
class IgnoreAction implements SlashAction {

	/**
	 * Execute an ignore command.
	 * 
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 * 
	 * @return <code>true</code> if command was handled.
	 */
	public boolean execute(String[] params, String remainder) {
		String duration = params[1];
		RPAction action = new RPAction();

		action.put("type", "ignore");
		action.put("target", params[0]);

		if (duration != null) {
			/*
			 * Ignore "forever" values
			 */
			if (!duration.equals("*") || !duration.equals("-")) {
				/*
				 * Validate it's a number
				 */
				try {
					Integer.parseInt(duration);
				} catch (NumberFormatException ex) {
					return false;
				}

				action.put("duration", duration);
			}
		}

		if (remainder.length() != 0) {
			action.put("reason", remainder);
		}

		StendhalClient.get().send(action);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 * 
	 * @return The parameter count.
	 */
	public int getMaximumParameters() {
		return 2;
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
