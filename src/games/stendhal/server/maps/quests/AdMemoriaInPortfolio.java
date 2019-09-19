/***************************************************************************
 *                   (C) Copyright 2003-2019 - Stendhal                    *
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
import java.util.List;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

//import org.apache.log4j.Logger;

/**
//import java.util.Arrays;
//import java.util.LinkedList;
//import com.google.common.collect.ImmutableList;
//import games.stendhal.server.entity.npc.ChatAction;
//import games.stendhal.server.entity.npc.ConversationPhrases;
//import games.stendhal.server.entity.npc.ConversationStates;
//import games.stendhal.server.entity.npc.action.CreateSlotAction;
//import games.stendhal.server.entity.npc.action.DropItemAction;
//import games.stendhal.server.entity.npc.action.EnableFeatureAction;
//import games.stendhal.server.entity.npc.action.EquipItemAction;
//import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
//import games.stendhal.server.entity.npc.action.IncreaseXPAction;
//import games.stendhal.server.entity.npc.action.InflictStatusOnNPCAction;
//import games.stendhal.server.entity.npc.action.MultipleActions;
//import games.stendhal.server.entity.npc.action.SetQuestAction;
//import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
//import games.stendhal.server.entity.npc.condition.AndCondition;
//import games.stendhal.server.entity.npc.condition.NotCondition;
//import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
//import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
//import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
//import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
 * 
 */
/**
 *  jingo radish,
 *  + lost his memories in a magical duel
 *  + still remembers his sister Hazel
 *  + still remembers about Kirdneh
 *  
 *  hazen
 *  + sister of jingo radish lives in kirdneh
 *  + accomplished magician that can compile a magic memory log
 *  + needs blank scrolls to restore jingo radish memory log
 */

/**
 * QUEST: Ad Memoria In Portfolio
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Jingo Radish, the man with a hoe, in semos plains NE
 * <li> Hazel,  in Kirdneh Museum</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Talk with Jingo Radish to activate the quest.</li>
 * <li> Collect blank scrolls</li>
 * <li> Talk with Hazel in Kirdneh.</li>
 * <li> Return to Jingo Radish with a message from Hazel.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 10 XP</li>
 * <li> 10 Karma</li>
 * <li> ability to use portfolio</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> None.</li>
 * </ul>
 */
public class AdMemoriaInPortfolio extends AbstractQuest {
	private static final int SCROLL_AMOUNT = 5;
	private static final String QUEST_SLOT = "portfolio";
	
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
		res.add("I have asked Jingo Radish if he has a quest for me.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I do not want to help Jingo Radish");
		}
		if (player.isQuestInState(QUEST_SLOT, "start", "hazel", "done")) {
			res.add("I agreed to take scrolls to Hazel and tell her that I have some");
		}
		if (
			questState.equals("start")
			&& player.isEquipped("apple", SCROLL_AMOUNT) || questState.equals("done")) {
			res.add("I got scolls for Hazel.");
		}
		if (
			questState.equals("hazel") || questState.equals("done")) {
			res.add("I took scrolls to Hazel and she asked me to tell his brother Jingo Radish that she is ok, by saying 'Hazel'.");
		}
		if (questState.equals("done")) {
			res.add("I passed the message to Jingo Radish and he has fixed my portfolio for me.");
		}
		return res;
	}

	private void portfolio_step_1() {
		final SpeakerNPC npc = npcs.get("Jingo Radish");

		/** If quest is not started yet, start it. */
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES, new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.ATTENDING, "I need recover #memory",
				null);

		/** quest not started, try start */
		npc.add(
			ConversationStates.ATTENDING,
			"memory",
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"Can you help? say yes/no",
			null);
		
		//YES
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Said YES > step2, karma+2",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "start", 2.0));
		//NO
		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Said NO > hope someone else is more charitable. karma-5",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		npc.add(
			ConversationStates.QUEST_OFFERED,
			"Hazen",
			null,
			ConversationStates.QUEST_OFFERED,
			"Hazen lives in Kirdneh",
			null);

		npc.add(
			ConversationStates.QUEST_OFFERED,
			"Kirdneh",
			null,
			ConversationStates.QUEST_OFFERED,
			"Kirdneh is where Hazen lives",
			null);
	}

	/**
	private void portfolio_step_2() {
		final SpeakerNPC npc = npcs.get("Hazel");
		//Hazen. step2
	}
	*/

	/**
	private void portfolio_step_3() {
		final SpeakerNPC npc = npcs.get("Jingo Radish");
		//Jingo. step3

	}
	*/

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Talk Jingo, Find Hazel (have scrolls), return Jingo",
				"Jingo Radish, Hazel, Kirdneh",
				false);
		
		portfolio_step_1();
		//portfolio_step_2();
		//portfolio_step_3();
		
	}
	
	@Override
	public String getName() {
		return "AdMemoriaInPortfolio";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Jingo Radish";
	}
}
