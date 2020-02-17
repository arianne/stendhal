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
package games.stendhal.server.maps.quests;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Imperial princess

 * PARTICIPANTS:
 * <ul>
 * <li> The Princess and King in Kalavan Castle</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Princess asks you to fetch a number of herbs and potions</li>
 * <li> You bring them</li>
 * <li> She recommends you to her father</li>
 * <li> you speak with him</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> XP</li>
 * <li> ability to buy houses in Kalavan</li>
 * <li> 10 Karma</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> None.</li>
 * </ul>
 */
public class ImperialPrincess extends AbstractQuest {

	/** The player is asked to get a number of herbs depending on level.
	 * So if they are level 40, they must bring 1 + 1 arandula
	 */
	private static final int ARANDULA_DIVISOR = 40;

	/** The player is asked to get a number of herbs depending on level.
	 * So if they are level 40, they must bring 4 + 1  potions
	 */
	private static final int POTION_DIVISOR = 10;

	/** The player is asked to get a number of herbs depending on level.
	 * So if they are level 40, they must bring 2 + 1 antidotes
	 */
	private static final int ANTIDOTE_DIVISOR = 20;

	// It is called Imperial Princess because the soldiers in this castle are Imperial soldiers.
	private static final String QUEST_SLOT = "imperial_princess";


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		final String questState = player.getQuest(QUEST_SLOT);
		res.add("Princess Ylflia asked me for some herbs and potions to ease the pain of captives in Kalavan Basement.");
		if (!questState.equals("recommended") && !questState.equals("done")) {
			res.add("I must tell Princess Ylflia that I have \"herbs\" when I have collected all the herbs, potions and antidotes she needs.");
		}
		if (player.isQuestInState(QUEST_SLOT, "recommended", "done")) {
			res.add("I took Princess Ylflia the healing items and she told me she would recommend me to her father, the King.");
		}
		if (questState.equals("done")) {
			res.add("King Cozart granted me citizenship of Kalavan.");
		}
		return res;
	}

	private void step_1() {

		final SpeakerNPC npc = npcs.get("Princess Ylflia");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I cannot free the captives in the basement but I could do one thing: ease their pain. " +
				"I need #herbs for this.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestInStateCondition(QUEST_SLOT,"recommended"),
				ConversationStates.ATTENDING,
				"Speak to my father, the King. I have asked him to grant you citizenship of Kalavan, " +
				"to express my gratitude to you.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new NotCondition(new QuestInStateCondition(QUEST_SLOT,"recommended"))),
				ConversationStates.ATTENDING,
				"I'm sure I asked you to do something for me, already.",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"The trapped creatures looked much better last time I dared venture down to the basement, thank you!",
				null);

		/** If quest is not started yet, start it.
		 * The amount of each item that the player must collect depends on their level when they started the quest.
		 */
		npc.add(ConversationStates.ATTENDING, "herbs",
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						raiser.say("I need "
								+ Integer.toString(1 + player.getLevel()
										/ ARANDULA_DIVISOR)
								+ " arandula, 1 kokuda, 1 sclaria, 1 kekik, "
								+ Integer.toString(1 + player.getLevel()
										/ POTION_DIVISOR)
								+ " potions and "
								+ Integer.toString(1 + player.getLevel()
										/ ANTIDOTE_DIVISOR)
								+ " antidotes. Will you get these items?");
					}
				});

		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Thank you! We must be subtle about this, I do not want the scientists suspecting I interfere. " +
				"When you return with the items, please say codeword #herbs.",
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						// store the current level in case it increases before
						// she see them next.
						player.setQuest(QUEST_SLOT, Integer.toString(player.getLevel()));
						player.addKarma(10);
					}
				});

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				"So you'll just let them suffer! How despicable.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		// give some hints of where to find herbs. No warranties!
		npc.addReply(
				"kokuda",
				"I believe that herb can only be found on Athor, though they guard their secrets" +
				" closely over there.");
		npc.addReply(
				"sclaria",
				"Healers who use sclaria gather it in all sorts of places - around Or'ril, in Nalwor" +
				" forest, I am sure you will find that without trouble.");
		npc.addReply(
				"kekik",
				"My maid's friend Jenny has a source not far from her. The wooded areas at the eastern" +
				" end of Nalwor river may have it. too.");
	}

	private void step_2() {
		final SpeakerNPC npc = npcs.get("Princess Ylflia");

		/** If player has quest and not in state recommended,
		 * we can check the slot to see what the stored level was.
		 * If the player has brought the right number of herbs, get them */
		npc.add(ConversationStates.ATTENDING, Arrays.asList("herb", "herbs"),
				new AndCondition(
						new QuestActiveCondition(QUEST_SLOT),
						new NotCondition(new QuestInStateCondition(QUEST_SLOT,"recommended"))),
				ConversationStates.ATTENDING, null,
				new ChatAction() {
					@Override
					public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
						try {
							final int level = Integer.parseInt(player.getQuest(QUEST_SLOT));
							final int required_arandula = 1
								+ level / ARANDULA_DIVISOR;
							final int required_antidote = 1
								+ level / ANTIDOTE_DIVISOR;
							final int required_potion = 1
								+ level	/ POTION_DIVISOR;
							if (player.isEquipped("kekik")
								&& player.isEquipped("kokuda")
								&& player.isEquipped("sclaria")
								&& player.isEquipped("arandula",
										required_arandula)
								&& player.isEquipped("potion", required_potion)
								&& player.isEquipped("antidote",
										required_antidote))
							{
								player.drop("kekik");
								player.drop("kokuda");
								player.drop("sclaria");
								player.drop("antidote", required_antidote);
								player.drop("potion", required_potion);
								player.drop("arandula", required_arandula);
								raiser.say("Perfect! I will recommend you to my father, as a fine, " +
										"helpful person. He will certainly agree you are eligible for " +
										"citizenship of Kalavan.");
								player.addXP(level * 400);
								player.setQuest(QUEST_SLOT, "recommended");
								player.notifyWorldAboutChanges();
							} else {
								//reminder of the items to bring
								raiser.say("Shh! Don't say it till you have the "
									+ required_arandula
									+ " arandula, 1 #kokuda, 1 #sclaria, 1 #kekik, "
									+ required_potion
									+ " potions and "
									+ required_antidote
									+ " antidotes. I don't want anyone suspecting our code.");
							}
						} catch (final NumberFormatException e) {
							// Should not happen but catch the exception
							raiser.say("That's strange. I don't understand what has happened just now. " +
									"Sorry but I'm all confused, try asking someone else for help.");
						}
					}
				});

		/** The player asked about herbs but he brought them already and needs to speak to the King next */
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("herb", "herbs"),
				new QuestInStateCondition(QUEST_SLOT,"recommended"),
				ConversationStates.ATTENDING,
				"The herbs you brought did a wonderful job. I told my father you can be trusted, you should " +
				"go speak with him now.",
				null);

		/** The player asked about herbs but the quest was finished */
		npc.add(ConversationStates.ATTENDING,
				Arrays.asList("herb", "herbs"),
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"Thanks for the herbs you brought to heal the creatures, I'm glad my father recommended you for " +
				"being a citizen of Kalavan.",
				null);
	}

	private void step_3() {
		/** The King is also in the castle and he is the father of the Princess who gave the quest */
		final SpeakerNPC npc = npcs.get("King Cozart");

		/** Complete the quest by speaking to King, who will return right back to idle once he rewards the player*/
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestInStateCondition(QUEST_SLOT, "recommended")),
			ConversationStates.IDLE,
			"Greetings! My wonderful daughter requests that I grant you citizenship of Kalavan City. Consider it done. Now, forgive me while I go back to my meal. Goodbye.",
			new MultipleActions(new IncreaseXPAction(500), new SetQuestAction(QUEST_SLOT, "done")));

		/** If you aren't in the condition to speak to him (not completed quest, or already spoke) the King will dismiss you */
		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(new GreetingMatchesNameCondition(npc.getName()),
					new QuestNotInStateCondition(QUEST_SLOT, "recommended")),
			ConversationStates.IDLE,
			"Leave me! Can't you see I am trying to eat?",
			null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Gaining Citizenship of Kalavan",
				"To gain official citizenship for Kalavan City, one must first ask for the permission of the King. His daughter, Princess Ylflia, can help gain his ear.",
				true);
		step_1();
		step_2();
		step_3();
	}
	@Override
	public String getName() {
		return "ImperialPrincess";
	}

	@Override
	public int getMinLevel() {
		return 50;
	}

	@Override
	public String getRegion() {
		return Region.KALAVAN;
	}

	@Override
	public String getNPCName() {
		return "Princess Ylflia";
	}
}
