package games.stendhal.server.core.scripting;

import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * Default implementaion of the Script interface.
 * 
 * @author hendrik
 */
public class ScriptImpl implements Script {

	/** all modifications must be done using this object to be undoable on unload */
	protected ScriptingSandbox sandbox;

	public void execute(Player admin, List<String> args) {
		// do nothing
	}

	public void load(Player admin, List<String> args, ScriptingSandbox sandbox) {
		this.sandbox = sandbox;
	}

	public void unload(Player admin, List<String> args) {
		// do nothing
	}

}
