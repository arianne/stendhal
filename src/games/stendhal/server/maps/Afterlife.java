package games.stendhal.server.maps;

import games.stendhal.server.*;
import games.stendhal.server.entity.*;
import games.stendhal.server.entity.portal.Portal;
import marauroa.common.game.IRPZone;

public class Afterlife implements IContent {
	public Afterlife() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"int_afterlife"));

		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(30);
		portal.setY(6);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 60);
		zone.addPortal(portal);

		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(31);
		portal.setY(6);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 60);
		zone.addPortal(portal);

		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(32);
		portal.setY(6);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 60);
		zone.addPortal(portal);

		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(29);
		sign.setY(22);
		sign.setText("Sorry, you have died.\nYou lost some of your items and 10% of your experience points.\nBe more careful in future!\n\nWalk on, and you will be returned to the land of the living...");
		zone.add(sign);

		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(33);
		sign.setY(22);
		sign.setText("Sorry, you have died.\nYou lost some of your items and 10% of your experience points.\nBe more careful in future!\n\nWalk on, and you will be returned to the land of the living...");
		zone.add(sign);
	}
}
