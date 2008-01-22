package games.stendhal.server.actions.admin;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;

import java.util.Set;
import java.util.TreeSet;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;
import static games.stendhal.server.actions.WellKnownActionConstants.*;

public class TeleportAction extends AdministrationAction {

	private static final String _ZONE = "zone";

	private static final String _TELEPORT = "teleport";

	public static void register() {
		CommandCenter.register(_TELEPORT, new TeleportAction(), 400);

	}

	@Override
	public void perform(Player player, RPAction action) {
		if (action.has(TARGET) && action.has(_ZONE) && action.has(X)
				&& action.has(Y)) {
			String name = action.get(TARGET);
			Player teleported = SingletonRepository.getRuleProcessor().getPlayer(name);

			if (teleported == null) {
				String text = "Player \"" + name + "\" not found";
				player.sendPrivateText(text);
				logger.debug(text);
				return;
			}

			// validate the zone-name.
			IRPZone.ID zoneid = new IRPZone.ID(action.get(_ZONE));
			if (!SingletonRepository.getRPWorld().hasRPZone(zoneid)) {
				String text = "Zone \"" + zoneid + "\" not found.";
				logger.debug(text);

				Set<String> zoneNames = new TreeSet<String>();
				for (IRPZone irpZone : SingletonRepository.getRPWorld()) {
					StendhalRPZone zone = (StendhalRPZone) irpZone;
					zoneNames.add(zone.getName());
				}
				player.sendPrivateText(text + " Valid zones: " + zoneNames);
				return;
			}

			StendhalRPZone zone = (StendhalRPZone) SingletonRepository.getRPWorld().getRPZone(
					zoneid);
			int x = action.getInt(X);
			int y = action.getInt(Y);

			SingletonRepository.getRuleProcessor().addGameEvent(player.getName(),
					_TELEPORT, action.get(TARGET), zone.getName(),
					Integer.toString(x), Integer.toString(y));
			teleported.teleport(zone, x, y, null, player);
			
			SingletonRepository.getJail().grantParoleIfPlayerWasAPrisoner(teleported);
		}
	}

}
