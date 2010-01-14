package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

public class AdminNoteAction implements SlashAction {

	public boolean execute(final String[] params, final String remainder) {
		final RPAction action = new RPAction();

		action.put("type", "adminnote");
		action.put("target", params[0]);
		action.put("note", remainder);

		ClientSingletonRepository.getClientFramework().send(action);

		return true;
	}

	public int getMaximumParameters() {
		return 1;
	}

	public int getMinimumParameters() {
		return 1;
	}

}
