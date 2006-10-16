package games.stendhal.server.maps.semos;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.NPCList;
import marauroa.common.game.IRPZone;

public class SemosPlainsSouth {
	private NPCList npcs = NPCList.get();

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		buildSemosSouthPlainsArea((StendhalRPZone) world
				.getRPZone(new IRPZone.ID("0_semos_plains_s")));
	}

	private void buildSemosSouthPlainsArea(StendhalRPZone zone) {
		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(118);
		sign.setY(43);
		sign.setText("TO THE FOREST\n\nShepherds please note: keep watch for\nthe wolves while searching for berries here");
		zone.add(sign);
		
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(38);
		sign.setY(3);
		sign.setText("TO OLD SEMOS VILLAGE\n\nShepherds wanted: please ask Nishiya");
		zone.add(sign);
		
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(113);
		sign.setY(3);
		sign.setText("TO SEMOS\n\nMature sheep wanted: please speak to Sato");
		zone.add(sign);
	}

}
