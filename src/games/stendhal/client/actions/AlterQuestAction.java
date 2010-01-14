package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

class AlterQuestAction implements SlashAction {

	public boolean execute(final String[] params, final String remainder) {
		if ((params == null) || (params.length < getMinimumParameters())) {
			return false;
		}
		final RPAction action = new RPAction();
		action.put("type", "alterquest");
		action.put("target", params[0]);
		action.put("name", params[1]);
		if ((params.length > 2) && (params[2] != null)) {
			action.put("state", params[2]);
		}
		ClientSingletonRepository.getClientFramework().send(action);
		return true;
	}

	public int getMaximumParameters() {
		return 3;
	}

	public int getMinimumParameters() {
		
		return 2;
	}

}
