package games.stendhal.server.maps.nalwor.weaponshop;

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
public class IL0_BuyerNPC implements ZoneConfigurator {

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
		SpeakerNPC npc = new SpeakerNPC("Elodrin") {

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
				addGreeting("*grrr* You dare come in my shop?");
				addJob("I buy weapons. I pay more to elves. Ha!");
				addHelp("I buy rare weapons, ask me for my #offer.");
				add(ConversationStates.ATTENDING, "offer", null, ConversationStates.ATTENDING,
				        "Look at the blackboard on the wall to see what I will buy.", null);
				add(ConversationStates.ATTENDING, ConversationPhrases.QUEST_MESSAGES, null,
				        ConversationStates.ATTENDING,
				        "You think I'd trust a human with anything important? You're wrong!", null);

				// Why does this false go here? What is it? -- addBuyer() adds an FSM trigger
				// to "offer" listing all items the player can sell. But "offer" should refer to
				// the blackboard, so do not make addBuyer() add "offer".
				addBuyer(new BuyerBehaviour(shops.get("elfbuyrare")), false);
				addGoodbye("Bye - be careful not to annoy the other elves as much.");
			}
		};
		npc.setDescription("You see Elodrin, a mean looking elf.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "elfbuyernpc");
		npc.setPosition(4, 5);
		npc.initHP(100);
		zone.add(npc);

		// Add a blackboard with the shop offers
		Sign blackboard = new Sign();
		zone.assignRPObjectID(blackboard);
		blackboard.setPosition(3, 1);
		blackboard.setText(shops.toString("elfbuyrare", "-- Buying --"));
		blackboard.setClass("blackboard");
		zone.add(blackboard);
	}
}
