/*
 * CreateGuildAction.java
 *@author timothyb89
 *Puts player in a guild.
 */

package games.stendhal.server.actions;

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Process guild actions from client. (Remove)
 */
public class RemoveFromGuildAction implements ActionListener {

	private static final String _REMOVE_FROM_GUILD = "removeFromGuild";
	private static final String _TYPE = "type";
	private static final String _GUILDREMOVE = "guildremove";

	/**
	 * Registers action.
	 */
	public static void register() {
		CommandCenter.register(_GUILDREMOVE, new RemoveFromGuildAction());
	}

	/**
	 * Handle the action.
	 * 
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	protected void removeFromGuild(Player player, RPAction action) {

		if (player.get("guild") != null) {
			player.remove("guild"); // resets guild
			player.remove("description"); // resets description
			player.sendPrivateText("You have been removed from your old guild.");
		} else {
			player.sendPrivateText("You are not in a guild!");
		}

		player.update();
		player.notifyWorldAboutChanges();

	}

	/**
	 * Handle client action.
	 * 
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	public void onAction(Player player, RPAction action) {
		if (action.get(_TYPE).equals(_REMOVE_FROM_GUILD)) {
			removeFromGuild(player, action);
		}
	}
}
