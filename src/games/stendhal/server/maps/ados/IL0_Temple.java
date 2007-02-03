package games.stendhal.server.maps.ados;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.Map;

import marauroa.common.game.IRPZone;

/**
 * Ados Temple (Inside / Level 0)
 *
 * @author hendrik
 */
public class IL0_Temple implements ZoneConfigurator {
	/**
	 * build the city insides
	 */
	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("int_ados_temple")),
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
		buildTemple(zone, attributes);
	}


	private void buildTemple(StendhalRPZone zone,
	 Map<String, String> attributes) {
		/*
		 * Portals configured in xml?
		 */
		if(attributes.get("xml-portals") == null) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(10);
			portal.setY(23);
			portal.setNumber(0);
			portal.setDestination("0_ados_city", 1);
			zone.addPortal(portal);
			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(11);
			portal.setY(23);
			portal.setNumber(1);
			portal.setDestination("0_ados_city", 1);
			zone.addPortal(portal);
			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(12);
			portal.setY(23);
			portal.setNumber(2);
			portal.setDestination("0_ados_city", 1);
			zone.addPortal(portal);
			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(13);
			portal.setY(23);
			portal.setNumber(3);
			portal.setDestination("0_ados_city", 1);
			zone.addPortal(portal);
		}
	}
}
