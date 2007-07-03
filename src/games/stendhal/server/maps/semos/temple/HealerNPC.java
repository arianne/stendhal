package games.stendhal.server.maps.semos.temple;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HealerNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	private ShopList shops = ShopList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosTempleArea(zone, attributes);
	}

	private void buildSemosTempleArea(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Ilisa") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(9, 5));
				nodes.add(new Path.Node(14, 5));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("My special powers help me to heal wounded people. I also sell potions and antidotes.");
				addHelp("I can #heal you here for free, or you can take one of my prepared medicines with you on your travels; just ask for an #offer.");
				addSeller(new SellerBehaviour(shops.get("healing")));
				addHealer(0);
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "welcomernpc");
		npc.set(9, 5);
		npc.initHP(100);
		zone.add(npc);
	}
}
