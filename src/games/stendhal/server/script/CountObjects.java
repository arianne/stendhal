/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.Iterator;
import java.util.List;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;

/**
 * Counts the number of creatures on the world
 * 
 * @author hendrik
 */
public class CountObjects extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		int count = 0;
		StendhalRPWorld world = StendhalRPWorld.get();
		for (IRPZone irpZone : world) {
			StendhalRPZone zone = (StendhalRPZone) irpZone;
			Iterator<RPObject> itr2 = zone.iterator();
			while (itr2.hasNext()) {
				itr2.next();
				count++;
			}
		}
		admin.sendPrivateText("Number of objects " + count);
	}
}
