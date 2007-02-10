package games.stendhal.server.maps.ados;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.PersonalChest;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.Map;

import marauroa.common.game.IRPZone;

/**
 * Ados Bank (Inside / Level 0)
 *
 * @author hendrik
 */
public class IL0_Bank implements ZoneConfigurator {
	/**
	 * build the city insides
	 */
	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("int_ados_bank")),
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
		buildBank(zone, attributes);
	}


	private void buildBank(StendhalRPZone zone,
	 Map<String, String> attributes) {
		/*
		 * Portals configured in xml?
		 */
		if(attributes.get("xml-portals") == null) {
			// portal from bank to city
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(22);
			portal.setY(17);
			portal.setNumber(0);
			portal.setDestination("0_ados_city", 6);
			zone.addPortal(portal);
		}

		/*
		 * Entities configured in xml?
		 */
		if(attributes.get("xml-entities") == null) {
			// personal chest
			PersonalChest chest = new PersonalChest();
			zone.assignRPObjectID(chest);
			chest.set(3, 12);
			zone.add(chest);

			chest = new PersonalChest();
			zone.assignRPObjectID(chest);
			chest.set(5, 12);
			zone.add(chest);
		
			chest = new PersonalChest();
			zone.assignRPObjectID(chest);
			chest.set(10, 12);
			zone.add(chest);
		}
	}
}
