package games.stendhal.client.actions;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

/**
 * Set and clear the happy message
 * 
 * @author madmetzger
 *
 */
public class HappyAction implements SlashAction {

	public boolean execute(String[] params, String remainder) {
		final RPAction action = new RPAction();

		action.put("type", "happy");

		if (remainder.length() != 0) {
			action.put("message", remainder);
		}

		StendhalClient.get().send(action);

		return true;
	}

	public int getMaximumParameters() {
		return 0;
	}

	public int getMinimumParameters() {
		return 0;
	}

}
