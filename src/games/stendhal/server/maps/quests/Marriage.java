package games.stendhal.server.maps.quests;

import java.awt.Rectangle;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.util.Area;

import marauroa.common.game.IRPZone;

/**
 * QUEST: Marriage
 * 
 * PARTICIPANTS:
 * - Sister Benedicta, the nun of Fado Church
 * - the Priest of Fado Church
 * 
 * STEPS:
 * - The nun explains that when two people are married, they can be together whenever they want
 * - When two players wish to become engaged, they tell the nun
 * - The nun gives them invitation scrolls for the wedding, marked with the church
 * - When an engaged player goes to the priest, he knows they are there to be married
 * - The marriage rites are performed
 * - The players are given rings
 * - When they go to the Hotel they choose a lovers room
 * - Champagne and fruit baskets is put in their bag (room if possible)
 * - They leave the lovers room when desired with another marked scroll 
 * 
 * 
 * REWARD:
 * - Wedding Ring that teleports you to your spouse if worn
 * - 1000 XP
 * - nice food in the lovers room
 * 
 *
 * REPETITIONS:
 * - None.
 * 
 * @author kymara
 */
public class Marriage extends AbstractQuest {
    private static final String QUEST_SLOT = "marriage";
    @Override
	public void init(String name) {
	super.init(name, QUEST_SLOT);
    }
    private String SPOUSE_QUEST_SLOT = "spouse";
    
    private NPCList npcs = NPCList.get();
    
	private Player groom;

	private Player bride;
	
	private SpeakerNPC nun;
    
    private void step_1() {
	nun = npcs.get("Sister Benedicta");
	nun.add(ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		null,
		ConversationStates.ATTENDING,
		null,
		new SpeakerNPC.ChatAction() {
		    @Override
			public void fire(Player player, String text, SpeakerNPC engine) {
			if (!player.hasQuest(QUEST_SLOT)) {
			    engine.say("The great quest of all life is to be #married.");
			} else if (player.isQuestCompleted(QUEST_SLOT)) {
			    engine.say("I hope you are enjoying married life.");	  
			}
			else { engine.say("something else");
			     }
		    }
		});
	nun.add(ConversationStates.ATTENDING,
			"married",
			null,
			ConversationStates.ATTENDING,
			"If you have a partner, you can marry them in a lovely #wedding. Once you have a wedding ring, you can be together whenever you want.",
			null
			);
			
	nun.add(ConversationStates.ATTENDING,
		"wedding",
		null,
		ConversationStates.ATTENDING,
		"You can be married here at this church. If you want to #engage someone just tell me who.",
		null
		);
	
	nun.add(ConversationStates.ATTENDING, "engage", null, ConversationStates.ATTENDING, null,
	        new SpeakerNPC.ChatAction() {

		        @Override
		        public void fire(Player player, String text, SpeakerNPC npc) {
			        // find out whom the player wants to marry.
			        String[] words = text.split(" ");

			        if (words.length >= 2) {
				        String brideName = words[1];
				        startEngagement(npc, player, brideName);
			        } else {
				        npc.say("You have to tell me who you want to marry.");
			        }
		        }
	        });
		nun.add(ConversationStates.QUESTION_1, ConversationPhrases.YES_MESSAGES, null,
	        ConversationStates.QUESTION_2, null, new SpeakerNPC.ChatAction() {

		        @Override
		        public void fire(Player player, String text, SpeakerNPC npc) {
			        askBride();
		        }
	        });

		nun.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
	        "What a shame! Goodbye!", null);
		
		nun.add(ConversationStates.QUESTION_2, ConversationPhrases.YES_MESSAGES, null,
	        ConversationStates.ATTENDING, null, new SpeakerNPC.ChatAction() {

		        @Override
		        public void fire(Player player, String text, SpeakerNPC npc) {
			        finishEngagement();
		        }
	        });

		nun.add(ConversationStates.QUESTION_2, ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
	        "What a shame! Goodbye!", null);
    }
    
	private void startEngagement(SpeakerNPC nun, Player player, String partnerName) {
		IRPZone outsideChurchZone = StendhalRPWorld.get().getRPZone(nun.getID());
		Area inFrontOfNun = new Area(outsideChurchZone, new Rectangle(52, 50, 4, 4));
		groom = player;
		bride = StendhalRPRuleProcessor.get().getPlayer(partnerName);
	
		if (!inFrontOfNun.contains(groom)) {
			nun.say("My hearing is not so good, please both come close to tell me who you want to get engaged to.");
		} else if (isMarried(groom)) {
			nun.say("You are married already, " + groom.getName() + "! You can't marry again.");
		} else if (bride == null || !inFrontOfNun.contains(bride)) {
			nun.say("My hearing is not so good, please both come close to tell me who you want to get engaged to.");
		} else if (bride.getName().equals(groom.getName())) {
			nun.say("You can't marry yourself!");
		} else if (isMarried(bride)) {
			nun.say("You are married already, " + bride.getName() + "! You can't marry again.");
		} else {
			askGroom();
		}
		
	}
	private void askGroom() {
		nun.say(groom.getName() + ", do you want to get engaged to " + bride.getName() + "?");
		nun.setCurrentState(ConversationStates.QUESTION_1);
	}
	
	private void askBride() {
		nun.say(bride.getName() + ", do you want to get engaged to " + groom.getName() + "?");
		nun.setCurrentState(ConversationStates.QUESTION_2);
		nun.setAttending(bride);
	}
	private void giveInvite(Player player) {
		Item invite = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("invitation_scroll");
		invite.put("infostring", "int_fado_church 12 20"); /*location of church*/
		player.equip(invite, true);
	}
	private void finishEngagement() {
		giveInvite(groom);
		giveInvite(bride);
		nun.say("Congratulations, " + groom.getName() + " and " + bride.getName() + ", you are now engaged! Please agree a time for your wedding and tell your guests, then use this invite to get to the church!");
		// Memorize that the two engaged so that the priest knows
		groom.setQuest(QUEST_SLOT, "engaged");
		bride.setQuest(QUEST_SLOT, "engaged");
		// Clear the variables so that other players can become groom and bride
		// later
		groom = null;
		bride = null;
	}
	
	private boolean isMarried(Player player) {
		return player.hasQuest(SPOUSE_QUEST_SLOT);
	}
    
    private void step_2() {
	/*go to the priest and get married, defined in PriestNPC for now */
    }
    
    private void step_3() {
	
	/*SpeakerNPC npc = npcs.get("Linda");*/
	// TODO: Hotel stuff


    }
    @Override
	public void addToWorld() {
	super.addToWorld();
	
	step_1();
	step_2();
	step_3();
    }

}
