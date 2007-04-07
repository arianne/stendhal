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
                
                if (action.get("guildname").equals("") || action.get("guildname") == null) {
                    player.sendPrivateText("You did not enter a guild name. Use #/joinguild #none to remove yourself from a guild.");
                    return;
                } else if (action.get("guildname") == "none" || action.get("guildname").equals("none")) { // the or for the update
                            player.put("guild", "");
                            player.sendPrivateText("You have been removed from your old guild.");
                    } else {
                        if (action.has("description")) {
                            if (player.get("guild") != null) {
                                guild = player.getName() + " is in the guild " + action.get("guildname") + ". Their motto is: " + action.get("description");
                            } else {
                                player.sendPrivateText("You are already in a guild. Use #/joinguild #none to remove yourself from it, and then try again.");
                            }

                        } else {
                            guild = player.getName() + " is in the " + action.get("guildname") + " guild.";
			    player.sendPrivateText("You have joined the " + action.get("guildname") + " guild.");
                        }
                    
                }
		// now we set the guild (just uncluttering the above)
		if (guild != null) player.put("guild", guild);
		else; // no nothing
                
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
