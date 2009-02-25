/*
 * CreateGuildAction.java
 *@author timothyb89
 *Puts player in a guild.
 */

package games.stendhal.server.actions.guild;
import static games.stendhal.common.constants.Actions.CREATEGUILD;
import static games.stendhal.common.constants.Actions.GUILD;
import static games.stendhal.common.constants.Actions.TYPE;
import static games.stendhal.common.constants.Actions.VALUE;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
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
	protected void joinGuild(final Player player, final RPAction action) {

		// now we see if the player is in a guild. If not, put them in the
		// requested one.
		if (player.get(GUILD) != null) {
			player.sendPrivateText("You are already in a guild! Please leave your old one and try again.");
		} else {
			player.put(GUILD, action.get(VALUE));
			final String description = "You see " + player.getTitle() + ".\n"
					+ player.getTitle() + " is level " + player.getLevel()
					+ " and is a member of the " + action.get(VALUE)
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
	public void onAction(final Player player, final RPAction action) {
		if (action.get(TYPE).equals(CREATEGUILD)) {
			joinGuild(player, action);
		}
	}
}
