/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.List;

import marauroa.server.game.PlayerEntryContainer;

import org.apache.log4j.Logger;

/**
 * Logout a player
 *
 * @author hendrik
 */
public class LogoutPlayer extends ScriptImpl {

	private static Logger logger = Logger.getLogger(LogoutPlayer.class);

	@Override
	public void execute(Player admin, List<String> args) {

		// help text
		if (args.size() == 0) {
			admin.sendPrivateText("/script KillPlayer.class <playername> logs a player out");
			return;
		}

		// remove the player from Narauroa's player container
		try {
			PlayerEntryContainer playerContainer = PlayerEntryContainer.getContainer();
			int clientid = playerContainer.getClientidPlayer(args.get(0));
			if (clientid > -1) {
				playerContainer.removeRuntimePlayer(clientid);
			}
		} catch (Exception e) {
			logger.error(e, e);
		}

		// TODO: remove player from Stendhal
	}

}
