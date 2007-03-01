package games.stendhal.server.maps.orril;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;
import java.util.Map;

/**
 * Configure Orril Jynath House (Outside/Level 0).
 */
public class OL0_JynathHouse implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildJynathHouseOutside(zone);
	}


	private void buildJynathHouseOutside(StendhalRPZone zone) {
		// create portal to Jynath' house (which is on the same
		// map as the campfire by accident)
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(39);
		portal.setY(5);
		portal.setReference(new Integer(0));
		portal.setDestination("int_orril_jynath_house",new Integer( 0));
		zone.addPortal(portal);
	}
}
