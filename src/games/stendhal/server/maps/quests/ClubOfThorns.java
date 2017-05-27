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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.IncreaseKarmaAction;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.StartRecordingKillsAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.KilledForQuestCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Club of Thorns
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Orc Saman</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Orc Saman asks you to kill mountain orc chief in prison for revenge</li>
 * <li> Go kill mountain orc chief in prison using key given by Saman to get in</li>
 * <li> Return and you get Club of Thorns as reward<li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> 1000 XP<li>
 * <li> Club of Thorns</li>
 * <li> Karma: 16<li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> None.</li>
 * </ul>
 */
public class ClubOfThorns extends AbstractQuest {
	private static final String QUEST_SLOT = "club_thorns";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void step_1() {
		final SpeakerNPC npc = npcs.get("Orc Saman");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotStartedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"Make revenge! Kill de Mountain Orc Chief! unnerstand? ok?",
			null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestActiveCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Make revenge! #Kill Mountain Orc Chief!",
			null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"Saman has revenged! dis Good!",
			null);


		final List<ChatAction> start = new LinkedList<ChatAction>();
		start.add(new EquipItemAction("kotoch prison key", 1, true));
		start.add(new IncreaseKarmaAction(6.0));
		start.add(new SetQuestAction(QUEST_SLOT, 0, "start"));
		start.add(new StartRecordingKillsAction(QUEST_SLOT, 1, "mountain orc chief", 0, 1));


		npc.add(
			ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			"Take dat key. he in jail. Kill! Denn, say me #kill! Say me #kill!",
			new MultipleActions(start));

		npc.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"Ugg! i want hooman make #task, kill!",
			new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -6.0));
	}

	private void step_2() {
		// Go kill the mountain orc chief using key to get into prison.
	}

	private void step_3() {

		final SpeakerNPC npc = npcs.get("Orc Saman");

		final List<ChatAction> reward = new LinkedList<ChatAction>();
		reward.add(new EquipItemAction("club of thorns", 1, true));
		reward.add(new IncreaseKarmaAction(10.0));
		reward.add(new IncreaseXPAction(1000));
		reward.add(new SetQuestAction(QUEST_SLOT, "done"));

		// the player returns after having started the quest.
		// Saman checks if kill was made
		npc.add(ConversationStates.ATTENDING, "kill",
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"), new KilledForQuestCondition(QUEST_SLOT, 1)),
			ConversationStates.ATTENDING,
			"Revenge! Good! Take club of hooman blud.",
			new MultipleActions(reward));

		npc.add(ConversationStates.ATTENDING, "kill",
			new AndCondition(new QuestInStateCondition(QUEST_SLOT, 0, "start"), new NotCondition(new KilledForQuestCondition(QUEST_SLOT, 1))),
			ConversationStates.ATTENDING,
			"kill Mountain Orc Chief! Kotoch orcs nid revenge!",
			null);
	}

	@Override
	public void addToWorld() {
		step_1();
		step_2();
		step_3();
		fillQuestInfo(
				"Club of Thorns",
				"The Orc Saman will give a dangerous weapon to a mercenary who will help him.",
				false);
	}

	@Override
	public List<String> getHistory(final Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(QUEST_SLOT)) {
			return res;
		}
		res.add("I met the Orc Saman in Kotoch.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("rejected")) {
			res.add("I do not want to kill anyone for the Orc Saman.");
		}
		if (questState.startsWith("start") || questState.equals("done")) {
			res.add("I like a challenge and want to try kill the captive mountain orc chief. I was given the prison key.");
		}
		if (questState.startsWith("start") && new KilledForQuestCondition(QUEST_SLOT, 1).fire(player,null,null) || questState.equals("done")) {
			res.add("I killed the mountain orc chief in Kotoch Prison.");
		}
		if (questState.equals("done")) {
			res.add("I told the Orc Saman about the kill and he gave me a powerful Club of Thorns to use.");
		}
		return res;
	}

	@Override
	public String getName() {
		return "ClubOfThorns";
	}

	@Override
	public int getMinLevel() {
		return 50;
	}

	@Override
	public String getNPCName() {
		return "Orc Saman";
	}

	@Override
	public String getRegion() {
		return Region.KOTOCH;
	}
}
