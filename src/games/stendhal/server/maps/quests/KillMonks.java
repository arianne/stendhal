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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import marauroa.common.Pair;

/**
 * QUEST: KillMonks
 *
 * PARTICIPANTS: <ul>
 * <li> Andy on Ados cementary
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
 * <li> 5 soup
 * <li> some karma
 * </ul>
 *
 * REPETITIONS: <ul><li>once in two weeks</ul>
 * 
 * @author Vanessa Julius, idea by anoyyou

 */

public class KillMonks extends AbstractQuest {

	private static final String QUEST_SLOT = "kill_monks";
	private static final int WEEK_IN_MINUTES = MathHelper.MINUTES_IN_ONE_HOUR * 24 * 7;
	protected HashMap<String, Pair<Integer, Integer>> creaturestokill = new HashMap<String, Pair<Integer,Integer>>();
	  protected HashMap<String, List<String>> creatures = new HashMap<String, List<String>>();
	
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
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
				"My lovely wife died when she went to Wofol for ordering some freshmade pizza by Kroip. Some monks stepped into her way and she had no chance. Now I want revenge! May you help me?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, 
				new AndCondition(new QuestStateStartsWithCondition(QUEST_SLOT,"killed"),
						 new TimePassedCondition(QUEST_SLOT, 1, WEEK_IN_MINUTES*2)),
				ConversationStates.QUEST_OFFERED,
				"Those monks are cruel and I still didn't get my revenge. May you help me again please?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, WEEK_IN_MINUTES*2)), new QuestStateStartsWithCondition(QUEST_SLOT, "killed")),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, WEEK_IN_MINUTES*2, "These monks learned their lesson for now but I could need your help again in"));
	

		final List<ChatAction> actions = new LinkedList<ChatAction>();
		actions.add(new SetQuestAction(QUEST_SLOT, "start"));
		actions.add(new StartRecordingKillsAction(QUEST_SLOT, 1, creaturestokill));

		
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Thank you! Also in the name of my beloved wife! Please kill 25 monks and 25 darkmonks.",
				new MultipleActions(actions));

		npc.add(ConversationStates.QUEST_OFFERED, 
				ConversationPhrases.NO_MESSAGES, 
				null,
				ConversationStates.ATTENDING,
				"That is a pity... Maybe you'll change your mind soon and help a sad man then.",
				new SetQuestAction(QUEST_SLOT, "rejected"));
	}

	private void step_2() {
		/* Player has to kill the creatures*/
	}

	private void step_3() {

		final SpeakerNPC npc = npcs.get("Andy");


		final List<ChatAction> actions = new LinkedList<ChatAction>();
	    actions.add(new EquipItemAction("soup", 5));
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
				"Thank you so much! Now I can sleep a bit better.",
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
		super.addToWorld();
		fillQuestInfo(
				"Kill Monks",
				"Andy lost his wife by monks, now he wants revenge on them.",
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
				res.add("I must kill 25 monks and 25 darkmonks to help Andy reaching his goal of taking revenge.");
			} else if(isRepeatable(player)){
				res.add("Now, after more than two weeks, I should take a look after Andy again. Maybe he needs my help");
			} else {
				res.add("I've killed some monks and Andy finally can sleep a bit better!");
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
				 new TimePassedCondition(QUEST_SLOT, 1, WEEK_IN_MINUTES*2)).fire(player,null, null);
	}
	
	@Override
	public boolean isCompleted(final Player player) {
		return new QuestStateStartsWithCondition(QUEST_SLOT,"killed").fire(player, null, null);
	}
}
