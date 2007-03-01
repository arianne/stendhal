package games.stendhal.server.maps.semos;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;
import marauroa.common.game.IRPZone;

public class IL0_Library implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("int_semos_library")),
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
		buildSemosLibraryArea(zone, attributes);
	}


	private void buildSemosLibraryArea(StendhalRPZone zone,
	 Map<String, String> attributes) {
		/*
		 * Portals configured in xml?
		 */
		if(attributes.get("xml-portals") == null) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(8);
			portal.setY(30);
			portal.setReference(new Integer(0));
			portal.setDestination("0_semos_city", 3);
			zone.addPortal(portal);

			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(21);
			portal.setY(30);
			portal.setReference(new Integer(1));
			portal.setDestination("0_semos_city", 4);
			zone.addPortal(portal);
		}

		SpeakerNPC npc = new SpeakerNPC("Zynn Iwuhos") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(15, 2));
				nodes.add(new Path.Node(12, 2));
				nodes.add(new Path.Node(12, 5));
				nodes.add(new Path.Node(13, 5));
				nodes.add(new Path.Node(13, 6));
				nodes.add(new Path.Node(13, 5));
				nodes.add(new Path.Node(15, 5));
				nodes.add(new Path.Node(15, 6));
				nodes.add(new Path.Node(15, 5));
				nodes.add(new Path.Node(17, 5));
				nodes.add(new Path.Node(17, 6));
				nodes.add(new Path.Node(17, 2));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.IDLE,
					GREETING_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							// A little trick to make NPC remember if it has met
							// player before anc react accordingly
							// NPC_name quest doesn't exist anywhere else neither is
							// used for any other purpose
							if (!player.isQuestCompleted("Zynn")) {
								engine.say("Hi, potential reader! Here you can find records of the history of Semos, and lots of interesting facts about this island of Faiumoni. If you like, I can give you a quick introduction to its #geography and #history! I also keep up with the #news, so feel free to ask me about that.");
								player.setQuest("Zynn", "done");
							} else {
								engine.say("Hi again, " + player.getName()
										+ ". How can I #help you this time?");
							}
						}
					});
				addHelp("I can best help you by sharing my knowledge of Faiumoni's #geography and #history, as well as the latest #news.");
				addJob("I'm a historian and geographer, committed to writing down every objective fact about Faiumoni. Did you know I wrote most of the books in this library? Well, apart from \"Know How To Kill Creatures\", of course... Hayunn Naratha wrote that.");				addSeller(new SellerBehaviour(shops.get("scrolls")));
				
				add(ConversationStates.ATTENDING,
					QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"I don't think there's really anything you could do for me right now. But thanks for asking!",
					null);

				add(ConversationStates.ATTENDING,
					Arrays.asList("offer", "buy", "scroll", "scrolls", "home", "empty", "marked", "summon", "magic", "wizard", "sorcerer"),
					null,
					ConversationStates.ATTENDING,
					"I don't sell scrolls anymore... I had a big argument with my supplier, #Haizen.",
					null);

				add(ConversationStates.ATTENDING,
					Arrays.asList("haizen", "haizen."),
					null,
					ConversationStates.ATTENDING,
					"Haizen? He's a wizard who lives in a small hut between Semos and Ados. I used to sell his scrolls here, but we had an argument... you'll have to go see him yourself, I'm afraid.",
					null);
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "wisemannpc");
		npc.set(15, 2);
		npc.initHP(100);
		zone.addNPC(npc);
		npc = new SpeakerNPC("Ceryl") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(28, 11));
				nodes.add(new Path.Node(28, 20));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am the librarian.");
				addHelp("Hey, read a book and help yourself! You're never too old to stop learning.");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "investigatornpc");
		npc.set(28, 11);
		npc.initHP(100);
		zone.addNPC(npc);
	}
}
