package games.stendhal.server.maps.semos;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.AdministrationAction;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.OneWayPortalDestination;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;

public class SemosCityOutside implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("0_semos_city")),
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
		buildSemosCityAreaPortals(zone, attributes);
		buildSemosCityAreaSigns(zone, attributes);
		buildSemosCityAreaChest(zone);

		buildSemosCityAreaNomyrAhba(zone);
		buildSemosCityAreaMonogenes(zone);
		buildSemosCityAreaHayunnNaratha(zone);
		buildSemosCityAreaDiogenes(zone);
		buildSemosCityAreaCarmen(zone);
	}


	private void buildSemosCityAreaPortals(StendhalRPZone zone,
	 Map<String, String> attributes) {
		/*
		 * Portals configured in xml?
		 */
		if(attributes.get("xml-portals") == null) {
			Portal portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(42);
			portal.setY(37);
			portal.setReference(new Integer(0));
			portal.setDestination("int_semos_tavern_0",new Integer( 0));
			zone.addPortal(portal);
		
			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(53);
			portal.setY(37);
			portal.setReference(new Integer(1));
			portal.setDestination("int_semos_temple",new Integer( 2));
			zone.addPortal(portal);
		
			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(15);
			portal.setY(41);
			portal.setReference(new Integer(2));
			portal.setDestination("int_semos_blacksmith",new Integer( 0));
			zone.addPortal(portal);
		
			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(6);
			portal.setY(22);
			portal.setReference(new Integer(3));
			portal.setDestination("int_semos_library",new Integer( 0));
			zone.addPortal(portal);
		
			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(11);
			portal.setY(22);
			portal.setReference(new Integer(4));
			portal.setDestination("int_semos_library",new Integer( 1));
			zone.addPortal(portal);
		
			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(52);
			portal.setY(19);
			portal.setReference(new Integer(5));
			portal.setDestination("int_semos_storage_0",new Integer( 0));
			zone.addPortal(portal);
		
			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(18);
			portal.setY(22);
			portal.setReference(new Integer(6));
			portal.setDestination("int_semos_bank", new Integer(0));
			zone.addPortal(portal);
		
			for (int i = 0; i < 3; i++) {
				portal = new Portal();
				zone.assignRPObjectID(portal);
				portal.setX(29 + i);
				portal.setY(13);
				portal.setReference(new Integer(7 + i));
				portal.setDestination("int_semos_townhall", new Integer(i));
				zone.addPortal(portal);
			}
		
			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(44);
			portal.setY(19);
			portal.setReference(new Integer(10));
			portal.setDestination("int_semos_bakery",new Integer( 0));
			zone.addPortal(portal);

			portal = new OneWayPortalDestination();
			zone.assignRPObjectID(portal);
			portal.setX(12);
			portal.setY(49);
			portal.setReference(new Integer(60));
			zone.addPortal(portal);
		}
	}

	private void buildSemosCityAreaSigns(StendhalRPZone zone,
	 Map<String, String> attributes) {
		/*
		 * Entities configured in xml?
		 */
		if(attributes.get("xml-entities") == null) {
			Sign sign = new Sign();
			zone.assignRPObjectID(sign);
			sign.setX(4);
			sign.setY(41);
			sign.setText("TO OLD SEMOS VILLAGE\n\nShepherds wanted: please ask Nishiya");
			zone.add(sign);
		
			sign = new Sign();
			zone.assignRPObjectID(sign);
			sign.setX(26);
			sign.setY(40);
			sign.setText("DUNGEONS\n\nCaution: These dungeons contain many rats,\nand more dangerous creatures. Enter at own risk.");
			zone.add(sign);
		
			sign = new Sign();
			zone.assignRPObjectID(sign);
			sign.setX(44);
			sign.setY(62);
			sign.setText("TO THE PLAINS\n\nShepherds please note: keep watch for\nthe wolves while searching for berries here");
			zone.add(sign);
		}
	}
	private void buildSemosCityAreaChest(StendhalRPZone zone) {

		Chest chest = new Chest();
		zone.assignRPObjectID(chest);
		chest.setX(44);
		chest.setY(60);
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager()
				.getItem("knife"));
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager()
				.getItem("wooden_shield"));
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager()
				.getItem("leather_armor"));
		chest.add(StendhalRPWorld.get().getRuleManager().getEntityManager()
				.getItem("money"));
		zone.add(chest);
	}

	private void buildSemosCityAreaNomyrAhba(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Nomyr Ahba") {
			@Override
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
							if (!player.isQuestCompleted("Nomyr")) {
								engine.say("Heh heh... Oh, hello stranger! You look a bit disoriented... d'you want to hear the latest gossip?");
								player.setQuest("Nomyr", "done");
							} else {
								engine.say("Hi again, " + player.getName()
										+ ". How can I #help you this time?");
							}
						}
					});
				addHelp("I'm a... let's call me an \"observer\". I can tell you about all the latest rumours. Do you want to hear?");
				addJob("I know every rumour that exists in Semos, and I invented most of them! The one about Hackim smuggling in weapons for wandering adventurers like you is true, though.");
				add(ConversationStates.ATTENDING,
					QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					"Thanks for asking, but I don't need anything right now.",
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
	}

	private void buildSemosCityAreaMonogenes(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Monogenes") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addJob("Me? I give directions to newcomers to Semos and help them settle in. When I'm in a bad mood I sometimes give misleading directions to amuse myself... hee hee hee! Of course, sometimes I get my wrong directions wrong and they end up being right after all! Ha ha!");
				
				// All further behaviour is defined in MeetMonogenes.java.
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "oldmannpc");
		npc.set(26, 21);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.addNPC(npc);
	}

	private void buildSemosCityAreaHayunnNaratha(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Hayunn Naratha") {
			@Override
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
							if (!player.isQuestCompleted("Hayunn")) {
								engine.say("You've probably heard of me; Hayunn Naratha, a retired adventurer. Have you read my book? No? It's called \"Know How To Kill Creatures\". Maybe we could talk about adventuring, if you like?");
								player.setQuest("Hayunn", "done");
							} else {
								engine.say("Hi again, " + player.getName()
										+ ". How can I #help you this time?");
							}
						}
					});
				addHelp("As I say, I'm a retired adventurer, and now I teach people. Do you want me to teach you about killing creatures?");
				addJob("My job is to guard the people of Semos from any creature that might escape this vile dungeon! With all our young people away battling Blordrough's evil legions to the south, the monsters down there are getting more confident about coming to the surface.");
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "oldheronpc");
		npc.set(27, 37);
		npc.initHP(100);
		zone.addNPC(npc);
	}

	private void buildSemosCityAreaDiogenes(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Diogenes") {
			@Override
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

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("Ha ha! Job? I retired decades ago! Ha ha!");
				addHelp("I can't help you, but you can help Stendhal; tell all your friends, and help out with development! Visit http://arianne.sourceforge.net and see how you can help!");
				addGoodbye();
				add(ConversationStates.ATTENDING,
					QUEST_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							// randomly select between two different messages
							switch (Rand.rand(2)) {
							case 0:
								say("Ah, quests... just like the old days when I was young! I remember one quest that was about... Oh look, a bird! Hmm, what? Ah, quests... just like the old days when I was young!");
								break;
							case 1:
								say("You know that Sato over there buys sheep? Well, rumour has it that there's a creature deep in the dungeons who also buys sheep... and it pays much better than Sato, too!");
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
						@Override
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							if (AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player, "alter", false)) {
								for (String quest : player.getQuests()) {
									player.removeQuest(quest);
								}
							} else {
								say("What? No; you clean me! Begin with my back, thanks.");
								player.setHP(player.getHP() - 5);
								player.notifyWorldAboutChanges();
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
	}

	private void buildSemosCityAreaCarmen(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Carmen") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(5, 45));
				nodes.add(new Path.Node(18, 45));
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
		npc.set(5, 45);
		npc.initHP(100);
		zone.addNPC(npc);
	}
}
