package games.stendhal.server.maps.ados;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Ados Tavern (Inside / Level 0)
 *
 * @author hendrik
 */
public class IL0_Tavern implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();

	
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildTavern(zone, attributes);
	}


	private void buildTavern(StendhalRPZone zone,
	 Map<String, String> attributes) {
		SpeakerNPC tavernMaid = new SpeakerNPC("Coralia") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(17, 12));
				nodes.add(new Path.Node(17, 13));
				nodes.add(new Path.Node(16, 8));
				nodes.add(new Path.Node(13, 8));
				nodes.add(new Path.Node(13, 6));
				nodes.add(new Path.Node(13, 10));
				nodes.add(new Path.Node(25, 10));
				nodes.add(new Path.Node(25, 13));
				nodes.add(new Path.Node(25, 10));
				nodes.add(new Path.Node(17, 10));
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
		tavernMaid.set(17, 12);
		tavernMaid.initHP(100);
		zone.add(tavernMaid);
	}
}
