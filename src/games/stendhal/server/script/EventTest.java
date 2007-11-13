/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.List;

import marauroa.common.game.RPEvent;

/**
 * Tries to add an RPEvent.
 *
 * @author hendrik
 */
public class EventTest extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		if (args.size() < 1) {
			admin.sendPrivateText("Usage: /script EventTest.class {some-text}");
			return;
		}
		RPEvent event = new RPEvent("testevent");
		event.put("arg", args.get(0));
		admin.addEvent(event);
	}
}
