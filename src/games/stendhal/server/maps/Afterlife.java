package games.stendhal.server.maps;

import java.util.Map;
import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.portal.Portal;
import marauroa.common.game.IRPZone;

public class Afterlife implements ZoneConfigurator, IContent {
	public Afterlife() {
	}


	public void build() {
		/**
		 * When ZoneConfigurator aware loader is used, remove this!!
		 */
		configureZone(
			(StendhalRPZone) StendhalRPWorld.get().getRPZone(
				new IRPZone.ID("int_afterlife")),
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
		/*
		 * Portals configured in xml?
		 */
		if(attributes.get("xml-portals") == null) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(30);
			portal.setY(6);
			portal.setReference(new Integer(0));
			portal.setDestination("0_semos_city", new Integer(60));
			zone.addPortal(portal);

			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(31);
			portal.setY(6);
			portal.setReference(new Integer(0));
			portal.setDestination("0_semos_city",new Integer( 60));
			zone.addPortal(portal);

			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(32);
			portal.setY(6);
			portal.setReference(new Integer(0));
			portal.setDestination("0_semos_city", new Integer(60));
			zone.addPortal(portal);
		}

		/*
		 * Entities configured in xml?
		 */
		if(attributes.get("xml-entities") == null) {
			String message = "Sorry, you have died.\nYou lost some of your items and 10% of your experience points.\nBe more careful in future!\n\nWalk on, and you will be returned to the land of the living...";

			Sign sign = new Sign();
			zone.assignRPObjectID(sign);
			sign.setX(29);
			sign.setY(22);
			sign.setText(message);
			zone.add(sign);

			sign = new Sign();
			zone.assignRPObjectID(sign);
			sign.setX(33);
			sign.setY(22);
			sign.setText(message);
			zone.add(sign);
		}
	}
}
