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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
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
import marauroa.common.Pair;

/**
 * QUEST: Clean Athors underground
 *
 * PARTICIPANTS: <ul>
 * <li> NPC on Athor island
 * <li> one of each creature in Athor underground
 * </ul>
 *
 * STEPS:<ul>
 * <li> John on Athor island asks players to kill some creatures of the dungeon for him, cause he can't explore it otherwise
 * <li> Kill them for him and go back to the NPC to get your reward
 * </ul>
 *
 *
 * REWARD:<ul>
 * <li> 5000 XP
 * <li> 10 greater potion
 * <li> Karma: 11 total (10 + 1)
 * </ul>
 *
 * REPETITIONS: <ul><li>once in a week</ul>
 *
 * @author Vanessa Julius, idea by anoyyou

 */

public class CleanAthorsUnderground extends AbstractQuest {

	private static final String QUEST_SLOT = "clean_athors_underground";
	private static final int WEEK_IN_MINUTES = MathHelper.MINUTES_IN_ONE_HOUR * 24 * 7;


	@Override
	public String getSlotName() {
		return QUEST_SLOT;

	}

	@Override
	public int getMinLevel() {
		return 70; // level of blue dragon
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("John");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"My wife Jane and me are on vacation here on Athor island. #Unfortunately we can't explore the whole island because some ugly #creatures step in our way each time. Can you help us by killing some of them to turn our vacation into a good one?",
				null);

		npc.add(
				ConversationStates.QUEST_OFFERED,
				Arrays.asList("Unfortunately"),
				null,
				ConversationStates.QUEST_OFFERED,
				"Yes, unfortunately. We wanted to have a great time here but all we did so far was sunbathe at the beach.",
				null);

		npc.add(
				ConversationStates.QUEST_OFFERED,
				Arrays.asList("creatures"),
				null,
				ConversationStates.QUEST_OFFERED,
				"We just want to visit the first part of the dungeon, it seems to be very interesting. Some of these ugly things jump around there, even some mummies!",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, WEEK_IN_MINUTES)), new QuestStateStartsWithCondition(QUEST_SLOT, "killed")),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, WEEK_IN_MINUTES, "These #creatures didn't return so far and we could see some lovely places all over. Please return in"));


		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"killed"),
						 new TimePassedCondition(QUEST_SLOT, 1, WEEK_IN_MINUTES)),
				ConversationStates.QUEST_OFFERED,
				"Those #creatures returned after the last time you helped us. Will you help us again please?",
				null);



		final Map<String, Pair<Integer, Integer>> toKill = new TreeMap<String, Pair<Integer, Integer>>();
		toKill.put("mummy", new Pair<Integer, Integer>(0,1));
		toKill.put("royal mummy", new Pair<Integer, Integer>(0,1));
		toKill.put("monk",new Pair<Integer, Integer>(0,1));
		toKill.put("darkmonk",new Pair<Integer, Integer>(0,1));
		toKill.put("bat",new Pair<Integer, Integer>(0,1));
		toKill.put("brown slime",new Pair<Integer, Integer>(0,1));
		toKill.put("green slime",new Pair<Integer, Integer>(0,1));
		toKill.put("black slime",new Pair<Integer, Integer>(0,1));
		toKill.put("minotaur",new Pair<Integer, Integer>(0,1));
		toKill.put("blue dragon",new Pair<Integer, Integer>(0,1));
		toKill.put("stone golem",new Pair<Integer, Integer>(0,1));

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 1.0));
		actions.add(new StartRecordingKillsAction(QUEST_SLOT, 1, toKill));


		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Fantastic! We can't wait for your return. Please kill one of each creature you can find in the underground of Athor island. I bet you'll get them all!",
				new MultipleActions(actions));

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Oh never mind. We'll go on sunbathing then. Not that we aren't tired of it...",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -2.0));
	}

	private void step_2() {
		/* Player has to kill the creatures*/
	}

	private void step_3() {

		final SpeakerNPC npc = npcs.get("John");


		final List<ChatAction> actions = new LinkedList<ChatAction>();
	    actions.add(new EquipItemAction("greater potion", 10));
		actions.add(new IncreaseXPAction(5000));
		actions.add(new SetQuestAction(QUEST_SLOT, "killed;1"));
		actions.add(new SetQuestToTimeStampAction(QUEST_SLOT, 1));
		actions.add(new IncreaseKarmaAction(10.0));


		LinkedList<String> triggers = new LinkedList<String>();
		triggers.addAll(ConversationPhrases.FINISH_MESSAGES);
		triggers.addAll(ConversationPhrases.QUEST_MESSAGES);
		npc.add(ConversationStates.ATTENDING,
				triggers,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new KilledForQuestCondition(QUEST_SLOT, 1)),
				ConversationStates.ATTENDING,
				"Brilliant! You killed some of these ugly creatures as I see! Hopefully they'll not return that fast or we will still not have the chance to explore some places."  + " Please take these greater potions as a reward for your help.",
				new MultipleActions(actions));

		npc.add(ConversationStates.ATTENDING,
				triggers,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new NotCondition(new KilledForQuestCondition(QUEST_SLOT, 1))),
				ConversationStates.ATTENDING,
				"Please free these lovely places on Athor from ugly creatures!",
				null);

	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Clean Athor's Underground",
				"John and his wife Jane want to explore Athor underground on their vacation, but unfortunately they can't.",
				false);
		step_1();
		step_2();
		step_3();
	}

	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}
			if (!isCompleted(player)) {
				res.add("I must kill one of each creature of the Athor underground to help John and Jane have a nice vacation!");
			} else if(isRepeatable(player)){
				res.add("It's a long time ago that I visited John and Jane on Athor island. Maybe they need my help again now.");
			} else {
				res.add("I've killed some creatures and John and Jane can finally enjoy their vacation! They will not need my help again during the next days.");
			}
			return res;
	}


	@Override
	public String getName() {
		return "CleanAthorsUnderground";

	}

	@Override
	public boolean isRepeatable(final Player player) {
		return new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"killed"),
				 new TimePassedCondition(QUEST_SLOT, 1, WEEK_IN_MINUTES)).fire(player,null, null);
	}

	@Override
	public boolean isCompleted(final Player player) {
		return new QuestStateStartsWithCondition(QUEST_SLOT,"killed").fire(player, null, null);
	}

	@Override
	public String getNPCName() {
		return "John";
	}

	@Override
	public String getRegion() {
		return Region.ATHOR_ISLAND;
	}
}
