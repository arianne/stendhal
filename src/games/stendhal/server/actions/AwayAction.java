/*
 * @(#) src/games/stendhal/server/actions/AwayAction.java
 *
 * $Id$
 */

package games.stendhal.server.actions;

//
//

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Process /away commands.
 */
public class AwayAction implements ActionListener {


	/**
	 * Registers action.
	 */
	public static void register() {
		StendhalRPRuleProcessor.register("away", new AwayAction());
	}

	/**
	 * Handle an away action.
	 *
	 * @param	player		The player.
	 * @param	action		The action.
	 */
	protected void onAway(Player player, RPAction action) {


		if (action.has("message")) {
			player.put("away", action.get("message"));
		} else if (player.has("away")) {
			player.remove("away");
		}

		player.resetAwayReplies();
		player.notifyWorldAboutChanges();


	}

	/**
	 * Handle client action.
	 *
	 * @param	player		The player.
	 * @param	action		The action.
	 */
	public void onAction(Player player, RPAction action) {
		if (action.get("type").equals("away")) {
			onAway(player, action);
		}
	}
}
