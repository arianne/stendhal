package games.stendhal.server.core.scripting;

import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * Default implementation of the Script interface.
 * 
 * @author hendrik
 */
public class ScriptImpl implements Script {

	/** all modifications must be done using this object to be undoable on unload. */
	protected ScriptingSandbox sandbox;

	public void execute(final Player admin, final List<String> args) {
		// do nothing
	}

	public void load(final Player admin, final List<String> args, final ScriptingSandbox sandbox) {
		this.sandbox = sandbox;
	}

	public void unload(final Player admin, final List<String> args) {
		// do nothing
	}

}
