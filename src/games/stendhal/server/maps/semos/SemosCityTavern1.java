package games.stendhal.server.maps.semos;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Blackboard;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;

/*
 * Inside Semos Tavern - Level 1 (upstairs)
 */
public class SemosCityTavern1 implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();

	public void build() {
		StendhalRPWorld	world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("int_semos_tavern_1")),
			java.util.Collections.EMPTY_MAP);
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildPortals(zone, attributes);
		buildOuchit(zone);
		buildMcPegleg(zone);
	}


	private void buildPortals(StendhalRPZone zone,
	 Map<String, String> attributes) {
		/*
		 * Portals configured in xml?
		 */
		if(attributes.get("xml-portals") == null) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.set(4, 4);
			portal.setReference(new Integer(0));
			portal.setDestination("int_semos_tavern_0", 1);
			zone.addPortal(portal);
		}
	}

	private void buildOuchit(StendhalRPZone zone) {
		SpeakerNPC ouchit = new SpeakerNPC("Ouchit") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(21, 2));
				nodes.add(new Path.Node(25, 2));
				nodes.add(new Path.Node(25, 4));
				nodes.add(new Path.Node(29, 4));
				nodes.add(new Path.Node(25, 4));
				nodes.add(new Path.Node(25, 2));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I sell bows and arrows.");
				addHelp("I sell several items, ask me for my #offer.");
				addSeller(new SellerBehaviour(shops.get("sellrangedstuff")));
				addGoodbye();
			}
		};
		npcs.add(ouchit);
		zone.assignRPObjectID(ouchit);
		ouchit.put("class", "weaponsellernpc");
		ouchit.set(21, 2);
		ouchit.initHP(100);
		zone.addNPC(ouchit);
	}

	private void buildMcPegleg(StendhalRPZone zone) {
		// Adding a new NPC that buys some of the stuff that Xin doesn't
		SpeakerNPC mcpegleg = new SpeakerNPC("McPegleg") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(16, 2));
				nodes.add(new Path.Node(13, 2));
				nodes.add(new Path.Node(13, 1));
				nodes.add(new Path.Node(13, 2));
				setPath(nodes, true);
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
					"Ssshh! I'm occasionally buying rare weapons and armor. Got any? Ask for my #offer",
					null);
				add(ConversationStates.ATTENDING,
					"offer",
					null,
					ConversationStates.ATTENDING,
					"Have a look at the blackboard on the wall to see my offers.",
					null);
				add(ConversationStates.ATTENDING, Arrays.asList("eye","leg","wood","patch"),
						ConversationStates.ATTENDING,
						"Not every day is a lucky day ...",
						null);
				add(ConversationStates.ATTENDING, "pirate", 
						null, ConversationStates.ATTENDING, 
						"That's none of you business!",
						null);
				addBuyer(new BuyerBehaviour(shops.get("buyrare")), false);
			}
		};

		// Add some atmosphere
		mcpegleg.setDescription("You see a dubious man with a patched eye and a wooden leg.");  
		  
		// Add our new NPC to the game world
		npcs.add(mcpegleg);
		zone.assignRPObjectID(mcpegleg);
		mcpegleg.put("class", "pirate_sailornpc");
		mcpegleg.set(16, 2);
		mcpegleg.initHP(100);
		zone.addNPC(mcpegleg);

		// Add a blackboard with the shop offers
		Blackboard board = new Blackboard(false);
		zone.assignRPObjectID(board);
		board.set(11, 4);
		board.setText(shops.toString("buyrare", "-- Buying --"));
		zone.add(board);
	}
}
