/*
 * @(#) src/games/stendhal/server/actions/AwayAction.java
 *
 * $Id$
 */

package games.stendhal.server.actions;

//
//

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;

/**
 * Process /away commands.
 */
public class AwayAction extends ActionListener {
	/**
	 * Logger.
	 */
	private static final Logger logger = Log4J.getLogger(AwayAction.class);


	//
	// AwayAction
	//

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
		Log4J.startMethod(logger, "away");

		if (action.has("message")) {
			player.put("away", action.get("message"));
		} else {
			player.remove("away");
		}

		player.notifyWorldAboutChanges();

		Log4J.finishMethod(logger, "away");
	}


	//
	// ActionListener
	//

	/**
	 * Handle client action.
	 *
	 * @param	player		The player.
	 * @param	action		The action.
	 */
	@Override
	public void onAction(Player player, RPAction action) {
		if (action.get("type").equals("away")) {
			onAway(player, action);
		}
	}
}
