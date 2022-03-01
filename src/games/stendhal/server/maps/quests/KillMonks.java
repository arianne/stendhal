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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.common.Rand;
import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DecreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.KilledForQuestCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateStartsWithCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;
import games.stendhal.server.util.KillsForQuestCounter;
import marauroa.common.Pair;

/**
 * QUEST: KillMonks
 *
 * PARTICIPANTS: <ul>
 * <li> Andy on Ados cemetery
 * <li> Darkmonks and normal monks
 * </ul>
 *
 * STEPS:<ul>
 * <li> Andy who is sad about the death of his wife, wants revenge for her death
 * <li> Kill 25 monks and 25 darkmonks for him for reaching his goal
 * </ul>
 *
 *
 * REWARD:<ul>
 * <li> 15000 XP
 * <li> 1-5 soup
 * <li> some karma
 * </ul>
 *
 * REPETITIONS: <ul><li>once in two weeks</ul>
 *
 * @author Vanessa Julius, idea by anoyyou

 */

public class KillMonks extends AbstractQuest {

	private static final String QUEST_SLOT = "kill_monks";
	protected HashMap<String, Pair<Integer, Integer>> creaturestokill = new HashMap<String, Pair<Integer,Integer>>();

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public int getMinLevel() {
		return 27; // level of monk
	}

