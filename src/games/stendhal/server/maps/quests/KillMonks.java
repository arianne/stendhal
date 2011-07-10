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

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Pair;

/**
 * QUEST: KillMonks
 *
 * PARTICIPANTS: <ul>
 * <li> NPC on Athor island
 * <li> Darkmonks and normal monks
 * </ul>
 *
 * STEPS:<ul>
 * <li> John on Athor island asks player to kill some monks for him, cause he can't enjoy his vacation
 * <li> Kill them for him and go back to the NPC to get your reward
 * </ul>
 * 
 *
 * REWARD:<ul>
 * <li> 15000 XP
 * <li> 10 greater potion
 * <li> some karma
 * </ul>
 *
 * REPETITIONS: <ul><li>once in a week</ul>
 * 
 * @author Vanessa Julius, idea by anoyyou

 */

public class KillMonks extends AbstractQuest {

	private static final String QUEST_SLOT = "kill_monks";
	private static final int WEEK_IN_MINUTES = MathHelper.MINUTES_IN_ONE_HOUR * 24 * 7;
	protected HashMap<String, Pair<Integer, String>> creaturestokill = new HashMap<String, Pair<Integer,String>>();
	protected HashMap<String, List<String>> creatures = new HashMap<String, List<String>>();
	
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	
	public KillMonks() {
		super();

		creaturestokill.put("monk", 
			new Pair<Integer, String>(50,"They mostly live in undergrounds and basements."));
		
		creatures.put("monk",
				Arrays.asList("monk",
							  "darkmonk"));
	}
	
	private void step_1() {
		final SpeakerNPC npc = npcs.get("John");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"My wife Jane and me are on vacation here on Athor island. Unfortunetly we can't explore the whole island cause some ugly monks step in our way each time. Can you help us by killing some of them to turn our vacation into a good one?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"killed"),
						 new TimePassedCondition(QUEST_SLOT, 1, WEEK_IN_MINUTES)),
				ConversationStates.QUEST_OFFERED,
				"Those monks returned after the last time you helped us. May you help us again please?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"killed"),
						 new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, WEEK_IN_MINUTES))),
				ConversationStates.ATTENDING,
				"These monks didn't return so far and we could see some lovely places all over.",
				null);
	

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new SetQuestAction(QUEST_SLOT, "start"));
		actions.add(new StartRecordingKillsAction(QUEST_SLOT, 1, creaturestokill));

		
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Fantastic! We can't wait for your return. Some of these gnomes are in the cool underground dungeon here. I bet you'll get them all!",
				new MultipleActions(actions));

		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.NO_MESSAGES, 
				null,
				ConversationStates.ATTENDING,
				"Oh nevermind. We'll go on sunbathing then. Not that we are tired of it...",
				new SetQuestAction(QUEST_SLOT, "rejected"));
	}

	private void step_2() {
		/* Player has to kill the creatures*/
	}

	private void step_3() {

		final SpeakerNPC npc = npcs.get("John");


		final List<ChatAction> actions = new LinkedList<ChatAction>();
	    actions.add(new EquipItemAction("greater potion", 10));
		actions.add(new IncreaseXPAction(15000));
		actions.add(new SetQuestAction(QUEST_SLOT, "killed;1"));
		actions.add(new SetQuestToTimeStampAction(QUEST_SLOT, 1));
		
		LinkedList<String> triggers = new LinkedList<String>();
		triggers.addAll(ConversationPhrases.FINISH_MESSAGES);
		triggers.addAll(ConversationPhrases.QUEST_MESSAGES);		
		npc.add(ConversationStates.ATTENDING, 
				triggers,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new KilledForQuestCondition(QUEST_SLOT, 1)),
				ConversationStates.ATTENDING, 
				"Brilliant! You killed some of these ugly monks as I see! Hopefully they'll not return that fast or we will still not have the chance 					to explore some places. Please take these greater potions as a reward for your help!" + "Please take these potions as a reward.",
				new MultipleActions(actions));

		npc.add(ConversationStates.ATTENDING, 
				triggers,
				new AndCondition(
						new QuestInStateCondition(QUEST_SLOT, 0, "start"),
						new NotCondition(new KilledForQuestCondition(QUEST_SLOT, 1))),
				ConversationStates.ATTENDING, 
				"Please free these lovely places on Athor from monks!",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();
		fillQuestInfo(
				"Kill Monks",
				"John and his wife Jane want to take revenge on monks cause they destroyed their vacation.",
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
				res.add("I must kill 50 monks for helping John and Jane having a nice vacation on Athor island!");
			} else if(isRepeatable(player)){
				res.add("It's a long time ago that I visited John and asked if he was able to explore some places on Athor. Maybe he needs my help 				again.");
			} else {
				res.add("I've killed some monks and John and Jane can finally enjoy their vacation!");
			}
			return res;
	}


	@Override
	public String getName() {
		return "KillMonks";

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
}
