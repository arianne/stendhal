/*
 * @(#) src/games/stendhal/server/actions/AwayAction.java
 *
 * $Id$
 */

package games.stendhal.server.actions;

//
//

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Process /away commands.
 */
public class AwayAction implements ActionListener {
	private static final String _AWAY = "away";

	/**
	 * Registers action.
	 */
	public static void register() {
		CommandCenter.register(_AWAY, new AwayAction());
	}

	/**
	 * Handle an away action.
	 *
	 * @param	player		The player.
	 * @param	action		The action.
	 */
	protected void onAway(Player player, RPAction action) {
		if (action.has(WellKnownActionConstants.MESSAGE)) {
			player.setAwayMessage(action.get(WellKnownActionConstants.MESSAGE));
		} else {
			player.setAwayMessage(null);
		}

		player.notifyWorldAboutChanges();
	}

	/**
	 * Handle client action.
	 *
	 * @param	player		The player.
	 * @param	action		The action.
	 */
	public void onAction(Player player, RPAction action) {
		if (action.get(WellKnownActionConstants.TYPE).equals(_AWAY)) {
			onAway(player, action);
		}
	}
}
