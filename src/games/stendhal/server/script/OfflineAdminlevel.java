package games.stendhal.server.script;

import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import marauroa.common.game.RPObject;

/**
 * Changes the admin level of an offline player.
 *
 * @author hendrik
 */
public class OfflineAdminlevel extends AbstractOfflineAction {

	@Override
	public boolean validateParameters(final Player admin, final List<String> args) {
		if (args.size() != 2) {
			admin.sendPrivateText("/script OfflineAdminlevel.class <playername> <newlevel>");
			return false;
		}
		return true;
	}

	@Override
	public void process(final Player admin, RPObject object, final List<String> args) {
		String playerName = args.get(0);
		String newLevel = args.get(1);

		// do the modifications here
		object.put("adminlevel", Integer.parseInt(newLevel));

		// log game event
		new GameEvent(admin.getName(), "adminlevel", playerName, "adminlevel", newLevel).raise();
	}
}
