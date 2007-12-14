package games.stendhal.server.maps.semos.jail;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;

import java.util.Map;

/**
 * Semos Jail - Level -3
 * 
 * @author hendrik
 */
public class NoMoveTo implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		zone.setMoveToAllowed(false);
	}
}
