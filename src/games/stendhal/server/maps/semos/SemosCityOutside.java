package games.stendhal.server.maps.semos;

import games.stendhal.common.Direction;
import games.stendhal.common.Rand;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.actions.AdministrationAction;
import games.stendhal.server.entity.Chest;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.portal.OneWayPortalDestination;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

public class SemosCityOutside {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();

	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		buildSemosCityArea((StendhalRPZone) world.getRPZone(new IRPZone.ID(
		"0_semos_city")));
	}

	private void buildSemosCityArea(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(42);
		portal.setY(37);
		portal.setNumber(0);
		portal.setDestination("int_semos_tavern_0", 0);
		zone.addPortal(portal);
		
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(53);
		portal.setY(37);
		portal.setNumber(1);
		portal.setDestination("int_semos_temple", 2);
		zone.addPortal(portal);
		
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(15);
		portal.setY(41);
		portal.setNumber(2);
		portal.setDestination("int_semos_blacksmith", 0);
		zone.addPortal(portal);
		
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(6);
		portal.setY(22);
		portal.setNumber(3);
		portal.setDestination("int_semos_library", 0);
		zone.addPortal(portal);
		
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(11);
		portal.setY(22);
		portal.setNumber(4);
		portal.setDestination("int_semos_library", 1);
		zone.addPortal(portal);
		
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(52);
		portal.setY(19);
		portal.setNumber(5);
		portal.setDestination("int_semos_storage_0", 0);
		zone.addPortal(portal);
		
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(18);
		portal.setY(22);
		portal.setNumber(6);
		portal.setDestination("int_semos_bank", 0);
		zone.addPortal(portal);
		
		for (int i = 0; i < 3; i++) {
			portal = new Portal();
			zone.assignRPObjectID(portal);
			portal.setX(29 + i);
			portal.setY(13);
			portal.setNumber(7 + i);
			portal.setDestination("int_semos_townhall", i);
			zone.addPortal(portal);
		}
		
		portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(44);
		portal.setY(19);
		portal.setNumber(10);
		portal.setDestination("int_semos_bakery", 0);
		zone.addPortal(portal);

		portal = new OneWayPortalDestination();
		zone.assignRPObjectID(portal);
		portal.setX(12);
		portal.setY(49);
		portal.setNumber(60);
		zone.addPortal(portal);
		
		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(4);
		sign.setY(41);
		sign.setText("You are about to leave this area to move to the village.\nYou can buy a new sheep there.");
		zone.add(sign);
		
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(26);
		sign.setY(40);
		sign.setText("You are about to enter the Dungeons.\nBut Beware! This area is infested with rats and legend has \nit that many Adventurers have died down there...");
		zone.add(sign);
		
		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(44);
		sign.setY(62);
		sign.setText("You are about to leave this area and move to the plains.\nYou may fatten up your sheep there on the wild berries.\nBe careful though, wolves roam these plains.");
		zone.add(sign);
		
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
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addJob("I am committed to give directions to foreigners and show them how to talk to people here. However, when I'm in a bad mood I give them misleading directions hehehe... What is not necessarily bad because I can give wrong directions unwillingly anyway and they can result in being the right directions");
				
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

		npc = new SpeakerNPC("Hayunn Naratha") {
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
				addJob("Hehehe! Job! hehehe! Muahahaha!");
				addHelp("I can't help you, but you can help Stendhal: tell your friends about Stendhal and help us to create maps.");
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
								say("Ah, quests... just like the old days when I was young! I remember one quest that was about... Oh look, a bird! Hmm, what?! Oh, Oops! I forgot it! :(");
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
						@Override
						public void fire(Player player, String text,
								SpeakerNPC engine) {
							if (AdministrationAction.isPlayerAllowedToExecuteAdminCommand(player, "alter", false)) {
								for (String quest : player.getQuests()) {
									player.removeQuest(quest);
								}
							} else {
								say("Ummm! No, you clean me! Begin with my back!");
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
		npc = new SpeakerNPC("Carmen") {
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
				addJob("I have healing abilities and I heal wounded people. I also sell potions and antidotes.");
				addHelp("Ask me to #heal you and I will help you or ask me for an #offer and I will show my shop's stuff.");
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
