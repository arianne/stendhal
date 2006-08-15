package games.stendhal.server.maps;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.AdministrationAction;
import games.stendhal.server.entity.Blackboard;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.OneWayPortal;
import games.stendhal.server.entity.PersonalChest;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Portal;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

public class Semos implements IContent {
	private StendhalRPWorld world;

	private NPCList npcs;

	private ShopList shops;

	public Semos(StendhalRPWorld world) {
		this.npcs = NPCList.get();
		this.shops = ShopList.get();
		this.world = world;
		buildSemosCityArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"0_semos_city")));
		buildSemosVillageArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"0_semos_village_w")));
		buildSemosNorthPlainsArea((StendhalRPZone) world
				.getRPZone(new IRPZone.ID("0_semos_plains_n")));
		buildSemosSouthPlainsArea((StendhalRPZone) world
				.getRPZone(new IRPZone.ID("0_semos_plains_s")));
		buildSemosTavernArea();
		buildSemosBlacksmithArea((StendhalRPZone) world
				.getRPZone(new IRPZone.ID("int_semos_blacksmith")));
		buildSemosTempleArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_semos_temple")));
		buildSemosLibraryArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_semos_library")));
		buildSemosStorageArea();
		buildSemosBankArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_semos_bank")));
		buildSemosTownhallArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_semos_townhall")));
	}

	private void buildSemosTownhallArea(StendhalRPZone zone) {
		for (int i = 0; i < 5; i++) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setx(13 + i);
			portal.sety(46);
			portal.setNumber(i);
			portal.setDestination("0_semos_city", 7);
			zone.addPortal(portal);
		}
		SpeakerNPC npc = new SpeakerNPC("Tad") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			protected void createDialog() {
				addGreeting(null, new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						if (!player.isQuestCompleted("introduce_players")) {
							engine.say("Ssshh! Come here #" + player.getName()
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
		portal.setx(9);
		portal.sety(30);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 6);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(10);
		portal.sety(30);
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
			protected void createPath() {
				// NPC doesn't move
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			protected void createDialog() {
				addGreeting("Welcome to the bank of Semos! Do you need #help on your personal chest?");
				addHelp("You can find your personal chest down the floor to the right. If you open it, you can store your belongings in it. I will take care that nobody else will touch them.");
				addJob("I'm the customer consultant.");
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
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_semos_storage_0"));
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(9);
		portal.sety(14);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 5);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(16);
		portal.sety(2);
		portal.setNumber(1);
		portal.setDestination("int_semos_storage_-1", 0);
		zone.addPortal(portal);
		SpeakerNPC npc = new SpeakerNPC("Eonna") {
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

			protected void createDialog() {
				addGreeting("Hi there, young hero.");
				addJob("I'm just a regular housewife");
				addHelp("I think I can't help you with anything.");
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
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(21, 5));
				nodes.add(new Path.Node(29, 5));
				nodes.add(new Path.Node(29, 9));
				nodes.add(new Path.Node(21, 9));
				setPath(nodes, true);
			}

			protected void createDialog() {
				addHelp("I am the good manners and decency observer. I can help you by telling you about obvious and common sense things you should already know like not wandering naked around...");
				addJob("I am committed to keep civilized customs in Semos. I know any kind of protocol ever known and one hundred manners of doing the same thing wrong. Well, I doubt about when it should be used the spoon or the fork but on the other hand nobody uses cutlery in Semos");
				add(ConversationStates.ATTENDING,
					QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"I do not have any task for you right now. If you need anything from me just say it.",
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
		zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_semos_storage_-1"));
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(26);
		portal.sety(10);
		portal.setNumber(0);
		portal.setDestination("int_semos_storage_0", 1);
		zone.addPortal(portal);
	}

	private void buildSemosBlacksmithArea(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(15);
		portal.sety(14);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 2);
		zone.addPortal(portal);
		
		SpeakerNPC hackim = new SpeakerNPC("Hackim Easso") {
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

			protected void createDialog() {
				add(ConversationStates.IDLE,
					GREETING_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new SpeakerNPC.ChatAction() {
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							// A little trick to make NPC remember if it has met
							// player before and react accordingly
							// NPC_name quest doesn't exist anywhere else neither is
							// used for any other purpose
							if (!player.isQuestCompleted("Hackim")) {
								engine.say("Hi foreigner, I'm Hackim Easso, the blacksmith's assistant. Have you come here to buy weapons?");
								player.setQuest("Hackim", "done");
							} else {
								engine.say("Hi again, " + player.getName()
										+ ". How can I #help you this time?");
							}
						}
					});
				addHelp("I'm the blacksmith's assistant. I can help you by sharing my curiosity with you... Have you come here to buy weapons?");
				addJob("I help Xoderos the blacksmith in making weapons for Deniran's army. I really only bring the coal for the fire but guess who puts the weapons so ordered on the shelves. Yes, it is me.");
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

			protected void createDialog() {
				addGreeting("Greetings. How can I serve you?");

				add(ConversationStates.ATTENDING,
						"wood",
						null,
						ConversationStates.ATTENDING,
						"I need wood to fire the melting furnace. You can find it lying around in the woods.",
						null);
				
				add(ConversationStates.ATTENDING,
						"iron_ore",
						null,
						ConversationStates.ATTENDING,
						"There is a dwarf mine in the mountains West of the Orril. You can find iron ore lying around there, but be careful!",
						null);

				add(ConversationStates.ATTENDING,
						"cast",
						null,
						ConversationStates.ATTENDING,
						null,
						new SpeakerNPC.ChatAction() {
							public void fire(Player player, String text,
									SpeakerNPC engine) {
								// how much iron should the player get?
								int numberOfIron;
								// TODO: It is possible that the player has several stacks of wood.
								// create a function player.getEquippedQuantity(String itemName)
								// that iterates over all slots.
								try {
									int numberOfWood = ((StackableItem) player.getEquipped("wood")).getQuantity();
									int numberOfIronOre = ((StackableItem) player.getEquipped("iron_ore")).getQuantity();
									numberOfIron = Math.min(numberOfWood, numberOfIronOre);
								} catch (NullPointerException e) {
									// The player lacks at least one of the
									// required resources 
									numberOfIron = 0;
								}
								if (numberOfIron == 0) {
									engine.say("I can only cast iron if you bring me both #wood and #iron_ore.");
								} else {
									player.drop("wood", numberOfIron);
									player.drop("iron_ore", numberOfIron);
									StackableItem iron = (StackableItem) world.getRuleManager().getEntityManager().getItem("iron");            
									iron.setQuantity(numberOfIron);
									player.equip(iron, true);
									engine.say("Thank you, here you have "
											+ numberOfIron + " bars of iron.");
								}
							}
						});

				add(ConversationStates.ATTENDING,
						new String[] {"offer", "buy"},
						null,
						ConversationStates.ATTENDING,
						"I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you.",
						null);
				
				 
				addHelp("If you bring me #wood and #iron_ore, I can #cast iron for you. You can then sell it back to the dwarves.");
				addJob("I am the local blacksmith. I am proud to help Deniran's army by producing weapons.");
				addGoodbye();
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
		portal.setx(8);
		portal.sety(30);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 3);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(21);
		portal.sety(30);
		portal.setNumber(1);
		portal.setDestination("0_semos_city", 4);
		zone.addPortal(portal);
		SpeakerNPC npc = new SpeakerNPC("Zynn Iwuhos") {
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

			protected void createDialog() {
				add(ConversationStates.IDLE,
					GREETING_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new SpeakerNPC.ChatAction() {
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							// A little trick to make NPC remember if it has met
							// player before anc react accordingly
							// NPC_name quest doesn't exist anywhere else neither is
							// used for any other purpose
							if (!player.isQuestCompleted("Zynn")) {
								engine
										.say("Hi, potential reader. Here's recorded all the history of Semos city and some facts about the whole island of Faiumoni in which we are. I can give you an introduction to its #geography and #history. I can report you the latest #news.");
								player.setQuest("Zynn", "done");
							} else {
								engine.say("Hi again, " + player.getName()
										+ ". How can I #help you this time?");
							}
						}
					});
				addHelp("I'm a historian. I can help you by sharing my knowledge with you... I can tell you about Faiumoni's #geography and #history. I can report you the latest #news.");
				addJob("I am committed to register every objective fact about Faiumoni. I've written most of the books in this library. Well, except the book \"Know how to kill creatures\" by Hayunn Naratha");
				addSeller(new SellerBehaviour(world, shops.get("scrolls")));
				
				add(ConversationStates.ATTENDING,
					QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"I do not have any task for you right now. If you need anything from me just say it.",
					null);

				add(ConversationStates.ATTENDING,
					new String[] { "offer", "buy", "scroll", "scrolls", "home", "empty", "marked", "summon", "magic", "wizard", "sorcerer" },
					null,
					ConversationStates.ATTENDING,
					"I stopped selling scrolls but you still can buy them from #Haizen.",
					null);

				add(ConversationStates.ATTENDING,
					new String[] { "haizen", "haizen." },
					null,
					ConversationStates.ATTENDING,
					"Haizen is a wizard living in a small hut between Semos and Ados.",
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
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(28, 11));
				nodes.add(new Path.Node(28, 20));
				setPath(nodes, true);
			}

			protected void createDialog() {
				addGreeting();
				addJob("I am the librarian.");
				addHelp("Read!");
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

	private void buildSemosTempleArea(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(10);
		portal.sety(23);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 1);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(11);
		portal.sety(23);
		portal.setNumber(1);
		portal.setDestination("0_semos_city", 1);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(12);
		portal.sety(23);
		portal.setNumber(2);
		portal.setDestination("0_semos_city", 1);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(13);
		portal.sety(23);
		portal.setNumber(3);
		portal.setDestination("0_semos_city", 1);
		zone.addPortal(portal);
		SpeakerNPC npc = new SpeakerNPC("Ilisa") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(9, 5));
				nodes.add(new Path.Node(14, 5));
				setPath(nodes, true);
			}

			protected void createDialog() {
				addGreeting();
				addJob("I have healing abilities and I heal wounded people. I also sell potions and antidotes.");
				addHelp("Ask me to #heal you and I will help you or ask me #offer and I will show my shop's stuff.");
				addSeller(new SellerBehaviour(world, 
						shops.get("healing")));
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

			protected void createDialog() {
				add(ConversationStates.IDLE,
					GREETING_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new SpeakerNPC.ChatAction() {
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							// A little trick to make NPC remember if it has met
							// player before anc react accordingly
							// NPC_name quest doesn't exist anywhere else neither is
							// used for any other purpose
							if (!player.isQuestCompleted("Io")) {
								engine
										.say("I waited you, "
												+ player.getName()
												+ ". How do I know your name? Easy, I'm Io Flotto, the telepath. Do you want me to show you the six basic elements of telepathy?");
								player.setQuest("Io", "done");
							} else {
								engine
										.say("Hi again, "
												+ player.getName()
												+ ". How can I #help you this time? Not that I don't already know...");
							}
						}
				});
				addHelp("I'm a telepath and telekinetic. I can help you by sharing my mental skills with you... Do you want me to show you the six basic elements of telepathy? I already know the answer but I'm being polite...");
				addJob("I am committed to develop the unknown potential power of the mind. Up to this day I've made great advances in telepathy and telekinesis. However, I can't foresee the future yet and if finally we will be able to destroy Blordrough's dark legion");
				add(ConversationStates.ATTENDING,
					QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"I do not have any task for you right now. If you need anything from me just say it. I think it's simply unkind reading one's mind without permission.",
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

	private void buildSemosTavernArea() {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_semos_tavern_1"));
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.set(4, 4);
		portal.setNumber(0);
		portal.setDestination("int_semos_tavern_0", 1);
		zone.addPortal(portal);
		zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"int_semos_tavern_0"));
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(22);
		portal.sety(17);
		portal.setNumber(0);
		portal.setDestination("0_semos_city", 0);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.set(4, 4);
		portal.setNumber(1);
		portal.setDestination("int_semos_tavern_1", 0);
		zone.addPortal(portal);
		SpeakerNPC npc = new SpeakerNPC("Margaret") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(17, 12));
				nodes.add(new Path.Node(17, 13));
				nodes.add(new Path.Node(16, 8));
				nodes.add(new Path.Node(13, 8));
				nodes.add(new Path.Node(13, 6));
				nodes.add(new Path.Node(13, 10));
				nodes.add(new Path.Node(23, 10));
				nodes.add(new Path.Node(23, 10));
				nodes.add(new Path.Node(23, 13));
				nodes.add(new Path.Node(23, 10));
				nodes.add(new Path.Node(17, 10));
				setPath(nodes, true);
			}

			protected void createDialog() {
				addGreeting();
				addJob("I am the bar maid for this fair tavern. We sell fine beers and food.");
				addHelp("At the tavern you can get an #offer of drinks and take a break to meet new people!");
				addSeller(new SellerBehaviour(world,
						shops.get("food&drinks")));
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "tavernbarmaidnpc");
		npc.set(17, 12);
		npc.initHP(100);
		zone.addNPC(npc);
		npc = new SpeakerNPC("Xin Blanca") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(2, 14));
				nodes.add(new Path.Node(2, 15));
				nodes.add(new Path.Node(5, 15));
				nodes.add(new Path.Node(5, 14));
				setPath(nodes, true);
			}

			protected void createDialog() {
				addGreeting();
				addJob("Shhh! I sell adventurers stuff.");
				addHelp("I buy and sell several items, ask me for my offer");
				addSeller(new SellerBehaviour(world,
						shops.get("sellstuff")), false);
				addBuyer(new BuyerBehaviour(world,
						shops.get("buystuff")), false);
				add(ConversationStates.ATTENDING,
					"offer",
					null,
					ConversationStates.ATTENDING,
					"Have a look at the blackboards on the wall to see my offers",
					null);
				addGoodbye();
				StendhalRPZone zone = (StendhalRPZone) world
						.getRPZone(new IRPZone.ID("int_semos_tavern_0"));
				Blackboard board = new Blackboard(false);
				zone.assignRPObjectID(board);
				board.set(2, 11);
				board.setText(shops.toString("sellstuff", "-- Selling --"));
				zone.add(board);
				board = new Blackboard(false);
				zone.assignRPObjectID(board);
				board.set(3, 11);
				board.setText(shops.toString("buystuff", "-- Buying --"));
				zone.add(board);
			}
		};
		npcs.add(npc);
		
		zone.assignRPObjectID(npc);
		npc.put("class", "weaponsellernpc");
		npc.setx(2);
		npc.sety(14);
		npc.setBaseHP(100);
		npc.setHP(npc.getBaseHP());
		zone.addNPC(npc);
		npc = new SpeakerNPC("Ouchit") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(24, 3));
				nodes.add(new Path.Node(24, 5));
				nodes.add(new Path.Node(28, 5));
				nodes.add(new Path.Node(28, 3));
				setPath(nodes, true);
			}

			protected void createDialog() {
				addGreeting();
				addJob("I sell bows and arrows stuff.");
				addHelp("I sell several items, ask me for my #offer");
				addSeller(new SellerBehaviour(world,
						shops.get("sellrangedstuff")));
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "weaponsellernpc");
		npc.set(24, 3);
		;
		npc.initHP(100);
		zone.addNPC(npc);
	}

	private void buildSemosSouthPlainsArea(StendhalRPZone zone) {
		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(118);
		sign.sety(43);
		sign.setText("You are about to leave this area to move to the forest.\nYou may fatten up your sheep there on wild berries.\nBe careful though, these forests crawl with wolves.");
		zone.add(sign);
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(38);
		sign.sety(3);
		sign.setText("You are about to leave this area to move to the village.\nYou can buy a new sheep there.");
		zone.add(sign);
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(113);
		sign.sety(3);
		sign
				.setText("You are about to leave this area to move to the city.\nYou can sell your sheep there.");
		zone.add(sign);
	}

	private void buildSemosVillageArea(StendhalRPZone zone) {
		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(23);
		sign.sety(61);
		sign.setText("You are about to leave this area and move to the plains.\nYou may fatten up your sheep there on the wild berries.\nBe careful though, wolves roam these plains.");
		zone.add(sign);
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(60);
		sign.sety(47);
		sign.setText("You are about to leave this area to move to the city.\nYou can sell your sheep there.");
		zone.add(sign);
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(16);
		sign.sety(35);
		sign.setText("[CLOSED]\nThe tavern has moved to a much\nbetter and central house in town.\nCome buy your weapons, find your\nquests and hang out there instead.");
		zone.add(sign);
	}

	private void buildSemosNorthPlainsArea(StendhalRPZone zone) {

		SpeakerNPC npc = new SpeakerNPC("Plink") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(36, 108));
				nodes.add(new Path.Node(37, 108));
				nodes.add(new Path.Node(37, 105));
				nodes.add(new Path.Node(42, 105));
				nodes.add(new Path.Node(42, 111));
				nodes.add(new Path.Node(48, 111));
				nodes.add(new Path.Node(47, 103));
				nodes.add(new Path.Node(47, 100));
				nodes.add(new Path.Node(53, 100));
				nodes.add(new Path.Node(53, 90));
				nodes.add(new Path.Node(49, 90));
				nodes.add(new Path.Node(49, 98));
				nodes.add(new Path.Node(46, 98));
				nodes.add(new Path.Node(46, 99));
				nodes.add(new Path.Node(36, 99));
				
				setPath(nodes, true);
			}
		
			protected void createDialog() {
				addGreeting();
				addJob("I play all day.");
				addHelp("Be careful there are some wolves in the east of here");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "childnpc");
		npc.set(36, 108);
		npc.initHP(100);
		zone.addNPC(npc);
	}

	private void buildSemosCityArea(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(42);
		portal.sety(37);
		portal.setNumber(0);
		portal.setDestination("int_semos_tavern_0", 0);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(53);
		portal.sety(37);
		portal.setNumber(1);
		portal.setDestination("int_semos_temple", 2);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(15);
		portal.sety(41);
		portal.setNumber(2);
		portal.setDestination("int_semos_blacksmith", 0);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(6);
		portal.sety(22);
		portal.setNumber(3);
		portal.setDestination("int_semos_library", 0);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(11);
		portal.sety(22);
		portal.setNumber(4);
		portal.setDestination("int_semos_library", 1);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(52);
		portal.sety(19);
		portal.setNumber(5);
		portal.setDestination("int_semos_storage_0", 0);
		zone.addPortal(portal);
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setx(18);
		portal.sety(22);
		portal.setNumber(6);
		portal.setDestination("int_semos_bank", 0);
		zone.addPortal(portal);
		for (int i = 0; i < 3; i++) {
			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setx(29 + i);
			portal.sety(13);
			portal.setNumber(7 + i);
			portal.setDestination("int_semos_townhall", 2);
			zone.addPortal(portal);
		}
		portal = new OneWayPortal();
		zone.assignRPObjectID(portal);
		portal.setx(12);
		portal.sety(49);
		portal.setNumber(60);
		zone.addPortal(portal);
		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(4);
		sign.sety(41);
		sign.setText("You are about to leave this area to move to the village.\nYou can buy a new sheep there.");
		zone.add(sign);
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(26);
		sign.sety(40);
		sign.setText("You are about to enter the Dungeons.\nBut Beware! This area is infested with rats and legend has \nit that many Adventurers have died down there...");
		zone.add(sign);
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(44);
		sign.sety(62);
		sign.setText("You are about to leave this area and move to the plains.\nYou may fatten up your sheep there on the wild berries.\nBe careful though, wolves roam these plains.");
		zone.add(sign);
		Chest chest = new Chest();
		zone.assignRPObjectID(chest);
		chest.setx(44);
		chest.sety(60);
		chest.add(zone.getWorld().getRuleManager().getEntityManager().getItem(
				"knife"));
		chest.add(zone.getWorld().getRuleManager().getEntityManager().getItem(
				"wooden_shield"));
		chest.add(zone.getWorld().getRuleManager().getEntityManager().getItem(
				"leather_armor"));
		chest.add(zone.getWorld().getRuleManager().getEntityManager().getItem(
				"money"));
		zone.add(chest);
		SpeakerNPC npc = new SpeakerNPC("Nomyr Ahba") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(46, 19));
				nodes.add(new Path.Node(46, 20));
				nodes.add(new Path.Node(50, 20));
				nodes.add(new Path.Node(50, 19));
				nodes.add(new Path.Node(50, 20));
				nodes.add(new Path.Node(46, 20));
				setPath(nodes, true);
			}

			protected void createDialog() {
				add(ConversationStates.IDLE,
					GREETING_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new SpeakerNPC.ChatAction() {
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							// A little trick to make NPC remember if it has met
							// player before anc react accordingly
							// NPC_name quest doesn't exist anywhere else neither is
							// used for any other purpose
							if (!player.isQuestCompleted("Nomyr")) {
								engine
										.say("I've heard cries inside and I was just... but you look disoriented, foreigner. Do you want to know what has been happening around here lately?");
								player.setQuest("Nomyr", "done");
							} else {
								engine.say("Hi again, " + player.getName()
										+ ". How can I #help you this time?");
							}
						}
					});
				addHelp("I'm a... hmmm... observer. I can help you by sharing my information about rumours with you... Do you want to know what has been happening around here lately?");
				addJob("I am committed to peek every curious fact about Semos. I know any rumor that has ever existed in Semos and I have invented most of them. Well, except that about Hackim smuggling Deniran's army weapons to wandering adventurer's like you");
				add(ConversationStates.ATTENDING,
					QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"I do not have any task for you right now. If you need anything from me just say it.",
					null);
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "thiefnpc");
		npc.set(46, 19);
		npc.initHP(100);
		zone.addNPC(npc);
		npc = new SpeakerNPC("Monogenes") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			protected void createDialog() {
				add(ConversationStates.IDLE,
					GREETING_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new SpeakerNPC.ChatAction() {
						public void fire(Player player, String text,
							SpeakerNPC engine) {
							// A little trick to make NPC remember if it has met
							// player before anc react accordingly
							// NPC_name quest doesn't exist anywhere else neither is
							// used for any other purpose
							if (!player.isQuestCompleted("Monogenes")) {
								engine
										.say("Hi foreigner, don't be surprised if people here are reserved: the fear of the advances of Blordrough's dark legion has affected everybody, including me. Do you want to know how to socialize with Semos' people?");
								player.setQuest("Monogenes", "done");
							} else {
								engine.say("Hi again, " + player.getName()
										+ ". How can I #help you this time?");
							}
						}
					});
				addHelp("I'm Diogenes' older brother and I don't remember what I did before I retired. Anyway, I can help you by telling you how to treat Semos' people...  Do you want to know how to socialize with them?");
				addJob("I am committed to give directions to foreigners and show them how to talk to people here. However, when I'm in a bad mood I give them misleading directions hehehe... What is not necessarily bad because I can give wrong directions unwillingly anyway and they can result in being the right directions");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "oldmannpc");
		npc.set(26, 21);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.addNPC(npc);

		npc = new SpeakerNPC("Hayunn Naratha") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(27, 37));
				nodes.add(new Path.Node(27, 38));
				nodes.add(new Path.Node(29, 38));
				nodes.add(new Path.Node(29, 37));
				nodes.add(new Path.Node(29, 38));
				nodes.add(new Path.Node(27, 38));
				setPath(nodes, true);
			}

			protected void createDialog() {
				add(ConversationStates.IDLE,
					GREETING_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new SpeakerNPC.ChatAction() {
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							// A little trick to make NPC remember if it has met
							// player before anc react accordingly
							// NPC_name quest doesn't exist anywhere else neither is
							// used for any other purpose
							if (!player.isQuestCompleted("Hayunn")) {
								engine
										.say("Hi. I am Hayunn Naratha, a retired adventurer. Do you want me to tell you how I used to kill creatures?");
								player.setQuest("Hayunn", "done");
							} else {
								engine.say("Hi again, " + player.getName()
										+ ". How can I #help you this time?");
							}
						}
					});
				addHelp("Well, I'm a retired adventurer as I've told you before. I only can help you by sharing my experience with you... Do you want me to tell you how I used to kill creatures?");
				addJob("I've sworn defending with my life the people of Semos from any creature that dares to get out of this dungeon. With all our young people battling Blordrough's dark legion at south, monsters are getting more and more confident to go to the surface.");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "oldheronpc");
		npc.set(27, 37);
		npc.initHP(100);
		zone.addNPC(npc);
		npc = new SpeakerNPC("Diogenes") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(22, 42));
				nodes.add(new Path.Node(26, 42));
				nodes.add(new Path.Node(26, 44));
				nodes.add(new Path.Node(31, 44));
				nodes.add(new Path.Node(31, 42));
				nodes.add(new Path.Node(35, 42));
				nodes.add(new Path.Node(35, 28));
				nodes.add(new Path.Node(22, 28));
				setPath(nodes, true);
			}

			protected void createDialog() {
				addGreeting();
				addJob("Hehehe! Job! hehehe! Muahahaha!");
				addHelp("I can't help you, but you can help Stendhal: tell your friends about Stendhal and help us to create maps.");
				addGoodbye();
				add(ConversationStates.ATTENDING,
					QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new SpeakerNPC.ChatAction() {
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							// randomly select between two different messages
							switch (Rand.rand(2)) {
							case 0:
								say("Ah, quests... just like the old days when I was young! I remember one quest that was about... Oh look, a bird!hmm, what?! Oh, Oops! I forgot it! :(");
								break;
							case 1:
								say("I have been told that on the deepest place of the dungeon under this city someone also buy sheeps, but *it* pays better!");
								break;
							}
						}
					});
				
				add(ConversationStates.ATTENDING,
					"cleanme!",
					null,
					ConversationStates.ATTENDING,
					"What?",
					new SpeakerNPC.ChatAction() {
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							if (AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player, "alter", false)) {
								for (String quest : player.getQuests()) {
									player.removeQuest(quest);
								}
							} else {
								say("Ummm! No, you clean me! Begin with my back!");
								player.setHP(player.getHP() - 5);
								world.modify(player);
							}
						}
					});
			}
		};
		npcs.add(npc);		
		zone.assignRPObjectID(npc);
		npc.put("class", "beggarnpc");
		npc.set(24, 42);
		npc.initHP(100);
		zone.addNPC(npc);
		npc = new SpeakerNPC("Carmen") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(5, 45));
				nodes.add(new Path.Node(18, 45));
				setPath(nodes, true);
			}

			protected void createDialog() {
				addGreeting();
				addJob("I have healing abilities and I heal wounded people. I also sell potions and antidotes.");
				addHelp("Ask me to #heal you and I will help you or ask me for an #offer and I will show my shop's stuff.");
				addSeller(new SellerBehaviour(world, shops.get("healing")));
				addHealer(0);
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "welcomernpc");
		npc.set(5, 45);
		npc.initHP(100);
		zone.addNPC(npc);
	}
}