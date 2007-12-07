package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;
import games.stendhal.server.util.TimeUtil;

import java.awt.Rectangle;
import java.util.Arrays;

import marauroa.common.game.IRPZone;

/**
 * QUEST: Marriage
 * <p>
 * PARTICIPANTS:
 * <li> Sister Benedicta, the nun of Fado Church
 * <li> the Priest of Fado Church
 * <li> Ognir, the Ring Maker in Fado
 * <p>
 * STEPS:
 * <li> The nun explains that when two people are married, they can be together
 * whenever they want
 * <li> When two players wish to become engaged, they tell the nun
 * <li> The nun gives them invitation scrolls for the wedding, marked with the
 * church
 * <li>The players get a wedding ring made to give the other at the wedding
 * <li> They can get dressed into an outfit in the hotel
 * <li> When an engaged player goes to the priest, he knows they are there to be
 * married
 * <li> The marriage rites are performed
 * <li> The players are given rings
 * <li> When they go to the Hotel they choose a lovers room
 * <li> Champagne and fruit baskets is put in their bag (room if possible)
 * <li> They leave the lovers room when desired with another marked scroll
 *
 * <p>
 * REWARD:
 * <li> Wedding Ring that teleports you to your spouse if worn - 1500 XP in
 * total
 * <li> nice food in the lovers room
 * <p>
 *
 * REPETITIONS:
 * <li> None.
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

	private SpeakerNPC clerk;

	private void engagementStep() {
		nun = npcs.get("Sister Benedicta");
		nun.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
					if (!player.hasQuest(QUEST_SLOT)) {
						engine.say("The great quest of all life is to be #married.");
					} else if (player.isQuestCompleted(QUEST_SLOT)) {
						engine.say("I hope you are enjoying married life.");
					} else {
						engine.say("Haven't you organised your wedding yet?");
					}
				}
			});

		nun.add(
			ConversationStates.ATTENDING,
			"married",
			null,
			ConversationStates.ATTENDING,
			"If you have a partner, you can marry them at a #wedding. Once you have a wedding ring, you can be together whenever you want.",
			null);

		nun.add(
			ConversationStates.ATTENDING,
			"wedding",
			null,
			ConversationStates.ATTENDING,
			"You may marry here at this church. If you want to #engage someone, just tell me who.",
			null);

		nun.add(ConversationStates.ATTENDING, "engage", null,
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					// find out whom the player wants to marry.
			        String brideName = sentence.getObjectName();

			        if (brideName == null) {
			        	npc.say("You have to tell me who you want to marry.");
			        } else {
						startEngagement(npc, player, brideName);
					}
				}
			});

		nun.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.QUESTION_2, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					askBrideE();
				}
			});

		nun.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES,
			null, ConversationStates.IDLE, "What a shame! Goodbye!", null);

		nun.add(ConversationStates.QUESTION_2,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					finishEngagement();
				}
			});

		nun.add(ConversationStates.QUESTION_2, ConversationPhrases.NO_MESSAGES,
			null, ConversationStates.IDLE, "What a shame! Goodbye!", null);
	}

	private void startEngagement(SpeakerNPC nun, Player player, String partnerName) {
		IRPZone outsideChurchZone = nun.getZone();
		Area inFrontOfNun = new Area(outsideChurchZone, new Rectangle(51, 52, 6, 5));
		groom = player;
		bride = StendhalRPRuleProcessor.get().getPlayer(partnerName);

		if (!inFrontOfNun.contains(groom)) {
			nun.say("My hearing is not so good, please both come close to tell me who you want to get engaged to.");
		} else if (isMarried(groom)) {
			nun.say("You are married already, " + groom.getName()
					+ "! You can't marry again.");
		} else if (bride == null || !inFrontOfNun.contains(bride)) {
			nun.say("My hearing is not so good, please both come close to tell me who you want to get engaged to.");
		} else if (bride.getName().equals(groom.getName())) {
			nun.say("You can't marry yourself!");
		} else if (isMarried(bride)) {
			nun.say("You are married already, " + bride.getName()
					+ "! You can't marry again.");
		} else {
			askGroomE();
		}

	}

	private void askGroomE() {
		nun.say(groom.getName() + ", do you want to get engaged to "
				+ bride.getName() + "?");
		nun.setCurrentState(ConversationStates.QUESTION_1);
	}

	private void askBrideE() {
		nun.say(bride.getName() + ", do you want to get engaged to "
				+ groom.getName() + "?");
		nun.setCurrentState(ConversationStates.QUESTION_2);
		nun.setAttending(bride);
	}

	private void giveInvite(Player player) {
		StackableItem invite = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
				"invitation_scroll");
		invite.setQuantity(4);
		// location of church
		invite.setInfoString("int_fado_church 12 20");

		// perhaps change this to a hotel room where they can get dressed into
		// wedding outfits?
		// then they walk to the church?
		player.equip(invite, true);
	}

	private void finishEngagement() {
		giveInvite(groom);
		giveInvite(bride);
		nun.say("Congratulations, "
				+ groom.getName()
				+ " and "
				+ bride.getName()
				+ ", you are now engaged! Please make sure you have got wedding rings made before you go to the church for the service. And here are some invitations you can give to your guests.");
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

	private void makeRingsStep() {
		SpeakerNPC npc = npcs.get("Ognir");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestStateStartsWithCondition(QUEST_SLOT, "engaged"),
			ConversationStates.INFORMATION_1, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					if (player.isQuestInState(QUEST_SLOT, "engaged_with_ring")) {
						// player has wedding ring already. just remind to
						// get spouse to get one and hint to get dressed.
						npc.say("Looking forward to your wedding? Make sure your fiancee gets a wedding ring made for you, too! Oh and remember to get #dressed up for the big day.");
						npc.setCurrentState(ConversationStates.INFORMATION_2);
					} else {
						// says you'll need a ring
						npc.say("I see you're on a life-long quest to get married! I find marriage more of a task, ha ha! Anyway, you'll need a #wedding_ring.");
					}
				}
			});

		// response to QUEST_MESSAGES if you are not engaged
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, 
			"I'd forge a wedding ring for you to give your partner, if you were engaged to someone. If you want to get engaged, speak to the nun outside the church.",
			null);

		// response to QUEST_MESSAGES when you're already married
		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, 
			"You must already have enough to do, now that you're married. Don't worry about me!",
			null);

		// Here the beahviour is defined for if you say hi to Ognir and your
		// ring is being made
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new QuestStateStartsWithCondition(QUEST_SLOT, "forging;"),
			ConversationStates.IDLE, null, new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					String[] tokens = player.getQuest(QUEST_SLOT).split(";");
					long delay = REQUIRED_MINUTES * 60 * 1000; // minutes
					// ->
					// milliseconds
					long timeRemaining = (Long.parseLong(tokens[1]) + delay)
							- System.currentTimeMillis();
					// ring is not ready yet
					if (timeRemaining > 0L) {
						npc.say("I haven't finished making the wedding ring. Please check back in "
								+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
								+ ". Bye for now.");
						return;
					}
					/*
					 * ring is ready now. Bind it to person who made
					 * it.until the wedding day comes when the rings are
					 * exchanged Give a prompt to a little hint about
					 * getting dressed for the wedding, if players like to.
					 */
					npc.say("I'm pleased to say, the wedding ring for your fiancee is finished! Make sure one is made for you, too! *psst* just a little #hint for the wedding day ...");
					player.addXP(500);
					Item weddingRing = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
							"wedding_ring");
					weddingRing.setBoundTo(player.getName());
					player.equip(weddingRing, true);
					player.setQuest(QUEST_SLOT, "engaged_with_ring");
					player.notifyWorldAboutChanges();
					npc.setCurrentState(ConversationStates.INFORMATION_2);
				}
			});

		npc.add(
			ConversationStates.INFORMATION_1,
			Arrays.asList("wedding_ring", "wedding", "ring"),
			null,
			ConversationStates.QUEST_ITEM_QUESTION,
			"I need "
					+ REQUIRED_GOLD
					+ " gold bars and a fee of "
					+ REQUIRED_MONEY
					+ " money, to make a wedding ring for your fiancee. Do you have it?",
			null);

		// player says yes, they want a wedding ring made
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					if ((player.isEquipped("gold_bar", REQUIRED_GOLD))
							&& (player.isEquipped("money", REQUIRED_MONEY))) {
						player.drop("gold_bar", REQUIRED_GOLD);
						player.drop("money", REQUIRED_MONEY);
						npc.say("Good, come back in "
								+ REQUIRED_MINUTES
								+ " minutes and it will be ready. Goodbye until then.");
						player.setQuest(QUEST_SLOT, "forging;"
								+ System.currentTimeMillis());
						npc.setCurrentState(ConversationStates.IDLE);
					} else {
						// player said they had the money and/or gold but
						// they lied
						npc.say("Come back when you have both the money and the gold.");
					}
				}
			});

		// player says (s)he doesn't have the money and/or gold
		npc.add(
			ConversationStates.QUEST_ITEM_QUESTION,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"No problem, just come back when you have both the money and the gold.",
			null);

		// Just a little hint about getting dressed for the wedding.
		npc.add(
			ConversationStates.INFORMATION_2,
			Arrays.asList("dressed", "hint", "dress"),
			null,
			ConversationStates.ATTENDING,
			"When my wife and I got married we went to Fado hotel and hired special clothes. The dressing rooms are on your right when you go in, look for the wooden door. Good luck!",
			null);

	}

	private void getDressedStep() {

		// Just go to the NPCs Tamara and Timothy
		// you can only get into the room if you have the quest slot for
		// marriage
	}

	private void marriageStep() {

		/**
		 * Creates a priest NPC who can celebrate marriages between two players.
		 *
		 * Note: in this class, the Player variables are called groom and bride.
		 * However, the game doesn't know the concept of genders. The player who
		 * initiates the wedding is just called groom, the other bride.
		 *
		 * @author daniel
		 *
		 */

		priest = npcs.get("Priest");
		priest.add(ConversationStates.ATTENDING,
				"marry",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						return player.hasQuest(QUEST_SLOT)
								&& player.getQuest(QUEST_SLOT).equals("engaged_with_ring")
								&& player.isEquipped("wedding_ring");
					}
				}
				// TODO: make sure the pair getting married are engaged to each
				// other, if this is desired.
				, ConversationStates.ATTENDING, null,
				new SpeakerNPC.ChatAction() {

					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						// find out whom the player wants to marry.
				        String brideName = sentence.getObjectName();

				        if (brideName == null) {
				        	npc.say("You have to tell me who you want to marry.");
				        } else {
							startMarriage(npc, player, brideName);
						}
					}
				});

		priest.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.QUESTION_2, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					askBride();
				}
			});

		priest.add(ConversationStates.QUESTION_1,
			ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
			"What a pity! Goodbye!", null);

		priest.add(ConversationStates.QUESTION_2,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {

				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					finishMarriage();
				}
			});

		priest.add(ConversationStates.QUESTION_2,
			ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
			"What a pity! Goodbye!", null);

		// What he responds to marry if you haven't fulfilled all objectives
		// before hand
		priest.add(
			ConversationStates.ATTENDING,
			"marry",
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
					return (!player.hasQuest(QUEST_SLOT)
						|| (player.hasQuest(QUEST_SLOT) && player.getQuest(
								QUEST_SLOT).equals("engaged")) || (player.hasQuest(QUEST_SLOT)
						&& player.getQuest(QUEST_SLOT).equals("engaged_with_ring") 
						&& !player.isEquipped("wedding_ring")));
				}
			},
			ConversationStates.ATTENDING,
			"You're not ready to be married yet. Come back when you are properly engaged, and bring your wedding ring. And try to remember not to leave your partner behind ....",
			null);

		// What he responds to marry if you are already married
		priest.add(ConversationStates.ATTENDING, "marry",
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
					return (player.isQuestCompleted(QUEST_SLOT));
				}
			}, ConversationStates.ATTENDING,
			"You're married already, so you cannot marry again.", null);

	}

	private void divorceStep() {

		/**
		 * Creates a clerk NPC who can divorce couples.
		 *
		 * Note: in this class, the Player variables are called husband and
		 * wife. However, the game doesn't know the concept of genders. The
		 * player who initiates the divorce is just called husband, the other
		 * wife.
		 *
		 * @author immibis
		 *
		 */

		clerk = npcs.get("Wilfred");

		clerk.add(ConversationStates.ATTENDING, "divorce",
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
					return (player.isQuestCompleted(QUEST_SLOT)) && player.isEquipped("wedding_ring");
				}
			}, ConversationStates.QUESTION_3,
			"Are you sure you want to divorce?", null);

		clerk.add(
			ConversationStates.ATTENDING, "divorce",
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
					return (player.hasQuest(QUEST_SLOT) && player.getQuest(
							QUEST_SLOT).equals("just_married"))
							&& player.isEquipped("wedding_ring");
				}
			},
			ConversationStates.QUESTION_3,
			"I see you haven't been on your honeymoon yet. Are you sure you want to divorce so soon?",
			null);

		clerk.add(ConversationStates.ATTENDING, "divorce",
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
					return !(player.isQuestCompleted(QUEST_SLOT)||(player.hasQuest(QUEST_SLOT) && player.getQuest(QUEST_SLOT).equals("just_married")));
				}
			}, ConversationStates.ATTENDING,
			"You're not even married. Stop wasting my time!", null);

		clerk.add(ConversationStates.ATTENDING, "divorce",
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
					return !player.isEquipped("wedding_ring");
				}
			}, ConversationStates.ATTENDING, "I apologise, but I need your wedding ring in order to divorce you.",
			null);

		// If they say no
		clerk.add(ConversationStates.QUESTION_3,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"I hope you have a happy marriage, then.", null);

		// If they say yes
		clerk.add(ConversationStates.QUESTION_3,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING, null,
			new SpeakerNPC.ChatAction() {
				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					Player husband, wife;
					String partnerName;
					husband = player;
					partnerName = husband.getQuest(SPOUSE_QUEST_SLOT);
					wife = StendhalRPRuleProcessor.get().getPlayer(
							partnerName);
					// check wife is online and check that they're still
					// married to the current husband
					if (wife != null
							&& wife.hasQuest(QUEST_SLOT)
							&& wife.getQuest(SPOUSE_QUEST_SLOT).equals(
									husband.getName())) {
						if (wife.isEquipped("wedding_ring")) {
							wife.drop("wedding_ring");
						}
						int xp = (int) (wife.getXP() * 0.03);
						wife.subXP(xp);
						wife.removeQuest(QUEST_SLOT);
						wife.removeQuest(SPOUSE_QUEST_SLOT);
						wife.sendPrivateText(husband.getName()
								+ " has divorced from you.");
						npc.say("What a pity...what a pity...and you two were married so happily, too...");
					} else {
						Player postman = StendhalRPRuleProcessor.get().getPlayer("postman");
						if (postman != null) {
							postman.sendPrivateText("Wilfred tells you: msg "
									+ partnerName
									+ " "
									+ husband.getName()
									+ " has divorced from you!");
						}
					}
					int xp = (int) (husband.getXP() * 0.03);
					husband.subXP(xp);
					husband.drop("wedding_ring");
					husband.removeQuest(QUEST_SLOT);
					husband.removeQuest(SPOUSE_QUEST_SLOT);
					npc.say("What a pity...what a pity...and you two were married so happily, too...");
				}
			});

	}

	private void startMarriage(SpeakerNPC priest, Player player, String partnerName) {
		IRPZone churchZone = priest.getZone();
		Area inFrontOfAltar = new Area(churchZone, new Rectangle(10, 9, 4, 1));

		groom = player;
		bride = StendhalRPRuleProcessor.get().getPlayer(partnerName);

		if (!inFrontOfAltar.contains(groom)) {
			priest.say("You must step in front of the altar if you want to marry.");
		} else if (isMarried(groom)) {
			priest.say("You are married already, " + groom.getName()
					+ "! You can't marry again.");
		} else if (bride == null || !inFrontOfAltar.contains(bride)) {
			priest.say("You must bring your partner to the altar if you want to marry.");
		} else if (bride.getName().equals(groom.getName())) {
			priest.say("You can't marry yourself!");
		} else if (isMarried(bride)) {
			priest.say("You are married already, " + bride.getName()
					+ "! You can't marry again.");
		} else if (!bride.hasQuest(QUEST_SLOT)) {
			priest.say(bride.getName() + " isn't engaged.");
		} else if (bride.hasQuest(QUEST_SLOT)
				&& !bride.getQuest(QUEST_SLOT).startsWith("engaged")) {
			priest.say(bride.getName() + " isn't engaged.");
		} else if (bride.hasQuest(QUEST_SLOT)
				&& !bride.getQuest(QUEST_SLOT).equals("engaged_with_ring")) {
			priest.say(bride.getName()
					+ " hasn't been to Ognir to get a ring cast for you!");
		} else if (!bride.isEquipped("wedding_ring")) {
			priest.say(bride.getName()
					+ " hasn't got a wedding ring to give you.");
		} else {
			askGroom();
		}
	}

	private void askGroom() {
		priest.say(groom.getName() + ", do you really want to marry "
				+ bride.getName() + "?");
		priest.setCurrentState(ConversationStates.QUESTION_1);
	}

	private void askBride() {
		priest.say(bride.getName() + ", do you really want to marry "
				+ groom.getName() + "?");
		priest.setCurrentState(ConversationStates.QUESTION_2);
		priest.setAttending(bride);
	}

	private void finishMarriage() {
		exchangeRings();
		priest.say("Congratulations, "
				+ groom.getName()
				+ " and "
				+ bride.getName()
				+ ", you are now married! I don't really approve of this, but if you would like a honeymoon, go ask Linda in the hotel. Just say 'honeymoon' to her and she will understand.");
		// Memorize that the two married so that they can't just marry other
		// persons
		groom.setQuest(SPOUSE_QUEST_SLOT, bride.getName());
		bride.setQuest(SPOUSE_QUEST_SLOT, groom.getName());
		groom.setQuest(QUEST_SLOT, "just_married");
		bride.setQuest(QUEST_SLOT, "just_married");
		// Clear the variables so that other players can become groom and bride
		// later
		groom = null;
		bride = null;
	}

	private void giveRing(Player player, Player partner) {
		// players bring their own golden rings
		player.drop("wedding_ring");
		Item ring = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("wedding_ring");
		ring.setInfoString(partner.getName());
		ring.setBoundTo(player.getName());
		player.equip(ring, true);
	}

	private void exchangeRings() {
		giveRing(groom, bride);
		giveRing(bride, groom);
	}

	private void honeymoonStep() {

		SpeakerNPC linda = npcs.get("Linda");
		// tell her you want a honeymoon
		linda.add(
			ConversationStates.ATTENDING,"honeymoon",
			new SpeakerNPC.ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
					return (player.hasQuest(QUEST_SLOT) 
							&& player.getQuest(QUEST_SLOT).equals("just_married"));
				}
			},
			ConversationStates.QUESTION_1,
			"How lovely! Please read our catalogue here and tell me the room number that you would like.",
			null);

		// player says room number
		for (int room = 0; room < 16; room++) {
			linda.add(ConversationStates.QUESTION_1, Integer.toString(room),
				null, ConversationStates.QUESTION_1, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						npc.say("Great choice! Use this scroll to return to the hotel,"
								+ "our special honeymoon suites are so private that they don't use normal entrances and exits!");
						player.setQuest(QUEST_SLOT, "done");
						// yes i know it is stupid to do this here when i
						// could use the giveInvite thing above but i don't
						// know how to make it so that GiveInvite() has a
						// parameter for quantity and a parameter for
						// location so i gave up.
						StackableItem invite = (StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem(
								"invitation_scroll");
						invite.setQuantity(1);
						// interior of hotel
						invite.setInfoString("int_fado_hotel_0 4 40");
						player.equip(invite, true);
						StendhalRPZone zone = StendhalRPWorld.get().getZone(
								"int_fado_lovers_room_" + sentence.toString());
						player.teleport(zone, 5, 5, Direction.DOWN, player);
						player.notifyWorldAboutChanges();
						npc.setCurrentState(ConversationStates.IDLE);
					}
				});
		}

		// player says something which isn't a room number
		/*
		 * npc.add(ConversationStates.QUESTION_1, "", new
		 * SpeakerNPC.ChatCondition() { @Override public boolean fire(Player
		 * player, Sentence sentence, SpeakerNPC npc) { return !ROOMS.contains(text); } },
		 * ConversationStates.QUESTION_1, "Sorry, that's not a room number we
		 * have available.", null);
		 */

		// say honeymoon but you aren't 'just married'
		linda.add(
				ConversationStates.ATTENDING,
				"honeymoon",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						return (!(player.hasQuest(QUEST_SLOT) 
							&& player.getQuest(QUEST_SLOT).equals("just_married")));
					}
				},
				ConversationStates.ATTENDING,
				"Our honeymoon suites are only available for just married customers.",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		engagementStep();
		makeRingsStep();
		getDressedStep();
		marriageStep();
		honeymoonStep();
		divorceStep();
	}

}
