/*
 * CreateGuildAction.java
 *@author timothyb89
 *Puts player in a guild.
 */

package games.stendhal.server.actions.guild;

import static games.stendhal.common.constants.Actions.GUILD;
import static games.stendhal.common.constants.Actions.GUILDNAME;
import static games.stendhal.common.constants.Actions.INVITE_GUILD;
import static games.stendhal.common.constants.Actions.PLAYERNAME;
import static games.stendhal.common.constants.Actions.TYPE;
import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

/**
 * Process guildl actions from client. (Invite)
 */
public class InviteGuildAction implements ActionListener {


	/**
	 * Registers action.
	 */
	public static void register() {
		CommandCenter.register(GUILD, new InviteGuildAction());
	}

	/**
	 * Handle the action.
	 * 
	 * @param player
	 *            The player.
	 * @param action
	 *            The action.
	 */
	protected void inviteToGuild(final Player player, final RPAction action) {

		// invites to guild
		if (action.has(PLAYERNAME) && action.has(GUILDNAME)) {
			// we have all the options, so let's coninue...

			// we use player1 for other player
			final Player player1 = SingletonRepository.getRuleProcessor().getPlayer(
					action.get(PLAYERNAME));
			if (player1.get(GUILD) == null) {
				// it is safe to put the player from the guild as they are not
				// in one
				player1.put(GUILD, action.get(GUILDNAME));

				// set the description
				final String description = "You see " + player1.getTitle() + ".\n"
						+ player1.getTitle() + " is level "
						+ player1.getLevel() + " and is a member of the "
						+ action.get(GUILDNAME) + " guild.";
				player1.setDescription(description);

				// update player
				player1.update();
				player1.notifyWorldAboutChanges();

				// notify player(s)
				player1.sendPrivateText("You have beed added to the \""
						+ action.get(GUILDNAME)
						+ "\" guild by "
						+ player.getTitle()
						+ ". If you did not want to be in this guild, open the Guild Management window and choose \"Leave Guild\".");
				player.sendPrivateText(player1.getTitle()
						+ " has been added to the \"" + action.get(GUILDNAME)
						+ "\" guild.");
			} else {
				player1.sendPrivateText(player.getTitle()
						+ " tried to add you to a guild but you are already in one. If you would like to join it, please leave your old guild first, and have them re-invite you.");
				player.sendPrivateText(player1.getTitle()
						+ " was not added to the guild because he or she is already in one. Please have them remove themselves from their old guild and try again.");
			}

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
		if (action.get(TYPE).equals(INVITE_GUILD)) {
			inviteToGuild(player, action);
		}
	}
}
