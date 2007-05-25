
package games.stendhal.server.script;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.List;

import marauroa.common.Logger;

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
			admin.sendPrivateText("/script LogoutPlayer.class <playername> logs a player out");
			return;
		}
		
		try {
			//see processLogoutEvent in marauroa-1.34/src/marauroa/server/game/GameServerManager.java

			Player player = StendhalRPRuleProcessor.get().getPlayer(args.get(0));
			StendhalRPRuleProcessor.get().getRPManager().disconnectPlayer(player);
			admin.sendPrivateText(args.get(0) + " has been logged out");
		} catch (Exception e) {
			logger.error(e, e);
		}

	}

}
