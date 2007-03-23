package games.stendhal.server.script;

import java.util.List;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

/**
 * Kills the server the hard way without doing a normal shutdown.
 * Do not use it unless the server has already crashed. You should
 * warn connected players to logout if that is still possible.
 * 
 * If the server is started in a loop, it will come up again:
 * while sleep 60; do java -jar marauroa -c marauroa.ini -l; done
 *
 * @author hendrik
 */
public class ServerReset extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {

		try {
			List<Player> players = StendhalRPRuleProcessor.get().getPlayers();
			for (Player player : players) {
				player.sendPrivateText(admin.getName() + " started emergency shutdown of the server.");
			}
		} catch (Throwable e) {
			// Yes, i know that you are not supposed to catch Throwable 
			// because of ThreadDeath. But we are here because of an 
			// emergency situation and don't know what went wrong. So we
			// try very hard to reach the following line.
		}

		Runtime.getRuntime().halt(1);
	}

}
