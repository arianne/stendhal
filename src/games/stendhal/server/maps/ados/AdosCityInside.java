package games.stendhal.server.maps.ados;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.PersonalChest;
import games.stendhal.server.entity.portal.Portal;
import marauroa.common.game.IRPZone;

/**
 * Builds the inside of buildings in Ados City
 *
 * @author hendrik
 */
public class AdosCityInside {

	/**
	 * build the city insides
	 */
	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();
		buildBank((StendhalRPZone) world.getRPZone(new IRPZone.ID("int_ados_bank")));
		buildBakery((StendhalRPZone) world.getRPZone(new IRPZone.ID("int_ados_bakery")));
		buildSemosTavernPortals((StendhalRPZone) world.getRPZone(new IRPZone.ID("int_ados_tavern_0")));
		buildTempel((StendhalRPZone) world.getRPZone(new IRPZone.ID("int_ados_temple")));
	}

	private void buildBank(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(9);
		portal.setY(30);
		portal.setNumber(0);
		portal.setDestination("0_ados_city", 6);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(10);
		portal.setY(30);
		portal.setNumber(1);
		portal.setDestination("0_ados_city", 6);
		zone.addPortal(portal);
		for (int i = 0; i < 4; i++) {
			PersonalChest chest = new PersonalChest();
			zone.assignRPObjectID(chest);
			chest.set(2 + 6 * i, 2);
			zone.add(chest);
			chest = new PersonalChest();
			zone.assignRPObjectID(chest);
			chest.set(2 + 6 * i, 13);
			zone.add(chest);
		}
	}

	private void buildSemosTavernPortals(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(12);
		portal.setY(17);
		portal.setNumber(0);
		portal.setDestination("0_ados_city", 0);
		zone.addPortal(portal);
		
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(27);
		portal.setY(17);
		portal.setNumber(1);
		portal.setDestination("0_ados_city", 1);
		zone.addPortal(portal);
	}

	private void buildBakery(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(26);
		portal.setY(14);
		portal.setNumber(0);
		portal.setDestination("0_ados_city", 10);
		zone.addPortal(portal);
	}

	private void buildTempel(StendhalRPZone zone) {
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
