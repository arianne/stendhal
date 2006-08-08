package games.stendhal.server.maps;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

public class Athos implements IContent {
	//private StendhalRPWorld world;
	private ShopList shops;
	private NPCList npcs;
	private StendhalRPWorld world;
	
	public Athos(StendhalRPWorld world) {
		this.npcs = NPCList.get();
		this.shops = ShopList.get();
		this.world = world;

		buildShipArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"0_athos_ship_w")));
	}


	private void buildShipArea(StendhalRPZone zone) {
	}
}
