package games.stendhal.server.maps.ados.barracks;

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
 * Builds an NPC to buy previously un bought armor.
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
		SpeakerNPC npc = new SpeakerNPC("Mrotho") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(45, 48));
				nodes.add(new Node(29, 48));
				nodes.add(new Node(29, 56));
				nodes.add(new Node(45, 56));
				nodes.add(new Node(19, 56));
				nodes.add(new Node(19, 48));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings. Have you come to enlist as a soldier?");
				addReply("yes", "Huh! Well I don't let your type enlist! Perhaps you want to #offer some of that armor instead...");
				addReply("no", "Good! You wouldn't have fit in here anyway.");
				addJob("I'm looking after the weaponry here. We're running low. I see you have some armor you might #offer though.");
				addHelp("I buy armor for the barracks here, make me an #offer.");
				add(ConversationStates.ATTENDING, "offer", null, ConversationStates.ATTENDING,
				        "Please look at the blackboard by the shields rack to see what we are short of, and what we pay.", null);
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING,
				        "Oh, thanks but no thanks. I don't need anything.", null);
				addBuyer(new BuyerBehaviour(shops.get("buyrare3")), false);
				addGoodbye("Goodbye, comrade.");
			}
		};
		npc.setDescription("You see Mrotho, guarding over Ados Barracks.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "barracksbuyernpc");
		npc.set(45, 48);
		npc.initHP(500);
		zone.add(npc);

		// Add a blackboard with the shop offers
		Sign blackboard = new Sign();
		zone.assignRPObjectID(blackboard);
		blackboard.set(35, 52);
		blackboard.setText(shops.toString("buyrare3", "-- Required --"));
		blackboard.setClass("blackboard");
		zone.add(blackboard);
	}
}
