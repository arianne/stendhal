package games.stendhal.server.maps.orril;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.Map;

/**
 * Configure Orril Castle (Underground/Level -1).
 */
public class USL1_Castle implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildCastleSub1(zone);
	}


	private void buildCastleSub1(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(19);
		portal.setY(22);
		portal.setReference(new Integer(0));
		portal.setDestination("int_orril_castle_0",new Integer( 4));
		zone.addPortal(portal);

		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(20);
		portal.setY(22);
		portal.setReference(new Integer(1));
		portal.setDestination("int_orril_castle_0", new Integer(4));
		zone.addPortal(portal);
	}
}
