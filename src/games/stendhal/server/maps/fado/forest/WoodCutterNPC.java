package games.stendhal.server.maps.fado.forest;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds an NPC to buy previously un bought axes
 * He is a wood cutter.
 *
 * @author kymara
 */
public class WoodCutterNPC implements ZoneConfigurator {

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
		SpeakerNPC npc = new SpeakerNPC("Woody") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(55, 83));
				nodes.add(new Node(68, 83));
				nodes.add(new Node(68, 83));
				nodes.add(new Node(68, 70));
				nodes.add(new Node(57, 70));
				nodes.add(new Node(57, 75));
				nodes.add(new Node(57, 74));
				nodes.add(new Node(53, 74));
				nodes.add(new Node(53, 81));
				nodes.add(new Node(55, 81));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to this forest, south of Or'ril river.");
				addJob("I'm a wood cutter by trade. Can you #offer me any axes?");
				addHelp("You can sometimes collect wood that's lying around the forest. Oh, and I take #offers of any good axe you might sell.");
				add(ConversationStates.ATTENDING, "offer", null, ConversationStates.ATTENDING,
				        "My axes become blunt fast. Please check the sign I made outside my lodge to see the axes I buy.", null);
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING,
				        "What's that you say? I don't need anything, though my young friend Sally over the river might need a hand.", null);
				addBuyer(new BuyerBehaviour(shops.get("buyaxe")), false);
 				addGoodbye("Bye.");
			}
		};
		npc.setDescription("You see Woody, an outdoorsy-looking fellow.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "woodcutternpc");
		npc.set(55, 83);
		npc.initHP(100);
		zone.add(npc);

	}
}
