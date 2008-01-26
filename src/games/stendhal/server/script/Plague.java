package games.stendhal.server.script;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.player.Player;

import java.util.List;

/**
 * @author hendrik
 */
public class Plague extends ScriptImpl {

	private static final int MAX_RING_COUNT = 2;

	@Override
	public void execute(Player admin, List<String> args) {

		// help
		if (args.size() == 0) {
			admin.sendPrivateText("/script Plague.class [ringcount] <creature>");
			return;
		}

		// extract position of admin
		StendhalRPZone myZone = sandbox.getZone(admin);
		int x = admin.getX();
		int y = admin.getY();
		sandbox.setZone(myZone);

		// select creature
		String creatureClass;

		if (args.size() >= 2) {
			creatureClass = args.get(1);
		} else {
			creatureClass = args.get(0);
		}

		Creature tempCreature = sandbox.getCreature(creatureClass);
		if (tempCreature == null) {
			admin.sendPrivateText("No such creature");
		} else {
			Creature creature = new RaidCreature(tempCreature);

			// spawn the specified amout of them
			if (args.size() == 1) {
				sandbox.add(creature, x, y);
			} else {
				int k = MathHelper.parseIntDefault(args.get(0), 1);
				if (k <= MAX_RING_COUNT) {
					for (int dx = -k; dx <= k; dx++) {
						for (int dy = -k; dy <= k; dy++) {
							if ((dx != 0) || (dy != 0)) {
								sandbox.add(creature, x + dx, y + dy + 1);
							}
						}
					}
				} else {
					admin.sendPrivateText("That's too many! Please keep <ringcount> less or equal to "
							+ MAX_RING_COUNT + ".");
				}
			}
		}
	}
}
