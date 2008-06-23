package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.parser.Expression;
import games.stendhal.server.entity.npc.parser.JokerExprMatcher;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;
import games.stendhal.server.util.TimeUtil;

import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

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

	private NPCList npcs = SingletonRepository.getNPCList();

	private Player groom;

	private Player bride;

	private SpeakerNPC nun;

	private SpeakerNPC priest;

	private SpeakerNPC clerk;

	private void engagementStep() {
		nun = npcs.get("Sister Benedicta");
		nun.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC engine) {
						if (!player.hasQuest(QUEST_SLOT)) {
							engine.say("The great quest of all life is to be #married.");
						} else if (player.isQuestCompleted(QUEST_SLOT)) {
							engine.say("I hope you are enjoying married life.");
						} else {
							engine.say("Haven't you organised your wedding yet?");
						}
					}
				});

		nun.add(ConversationStates.ATTENDING,
				"married",
				null,
				ConversationStates.ATTENDING,
				"If you have a partner, you can marry them at a #wedding. Once you have a wedding ring, you can be together whenever you want.",
				null);

		nun.add(ConversationStates.ATTENDING,
				"wedding",
				null,
				ConversationStates.ATTENDING,
				"You may marry here at this church. If you want to #engage someone, just tell me who.",
				null);

		nun.add(ConversationStates.ATTENDING, 
				"engage", 
				null,
				ConversationStates.ATTENDING, 
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC npc) {
						// find out whom the player wants to marry.
						String brideName = sentence.getSubjectName();

						if (brideName == null) {
							npc.say("You have to tell me who you want to marry.");
						} else {
							startEngagement(npc, player, brideName);
						}
					}
				});

		nun.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.QUESTION_2, 
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC npc) {
						askBrideE();
					}
				});

		nun.add(ConversationStates.QUESTION_1, 
				ConversationPhrases.NO_MESSAGES,
				null, 
				ConversationStates.IDLE, 
				"What a shame! Goodbye!", 
				null);

		nun.add(ConversationStates.QUESTION_2,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence,
							SpeakerNPC npc) {
						finishEngagement();
					}
				});

		nun.add(ConversationStates.QUESTION_2, 
				ConversationPhrases.NO_MESSAGES,
				null, 
				ConversationStates.IDLE, 
				"What a shame! Goodbye!", 
				null);
	}

	private void startEngagement(SpeakerNPC nun, Player player,
			String partnerName) {
		IRPZone outsideChurchZone = nun.getZone();
		Area inFrontOfNun = new Area(outsideChurchZone, new Rectangle(51, 52, 6, 5));
		groom = player;
		bride = SingletonRepository.getRuleProcessor().getPlayer(partnerName);

		if (!inFrontOfNun.contains(groom)) {
			nun.say("My hearing is not so good, please both come close to tell me who you want to get engaged to.");
		} else if (isMarried(groom)) {
			nun.say("You are married already, " 
					+ groom.getName()
					+ "! You can't marry again.");
		} else if (bride == null || !inFrontOfNun.contains(bride)) {
			nun.say("My hearing is not so good, please both come close to tell me who you want to get engaged to.");
		} else if (bride.getName().equals(groom.getName())) {
			nun.say("You can't marry yourself!");
		} else if (isMarried(bride)) {
			nun.say("You are married already, " 
					+ bride.getName()
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
		StackableItem invite = (StackableItem) SingletonRepository.getEntityManager().getItem(
				"invitation scroll");
		invite.setQuantity(4);
		// location of church
		invite.setInfoString("int_fado_church 12 20");

		// perhaps change this to a hotel room where they can get dressed into
		// wedding outfits?
		// then they walk to the church?
		player.equip(invite, true);
	}

	private void finishEngagement() {
		// we check if each of the bride and groom are engaged, or both, and only give invites 
		// if they were not already engaged.
		String additional;
		if (!isEngaged(groom)) {
			giveInvite(groom);
			if (!isEngaged(bride)) {
				giveInvite(bride);
				additional = "And here are some invitations you can give to your guests.";
			} else {
				additional = "I have given invitations for your guests to " + groom.getName() + ".";
				}
		} else if (!isEngaged(bride)) {
			giveInvite(bride);
			additional = "I have given invitations for your guests to " + bride.getName() + ".";
		} else {
			additional = "I have not given you more invitation scrolls, as you were both already engaged, and had them before.";
		}		
		nun.say("Congratulations, "
				+ groom.getName()
				+ " and "
				+ bride.getName()
				+ ", you are now engaged! Please make sure you have got wedding rings made before you go to the church for the service. " + additional);
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
	
    private boolean isEngaged(Player player) {
        return (player.hasQuest(QUEST_SLOT) && player.getQuest(QUEST_SLOT).startsWith("engaged"));
    }

	private void makeRingsStep() {
		SpeakerNPC npc = npcs.get("Ognir");

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("wedding ring", "wedding"),
				new QuestStateStartsWithCondition(QUEST_SLOT, "engaged"),
				ConversationStates.QUEST_ITEM_QUESTION, 
				null,
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
							npc.say("I need "
									+ REQUIRED_GOLD
									+ " gold bars and a fee of "
									+ REQUIRED_MONEY
									+ " money, to make a wedding ring for your fiancee. Do you have it?");
						}
					}
				});

		// response to wedding ring enquiry if you are not engaged
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("wedding ring", "wedding"),
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I'd forge a wedding ring for you to give your partner, if you were engaged to someone. If you want to get engaged, speak to the nun outside the church.",
				null);

		// response to wedding ring enquiry when you're already married
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("wedding ring", "wedding"),
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I hope you're still happily married, else I can't see why you'd need another ring...though if you are having trouble and want a divorce, speak to the clerk in Ados Town Hall.",
				null);

		// response to wedding ring enquiry when you're married but not taken honeymoon
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("wedding ring", "wedding"),
				new QuestInStateCondition(QUEST_SLOT, "just_married"),
				ConversationStates.ATTENDING,
				"Congratulations on your recent wedding! Don't forget to ask Linda in Fado Hotel about your honeymoon.",
				null);

		// Here the behaviour is defined for if you make a wedding ring enquiry to Ognir and your
		// ring is being made
	 	npc.add(ConversationStates.ATTENDING, 
				Arrays.asList("wedding ring", "wedding"),
		 		new QuestStateStartsWithCondition(QUEST_SLOT, "forging;"),
				ConversationStates.IDLE, 
		 		null, 
				new SpeakerNPC.ChatAction() {
	 				@Override
	 				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
	 					String[] tokens = player.getQuest(QUEST_SLOT).split(";");
						long delayInMIlliSeconds = REQUIRED_MINUTES * MathHelper.MILLISECONDS_IN_ONE_MINUTE; 
						long timeRemaining = (Long.parseLong(tokens[1]) + delayInMIlliSeconds)
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
						Item weddingRing = SingletonRepository.getEntityManager().getItem(
								"wedding ring");
						weddingRing.setBoundTo(player.getName());
						player.equip(weddingRing, true);
						player.setQuest(QUEST_SLOT, "engaged_with_ring");
						player.notifyWorldAboutChanges();
						npc.setCurrentState(ConversationStates.INFORMATION_2);
					}
				});


		// player says yes, they want a wedding ring made
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						if ((player.isEquipped("gold bar", REQUIRED_GOLD))
								&& (player.isEquipped("money", REQUIRED_MONEY))) {
							player.drop("gold bar", REQUIRED_GOLD);
							player.drop("money", REQUIRED_MONEY);
							npc.say("Good, come back in "
									+ REQUIRED_MINUTES
									+ " minutes and it will be ready. Goodbye until then.");
							player.setQuest(QUEST_SLOT, "forging;"
									+ System.currentTimeMillis());
							npc.setCurrentState(ConversationStates.IDLE);
						} else {
							// player said they had the money and/or gold but they lied
							npc.say("Come back when you have both the money and the gold.");
						}
					}
				});

		// player says (s)he doesn't have the money and/or gold
		npc.add(ConversationStates.QUEST_ITEM_QUESTION,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"No problem, just come back when you have both the money and the gold.",
				null);

		// Just a little hint about getting dressed for the wedding.
		npc.add(ConversationStates.INFORMATION_2,
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
						public boolean fire(Player player, Sentence sentence,
								SpeakerNPC npc) {
							return player.hasQuest(QUEST_SLOT)
									&& player.getQuest(QUEST_SLOT).startsWith(
											"engaged")
									&& player.isEquipped("wedding ring");
						}
					},
					// TODO: make sure the pair getting married are engaged to each
					// other, if this is desired.
					ConversationStates.ATTENDING, 
					null,
					new SpeakerNPC.ChatAction() {
	
						@Override
						public void fire(Player player, Sentence sentence,
								SpeakerNPC npc) {
							// find out whom the player wants to marry.
							String brideName = sentence.getSubjectName();
	
							if (brideName == null) {
								npc.say("You have to tell me who you want to marry.");
							} else {
								startMarriage(npc, player, brideName);
							}
						}
					});

		priest.add(ConversationStates.QUESTION_1,
					ConversationPhrases.YES_MESSAGES, 
					null,
					ConversationStates.QUESTION_2, 
					null,
					new SpeakerNPC.ChatAction() {
						@Override
						public void fire(Player player, Sentence sentence,
								SpeakerNPC npc) {
							askBride();
						}
					});

		priest.add(ConversationStates.QUESTION_1,
					ConversationPhrases.NO_MESSAGES, 
					null, 
					ConversationStates.IDLE,
					"What a pity! Goodbye!", 
					null);

		priest.add(ConversationStates.QUESTION_2,
					ConversationPhrases.YES_MESSAGES, 
					null,
					ConversationStates.ATTENDING, 
					null,
					new SpeakerNPC.ChatAction() {
	
						@Override
						public void fire(Player player, Sentence sentence,
								SpeakerNPC npc) {
							finishMarriage();
						}
					});

		priest.add(ConversationStates.QUESTION_2,
					ConversationPhrases.NO_MESSAGES, 
					null, 
					ConversationStates.IDLE,
					"What a pity! Goodbye!", 
					null);

		// What he responds to marry if you haven't fulfilled all objectives
		// before hand
		priest.add(ConversationStates.ATTENDING,
					"marry",
					new SpeakerNPC.ChatCondition() {
						@Override
						public boolean fire(Player player, Sentence sentence,
								SpeakerNPC npc) {
							return (!player.hasQuest(QUEST_SLOT) 
									|| (player.hasQuest(QUEST_SLOT)	&& player.getQuest(QUEST_SLOT).startsWith("engaged") && !player.isEquipped("wedding ring")));
						}
					},
					ConversationStates.ATTENDING,
					"You're not ready to be married yet. Come back when you are properly engaged, and bring your wedding ring. And try to remember not to leave your partner behind ....",
					null);

		// What he responds to marry if you are already married
		priest.add(ConversationStates.ATTENDING, 
				"marry",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence,
							SpeakerNPC npc) {
						return (player.isQuestCompleted(QUEST_SLOT));
					}
				}, 
				ConversationStates.ATTENDING,
				"You're married already, so you cannot marry again.", 
				null);

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

		clerk.add(ConversationStates.ATTENDING, 
				"divorce",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						return (player.isQuestCompleted(QUEST_SLOT))
								&& player.isEquipped("wedding ring");
					}
				}, 
				ConversationStates.QUESTION_3,
				"Are you sure you want to divorce?", 
				null);

		clerk.add(ConversationStates.ATTENDING,
					"divorce",
					new SpeakerNPC.ChatCondition() {
						@Override
						public boolean fire(Player player, Sentence sentence,
								SpeakerNPC npc) {
							return (player.hasQuest(QUEST_SLOT) && player.getQuest(
									QUEST_SLOT).equals("just_married"))
									&& player.isEquipped("wedding ring");
						}
					},
					ConversationStates.QUESTION_3,
					"I see you haven't been on your honeymoon yet. Are you sure you want to divorce so soon?",
					null);

		clerk.add(ConversationStates.ATTENDING,
				"divorce",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						return !(player.isQuestCompleted(QUEST_SLOT) || (player.hasQuest(QUEST_SLOT) && player.getQuest(
								QUEST_SLOT).equals("just_married")));
					}
				}, ConversationStates.ATTENDING,
				"You're not even married. Stop wasting my time!",
				null);

		clerk.add(ConversationStates.ATTENDING,
				"divorce",
				new SpeakerNPC.ChatCondition() {
					@Override
					public boolean fire(Player player, Sentence sentence,
							SpeakerNPC npc) {
						return !player.isEquipped("wedding ring");
					}
				},
				ConversationStates.ATTENDING,
				"I apologise, but I need your wedding ring in order to divorce you.",
				null);

		// If they say no
		clerk.add(ConversationStates.QUESTION_3,
				ConversationPhrases.NO_MESSAGES, 
				null,
				ConversationStates.ATTENDING,
				"I hope you have a happy marriage, then.", 
				null);

		// If they say yes
		clerk.add(ConversationStates.QUESTION_3,
				ConversationPhrases.YES_MESSAGES, 
				null,
				ConversationStates.ATTENDING, 
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
						Player husband;
						Player wife;
						String partnerName;
						husband = player;
						partnerName = husband.getQuest(SPOUSE_QUEST_SLOT);
						wife = SingletonRepository.getRuleProcessor().getPlayer(
								partnerName);
						// check wife is online and check that they're still
						// married to the current husband
						if (wife != null
								&& wife.hasQuest(QUEST_SLOT)
								&& wife.getQuest(SPOUSE_QUEST_SLOT).equals(
										husband.getName())) {
							if (wife.isEquipped("wedding ring")) {
								wife.drop("wedding ring");
							}
							int xp = (int) (wife.getXP() * 0.03);
							wife.subXP(xp);
							wife.removeQuest(QUEST_SLOT);
							wife.removeQuest(SPOUSE_QUEST_SLOT);
							wife.sendPrivateText(husband.getName()
									+ " has divorced from you.");
							npc.say("What a pity...what a pity...and you two were married so happily, too...");
						} else {
							Player postman = SingletonRepository.getRuleProcessor().getPlayer(
									"postman");
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
						husband.drop("wedding ring");
						husband.removeQuest(QUEST_SLOT);
						husband.removeQuest(SPOUSE_QUEST_SLOT);
						npc.say("What a pity...what a pity...and you two were married so happily, too...");
					}
				});

	}

	private void startMarriage(SpeakerNPC priest, Player player,
			String partnerName) {
		IRPZone churchZone = priest.getZone();
		Area inFrontOfAltar = new Area(churchZone, new Rectangle(10, 9, 4, 1));

		groom = player;
		bride = SingletonRepository.getRuleProcessor().getPlayer(partnerName);

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
		}  else if (!bride.isEquipped("wedding ring")) {
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
		player.drop("wedding ring");
		Item ring = SingletonRepository.getEntityManager().getItem(
				"wedding ring");
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
				ConversationStates.ATTENDING,
				"honeymoon",
				null,
				ConversationStates.QUESTION_1, null,
				new SpeakerNPC.ChatAction() {
                    @Override
						public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
                        IRPZone fadoHotel = npc.getZone();
                        Area hotelReception = new Area(fadoHotel, new Rectangle(11, 46, 19, 10));

                        Player husband;
                        Player wife;
                        String partnerName;
                        husband = player;
                        partnerName = husband.getQuest(SPOUSE_QUEST_SLOT);
                        wife = SingletonRepository.getRuleProcessor().getPlayer(partnerName);
                        // check person asking is just married
						if (!(player.hasQuest(QUEST_SLOT)) || !("just_married".equals(player.getQuest(QUEST_SLOT)))) {
							npc.say("Sorry, our honeymoon suites are only available for just married customers.");
							npc.setCurrentState(ConversationStates.ATTENDING);						
						} 
						// check wife is online and check that they're still
						// married to the current husband    
						else if (wife == null){
                            npc.say("Come back when " + partnerName + " is with you - you're meant to have your honeymoon together!");
                            npc.setCurrentState(ConversationStates.IDLE);
                        } else if (!(wife.hasQuest(QUEST_SLOT)
                                     && wife.getQuest(SPOUSE_QUEST_SLOT).equals(husband.getName()))) {
                            npc.say("Oh dear, this is embarassing. You seem to be married, but " + partnerName + " is not married to you.");
                            npc.setCurrentState(ConversationStates.ATTENDING);
                        }
                        // check wife has bothered to come to reception desk
						else if (!hotelReception.contains(wife)){
                            npc.say("Could you get " + partnerName + " to come to the reception desk, please. Then please read our catalogue here and tell me the room number that you would like.");
                        }  else { 
							npc.say("How lovely! Please read our catalogue here and tell me the room number that you would like.");
						}
					}
				});
		// player says room number
		linda.add(ConversationStates.QUESTION_1,
				// match for all numbers as trigger expression
				"NUM", new JokerExprMatcher(),
				new ChatCondition() {
					@Override
                    public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
						Expression number = sentence.getNumeral();

						if (number != null) {
    						int roomNr = number.getAmount();

    						// check for correct room numbers
    						if (roomNr >= 1 && roomNr <= 15) {
    							return true;
    						}
						}

    					return false;
                    }
				}, ConversationStates.QUESTION_1, null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, Sentence sentence, SpeakerNPC npc) {

                        String room = Integer.toString(sentence.getNumeral().getAmount());
                        StendhalRPZone zone = SingletonRepository.getRPWorld().getZone(
                                                                                       "int_fado_lovers_room_" + room);
						if (zone.getPlayers().size() > 0) {
							npc.say("Sorry, that room is currently occupied, would you give me your next choice please?");
						} else {

							Player husband;
							Player wife;
							String partnerName;
							husband = player;
							partnerName = husband.getQuest(SPOUSE_QUEST_SLOT);
							wife = SingletonRepository.getRuleProcessor().getPlayer(
                                                                                partnerName);
							// I (kym) have to make two of these because when I only did one, the second one sometimes had quantity 0 sometimes didn't.
							StackableItem invite1 = (StackableItem) SingletonRepository.getEntityManager().getItem(
																												  "invitation scroll");
							invite1.setQuantity(1);
                            StackableItem invite2 = (StackableItem) SingletonRepository.getEntityManager().getItem(
                                                                                                                  "invitation scroll");
                            invite2.setQuantity(1);
                            
							invite1.setInfoString("int_fado_hotel_0 4 40");
							invite2.setInfoString("int_fado_hotel_0 4 40");
							if (wife.equip(invite1) &&  husband.equip(invite2)) {
								npc.say("Great choice! I will arrange that now."); 
								husband.setQuest(QUEST_SLOT, "done");
								wife.setQuest(QUEST_SLOT, "done");
								wife.teleport(zone, 5, 5, Direction.DOWN, player);
								husband.teleport(zone, 6, 5, Direction.DOWN, player);
								String scrollmessage = "Linda tells you: Use the scroll in your bag to return to the hotel, our special honeymoon suites are so private that they don't use normal entrances and exits!";
								wife.sendPrivateText(scrollmessage);
                                husband.sendPrivateText(scrollmessage);
								wife.notifyWorldAboutChanges();
								husband.notifyWorldAboutChanges();
								npc.setCurrentState(ConversationStates.IDLE);
							} else {
								npc.say("You each need one space in your bags to take a scroll. Please make a space and then ask me again. Thank you.");
							}
						}
					}
				});

		// player says something which isn't a room number
//		npc.add(ConversationStates.QUESTION_1, "",
//			new SpeakerNPC.ChatCondition() {
//				@Override public boolean fire(Player player, Sentence sentence, SpeakerNPC npc) {
//					return !ROOMS.contains(text);
//				}
//			}, ConversationStates.QUESTION_1,
//			"Sorry, that's not a room number we have available.", null
//		);

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
