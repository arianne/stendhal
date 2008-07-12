package games.stendhal.server.script;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.Task;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * List all players an the zones they are in.
 * 
 * @author hendrik
 */
public class WhereWho extends ScriptImpl {
	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		final Map<String, StringBuilder> maps = new TreeMap<String, StringBuilder>();
		SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(
				
			new Task<Player>() {

			public void execute(final Player player) {
				final StendhalRPZone zone = player.getZone();
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
			
		});
	

		// create response
		final StringBuilder sb = new StringBuilder();
		for (final StringBuilder mapString : maps.values()) {
			sb.append(mapString);
			sb.append('\n');
		}

		admin.sendPrivateText(sb.toString());
	}
}
