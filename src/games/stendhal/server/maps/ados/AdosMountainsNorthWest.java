package games.stendhal.server.maps.ados;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.Map;

import marauroa.common.game.IRPZone;

public class AdosMountainsNorthWest implements ZoneConfigurator {

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("0_ados_mountain_nw")),
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
		buildMagicianHouseOutside(zone, attributes);
	}


	private void buildMagicianHouseOutside(StendhalRPZone zone,
	 Map<String, String> attributes) {
		/*
		 * Portals configured in xml?
		 */
		if(attributes.get("xml-portals") == null) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(75);
			portal.setY(50);
			portal.setNumber(0);
			portal.setDestination("int_ados_magician_house", 0);
			zone.addPortal(portal);
		}
	}
}
