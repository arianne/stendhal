package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.portal.OneWayPortalDestination;
import games.stendhal.server.entity.portal.Portal;

import marauroa.common.game.IRPZone;

public class Nalwor implements IContent {
	public Nalwor() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"0_nalwor_forest_w"));

		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(84);
		portal.setY(92);
		portal.setNumber(0);
		portal.setDestination("0_nalwor_forest_w", 61);
		zone.addPortal(portal);

		portal = new OneWayPortalDestination();
		zone.assignRPObjectID(portal);
		portal.setX(87);
		portal.setY(92);
		portal.setNumber(60);
		zone.addPortal(portal);

		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(114);
		portal.setY(91);
		portal.setNumber(1);
		portal.setDestination("0_nalwor_forest_w", 60);
		zone.addPortal(portal);

		portal = new OneWayPortalDestination();
		zone.assignRPObjectID(portal);
		portal.setX(117);
		portal.setY(91);
		portal.setNumber(61);
		zone.addPortal(portal);
	}
}
