package games.stendhal.server.script;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.List;

/**
 * @author hendrik
 */
public class Plague extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {

		// help
		if (args.size() == 0) {
			admin.sendPrivateText("/script Plague <ringcount> <creature>");
			return;
		}

		// extract position of admin
		StendhalRPZone myZone = sandbox.getZone(admin);
		int x = admin.getX();
		int y = admin.getY();
		sandbox.setZone(myZone);

		// select creature
		String creatureClass = args.get(0);
		if (args.size() >= 2) {
			creatureClass = args.get(1);
		}
		Creature creature = sandbox.getCreature(creatureClass);
		if (creature == null) {
			admin.sendPrivateText("No such creature");
		} else {

			// spawn the specified amout of them
			if (args.size() == 1) {
				sandbox.add(creature, x, y);
			} else {
				int k = Integer.parseInt(args.get(0));
				if (k < 3) {
					for (int dx = -k; dx <= k; dx++) {
						for (int dy = -k; dy <= k; dy++) {
							if ((dx != 0) || (dy != 0)) {
								sandbox.add(creature, x + dx, y + dy + 1);
							}
						}
					}
				}
			}
		}
	}
}
