/*
 * CreateGuildAction.java
 *@author timothyb89
 *Puts player in a guild.
 */

package games.stendhal.server.actions.guild;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Process /joinguild commands.
 */
public class CreateGuildAction implements ActionListener {

	private static final String _CREATEGUILD = "createguild";
	private static final String TYPE = "type";
	private static final String _VALUE = "value";
	private static final String _ATTR_GUILD = "guild";

	/**
	 * Registers action.
	 */
	public static void register() {
		CommandCenter.register("joinguild", new CreateGuildAction());
	}

	/**
	 * Handle an away action.
	 * 
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	protected void joinGuild(Player player, RPAction action) {

		// now we see if the player is in a guild. If not, put them in the
		// requested one.
		if (player.get(_ATTR_GUILD) != null) {
			player.sendPrivateText("You are already in a guild! Please leave your old one and try again.");
		} else {
			player.put(_ATTR_GUILD, action.get(_VALUE));
			String description = "You see " + player.getTitle() + ".\n"
					+ player.getTitle() + " is level " + player.getLevel()
					+ " and is a member of the " + action.get(_VALUE)
					+ " guild.";
			player.setDescription(description);
		}
		// done!

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
		if (action.get(TYPE).equals(_CREATEGUILD)) {
			joinGuild(player, action);
		}
	}
}
