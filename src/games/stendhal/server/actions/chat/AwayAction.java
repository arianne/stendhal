/*
 * @(#) src/games/stendhal/server/actions/AwayAction.java
 *
 * $Id$
 */

package games.stendhal.server.actions.chat;

import static games.stendhal.server.actions.WellKnownActionConstants.MESSAGE;
import static games.stendhal.server.actions.WellKnownActionConstants.TYPE;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Process /away commands.
 */
public class AwayAction implements ActionListener {
	private static final String _AWAY = "away";

	/**
	 * Registers AwayAction with its trigger word "away".
	 */
	public static void register() {
		CommandCenter.register(_AWAY, new AwayAction());
	}

	/**
	 * changes away status depending on existence of MESSAGE in action.
	 * 
	 * If action contains MESSAGE, the away status is set else the away status
	 * is unset.
	 * 
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	public void onAction(final Player player, final RPAction action) {
		if (_AWAY.equals(action.get(TYPE))) {
			if (action.has(MESSAGE)) {
				player.setAwayMessage(action.get(MESSAGE));
			} else {
				player.setAwayMessage(null);
			}

			player.notifyWorldAboutChanges();
		}
	}
}
