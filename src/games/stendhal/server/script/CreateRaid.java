package games.stendhal.server.script;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;

import java.util.List;
import java.util.Map;

public abstract class CreateRaid extends ScriptImpl {

	protected abstract Map<String, Integer> createArmy();

	@Override
	public void execute(Player admin, List<String> args) {
		// extract position of admin
		StendhalRPZone myZone = sandbox.getZone(admin);
		int x = admin.getX();
		int y = admin.getY();
		sandbox.setZone(myZone);

		for (Map.Entry<String, Integer> entry : createArmy().entrySet()) {
			Creature creature = sandbox.getCreature(entry.getKey());

			for (int i = 0; i < entry.getValue(); i++) {
				sandbox.add(creature, x
						+ games.stendhal.common.Rand.rand(0, 30), y
						+ games.stendhal.common.Rand.rand(0, 30));
			}
		}
	}
}
