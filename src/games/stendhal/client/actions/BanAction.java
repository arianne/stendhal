package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

public class BanAction implements SlashAction {

public boolean execute(final String[] params, final String remainder) {
			final RPAction action = new RPAction();

			action.put("type", "ban");
			action.put("target", params[0]);
			if (params.length > 1) {
				if (params[1] != null) {
					action.put("reason", params[1] + " " + remainder);
				}
			}

			ClientSingletonRepository.getClientFramework().send(action);

			return true;
		}

	public int getMaximumParameters() {
		return 2;
	}

	public int getMinimumParameters() {
		return 1;
	}

}
