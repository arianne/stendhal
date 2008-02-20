package games.stendhal.client.actions;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

public class AlterQuestAction implements SlashAction {

	public boolean execute(String[] params, String remainder) {
		if (params == null || params.length < getMinimumParameters()) {
			return false;
		}
		RPAction action = new RPAction();
		action.put("type", "alterquest");
		action.put("target", params[0]);
		action.put("name", params[1]);
		if (params.length > 2 && params[2] != null) {
			action.put("state", params[2]);
		}
		StendhalClient.get().send(action);
		return true;
	}

	public int getMaximumParameters() {
		return 3;
	}

	public int getMinimumParameters() {
		
		return 2;
	}

}
