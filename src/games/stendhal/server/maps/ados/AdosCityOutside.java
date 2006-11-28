package games.stendhal.server.maps.ados;

import marauroa.common.game.IRPZone;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.portal.OneWayPortalDestination;
import games.stendhal.server.entity.portal.Portal;

public class AdosCityOutside {

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID("0_ados_city"));
		buildAdosCityAreaPortals(zone);
	}
	
	private void buildAdosCityAreaPortals(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(60);
		portal.setY(16);
		portal.setNumber(0);
		portal.setDestination("int_ados_tavern_0", 0);
		zone.addPortal(portal);
		
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(65);
		portal.setY(16);
		portal.setNumber(1);
		portal.setDestination("int_ados_temple", 2);
		zone.addPortal(portal);
					
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(54);
		portal.setY(19);
		portal.setNumber(6);
		portal.setDestination("int_ados_bank", 0);
		zone.addPortal(portal);
		
	}
}
