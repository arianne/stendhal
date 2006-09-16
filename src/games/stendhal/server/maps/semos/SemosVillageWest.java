package games.stendhal.server.maps.semos;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.NPCList;
import marauroa.common.game.IRPZone;

public class SemosVillageWest {
	private NPCList npcs = NPCList.get();

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		buildSemosVillageArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"0_semos_village_w")));
	}

	private void buildSemosVillageArea(StendhalRPZone zone) {
		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(23);
		sign.setY(61);
		sign.setText("You are about to leave this area and move to the plains.\nYou may fatten up your sheep there on the wild berries.\nBe careful though, wolves roam these plains.");
		zone.add(sign);
		
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(60);
		sign.setY(47);
		sign.setText("You are about to leave this area to move to the city.\nYou can sell your sheep there.");
		zone.add(sign);
		
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(16);
		sign.setY(35);
		sign.setText("[CLOSED]\nThe tavern has moved to a much\nbetter and central house in town.\nCome buy your weapons, find your\nquests and hang out there instead.");
		zone.add(sign);
	}

}
