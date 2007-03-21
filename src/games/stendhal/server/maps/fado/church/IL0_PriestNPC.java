package games.stendhal.server.maps.fado.church;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.util.Area;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;

public class IL0_PriestNPC implements ZoneConfigurator {
	
	private NPCList npcs = NPCList.get();
	
	private Player groom;
	
	private Player bride;
	
	private SpeakerNPC priest;

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildNPC(zone);
	}


	private void buildNPC(StendhalRPZone zone) {
		priest = new SpeakerNPC("Priest") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to my church!");
				addJob("I am the priest.");
				//addHelp("My only advice is to love and be kind to one another");
				//addQuest("I have eveything I need. But it does bring me pleasure to see people #married.");
				addGoodbye("May the force be with you.");
				
				add(ConversationStates.ATTENDING,
						"marry",
						null,
						ConversationStates.ATTENDING,
						null,
						new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						// find out whom the player wants to marry.
						String[] words = text.split(" ");
		
						if (words.length >= 2) {
							String brideName = words[1];
							startMarriage(npc, player, brideName);
						} else {
							npc.say("You have to tell me who you want to marry.");
						}
					}
				});

				add(ConversationStates.QUESTION_1,
						ConversationPhrases.YES_MESSAGES,
						null,
						ConversationStates.QUESTION_2,
						null,
						new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						askBride();
					}
				});
				
				add(ConversationStates.QUESTION_1, 
						ConversationPhrases.NO_MESSAGES,
						null,
						ConversationStates.IDLE,
						"What a pity! Goodbye!",
						null);
				add(ConversationStates.QUESTION_2,
						ConversationPhrases.YES_MESSAGES,
						null,
						ConversationStates.ATTENDING,
						null,
						new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC npc) {
						finishMarriage();
					}
				});
				
				add(ConversationStates.QUESTION_2, 
						ConversationPhrases.NO_MESSAGES,
						null,
						ConversationStates.IDLE,
						"What a pity! Goodbye!",
						null);
			}
		};
		//npc.setDescription("You see Lukas, the humble church verger.");
		npcs.add(priest);
		zone.assignRPObjectID(priest);
		priest.put("class", "orcbuyernpc");
		priest.set(11, 4);
		priest.setDirection(Direction.DOWN);
		priest.initHP(100);
		zone.add(priest);
	}
	
	private boolean isMarried(Player player) {
		// TODO
		return false;
	}
	
	private void giveRing(Player player, Player partner) {
		Item ring = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("wedding_ring");
		ring.put("infostring", partner.getName());
		ring.put("bound", player.getName());
		player.equip(ring, true);
	}
		
	private void exchangeRings() {
		// TODO: players should bring their own golden rings
		giveRing(groom, bride);
		giveRing(bride, groom);
	}
	
	private void startMarriage(SpeakerNPC priest, Player player, String partnerName) {
		IRPZone churchZone = StendhalRPWorld.get().getRPZone(priest.getID());
		Area inFrontOfAltar = new Area(churchZone, new Rectangle(10, 8, 4, 3));

		groom = player;
		bride = StendhalRPRuleProcessor.get().getPlayer(partnerName);

		if (! inFrontOfAltar.contains(groom)) {
			priest.say("You must step in front of the altar if you want to marry.");
		} else if (isMarried(groom)) {
			priest.say("You are married already, " + groom.getName() + "! You can't marry again.");
		} else if (bride == null || ! inFrontOfAltar.contains(bride)) {
			priest.say("You must bring your partner to the altar if you want to marry.");
		} else if (bride.getName().equals(groom.getName())) {
			priest.say("You can't marry yourself!");
		} else if (isMarried(bride)) {
			priest.say("You are married already, " + bride.getName() + "! You can't marry again.");
		} else {
			askGroom();
		}
	}
	
	private void askGroom() {
		priest.say(groom.getName() + ", do you really want to marry " + bride.getName() + "?");
		priest.setCurrentState(ConversationStates.QUESTION_1);
	}
	
	private void askBride() {
		priest.say(bride.getName() + ", do you really want to marry " + groom.getName() + "?");
		priest.setCurrentState(ConversationStates.QUESTION_2);
		priest.setAttending(bride);
	}
	
	private void finishMarriage() {
		exchangeRings();
		priest.say("Congratulations, " + groom.getName() + " and " + bride.getName() + ", you are now married!");
		groom = null;
		bride = null;
	}
}
