/*
 * CreateGuildAction.java
 *@author timothyb89
 *Puts player in a guild.
 */

package games.stendhal.server.actions;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;

/**
 * Process /joinguild commands.
 */
public class CreateGuildAction implements ActionListener {

	/**
	 * Logger.
	 */
	private static final Logger logger = Log4J.getLogger(CreateGuildAction.class);

	/**
	 * Registers action.
	 */
	public static void register() {
		StendhalRPRuleProcessor.register("joinguild", new CreateGuildAction());
	}

	/**
	 * Handle an away action.
	 *
	 * @param	player		The player.
	 * @param	action		The action.
	 */
	protected void joinGuild(Player player, RPAction action) {
		Log4J.startMethod(logger, "joinguild");
                String guild = null;
                
                //now we see if the player is in a guild. If not, put them in the requested one.
		if (player.get("guild") != null) {
		    player.sendPrivateText("You are already in a guild! Please leave your old one and try again.");
		} else {
		    player.put("guild", action.get("value"));
		    String description = "You see " + player.getName() + ".\n" + player.getName() + " is level " + player.getLevel() + " and is a member of the " + action.get("value") + " guild.";
		    player.setDescription(description);
		}
		// done!
		
		//TODO: Add list of guilds and make them unique.
		
                
		player.update();
		player.notifyWorldAboutChanges();

		Log4J.finishMethod(logger, "joinguild");
	}

	/**
	 * Handle client action.
	 *
	 * @param	player		The player.
	 * @param	action		The action.
	 */
	public void onAction(Player player, RPAction action) {
		if (action.get("type").equals("createguild")) {
			joinGuild(player, action);
		}
	}
}
