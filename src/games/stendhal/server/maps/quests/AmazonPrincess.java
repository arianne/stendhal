/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.InflictStatusOnNPCAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: The Amazon Princess
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Princess Esclara, the Amazon Princess in a Hut on Amazon Island</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>The princess asks you for an exotic drink</li>
 * <li>Find someone who serves exotic drinks</li>
 * <li>Take exotic drink back to princess</li>
 * <li>Princess gives you a reward</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>Karma +25 in all</li>
 * <li>Some fish pie, random between 2 and 7.</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>You can repeat it once an hour.</li>
 * </ul>
 */
public class AmazonPrincess extends AbstractQuest {

	private static final String QUEST_SLOT = "amazon_princess";

	// The delay between repeating quests is 60 minutes
	private static final int REQUIRED_MINUTES = 60;
	private static final List<String> triggers = Arrays.asList("drink", "pina colada", "cocktail", "cheers", "pina");


	private void offerQuestStep() {
		final SpeakerNPC npc = npcs.get("Princess Esclara");
npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"I'm looking for a drink, should be an exotic one. Can you bring me one?",
				null);
npc.add(ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		new QuestCompletedCondition(QUEST_SLOT),
		ConversationStates.ATTENDING,
		"I'm drunken now thank you!",
		null);

npc.add(ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		new AndCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES), new QuestStateStartsWithCondition(QUEST_SLOT, "drinking;")),
		ConversationStates.QUEST_OFFERED,
		"The last cocktail you brought me was so lovely. Will you bring me another?",
		null);

npc.add(ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		new AndCondition(new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)), new QuestStateStartsWithCondition(QUEST_SLOT, "drinking;")),
		ConversationStates.ATTENDING,
		null,
		new SayTimeRemainingAction(QUEST_SLOT, 1, REQUIRED_MINUTES, "I'm sure I'll be too drunk to have another for at least "));

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, null,
				ConversationStates.ATTENDING,
				"I like these exotic drinks, I forget the name of my favourite one.",
				null);

// Player agrees to get the drink
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Thank you! If you have found some, say #drink to me so I know you have it. I'll be sure to give you a nice reward.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 3));

		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
				"Oh, never mind. Bye then.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));
	}

	/**
	 * Get Drink Step :
	 * src/games/stendhal/server/maps/athor/cocktail_bar/BarmanNPC.java he
	 * serves drinks to all, not just those with the quest
	 */
	private void bringCocktailStep() {
		final SpeakerNPC npc = npcs.get("Princess Esclara");
		npc.add(
			ConversationStates.ATTENDING, triggers,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new PlayerHasItemWithHimCondition("pina colada")),
			ConversationStates.ATTENDING,
			null,
			new MultipleActions(
						new DropItemAction("pina colada"),
						new ChatAction() {
							@Override
							public void fire(final Player player,
									final Sentence sentence,
									final EventRaiser npc) {
								int pieAmount = Rand.roll1D6() + 1;
								new EquipItemAction("fish pie", pieAmount, true).fire(player, sentence, npc);
								npc.say("Thank you!! Take " +
										Grammar.thisthese(pieAmount) + " " +
										Grammar.quantityplnoun(pieAmount, "fish pie", "") +
										" from my cook, and this kiss, from me.");
								new SetQuestAndModifyKarmaAction(getSlotName(), "drinking;"
																 + System.currentTimeMillis(), 15.0).fire(player, sentence, npc);
							}
						},
						new InflictStatusOnNPCAction("pina colada")
						));

		npc.add(
			ConversationStates.ATTENDING, triggers,
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new PlayerHasItemWithHimCondition("pina colada"))),
			ConversationStates.ATTENDING,
			"You don't have any drink I like yet. Go, and you better get an exotic one!",
			null);

		npc.add(
			ConversationStates.ATTENDING, triggers,
			new QuestNotInStateCondition(QUEST_SLOT, "start"),
			ConversationStates.ATTENDING,
			"Sometime you could do me a #favour ...", null);

	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Amazon Princess",
				"A thirsty princess wants a drink.",
				true);
		offerQuestStep();
		bringCocktailStep();
	}


	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("Princess Esclara welcomed me to her home on Amazon Island.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("She asked me to fetch her a drink but I didn't think she should have one.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start") || isCompleted(player)) {
			res.add("The Princess is thirsty, I promised her an exotic drink, and should tell her 'drink' when I have it.");
		}
		if ("start".equals(questState) && player.isEquipped("pina colada") || isCompleted(player)) {
			res.add("I found a pina colada for the Princess, I think she'd like that.");
		}
        if (isCompleted(player)) {
            if (isRepeatable(player)) {
                res.add("I took a pina colada to the Princess, but I'd bet she's ready for another. Maybe I'll get more fish pies.");
            } else {
                res.add("Princess Esclara loved the pina colada I took her, she's not thirsty now. She gave me fish pies and a kiss!!");
            }
		}
		return res;
	}

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public String getName() {
		return "AmazonPrincess";
	}

	// Amazon is dangerous below this level - don't hint to go there
	@Override
	public int getMinLevel() {
		return 70;
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"drinking;"),
				 new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)).fire(player,null, null);
	}

	@Override
	public boolean isCompleted(final Player player) {
		return new QuestStateStartsWithCondition(QUEST_SLOT,"drinking;").fire(player, null, null);
	}

	@Override
	public String getRegion() {
		return Region.AMAZON_ISLAND;
	}

	@Override
	public String getNPCName() {
		return "Princess Esclara";
	}
}
