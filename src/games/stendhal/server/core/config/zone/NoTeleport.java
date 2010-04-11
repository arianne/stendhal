package games.stendhal.server.core.config.zone;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;

import java.util.Map;

/**
 * Semos Jail - Level -3.
 * 
 * @author hendrik
 */
public class NoTeleport implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		if ((attributes != null) && attributes.containsKey("x")) {
			int x = Integer.parseInt(attributes.get("x"));
			int y = Integer.parseInt(attributes.get("y"));
			int width = Integer.parseInt(attributes.get("width"));
			int height = Integer.parseInt(attributes.get("height"));
			
			zone.disAllowTeleport(x, y, width, height);
		} else {
			zone.disAllowTeleport();
		}
	}
}
