package games.stendhal.server.maps.ados;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.Map;

import marauroa.common.game.IRPZone;

/**
 * Ados Haunted House (Inside / Level 0)
 *
 * @author hendrik
 */
public class IL0_HauntedHouse implements ZoneConfigurator {
	/**
	 * build the city insides
	 */
	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("int_ados_haunted_house")),
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
		buildHauntedHouse(zone, attributes);
	}


	private void buildHauntedHouse(StendhalRPZone zone,
	 Map<String, String> attributes) {
		/*
		 * Portals configured in xml?
		 */
		if(attributes.get("xml-portals") == null) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(7);
			portal.setY(1);
			portal.setNumber(0);
			portal.setDestination("0_ados_city", 11);
			zone.addPortal(portal);
		}
	}
}
