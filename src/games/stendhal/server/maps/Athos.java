package games.stendhal.server.maps;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;

import marauroa.common.game.IRPZone;

public class Athos implements IContent {
	private ShopList shops;
	private NPCList npcs;
	
	public Athos() {
		this.npcs = NPCList.get();
		this.shops = ShopList.get();

		buildShipArea((StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"0_athos_ship_w")));
	}


	private void buildShipArea(StendhalRPZone zone) {
	}
}
