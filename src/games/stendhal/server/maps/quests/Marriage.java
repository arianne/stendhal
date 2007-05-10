package games.stendhal.server.maps.quests;

import java.awt.Rectangle;
import java.util.Arrays;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.util.Area;
import games.stendhal.server.util.TimeUtil;

import marauroa.common.game.IRPZone;

/**
 * QUEST: Marriage
 * 
 * PARTICIPANTS:
 * - Sister Benedicta, the nun of Fado Church
 * - the Priest of Fado Church
 * - Ognir, the Ring Maker in Fado
 * 
 * STEPS:
 * - The nun explains that when two people are married, they can be together whenever they want
 * - When two players wish to become engaged, they tell the nun
 * - The nun gives them invitation scrolls for the wedding, marked with the church
 * - The players get a wedding ring made to give the other at the wedding
 * - They can get dressed into an outfit in the hotel
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
 * - 1500 XP in total
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

	// The spouse's name is stored in one of the player's quest slots.
	// This is necessary to disallow polygamy.
    private String SPOUSE_QUEST_SLOT = "spouse";
    
	private static final int REQUIRED_GOLD = 10;

	private static final int REQUIRED_MONEY = 500;
	
	private static final int REQUIRED_MINUTES = 10;
    
    private NPCList npcs = NPCList.get();
    
	private Player groom;

	private Player bride;
	
	private SpeakerNPC nun;
	
	private SpeakerNPC priest;
    
    private void EngagementStep() {
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
			else { engine.say("Haven't you organised your wedding yet?");
			     }
		    }
		});
	nun.add(ConversationStates.ATTENDING,
			"married",
			null,
			ConversationStates.ATTENDING,
			"If you have a partner, you can marry them at a #wedding. Once you have a wedding ring, you can be together whenever you want.",
			null
			);
			
	nun.add(ConversationStates.ATTENDING,
		"wedding",
		null,
		ConversationStates.ATTENDING,
		"You may marry here at this church. If you want to #engage someone, just tell me who.",
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
			        askBrideE();
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
		Area inFrontOfNun = new Area(outsideChurchZone, new Rectangle(51, 50, 4, 5));
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
			askGroomE();
		}
		
	}
	private void askGroomE() {
		nun.say(groom.getName() + ", do you want to get engaged to " + bride.getName() + "?");
		nun.setCurrentState(ConversationStates.QUESTION_1);
	}
	
	private void askBrideE() {
		nun.say(bride.getName() + ", do you want to get engaged to " + groom.getName() + "?");
		nun.setCurrentState(ConversationStates.QUESTION_2);
		nun.setAttending(bride);
	}
	private void giveInvite(Player player) {
		StackableItem invite = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("invitation_scroll");
		invite.setQuantity(4);
		invite.put("infostring", "int_fado_church 12 20"); /*location of church*/
		// perhaps change this to a hotel room where they can get dressed into wedding outfits?
		// then they walk to the church?
		player.equip(invite, true);
	}
	private void finishEngagement() {
		giveInvite(groom);
		giveInvite(bride);
		nun.say("Congratulations, " + groom.getName() + " and " + bride.getName() + ", you are now engaged! Please make sure you have got wedding rings made before you go to the church for the service.");
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
	
	private void MakeRingsStep() {
		SpeakerNPC npc = npcs.get("Ognir");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
		        new SpeakerNPC.ChatCondition() {
			        @Override
			        public boolean fire(Player player, String text, SpeakerNPC npc) {
				        return player.getQuest(QUEST_SLOT).startsWith("engaged"); 
			        }
		        },
		        ConversationStates.INFORMATION_1,
		        null,
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, String text, SpeakerNPC npc) {
			        	if(player.isQuestInState(QUEST_SLOT, "engaged_with_ring")){
			        		// player has wedding ring already. just remind to get spouse to get one.
					        npc.say("Looking forward to your wedding? Make sure your fiancee gets a wedding ring made for you, too!");
					        npc.setCurrentState(ConversationStates.ATTENDING);
				        } else {
				        	// says you'll need a ring
					        npc.say("I see you're on a life-long quest to get married! I find marriage more of a task, ha ha! Anyway, you'll need a #wedding_ring.");
				        }
			        }
		        });
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
		        new SpeakerNPC.ChatCondition() {
			        @Override
			        public boolean fire(Player player, String text, SpeakerNPC npc) {
				        return player.hasQuest(QUEST_SLOT)
				                && player.getQuest(QUEST_SLOT).startsWith("forging;");
			        }
		        },
		        ConversationStates.IDLE,
		        null,
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, String text, SpeakerNPC npc) {
				        String[] tokens = player.getQuest(QUEST_SLOT).split(";");
				        long delay = REQUIRED_MINUTES * 60 * 1000; // minutes -> milliseconds
				        long timeRemaining = (Long.parseLong(tokens[1]) + delay)
				                - System.currentTimeMillis();
				        if (timeRemaining > 0L) {
					        npc.say("I haven't finished making the wedding ring. Please check back in "
			                        + TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
			                        + ".");
					        return;
				        }
				        npc.say("I'm pleased to say, the wedding ring for your fiancee is finished! Look after it till the ceremony. Make sure one is made for you, too!");
				        player.addXP(500);
				        Item weddingRing = StendhalRPWorld.get().getRuleManager()
				                .getEntityManager().getItem("wedding_ring");
				        weddingRing.put("bound", player.getName());
				        player.equip(weddingRing, true);
				        player.setQuest(QUEST_SLOT, "engaged_with_ring");
				        player.notifyWorldAboutChanges();
			        }
		        });

		npc.add(ConversationStates.INFORMATION_1,
                Arrays.asList("wedding_ring", "wedding", "ring"),
                null,
                ConversationStates.QUEST_ITEM_QUESTION,
                "I need " + REQUIRED_GOLD + " gold bars and a fee of " + REQUIRED_MONEY + " money, to make a wedding ring for your fiancee. Do you have it?",
                null);
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.YES_MESSAGES,
                null,
                ConversationStates.ATTENDING, null,
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, String text, SpeakerNPC npc) {
                if ((player.isEquipped("gold_bar", REQUIRED_GOLD)) && (player.isEquipped("money", REQUIRED_MONEY))){
                	player.drop("gold_bar", REQUIRED_GOLD);
                	player.drop("money", REQUIRED_MONEY);
                	npc.say("Good, come back in "
			        		+ REQUIRED_MINUTES + " minutes and it will be ready");
		        	player.setQuest(QUEST_SLOT, "forging;" + System.currentTimeMillis());
		        	npc.setCurrentState(ConversationStates.IDLE);
		        } else {
		        	npc.say("Come back when you have both the money and the gold.");	
		        }
			        }
		        });
		
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.NO_MESSAGES,
                null,
                ConversationStates.ATTENDING,
                "No problem, just come back when you have both the money and the gold.",
                null);
		
	}
	
   private void GetDressedStep() {
    	
    	// Just go to the NPCs Tamara and Timothy
	   // TODO: make only engaged person able to get into wedding dress/suit
    }
    private void MarriageStep() {
    	
    	/**
    	 * Creates a priest NPC who can celebrate marriages between two
    	 * players.
    	 * 
    	 * Note: in this class, the Player variables are called groom
    	 * and bride. However, the game doesn't know the concept of
    	 * genders. The player who initiates the wedding is just called
    	 * groom, the other bride.
    	 *   
    	 * @author daniel
    	 *
    	 */
    	
    	priest = npcs.get("Priest");
    	priest.add(ConversationStates.ATTENDING, "marry", 
				
				new SpeakerNPC.ChatCondition() {
			@Override
			public boolean fire(Player player, String text, SpeakerNPC npc) {
				return player.hasQuest(QUEST_SLOT)
						&& player.getQuest(QUEST_SLOT).equals("engaged_with_ring");
			}
		}	
    	// TODO: make sure the pair getting married are *both* engaged, and to each other
    	// TODO: make sure they both have a ring not just first player
    	
			, ConversationStates.ATTENDING, null,
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

    	priest.add(ConversationStates.QUESTION_1, ConversationPhrases.YES_MESSAGES, null,
		        ConversationStates.QUESTION_2, null, new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC npc) {
				        askBride();
			        }
		        });

		priest.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
		        "What a pity! Goodbye!", null);
		priest.add(ConversationStates.QUESTION_2, ConversationPhrases.YES_MESSAGES, null,
		        ConversationStates.ATTENDING, null, new SpeakerNPC.ChatAction() {

			        @Override
			        public void fire(Player player, String text, SpeakerNPC npc) {
				        finishMarriage();
			        }
		        });

		priest.add(ConversationStates.QUESTION_2, ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
		        "What a pity! Goodbye!", null);
    	
    }
    
    private void startMarriage(SpeakerNPC priest, Player player, String partnerName) {
		IRPZone churchZone = StendhalRPWorld.get().getRPZone(priest.getID());
		Area inFrontOfAltar = new Area(churchZone, new Rectangle(10, 8, 4, 1));

		groom = player;
		bride = StendhalRPRuleProcessor.get().getPlayer(partnerName);

		if (!inFrontOfAltar.contains(groom)) {
			priest.say("You must step in front of the altar if you want to marry.");
		} else if (isMarried(groom)) {
			priest.say("You are married already, " + groom.getName() + "! You can't marry again.");
		} else if (bride == null || !inFrontOfAltar.contains(bride)) {
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
		// Memorize that the two married so that they can't just marry other
		// persons
		groom.setQuest(SPOUSE_QUEST_SLOT, bride.getName());
		bride.setQuest(SPOUSE_QUEST_SLOT, groom.getName());
		// Clear the variables so that other players can become groom and bride
		// later
		groom = null;
		bride = null;
	}
	

	private void giveRing(Player player, Player partner) {
		//	 players bring their own golden rings
		// TODO: have only checked one player has ring. need to check both. do it here or earlier?
		player.drop("wedding_ring");
		Item ring = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("wedding_ring");
		ring.put("infostring", partner.getName());
		ring.put("bound", player.getName());
		player.equip(ring, true);
	}

	private void exchangeRings() {
		giveRing(groom, bride);
		giveRing(bride, groom);
	}

    
    
    
    private void HoneymoonStep() {
	
	/*SpeakerNPC npc = npcs.get("Linda");*/
	// TODO: Hotel stuff


    }
    @Override
	public void addToWorld() {
	super.addToWorld();
	
	EngagementStep();
	MakeRingsStep();
	GetDressedStep();
	MarriageStep();
	HoneymoonStep();
    }

}
