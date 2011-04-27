/* $Id$
 * $Log$
 */
package games.stendhal.server.script;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * goes into or comes out of server down mode
 *
 * @author hendrik
 */
public class ServerDown extends ScriptImpl {
	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() != 1) {
			admin.sendPrivateText("/script ServerDown.class {true|false}");
			return;
		}

		boolean enable = Boolean.parseBoolean(args.get(0));
		if (enable) {
			start();
		} else {
			stop();
		}
	}

	/**
	 * goes into server down mode
	 */
	private void start() {
		System.setProperty("stendhal.forcezone", "int_abstract_server_down");
		// TODO: remove portal in int_abstract_server_down, but not Entry-portal
		// TODO: add NPC

		/*
		This is... Think of it as after the game, outside the theater, beyond reality.
		 */
	}

	/**
	 * comes out of server down mode
	 */
	private void stop() {
		System.getProperties().remove("stendhal.forcezone");
		// TODO: add portal in int_abstract_server_down going back to semos town hall
		// TODO: remove NPC
	}
}
