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
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * QUEST: Coal for Haunchy
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Haunchy Meatoch, the BBQ grillmaster on the Ados market</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Haunchy Meatoch asks you to fetch coal for his BBQ</li>
 * <li>Find some coal in Semos Mine or buy some from other players</li>
 * <li>Take the coal to Haunchy</li>
 * <li>Haunchy gives you a tasty reward</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>Karma +25 in all</li>
 * <li>XP +200 in all</li>
 * <li>Some grilled steaks, random between 1 and 4.</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>You can repeat it each 2 days.</li>
 * </ul>
 * 
 * @author Vanessa Julius and storyteller
 */
public class CoalForHaunchy extends AbstractQuest {

	private static final String QUEST_SLOT = "coal_for_haunchy";

	// The delay between repeating quests is 48 hours or 2880 minutes
	private static final int REQUIRED_MINUTES = 2880;

	private void offerQuestStep() {
		final SpeakerNPC npc = npcs.get("Haunchy Meatoch");
		
		// player says quest when he has not ever done the quest before (rejected or just new)
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED, 
				"I cannot use wood for this huge BBQ. To keep the heat I need some really old stone coal but there isn't much left. The problem is, that I can't fetch it myself because my steaks would burn then so I have to stay here. Can you bring me 25 pieces of #coal for my BBQ please?",
				null);

		npc.add(
				ConversationStates.QUEST_OFFERED,
				Arrays.asList("coal"),
				null,
				ConversationStates.QUEST_OFFERED,
				"Coal isn't easy to find. You normally can find it somewhere in the ground but perhaps you are lucky and find some in the old Semos Mine tunnels...",
				null);

        // player has completed the quest (doesn't happen here)
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestCompletedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I can go on with grilling my tasty steaks now! Thank you!",
				null);

		// player asks about quest which he has done already and he is allowed to repeat it
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES), new QuestStateStartsWithCondition(QUEST_SLOT, "waiting;")),
				ConversationStates.QUEST_OFFERED,
				"The last coal you brought me is mostly gone again. Will you bring me some more?",
				null);
		
		// player asks about quest which he has done already but it is not time to repeat it
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)), new QuestStateStartsWithCondition(QUEST_SLOT, "waiting;")),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, REQUIRED_MINUTES, "The coal amount behind my counter is still high enough. I will not need more for"));

		// Player agrees to get the coal, increase 5 karma
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES, null,
				ConversationStates.ATTENDING,
				"Thank you! If you have found 25 pieces, say #coal to me so I know you have it. I'll be sure to give you a nice and tasty reward.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 5));

		// Player says no, they've lost karma.
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null, ConversationStates.IDLE,
				"Oh, never mind. I thought you love BBQs like I do. Bye then.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -10.0));
	}

	/*
	 * Get Coal Step :
	 * Players will get some coal in Semos Mine and with buying some from other players.
	 * 
	 */
	private void bringCoalStep() {
		final SpeakerNPC npc = npcs.get("Haunchy Meatoch");
		
		final List<String> triggers = new ArrayList<String>();
		triggers.add("coal");
		triggers.add("stone coal");
		triggers.addAll(ConversationPhrases.QUEST_MESSAGES);

		// player asks about quest or says coal when they are supposed to bring some coal and they have it
		npc.add(
				ConversationStates.ATTENDING, triggers,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new PlayerHasItemWithHimCondition("coal",25)),
				ConversationStates.ATTENDING, 
				null,
				new MultipleActions(
						new DropItemAction("coal",25), 
						new IncreaseXPAction(200),
						new IncreaseKarmaAction(20),
						new ChatAction() {
							@Override
							public void fire(final Player player,
									final Sentence sentence,
									final EventRaiser npc) {
								int grilledsteakAmount = Rand.rand(4) + 1;
								new EquipItemAction("grilled steak", grilledsteakAmount, true).fire(player, sentence, npc);
								npc.say("Thank you!! Take " + Grammar.thisthese(grilledsteakAmount) + " " +
										Grammar.quantityNumberStrNoun(grilledsteakAmount, "grilled steak") + " from my grill!");
								new SetQuestAndModifyKarmaAction(getSlotName(), "waiting;" 
										+ System.currentTimeMillis(), 10.0).fire(player, sentence, npc);
							}
						}));

		// player asks about quest or says coal when they are supposed to bring some coal and they don't have it
		npc.add(
				ConversationStates.ATTENDING, triggers,
				new AndCondition(new QuestInStateCondition(QUEST_SLOT, "start"), new NotCondition(new PlayerHasItemWithHimCondition("coal",25))),
				ConversationStates.ATTENDING,
				"You don't have the coal amount which I need yet. Go and pick some more pieces up, please.",
				null);
		
		npc.add(
				ConversationStates.ATTENDING, 
				Arrays.asList("coal","stone coal"),
				new QuestNotInStateCondition(QUEST_SLOT,"start"),
				ConversationStates.ATTENDING,
				"Sometime you could do me a #favour ...", null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Coal for Haunchy",
				"Haunchy Meatoch is afraid of his BBQ grillfire. Will his coal last till the steaks are ready or will he need some more?",
				true);
		offerQuestStep();
		bringCoalStep();
	}


	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("Haunchy Meatoch welcomed me to the Ados market.");
		final String questState = player.getQuest(QUEST_SLOT);
		if ("rejected".equals(questState)) {
			res.add("He asked me to fetch him some pieces of coal but I don't have time to collect some.");
		}
		if (player.isQuestInState(QUEST_SLOT, "start") || isCompleted(player)) {
			res.add("The BBQ grill-heat is low and I promised Haunchy to help him out with 25 pieces of coal.");
		}
		if ("start".equals(questState) && player.isEquipped("coal",25) || isCompleted(player)) {
			res.add("I found 25 pieces of coal for the Haunchy and think he will be happy.");
		}
		if (isCompleted(player)) {
			if (isRepeatable(player)) {
				res.add("I took 25 pieces of coal to the Haunchy, but I'd bet his amount is low again and needs more. Maybe I'll get more grilled tasty steaks.");
			} else {
				res.add("Haunchy Meatoch was really happy when I gave him the coal, he has enough for now. He gave me some of the best steaks which I ever ate!");
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
		return "CoalForHaunchy";
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"waiting;"),
				new TimePassedCondition(QUEST_SLOT, 1, REQUIRED_MINUTES)).fire(player,null, null);
	}

	@Override
	public boolean isCompleted(final Player player) {
		return new QuestStateStartsWithCondition(QUEST_SLOT,"waiting;").fire(player, null, null);
	}
	
	@Override
	public String getRegion() {
		return Region.ADOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Haunchy Meatoch";
	}
}
