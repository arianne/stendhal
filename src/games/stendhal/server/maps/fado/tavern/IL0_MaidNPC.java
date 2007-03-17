package games.stendhal.server.maps.fado.tavern;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

/**
 * Builds the tavern maid NPC.
 *
 * @author timothyb89
 */
public class IL0_MaidNPC implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();


	//
	// ZoneConfigurator
	//

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildNPC(zone, attributes);
	}


	//
	// L0_TellerNPC
	//

	private void buildNPC(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC tavernMaid = new SpeakerNPC("Helena") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(28, 14));
				nodes.add(new Path.Node(10, 14));
				nodes.add(new Path.Node(10, 26));
				nodes.add(new Path.Node(19, 26));
				nodes.add(new Path.Node(19, 27));
				nodes.add(new Path.Node(20, 27));
				nodes.add(new Path.Node(21, 27));
				nodes.add(new Path.Node(21, 26));
				nodes.add(new Path.Node(28, 26));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am the bar maid for this fair tavern. We sell both imported and local beers, and fine food.");
				addHelp("This tavern is a great place to take a break and meet new people! Just ask if you want me to #offer you a drink.");
				addSeller(new SellerBehaviour(shops.get("food&drinks")));
				addGoodbye();
			}
		};
		npcs.add(tavernMaid);
		zone.assignRPObjectID(tavernMaid);
		tavernMaid.put("class", "maidnpc");
		tavernMaid.set(10, 16);
		tavernMaid.initHP(100);
		zone.add(tavernMaid);

	}
}
