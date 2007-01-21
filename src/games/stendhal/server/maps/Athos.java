package games.stendhal.server.maps;

import java.util.Map;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;

import marauroa.common.game.IRPZone;

public class Athos implements ZoneConfigurator, IContent {
	private ShopList shops;
	private NPCList npcs;
	
	public Athos() {
		this.npcs = NPCList.get();
		this.shops = ShopList.get();

		/**
		 * When ZoneConfigurator aware loader is used, remove this!!
		 */
		configureZone(
			(StendhalRPZone) StendhalRPWorld.get().getRPZone(
				new IRPZone.ID("0_athos_ship_w")),
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
		buildShipArea(zone);
	}


	private void buildShipArea(StendhalRPZone zone) {
	}
}
