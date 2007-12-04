/*
 * CreateGuildAction.java
 *@author timothyb89
 *Puts player in a guild.
 */

package games.stendhal.server.actions;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Process /joinguild commands.
 */
public class CreateGuildAction implements ActionListener {


	/**
	 * Registers action.
	 */
	public static void register() {
		CommandCentre.register("joinguild", new CreateGuildAction());
	}

	/**
	 * Handle an away action.
	 *
	 * @param	player		The player.
	 * @param	action		The action.
	 */
	protected void joinGuild(Player player, RPAction action) {


                //now we see if the player is in a guild. If not, put them in the requested one.
		if (player.get("guild") != null) {
		    player.sendPrivateText("You are already in a guild! Please leave your old one and try again.");
		} else {
		    player.put("guild", action.get("value"));
		    String description = "You see " + player.getTitle() + ".\n" + player.getTitle() + " is level " + player.getLevel() + " and is a member of the " + action.get("value") + " guild.";
		    player.setDescription(description);
		}
		// done!

		//TODO: Add list of guilds and make them unique.


		player.update();
		player.notifyWorldAboutChanges();


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
