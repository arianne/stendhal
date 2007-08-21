package games.stendhal.server.maps.semos.tavern;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Inside Semos Tavern - Level 1 (upstairs)
 */
public class RareWeaponsSellerNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	private ShopList shops = ShopList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildMcPegleg(zone);
	}

	private void buildMcPegleg(StendhalRPZone zone) {
		// Adding a new NPC that buys some of the stuff that Xin doesn't
		SpeakerNPC mcpegleg = new SpeakerNPC("McPegleg") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(16, 3));
				nodes.add(new Node(13, 3));
				nodes.add(new Node(13, 2));
				nodes.add(new Node(13, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Yo matey! You look like you need #help.");
				addJob("I'm a trader of ... let's say ... #rare things.");
				addHelp("Not sure if I can trust you ....");
				addQuest("Perhaps if you find some #rare #armor or #weapon ...");
				addGoodbye("I see you!");
				add(ConversationStates.ATTENDING, Arrays.asList("weapon", "armor", "rare"),
				        ConversationStates.ATTENDING,
				        "Ssshh! I'm occasionally buying rare weapons and armor. Got any? Ask for my #offer", null);
				add(ConversationStates.ATTENDING, "offer", null, ConversationStates.ATTENDING,
				        "Have a look at the blackboard on the wall to see my offers.", null);
				add(ConversationStates.ATTENDING, Arrays.asList("eye", "leg", "wood", "patch"),
				        ConversationStates.ATTENDING, "Not every day is a lucky day ...", null);
				add(ConversationStates.ATTENDING, "pirate", null, ConversationStates.ATTENDING,
				        "That's none of you business!", null);
				addBuyer(new BuyerBehaviour(shops.get("buyrare")), false);
			}
		};

		// Add some atmosphere
		mcpegleg.setDescription("You see a dubious man with a patched eye and a wooden leg.");

		// Add our new NPC to the game world
		npcs.add(mcpegleg);
		zone.assignRPObjectID(mcpegleg);
		mcpegleg.put("class", "pirate_sailornpc");
		mcpegleg.set(16, 3);
		mcpegleg.initHP(100);
		zone.add(mcpegleg);

		// Add a blackboard with the shop offers
		Sign board = new Sign();
		zone.assignRPObjectID(board);
		board.set(11, 4);
		board.setClass("blackboard");
		board.setText(shops.toString("buyrare", "-- Buying --"));
		zone.add(board);
	}
}
