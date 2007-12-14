/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.util.ObjectCounter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * dumps debug information about turn listener events
 * 
 * @author hendrik
 */
public class DumpTurnListenerEvents extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		int outdated = 0;
		ObjectCounter<Class<?>> counter = new ObjectCounter<Class<?>>();

		TurnNotifier turnNotifier = TurnNotifier.get();
		int currentTurn = turnNotifier.getCurrentTurnForDebugging();
		Map<Integer, Set<TurnListener>> events = turnNotifier.getEventListForDebugging();

		for (Integer turn : events.keySet()) {

			// count outdated
			if (turn.intValue() < currentTurn) {
				outdated++;
			}

			// count classes
			for (TurnListener event : events.get(turn)) {
				counter.add(event.getClass());
			}
		}

		// send result
		admin.sendPrivateText("Statistics: " + "\n" + counter.getMap()
				+ "\nCounted turn events:" + events.size()
				+ "\nOutdated turn events: " + outdated);
	}
}
