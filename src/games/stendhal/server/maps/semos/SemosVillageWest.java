package games.stendhal.server.maps.semos;

import java.util.Map;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.maps.ZoneConfigurator;
import marauroa.common.game.IRPZone;

public class SemosVillageWest implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("0_semos_village_w")),
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
		buildSemosVillageArea(zone);
	}


	private void buildSemosVillageArea(StendhalRPZone zone) {
		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(23);
		sign.setY(61);
		sign.setText("TO THE PLAINS\n\nShepherds please note: keep watch for\nthe wolves while searching for berries here");
		zone.add(sign);
		
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(60);
		sign.setY(47);
		sign.setText("TO SEMOS\n\nMature sheep wanted: please speak to Sato");
		zone.add(sign);
		
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(16);
		sign.setY(35);
		sign.setText("[CLOSED]\n\nThe tavern has moved into a more central\nlocation in Semos. Please come join us at the\nsocial hub of the Semos area!");
		zone.add(sign);
	}

}
