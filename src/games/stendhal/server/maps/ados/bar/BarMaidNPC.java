package games.stendhal.server.maps.ados.bar;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
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

	private NPCList npcs = NPCList.get();
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
				nodes.add(new Node(8, 26));
				nodes.add(new Node(3, 26));
				nodes.add(new Node(3, 12));
				nodes.add(new Node(20, 12));
				nodes.add(new Node(20, 17));
				nodes.add(new Node(28, 17));
				nodes.add(new Node(28, 6));
				nodes.add(new Node(12, 6));
				nodes.add(new Node(12, 12));
				nodes.add(new Node(3, 12));
				nodes.add(new Node(3, 26));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi!");
				addJob("I'm a bar maid. But we've run out of food to feed our customers, can you #offer any?");
				addHelp("If you could #offer any meat, ham or cheese to restock our larders I'd be grateful.");
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING,
				        "Just #offers of food is enough, thank you.", null);
 				addGoodbye("Bye bye!");
				addBuyer(new BuyerBehaviour(shops.get("buyfood")));
			}
		};
		npc.setDescription("You see a pretty young bar maid.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "woman_004_npc");
		npc.set(8, 26);
		npc.initHP(100);
		zone.add(npc);

	}
}
