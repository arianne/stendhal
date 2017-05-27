/* $Id$ */
package games.stendhal.server.script;

import java.util.List;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * Runs the garbage collector manually (for memory profiling).
 *
 * @author hendrik
 */
public class GC extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		System.gc();
	}
}
