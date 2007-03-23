package games.stendhal.server.script;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

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
		List<Player> players = StendhalRPRuleProcessor.get().getPlayers();
		Map<String, StringBuilder> maps = new TreeMap<String, StringBuilder>();
		for (Player player : players) {

			// get zone and add it to map
			StringBuilder sb = maps.get(player.get("zoneid"));
			if (sb == null) {
				sb = new StringBuilder();
				sb.append(player.get("zoneid") + ": ");
				maps.put(player.get("zoneid"), sb);
			}

			// add player
			sb.append(player.getName() + " (" + player.getLevel() + ")  ");
		}

		// create response
		StringBuilder sb = new StringBuilder();
		for (StringBuilder mapString : maps.values()) {
			sb.append(mapString + "\n");
		}
		admin.sendPrivateText(sb.toString());
	}

}
