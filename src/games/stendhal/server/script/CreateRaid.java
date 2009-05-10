package games.stendhal.server.script;

import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.creature.RaidCreature;
import games.stendhal.server.entity.player.Player;

import java.util.List;
import java.util.Map;

public abstract class CreateRaid extends ScriptImpl {

	protected abstract Map<String, Integer> createArmy();

	@Override
	public void execute(final Player admin, final List<String> args) {
		// extract position of admin
		final StendhalRPZone myZone = sandbox.getZone(admin);
		final int x = admin.getX();
		final int y = admin.getY();
		sandbox.setZone(myZone);

		for (final Map.Entry<String, Integer> entry : createArmy().entrySet()) {
			final RaidCreature creature = new RaidCreature(sandbox.getCreature(entry.getKey()));

			for (int i = 0; i < entry.getValue(); i++) {
				sandbox.add(creature, x
						+ games.stendhal.common.Rand.randUniform(-20, 20), y
						+ games.stendhal.common.Rand.randUniform(-20, 20));
			}
		}
	}
}
