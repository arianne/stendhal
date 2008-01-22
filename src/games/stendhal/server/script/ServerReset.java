package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * Kills the server the hard way without doing a normal shutdown. Do not use it
 * unless the server has already crashed. You should warn connected players to
 * logout if that is still possible.
 * 
 * If the server is started in a loop, it will come up again: while sleep 60; do
 * java -jar marauroa -c marauroa.ini -l; done
 * 
 * @author hendrik
 */
public class ServerReset extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {

		try {
			String text = admin.getTitle()
					+ " started emergency shutdown of the server.";
			SingletonRepository.getRuleProcessor().tellAllPlayers(text);

		} catch (Throwable e) {
			// Yes, i know that you are not supposed to catch Throwable
			// because of ThreadDeath. But we are here because of an
			// emergency situation and don't know what went wrong. So we
			// try very hard to reach the following line.
		}

		Runtime.getRuntime().halt(1);
	}
}
