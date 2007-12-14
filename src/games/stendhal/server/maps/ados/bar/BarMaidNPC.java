package games.stendhal.server.maps.ados.bar;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds a Bar Maid NPC to buy food from players
 *
 * @author kymara
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
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Siandra") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(8, 27));
				nodes.add(new Node(3, 27));
				nodes.add(new Node(3, 13));
				nodes.add(new Node(20, 13));
				nodes.add(new Node(20, 18));
				nodes.add(new Node(28, 18));
				nodes.add(new Node(28, 7));
				nodes.add(new Node(12, 7));
				nodes.add(new Node(12, 13));
				nodes.add(new Node(3, 13));
				nodes.add(new Node(3, 27));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi!");
				addJob("I'm a bar maid. But we've run out of food to feed our customers, can you #offer any?");
				addHelp("If you could #offer any meat, ham or cheese to restock our larders I'd be grateful.");
				addQuest("Just #offers of food is enough, thank you.");
 				addGoodbye("Bye bye!");
 				new BuyerAdder().add(this, new BuyerBehaviour(shops.get("buyfood")), true);
			}
		};
		npc.setDescription("You see a pretty young bar maid.");
		npc.setEntityClass("woman_004_npc");
		npc.setPosition(8, 27);
		npc.initHP(100);
		zone.add(npc);
	}
}
