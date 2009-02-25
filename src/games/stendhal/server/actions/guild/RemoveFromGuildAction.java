/*
 * CreateGuildAction.java
 *@author timothyb89
 *Puts player in a guild.
 */

package games.stendhal.server.actions.guild;

import static games.stendhal.common.constants.Actions.GUILDREMOVE;
import static games.stendhal.common.constants.Actions.REMOVE_FROM_GUILD;
import static games.stendhal.common.constants.Actions.TYPE;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;
/**
 * Process guild actions from client. (Remove)
 */
public class RemoveFromGuildAction implements ActionListener {

	
	/**
	 * Registers action.
	 */
	public static void register() {
		CommandCenter.register(GUILDREMOVE, new RemoveFromGuildAction());
	}

	/**
	 * Handle the action.
	 * 
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	protected void removeFromGuild(final Player player, final RPAction action) {

		if (player.get("guild") != null) {
			player.remove("guild"); 
			player.remove("description"); 
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
	public void onAction(final Player player, final RPAction action) {
		if (action.get(TYPE).equals(REMOVE_FROM_GUILD)) {
			removeFromGuild(player, action);
		}
	}
}
