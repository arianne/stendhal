package games.stendhal.server.maps;

import java.util.Map;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.portal.OneWayPortalDestination;
import games.stendhal.server.entity.portal.Portal;

import marauroa.common.game.IRPZone;

public class Nalwor implements ZoneConfigurator, IContent {
	public Nalwor() {
	}


	public void build() {
		configureZone(
			(StendhalRPZone) StendhalRPWorld.get().getRPZone(
				new IRPZone.ID("0_nalwor_forest_w")),
			java.util.Collections.EMPTY_MAP);
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(84);
		portal.setY(92);
		portal.setReference(new Integer(0));
		portal.setDestination("0_nalwor_forest_w",new Integer( 61));
		zone.addPortal(portal);

		portal = new OneWayPortalDestination();
		zone.assignRPObjectID(portal);
		portal.setX(87);
		portal.setY(92);
		portal.setReference(new Integer(60));
		zone.addPortal(portal);

		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(114);
		portal.setY(91);
		portal.setReference(new Integer(1));
		portal.setDestination("0_nalwor_forest_w",new Integer( 60));
		zone.addPortal(portal);

		portal = new OneWayPortalDestination();
		zone.assignRPObjectID(portal);
		portal.setX(117);
		portal.setY(91);
		portal.setReference(new Integer(61));
		zone.addPortal(portal);
	}
}
