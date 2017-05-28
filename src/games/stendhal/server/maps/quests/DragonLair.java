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
import java.util.List;

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Dragon Lair Access
 * <p>
 * PARTICIPANTS:
 * <ul>
 * <li> Wishman, storm trooper extraordinaire from Blordrough's dark legion, guards the remaining dragons
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Wishman
 * </ul>
 * <p>
 * REWARD:
 * <ul>
 * <li> admittance to dragon lair
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> after 1 week.
 * </ul>
 */

public class DragonLair extends AbstractQuest {

	private static final String QUEST_SLOT = "dragon_lair";
	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void step_1() {

		final SpeakerNPC npc = npcs.get("Wishman");

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"Would you like to visit our dragon lair?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new NotCondition(new TimePassedCondition(QUEST_SLOT, 1, MathHelper.MINUTES_IN_ONE_WEEK))),
				ConversationStates.ATTENDING,
				null,
				new SayTimeRemainingAction(QUEST_SLOT, 1, MathHelper.MINUTES_IN_ONE_WEEK, "I think they've had enough excitement for a while. Come back in"));

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(new QuestCompletedCondition(QUEST_SLOT), new TimePassedCondition(QUEST_SLOT, 1, MathHelper.MINUTES_IN_ONE_WEEK)),
				ConversationStates.QUEST_OFFERED,
				"Be warned, the dragons have started breathing fire! Anyway, would you like to visit our dragons again?",
				null);

		// Player asks for quest while quest is already active
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"I have already opened the door to the dragon lair.",
				null);

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Great! Enjoy your visit. I know THEY will. Oh, watch out, we have a couple chaos dragonriders exercising our dragons. Don't get in their way!",
				new SetQuestAction(QUEST_SLOT, "start")); // Portal closes quest

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Ok, but our dragons will be sorry you didn't stop in for a visit.",
				new SetQuestAndModifyKarmaAction(QUEST_SLOT, "rejected", -5.0));

		// Leave the dragon lair to complete quest
	}

	@Override
	public void addToWorld() {
		step_1();
		fillQuestInfo(
				"Dragon Lair",
				"Wishman, storm trooper extraordinaire from Blordrough's dark legion, guards the remaining dragons... and lets visitors into their lair.",
				true);

	}

	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			if (!player.hasQuest(QUEST_SLOT)) {
				return res;
			}

			final String questState = player.getQuest(QUEST_SLOT);
			res.add("Wishman offered that I may play with his dragons!");
			if ("rejected".equals(questState)) {
				res.add("They look a bit scary to me.");
				return res;
            }

			if (player.isQuestInState(QUEST_SLOT, 0, "start")) {
                res.add("Wishman has unlocked the dragon lair.");
                return res;
			}

			if (isRepeatable(player)) {
				res.add("Those dragons might need some fun again, I should visit soon.");
			} else if (player.isQuestInState(QUEST_SLOT, 0, "done")) {
				res.add("The dragons have had plenty of excitement recently, Wishman won't let me back in yet.");
			}

			return res;
	}
	@Override
	public String getName() {
		return "DragonLair";
	}

	// getting past the assassins to this location needs a higher level; the lair itself is dangerous too
	@Override
	public int getMinLevel() {
		return 100;
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return	new AndCondition(new QuestCompletedCondition(QUEST_SLOT),
						 new TimePassedCondition(QUEST_SLOT,1,MathHelper.MINUTES_IN_ONE_WEEK)).fire(player, null, null);
	}
	@Override
	public String getNPCName() {
		return "Wishman";
	}
}
