package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;
import static games.stendhal.common.constants.Actions.CASTSPELL;
/**
 * Simple action to cast a spell at a target (for testing purposes)
 * Usage:
 * 	[spell id] [target name]
 * 
 * @author madmetzger
 */
public class CastSpellAction implements SlashAction {

	public boolean execute(String[] params, String remainder) {
		final RPAction action = new RPAction();
		action.put("type", CASTSPELL);
		action.put("target", params[1]);
		action.put("baseitem", params[0]);
		action.put("baseslot", "spells");
		ClientSingletonRepository.getClientFramework().send(action);
		return true;
	}

	public int getMaximumParameters() {
		return 2;
	}

	public int getMinimumParameters() {
		return 2;
	}

}
