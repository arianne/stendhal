package games.stendhal.server.script;

import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import org.apache.log4j.Logger;
import marauroa.server.game.container.PlayerEntry;
import marauroa.server.game.container.PlayerEntryContainer;

/**
 * Logs a player out.
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
			// see processLogoutEvent in
			// marauroa-1.34/src/marauroa/server/game/GameServerManager.java

			PlayerEntryContainer playerContainer = PlayerEntryContainer.getContainer();
			PlayerEntry entry = playerContainer.get(args.get(0));
			if (entry == null) {
				admin.sendPrivateText(args.get(0) + " not found");
				return;
			}

			Player player = (Player) entry.object;
			StendhalRPRuleProcessor.get().getRPManager().disconnectPlayer(
					player);
			admin.sendPrivateText(args.get(0) + " has been logged out");
		} catch (Exception e) {
			logger.error(e, e);
		}

	}

}
