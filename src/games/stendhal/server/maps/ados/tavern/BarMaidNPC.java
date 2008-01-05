package games.stendhal.server.maps.ados.tavern;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Ados Tavern (Inside / Level 0).
 *
 * @author hendrik
 */
public class BarMaidNPC implements ZoneConfigurator {
	private ShopList shops = ShopList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildTavern(zone, attributes);
	}

	private void buildTavern(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC tavernMaid = new SpeakerNPC("Coralia") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(17, 13));
				nodes.add(new Node(17, 14));
				nodes.add(new Node(16, 9));
				nodes.add(new Node(13, 9));
				nodes.add(new Node(13, 7));
				nodes.add(new Node(13, 11));
				nodes.add(new Node(25, 11));
				nodes.add(new Node(25, 14));
				nodes.add(new Node(25, 11));
				nodes.add(new Node(17, 11));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am the bar maid for this fair tavern. We sell both imported and local beers, and fine food.");
				addHelp("This tavern is a great place to take a break and meet new people! Just ask if you want me to #offer you a drink.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("food&drinks")));
				addGoodbye();
			}
		};

		tavernMaid.setEntityClass("maidnpc");
		tavernMaid.setPosition(17, 13);
		tavernMaid.initHP(100);
		zone.add(tavernMaid);
	}
}
