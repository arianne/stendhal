/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.Iterator;
import java.util.List;

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
		Iterator itr = world.iterator();
		while (itr.hasNext()) {
			StendhalRPZone zone = (StendhalRPZone) itr.next();
			count = count + zone.getNPCList().size();
		}
		admin.sendPrivateText("Number of objects " + count);
	}
}
