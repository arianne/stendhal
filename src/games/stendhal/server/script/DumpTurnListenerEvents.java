/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.events.TurnNotifier.TurnEvent;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author hendrik
 */
public class DumpTurnListenerEvents extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		TurnNotifier turnNotifier = TurnNotifier.get();
		int currentTurn = turnNotifier.getCurrentTurnForDebugging();
		Map<Integer, Set<TurnEvent>> events = turnNotifier.getEventListForDebugging();
		for (Integer turn : events.keySet()) {
			
		}
	}
}