	public KillMonks() {
		super();

		 creaturestokill.put("monk",
				 new Pair<Integer, Integer>(0, 25));

		 creaturestokill.put("darkmonk",
				 new Pair<Integer, Integer>(0, 25));

	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Andy");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"My lovely wife was killed when she went to Wo'fol to order some freshmade pizza by Kroip. Some monks stepped into her way and she had no chance. Now I want revenge! May you help me?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"killed"),
						 new TimePassedCondition(QUEST_SLOT, 1, MathHelper.MINUTES_IN_ONE_WEEK*2)),
				ConversationStates.QUEST_OFFERED,
				"Those monks are cruel and I still didn't get my revenge. May you help me again please?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, MathHelper.MINUTES_IN_ONE_WEEK*2)), new QuestStateStartsWithCondition(QUEST_SLOT, "killed")),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, MathHelper.MINUTES_IN_ONE_WEEK*2, "These monks learned their lesson for now but I could need your help again in"));


		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new SetQuestAction(QUEST_SLOT, 0, "start"));
		actions.add(new IncreaseKarmaAction(5));
		actions.add(new StartRecordingKillsAction(QUEST_SLOT, 1, creaturestokill));


		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Thank you! Please kill 25 monks and 25 darkmonks in the name of my beloved wife.",
				new MultipleActions(actions));

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"That is a pity... Maybe you'll change your mind soon and help a sad man then.",
				new MultipleActions(
				        new SetQuestAction(QUEST_SLOT, 0, "rejected"),
				        new DecreaseKarmaAction(5)));
	}

	private void step_2() {
		/* Player has to kill the creatures*/
	}

	private void step_3() {

		final SpeakerNPC npc = npcs.get("Andy");

		ChatAction addRandomNumberOfItemsAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				//add random number of soups
				final StackableItem soup = (StackableItem) SingletonRepository.getEntityManager()
						.getItem("soup");
				int amount;
				// between 1 and 5 soup
				amount = Rand.rand(4) + 1;
				soup.setQuantity(amount);
				player.equipOrPutOnGround(soup);
			}
		};

		final List<ChatAction> actions = new LinkedList<ChatAction>();
	    actions.add(addRandomNumberOfItemsAction);
		actions.add(new IncreaseXPAction(15000));
		actions.add(new SetQuestAction(QUEST_SLOT, 0, "killed"));
		actions.add(new SetQuestToTimeStampAction(QUEST_SLOT, 1));
		actions.add(new IncrementQuestAction(QUEST_SLOT,2,1));

		LinkedList<String> triggers = new LinkedList<String>();
		triggers.addAll(ConversationPhrases.FINISH_MESSAGES);
		triggers.addAll(ConversationPhrases.QUEST_MESSAGES);
		npc.add(ConversationStates.ATTENDING,
				triggers,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new KilledForQuestCondition(QUEST_SLOT, 1)),
				ConversationStates.ATTENDING,
				"Thank you so much! Now I can sleep a bit better. Please take some soup.",
				new MultipleActions(actions));

		npc.add(ConversationStates.ATTENDING,
				triggers,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new NotCondition(new KilledForQuestCondition(QUEST_SLOT, 1))),
				ConversationStates.ATTENDING,
				"Please help me with reaching my goal of taking revenge!",
				null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Kill Monks",
				"Andy's wife was killed by monks, now he wants revenge on them.",
				false);
		step_1();
		step_2();
		step_3();
	}

	@Override
	public List<String> getHistory(final Player player) {
		return getHistory(player, false);
	}

	@Override
	public List<String> getFormattedHistory(final Player player) {
		return getHistory(player, true);
	}

	private List<String> getHistory(final Player player, boolean formatted) {
			final List<String> res = new ArrayList<>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}
			res.add("I met Andy in Ados city. He asked me to get revenge for his wife.");
			final String questStateFull = player.getQuest(QUEST_SLOT);
			final String[] parts = questStateFull.split(";");
			final String questState = parts[0];

			if ("rejected".equals(questState)) {
				res.add("I rejected his request.");
			}
			if ("start".equals(questState)) {
				res.add("I promised to kill 25 monks and 25 darkmonks to get revenge for Andy's wife.");
				if (formatted) {
					res.addAll(howManyWereKilledFormatted(player, parts[1]));
				} else {
					res.add(howManyWereKilled(player, parts[1]));
				}
			}
			if (isCompleted(player)) {
				if(isRepeatable(player)){
					res.add("Now, after more than two weeks, I should check on Andy again. Maybe he needs my help!");
				} else {
					res.add("I've killed some monks and Andy finally can sleep a bit better!");
				}
			}
			int repetitions = player.getNumberOfRepetitions(getSlotName(), 2);
			if (repetitions > 0) {
				res.add("I have taken revenge for Andy "
						+ Grammar.quantityplnoun(repetitions, "time") + " now.");
			}
			return res;
	}

	private String howManyWereKilled(final Player player, final String questState) {
		KillsForQuestCounter killsCounter = new KillsForQuestCounter(questState);
		int killedMonks = 25 - killsCounter.remainingKills(player, "monk");
		int killedDarkMonks = 25 - killsCounter.remainingKills(player, "darkmonk");
		return "I have killed " + Grammar.quantityplnoun(killedMonks, "monk") + " and " + Grammar.quantityplnoun(killedDarkMonks, "darkmonk") + ".";
	}

	private List<String> howManyWereKilledFormatted(final Player player, final String questState) {
		KillsForQuestCounter killsCounter = new KillsForQuestCounter(questState);
		int killedMonks = 25 - killsCounter.remainingKills(player, "monk");
		int killedDarkMonks = 25 - killsCounter.remainingKills(player, "darkmonk");

		List<String> entries = new ArrayList<>();
		entries.add("Monks: <tally>" + killedMonks + "</tally>");
		entries.add("Darkmonks: <tally>" + killedDarkMonks + "</tally>");
		return entries;
	}

	@Override
	public String getName() {
		return "KillMonks";

	}

	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"killed"),
				 new TimePassedCondition(QUEST_SLOT, 1, MathHelper.MINUTES_IN_ONE_WEEK*2)).fire(player,null, null);
	}

	@Override
	public boolean isCompleted(final Player player) {
		return new QuestStateStartsWithCondition(QUEST_SLOT,"killed").fire(player, null, null);
	}

	@Override
	public String getNPCName() {
		return "Andy";
	}

	@Override
	public String getRegion() {
		return Region.ADOS_CITY;
	}
}
