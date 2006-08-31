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
		sign.setText("I regret to tell you that you have died!\nYou have lost some of your items and 10% of your eXPerience points.\nBe more careful next time. On the up side you can now return to the city.");
		zone.add(sign);

		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(33);
		sign.setY(22);
		sign.setText("I regret to tell you that you have died!\nYou have lost some of your items and 10% of your eXPerience points.\nBe more careful next time. On the up side you can now return to the city.");
		zone.add(sign);
	}
}
