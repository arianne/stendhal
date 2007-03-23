/*
 *
 */
package games.stendhal.server.maps;

//
//

import java.util.Map;
import games.stendhal.server.StendhalRPZone;

/**
 * Zone post-configuration.
 */
public interface ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes);
}
