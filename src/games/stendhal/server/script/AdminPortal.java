/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.mapstuff.portal.Portal;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * Enables admins to create portals.
 * 
 * @author hendrik
 */
public class AdminPortal extends ScriptImpl {


	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() == 2) {

			sandbox.setZone(sandbox.getZone(admin));
			int x = admin.getX();
			int y = admin.getY();

			Portal portal = new Portal();
			portal.setPosition(x, y);
			portal.setDestination(args.get(0), args.get(1));

			// add sign to game
			sandbox.add(portal);
		} else {
			// syntax error, print help text
			sandbox.privateText(
					admin,
					"This script creates portals. /script AdminPortal.class <destination-zone> <destination-ref>");
		}
	}

}
