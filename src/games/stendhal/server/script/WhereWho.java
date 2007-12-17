package games.stendhal.server.script;

import games.stendhal.server.core.engine.StendhalRPRuleProcessor;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * List all players an the zones they are in
 * 
 * @author hendrik
 */
public class WhereWho extends ScriptImpl {
	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);

		// create player list
		Collection<Player> players = StendhalRPRuleProcessor.get().getPlayers();
		Map<String, StringBuilder> maps = new TreeMap<String, StringBuilder>();
		for (Player player : players) {
			StendhalRPZone zone = player.getZone();
			String zoneid;

			if (zone != null) {
				zoneid = zone.getName();
			} else {
				// Indicate players in world, but not zone
				zoneid = "(none)";
			}

			// get zone and add it to map
			StringBuilder sb = maps.get(zoneid);
			if (sb == null) {
				sb = new StringBuilder();
				sb.append(zoneid);
				sb.append(": ");
				maps.put(zoneid, sb);
			}

			// add player
			sb.append(player.getTitle());
			sb.append(" (");
			sb.append(player.getLevel());
			sb.append(")  ");
		}

		// create response
		StringBuilder sb = new StringBuilder();
		for (StringBuilder mapString : maps.values()) {
			sb.append(mapString);
			sb.append('\n');
		}

		admin.sendPrivateText(sb.toString());
	}
}
