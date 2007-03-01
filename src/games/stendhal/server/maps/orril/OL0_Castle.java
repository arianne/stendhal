package games.stendhal.server.maps.orril;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.Map;

/**
 * Configure Orril Castle (Outside/Level 0).
 */
public class OL0_Castle implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildCastleArea(zone);
	}


	private void buildCastleArea(StendhalRPZone zone) {
		for (int i = 0; i < 5; i++) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(60 + i);
			portal.setY(96);
			portal.setReference(new Integer(i));
			portal.setDestination("0_orril_castle", new Integer(5 + i));
			zone.addPortal(portal);

			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(60 + i);
			portal.setY(93);
			portal.setReference(new Integer(5 + i));
			portal.setDestination("0_orril_castle", new Integer(i));
			zone.addPortal(portal);
		}

		for (int i = 0; i < 3; i++) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(61 + i);
			portal.setY(72);
			portal.setReference(new Integer(10 + i));
			portal.setDestination("int_orril_castle_0",new Integer( 1));
			zone.addPortal(portal);
		}
	}
}
