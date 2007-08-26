package games.stendhal.server.maps.fado.weaponshop;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
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
 * Builds an NPC to buy previously un bought weapons.
 *
 * @author kymara
 */
public class BuyerNPC implements ZoneConfigurator {

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
		SpeakerNPC npc = new SpeakerNPC("Yorphin Baos") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(4, 5));
				nodes.add(new Node(7, 5));
				nodes.add(new Node(7, 3));
				nodes.add(new Node(7, 5));
				nodes.add(new Node(4, 5));
				nodes.add(new Node(4, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to my shop. I take #offers to buy items.");
				addJob("I buy and sell weapons. I sell only to the locals here in Fado, but I will buy from you.");
				addHelp("I buy rare weapons, ask me for my #offer.");
				add(ConversationStates.ATTENDING, "offer", null, ConversationStates.ATTENDING,
				        "Please look at the blackboard on the wall to see what I buy.", null);
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING,
				        "Oh, thanks but no thanks. I don't need anything.", null);
				addBuyer(new BuyerBehaviour(shops.get("buyrare2")), false); 
				addGoodbye("Bye - and see you soon.");
			}
		};
		npc.setDescription("You see Yorphin Baos, the friendly shop keeper.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.setEntityClass("weaponsellernpc");
		npc.setPosition(4, 5);
		npc.initHP(100);
		zone.add(npc);

		// Add a blackboard with the shop offers
		Sign blackboard = new Sign();
		zone.assignRPObjectID(blackboard);
		blackboard.setPosition(3, 1);
		blackboard.setText(shops.toString("buyrare2", "-- Buying --"));
		blackboard.setEntityClass("blackboard");
		zone.add(blackboard);
	}
}
