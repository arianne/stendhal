package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

public class AcceptChallengeAction implements SlashAction {

	@Override
	public boolean execute(String[] params, String remainder) {
		if(params == null) {
			return false;
		}
		RPAction action = new RPAction();
		action.put("type", "challenge");
		action.put("action", "accept");
		action.put("target", params[0]);
		ClientSingletonRepository.getClientFramework().send(action);

		return true;
	}

	@Override
	public int getMaximumParameters() {
		return 1;
	}

	@Override
	public int getMinimumParameters() {
		return 1;
	}

}
