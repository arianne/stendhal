package games.stendhal.server.maps.semos;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.NPCOwnedChest;
import games.stendhal.server.entity.PersonalChest;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.pathfinder.Path;
import marauroa.common.game.IRPZone;

public class SemosCityInside {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		buildSemosTempleArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
			"int_semos_temple")));
		buildSemosLibraryArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
			"int_semos_library")));
		buildSemosStorageArea();
		buildSemosBlacksmithArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
			"int_semos_blacksmith")));
		buildSemosBankArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
			"int_semos_bank")));
		buildSemosTownhallArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
			"int_semos_townhall")));
		buildSemosBakeryArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
			"int_semos_bakery")));
	}


	private void buildSemosTownhallArea(StendhalRPZone zone) {
		for (int i = 0; i < 3; i++) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(14 + i);
			portal.setY(46);
			portal.setNumber(i);
			portal.setDestination("0_semos_city", 7 + i);
			zone.addPortal(portal);
		}
		SpeakerNPC npc = new SpeakerNPC("Tad") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting(null, new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("introduce_players")) {
							engine.say("Ssshh! Come here, " + player.getName()
									+ "! I have a #task for you.");
						} else {
							engine.say("Hi again "
									+ player.getName()
									+ "! Thanks again, I'm feeling much better now.");
						}
					}
				});
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.addInitChatMessage(null, new SpeakerNPC.ChatAction() {
			@Override
			public void fire(Player player, String text, SpeakerNPC engine) {
				if (!player.hasQuest("TadFirstChat")) {
					player.setQuest("TadFirstChat", "done");
					engine.listenTo(player, "hi");
				}
			}
		});
		npc.put("class", "childnpc");
		npc.set(13, 37);
		npc.setDirection(Direction.RIGHT);
		npc.initHP(100);
		zone.addNPC(npc);
	}

	private void buildSemosBankArea(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(9);
		portal.setY(30);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 6);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(10);
		portal.setY(30);
		portal.setNumber(1);
		portal.setDestination("0_semos_city", 6);
		zone.addPortal(portal);
		for (int i = 0; i < 4; i++) {
			PersonalChest chest = new PersonalChest();
			zone.assignRPObjectID(chest);
			chest.set(2 + 6 * i, 2);
			zone.add(chest);
			chest = new PersonalChest();
			zone.assignRPObjectID(chest);
			chest.set(2 + 6 * i, 13);
			zone.add(chest);
		}
		SpeakerNPC npc = new SpeakerNPC("Dagobert") {
			@Override
			protected void createPath() {
				// NPC doesn't move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to the bank of Semos! Do you need #help on your personal chest?");
				addHelp("Follow the corridor to the right, and you will find the magic chests. You can store your belongings in any of them, and nobody else will be able to touch them!");
				addJob("I'm the Customer Advisor here at Semos Bank.");
				addGoodbye("It was a pleasure to serve you.");
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "youngnpc");
		npc.set(9, 22);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.addNPC(npc);
	}

	private void buildSemosStorageArea() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"int_semos_storage_0"));
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(9);
		portal.setY(14);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 5);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(16);
		portal.setY(2);
		portal.setNumber(1);
		portal.setDestination("int_semos_storage_-1", 0);
		zone.addPortal(portal);
		SpeakerNPC npc = new SpeakerNPC("Eonna") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(4, 12)); // its around the table with
				// the beers and to the
				// furnance
				nodes.add(new Path.Node(15, 12));
				nodes.add(new Path.Node(15, 12));
				nodes.add(new Path.Node(15, 8));
				nodes.add(new Path.Node(10, 8));
				nodes.add(new Path.Node(10, 12));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi there, young hero.");
				addJob("I'm just a regular housewife.");
				addHelp("I don't think I can help you with anything.");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "welcomernpc");
		npc.set(4, 12);
		npc.initHP(100);
		zone.addNPC(npc);
		npc = new SpeakerNPC("Ketteh Wehoh") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(21, 5));
				nodes.add(new Path.Node(29, 5));
				nodes.add(new Path.Node(29, 9));
				nodes.add(new Path.Node(21, 9));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addHelp("I am the town Decency and Manners Warden. I can advise you on how to conduct yourself in many ways; like not wandering around naked, for instance.");
				addJob("My job is to maintain a civilized level of behaviour in Semos. I know the protocol for every situation, AND all the ways of handling it wrong. Well, sometimes I get confused on whether to use a spoon or a fork; but then, nobody really uses cutlery in Semos anyway.");
				add(ConversationStates.ATTENDING,
					QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"The only task I have for you is to behave nicely towards others.",
					null);
				addGoodbye();
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "elegantladynpc");
		npc.set(21, 5);
		npc.initHP(100);
		zone.addNPC(npc);
		zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"int_semos_storage_-1"));
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(26);
		portal.setY(10);
		portal.setNumber(0);
		portal.setDestination("int_semos_storage_0", 1);
		zone.addPortal(portal);
	}

	private void buildSemosBlacksmithArea(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(15);
		portal.setY(14);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 2);
		zone.addPortal(portal);
		
		SpeakerNPC hackim = new SpeakerNPC("Hackim Easso") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(5, 1));
				nodes.add(new Path.Node(8, 1));
				nodes.add(new Path.Node(7, 1));
				nodes.add(new Path.Node(7, 6));
				nodes.add(new Path.Node(16, 6));
				nodes.add(new Path.Node(16, 1));
				nodes.add(new Path.Node(15, 1));
				nodes.add(new Path.Node(16, 1));
				nodes.add(new Path.Node(16, 6));
				nodes.add(new Path.Node(7, 6));
				nodes.add(new Path.Node(7, 1));
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
							// player before and react accordingly
							// NPC_name quest doesn't exist anywhere else neither is
							// used for any other purpose
							if (!player.isQuestCompleted("Hackim")) {
								engine.say("Hi stranger, I'm Hackim Easso, the blacksmith's assistant. Have you come here to buy weapons?");								player.setQuest("Hackim", "done");
							} else {
								engine.say("Hi again, " + player.getName()
										+ ". How can I #help you this time?");
							}
						}
					});
				addHelp("I'm the blacksmith's assistant. Tell me... Have you come here to buy weapons?");
				addJob("I help Xoderos the blacksmith to make weapons for Deniran's army. I mostly only bring the coal for the fire and put the weapons up on the shelves. Sometimes, when Xoderos isn't looking, I like to use one of the swords to pretend I'm a famous adventurer!");
				addGoodbye();
			}
		};
		npcs.add(hackim);
		zone.assignRPObjectID(hackim);
		hackim.put("class", "naughtyteennpc");
		hackim.set(5, 1);
		hackim.initHP(100);
		zone.addNPC(hackim);
		
		SpeakerNPC xoderos = new SpeakerNPC("Xoderos") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(23, 11));
				nodes.add(new Path.Node(29, 11));
				nodes.add(new Path.Node(29, 4));
				nodes.add(new Path.Node(17, 4));
				nodes.add(new Path.Node(17, 8));
				nodes.add(new Path.Node(28, 8));
				nodes.add(new Path.Node(28, 11));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				add(ConversationStates.ATTENDING,
						"wood",
						null,
						ConversationStates.ATTENDING,
						"I need some wood to keep my furnace lit. You can find any amount of it just lying around in the forest.",
						null);
				
				add(ConversationStates.ATTENDING,
						Arrays.asList("ore", "iron", "iron_ore"),
						null,
						ConversationStates.ATTENDING,
						"You can find iron ore up in the mountains west of Or'ril, near the dwarf mines. Be careful up there!",
						null);
				addHelp("If you bring me #wood and #iron_ore, I can #cast the iron for you. Then you could sell it to the dwarves, to make yourself a little money.");
				addJob("Greetings. Unfortunately, because of the war, I'm not currently allowed to sell weapons to anyone except the official armoury. However, I can still #cast iron for you, or I can make you an #offer on some good tools.");
				addGoodbye();
				// Once Ados is ready, we can have an expert tool smith; then Xoderos
				// won't sell tools anymore.
				addSeller(new SellerBehaviour(ShopList.get().get("selltools")));

				// Xoderos casts iron if you bring him wood and iron ore.
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("wood", new Integer(1));
				requiredResources.put("iron_ore", new Integer(1));

				ProducerBehaviour behaviour = new ProducerBehaviour(
						"xoderos_cast_iron", "cast", "iron", requiredResources, 5 * 60);

				addProducer(behaviour,
							"Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.");

			}
		};
		npcs.add(xoderos);
		zone.assignRPObjectID(xoderos);
		xoderos.put("class", "blacksmithnpc");
		xoderos.set(23, 11);
		xoderos.initHP(100);
		zone.addNPC(xoderos);
	}

	private void buildSemosLibraryArea(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(8);
		portal.setY(30);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 3);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(21);
		portal.setY(30);
		portal.setNumber(1);
		portal.setDestination("0_semos_city", 4);
		zone.addPortal(portal);
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

	private void buildSemosBakeryArea(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(26);
		portal.setY(14);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 10);
		zone.addPortal(portal);

		SpeakerNPC erna = new SpeakerNPC("Erna") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(26, 8));
				nodes.add(new Path.Node(26, 5));
				nodes.add(new Path.Node(28, 5));
				nodes.add(new Path.Node(28, 1));
				nodes.add(new Path.Node(28, 4));
				nodes.add(new Path.Node(22, 4));
				nodes.add(new Path.Node(22, 3));
				nodes.add(new Path.Node(22, 6));
				nodes.add(new Path.Node(26, 6));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addJob("I'm the shop assistant at this bakery.");
				addReply("flour", "We usually get our #flour from a mill northeast of here, but the wolves ate their delivery boy! If you help us out by bringing some, we can #bake delicious bread for you.");
				addHelp("Bread is very good for you, especially for you adventurers who are always gulping down red meat. And my boss, Leander, happens to make the best sandwiches on the island!");
				addGoodbye();

				// Erna bakes bread if you bring her flour.
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("flour", new Integer(2));

				ProducerBehaviour behaviour = new ProducerBehaviour(
						"erna_bake_bread", "bake", "bread", requiredResources, 10 * 60);

				addProducer(behaviour, "Welcome to the Semos bakery! We'll #bake fine bread for anyone who helps bring our #flour delivery from the mill.");
			}
		};
		npcs.add(erna);
		zone.assignRPObjectID(erna);
		erna.put("class", "housewifenpc");
		erna.set(26, 8);
		erna.initHP(100);
		zone.addNPC(erna);

		SpeakerNPC leander = new SpeakerNPC("Leander") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				// to the well
				nodes.add(new Path.Node(15, 2));
				// to a barrel
				nodes.add(new Path.Node(15, 7));
				// to the baguette on the table
				nodes.add(new Path.Node(13, 7));
				// around the table
				nodes.add(new Path.Node(13, 9));
				nodes.add(new Path.Node(10, 9));
				// to the sink
				nodes.add(new Path.Node(10, 11));
				// to the pizza/cake/whatever
				nodes.add(new Path.Node(7, 11));
				nodes.add(new Path.Node(7, 9));
				// to the pot
				nodes.add(new Path.Node(3, 9));
				// towards the oven
				nodes.add(new Path.Node(3, 3));
				nodes.add(new Path.Node(5, 3));
				// to the oven
				nodes.add(new Path.Node(5, 2));
				// one step back
				nodes.add(new Path.Node(5, 3));
				// towards the well
				nodes.add(new Path.Node(15, 3));
				
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addJob("I'm the local baker. We used to get a lot of orders from Ados before the war broke out and they blocked the road. At least it gives me more time to #make sandwiches for out valuable customers; everybody says they're great!");
				addReply("bread", "Oh, Erna handles that side of the business; just go over and talk to her.");
				addReply("cheese", "Cheese is pretty hard to find at the minute, we had a big rat infestation recently. I wonder where the little rodents took it all to?");
				addReply("ham", "Well, you look like a skilled hunter; why not go to the forest and hunt some up fresh? Don't bring me those little pieces of meat, though... I only make sandwiches from high quality ham!");
				addHelp("My daughter Sally might be able to help you get ham. She's a scout, you see; I think she's currently camped out south of Or'ril Castle.");				addGoodbye();

				// Leander makes sandwiches if you bring him bread, cheese, and ham.
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("bread", new Integer(1));
				requiredResources.put("cheese", new Integer(2));
				requiredResources.put("ham", new Integer(1));

				ProducerBehaviour behaviour = new ProducerBehaviour(
						"leander_make_sandwiches", "make", "sandwich", requiredResources, 3 * 60);

				addProducer(behaviour,
						"Hi! I bet you've heard about my famous sandwiches and want me to #make you one, am I right?");
			}
		};
		npcs.add(leander);
		zone.assignRPObjectID(leander);
		leander.put("class", "chefnpc");
		leander.setDirection(Direction.DOWN);
		leander.set(15, 2);
		leander.initHP(100);
		zone.addNPC(leander);
		
		Chest chest = new NPCOwnedChest(erna);
		zone.assignRPObjectID(chest);
		chest.set(29, 6);
		zone.add(chest);
	}

	private void buildSemosTempleArea(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(10);
		portal.setY(23);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 1);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(11);
		portal.setY(23);
		portal.setNumber(1);
		portal.setDestination("0_semos_city", 1);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(12);
		portal.setY(23);
		portal.setNumber(2);
		portal.setDestination("0_semos_city", 1);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(13);
		portal.setY(23);
		portal.setNumber(3);
		portal.setDestination("0_semos_city", 1);
		zone.addPortal(portal);
		SpeakerNPC npc = new SpeakerNPC("Ilisa") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(9, 5));
				nodes.add(new Path.Node(14, 5));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("My special powers help me to heal wounded people. I also sell potions and antidotes.");
				addHelp("I can #heal you here for free, or you can take one of my prepared medicines with you on your travels; just ask for an #offer.");
				addSeller(new SellerBehaviour(shops.get("healing")));
				addHealer(0);
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "welcomernpc");
		npc.set(9, 5);
		npc.initHP(100);
		zone.addNPC(npc);
		npc = new SpeakerNPC("Io Flotto") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(8, 18));
				nodes.add(new Path.Node(8, 19));
				nodes.add(new Path.Node(15, 19));
				nodes.add(new Path.Node(15, 18));
				nodes.add(new Path.Node(16, 18));
				nodes.add(new Path.Node(16, 13));
				nodes.add(new Path.Node(15, 13));
				nodes.add(new Path.Node(15, 12));
				nodes.add(new Path.Node(12, 12));
				nodes.add(new Path.Node(8, 12));
				nodes.add(new Path.Node(8, 13));
				nodes.add(new Path.Node(7, 13));
				nodes.add(new Path.Node(7, 18));
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
							if (!player.isQuestCompleted("Io")) {
								engine.say("I awaited you, "
										   + player.getName()
										   + ". How do I know your name? Easy, I'm Io Flotto, the telepath. Do you want me to show you the six basic elements of telepathy?");
								player.setQuest("Io", "done");
							} else {
								engine.say("Hi again, "
										   + player.getName()
										   + ". How can I #help you this time? Not that I don't already know...");
							}
						}
				});
				addHelp("I'm a telepath and a telekinetic; I can help you by sharing my mental skills with you. Do you want me to teach you the six basic elements of telepathy? I already know the answer but I'm being polite...");
				addJob("I am committed to harnessing the total power of the human mind. I have already made great advances in telepathy and telekinesis; however, I can't yet foresee the future, so I don't know if we will truly be able to destroy Blordrough's dark legion...");
				add(ConversationStates.ATTENDING,
					QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"Well, there's not really much that I need anyone to do for me right now. And I... Hey! Were you just trying to read my private thoughts? You should always ask permission before doing that!",
					null);
				addGoodbye();
			}
		};
		npcs.add(npc);
		
		zone.assignRPObjectID(npc);
		npc.put("class", "floattingladynpc");
		npc.set(8, 18);
		npc.initHP(100);
		zone.addNPC(npc);
	}

}
