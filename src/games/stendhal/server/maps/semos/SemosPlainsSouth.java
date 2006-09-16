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
		sign.setText("You are about to leave this area to move to the forest.\nYou may fatten up your sheep there on wild berries.\nBe careful though, these forests crawl with wolves.");
		zone.add(sign);
		
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(38);
		sign.setY(3);
		sign.setText("You are about to leave this area to move to the village.\nYou can buy a new sheep there.");
		zone.add(sign);
		
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(113);
		sign.setY(3);
		sign.setText("You are about to leave this area to move to the city.\nYou can sell your sheep there.");
		zone.add(sign);
	}

}
