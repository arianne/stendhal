/* $Id$ */
package games.stendhal.server.script;

import java.util.List;
import java.util.Map;
import java.util.Set;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.ObjectCounter;

/**
 * Dumps debug information about turn listener events.
 *
 * @author hendrik
 */
public class DumpTurnListenerEvents extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		int outdated = 0;
		final ObjectCounter<Class< ? >> counter = new ObjectCounter<Class< ? >>();

		final TurnNotifier turnNotifier = SingletonRepository.getTurnNotifier();
		final int currentTurn = turnNotifier.getCurrentTurnForDebugging();
		final Map<Integer, Set<TurnListener>> events = turnNotifier.getEventListForDebugging();

		for (final Map.Entry<Integer, Set<TurnListener>> it : events.entrySet()) {
			final Integer turn = it.getKey();

			// count outdated
			if (turn.intValue() < currentTurn) {
				outdated++;
			}

			// count classes
			for (final TurnListener event : it.getValue()) {
				counter.add(event.getClass());
			}
		}

		// send result
		admin.sendPrivateText("Statistics: " + "\n" + counter.getMap()
				+ "\nCounted turn events:" + events.size()
				+ "\nOutdated turn events: " + outdated);
	}
}
