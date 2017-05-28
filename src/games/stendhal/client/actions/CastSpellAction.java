package games.stendhal.client.actions;

import static games.stendhal.common.constants.Actions.CASTSPELL;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;
/**
 * Simple action to cast a spell at a target (for testing purposes)
 * Usage:
 * 	[spell id] [target name or id]
 *
 * @author madmetzger
 */
public class CastSpellAction implements SlashAction {

	@Override
	public boolean execute(String[] params, String remainder) {
		final RPAction action = new RPAction();
		action.put("type", CASTSPELL);
		// make action capable of dealing with ids and names
		try {
			// normal case, objects should be addressed via id
			int targetId = Integer.parseInt(params[1]);
			action.put("target", "#"+targetId);

		} catch (NumberFormatException e) {
			// for testing purposes as addressing players via name is easier
			action.put("target", params[1]);
		}
		action.put("baseitem", params[0]);
		action.put("baseslot", "spells");
		ClientSingletonRepository.getClientFramework().send(action);
		return true;
	}

	@Override
	public int getMaximumParameters() {
		return 2;
	}

	@Override
	public int getMinimumParameters() {
		return 2;
	}

}
