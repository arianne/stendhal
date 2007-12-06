package games.stendhal.server.actions.admin;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;

import java.util.Set;
import java.util.TreeSet;

import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;

public class TeleportAction extends AdministrationAction {
	
	public static void register(){
		CommandCenter.register("teleport", new TeleportAction(), 400);
		
	}
	
	@Override
	public void perform(Player player, RPAction action) {
		if (action.has("target") && action.has("zone") && action.has("x")
				&& action.has("y")) {
			String name = action.get("target");
			Player teleported = StendhalRPRuleProcessor.get().getPlayer(name);

			if (teleported == null) {
				String text = "Player \"" + name + "\" not found";
				player.sendPrivateText(text);
				logger.debug(text);
				return;
			}

			// validate the zone-name.
			IRPZone.ID zoneid = new IRPZone.ID(action.get("zone"));
			if (!StendhalRPWorld.get().hasRPZone(zoneid)) {
				String text = "Zone \"" + zoneid + "\" not found.";
				logger.debug(text);

				Set<String> zoneNames = new TreeSet<String>();
				for (IRPZone irpZone : StendhalRPWorld.get()) {
					StendhalRPZone zone = (StendhalRPZone) irpZone;
					zoneNames.add(zone.getName());
				}
				player.sendPrivateText(text + " Valid zones: " + zoneNames);
				return;
			}

			StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get()
					.getRPZone(zoneid);
			int x = action.getInt("x");
			int y = action.getInt("y");

			StendhalRPRuleProcessor.get().addGameEvent(player.getName(),
					"teleport", action.get("target"), zone.getName(),
					Integer.toString(x), Integer.toString(y));
			teleported.teleport(zone, x, y, null, player);
		}
	}

}
