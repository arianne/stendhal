/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.marriage;

import java.util.Arrays;

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.TimeUtil;

public class MakeRings {
	private static final int REQUIRED_GOLD = 10;

	private static final int REQUIRED_MONEY = 500;

	private static final int REQUIRED_MINUTES = 10;

	private final NPCList npcs = SingletonRepository.getNPCList();
	private MarriageQuestInfo marriage;

	public MakeRings(final MarriageQuestInfo marriage) {
		this.marriage = marriage;
	}

	private void makeRingsStep() {
		final SpeakerNPC npc = npcs.get("Ognir");

		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("wedding ring", "wedding"),
				new QuestStateStartsWithCondition(marriage.getQuestSlot(), "engaged"),
				ConversationStates.QUEST_ITEM_QUESTION,
				null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						if (player.isQuestInState(marriage.getQuestSlot(), "engaged_with_ring")) {
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
				new QuestNotStartedCondition(marriage.getQuestSlot()),
				ConversationStates.ATTENDING,
				"I'd forge a wedding ring for you to give your partner, if you were engaged to someone. If you want to get engaged, speak to the nun outside the church.",
				null);

		// response to wedding ring enquiry when you're already married
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("wedding ring", "wedding"),
				new AndCondition(new QuestCompletedCondition(marriage.getQuestSlot()), new PlayerHasItemWithHimCondition("wedding ring")),
				ConversationStates.ATTENDING,
				"I hope you're still happily married! If you are having trouble and want a divorce, speak to the clerk in Ados Town Hall.",
				null);

		// response to wedding ring enquiry when you're already married and not wearing ring
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("wedding ring", "wedding"),
				new AndCondition(new QuestCompletedCondition(marriage.getQuestSlot()), new NotCondition(new PlayerHasItemWithHimCondition("wedding ring"))),
				ConversationStates.QUEST_ITEM_QUESTION,
				"Uh oh! You haven't got your wedding ring on! I can forge you another for " + REQUIRED_GOLD
									+ " gold bars and a fee of "
									+ REQUIRED_MONEY
									+ " money, do you want another?",
				null);

		// response to wedding ring enquiry when you're married but not taken honeymoon
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("wedding ring", "wedding"),
				new QuestInStateCondition(marriage.getQuestSlot(), "just_married"),
				ConversationStates.ATTENDING,
				"Congratulations on your recent wedding! Don't forget to ask Linda in Fado Hotel about your honeymoon.",
				null);

		// Here the behaviour is defined for if you make a wedding ring enquiry to Ognir and your
		// ring is being made
	 	npc.add(ConversationStates.ATTENDING,
				Arrays.asList("wedding ring", "wedding"),
		 		new QuestStateStartsWithCondition(marriage.getQuestSlot(), "forging"),
				ConversationStates.IDLE,
		 		null,
				new ChatAction() {
	 				@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
	 					final String[] tokens = player.getQuest(marriage.getQuestSlot()).split(";");
						final long delayInMIlliSeconds = REQUIRED_MINUTES * MathHelper.MILLISECONDS_IN_ONE_MINUTE;
						final long timeRemaining = (Long.parseLong(tokens[1]) + delayInMIlliSeconds)
								- System.currentTimeMillis();
						// ring is not ready yet
						if (timeRemaining > 0L) {
							npc.say("I haven't finished making the wedding ring. Please check back in "
									+ TimeUtil.approxTimeUntil((int) (timeRemaining / 1000L))
									+ ". Bye for now.");
							return;
						}
						/*The ring is ready now. It was either forging ready for a wedding or
						 * forging again because a married player lost theirs.
						 * In each case we bind to the player. If player is engaged the rings get swapped at marriage ceremony
						 * If this is a forgingagain we must set the infostring to spouse name so the ring works
						 * We don't give them any XP if it is to replace a lost ring. (fools.)
						 * If this is for an engaged player, npc gives a hitn about getting dressed for big day
						 */
						final Item weddingRing = SingletonRepository.getEntityManager().getItem(
								"wedding ring");
						weddingRing.setBoundTo(player.getName());
						if (player.getQuest(marriage.getQuestSlot()).startsWith("forgingagain")) {
							npc.say("I've finished making your replacement wedding ring. Do try to be more careful next time!");
							weddingRing.setInfoString(player.getQuest(marriage.getSpouseQuestSlot()));
							player.setQuest(marriage.getQuestSlot(), "done");
						} else {
							npc.say("I'm pleased to say, the wedding ring for your fiancee is finished! Make sure one is made for you, too! *psst* just a little #hint for the wedding day ...");
							player.setQuest(marriage.getQuestSlot(), "engaged_with_ring");
							player.addXP(500);
						}
						player.equipOrPutOnGround(weddingRing);
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
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
						if ((player.isEquipped("gold bar", REQUIRED_GOLD))
								&& (player.isEquipped("money", REQUIRED_MONEY))) {
							player.drop("gold bar", REQUIRED_GOLD);
							player.drop("money", REQUIRED_MONEY);
							npc.say("Good, come back in "
									+ REQUIRED_MINUTES
									+ " minutes and it will be ready. Goodbye until then.");
							if (player.isQuestCompleted(marriage.getQuestSlot())) {
								player.setQuest(marriage.getQuestSlot(), "forgingagain;"	+ System.currentTimeMillis());
							} else {
								player.setQuest(marriage.getQuestSlot(), "forging;"
												+ System.currentTimeMillis());
							}
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

	public void addToWorld() {
		makeRingsStep();
	}

}
