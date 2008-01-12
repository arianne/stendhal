package games.stendhal.server.script;

import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

//import marauroa.server.marauroad;

/**
 * Shuts down the server in a regular fashion. You should warn connected players
 * to logout if that is still possible.
 * 
 * If the server is started in a loop, it will come up again: while sleep 60; do
 * java -jar marauroa -c marauroa.ini -l; done
 * 
 * @author M. Fuchs
 */
public class ServerShutdown extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		String text = admin.getTitle()
				+ " started shutdown of the server.";

		StendhalRPRuleProcessor.get().tellAllPlayers(text);

		new Thread(
			new Runnable() {
    			public void run() {
    				//marauroad.getMarauroa().finish();

    				System.exit(0);
    			}
			}
		).start();
	}

}
