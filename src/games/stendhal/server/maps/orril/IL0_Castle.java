package games.stendhal.server.maps.orril;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.Map;

/**
 * Configure Orril Castle (Inside/Level 0).
 */
public class IL0_Castle implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildCastleInsideArea(zone);
	}


	private void buildCastleInsideArea(StendhalRPZone zone) {
		for (int i = 0; i < 3; i++) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(26 + i);
			portal.setY(62);
			portal.setNumber(i);
			portal.setDestination("0_orril_castle", 11);
			zone.addPortal(portal);
		}

		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(8);
		portal.setY(1);
		portal.setNumber(4);
		portal.setDestination("-1_orril_castle", 1);
		zone.addPortal(portal);
	}
}
