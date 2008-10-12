package games.stendhal.server.actions.chat;

import static games.stendhal.server.actions.WellKnownActionConstants.MESSAGE;
import static games.stendhal.server.actions.WellKnownActionConstants.TYPE;
import marauroa.common.game.RPAction;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;

public class HappyAction implements ActionListener  {
	
	private static final String _HAPPY = "happy";
	
	/**
	 * Registers HappyAction with its trigger word "happy".
	 */
	public static void register() {
		CommandCenter.register(_HAPPY, new HappyAction());
	}

	public void onAction(final Player player, final RPAction action) {
		if (_HAPPY.equals(action.get(TYPE))) {
			if (action.has(MESSAGE)) {
				player.setHappyMessage(action.get(MESSAGE));
			} else {
				player.setHappyMessage(null);
			}

			player.notifyWorldAboutChanges();
		}
	}

}
