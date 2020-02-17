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
import java.util.List;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: Learn about Orbs
 *
 * PARTICIPANTS:
 * <ul>
 * <li>Ilisa, the summon healer in Semos temple</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li>Ilisa offers to teach you about orbs</li>
 * <li>You use the orb</li>
 * <li>You tell her if you were successful.</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li>50 XP</li>
 * <li>Ability to use orb in semos temple which teleports you outside into city</li>
 * <li>Ability to use other orbs e.g. in orril lich palace</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li>Can always learn about orbs but not get the xp each time</li>
 * </ul>
 */
public class LearnAboutOrbs extends AbstractQuest {

	private static final String QUEST_SLOT = "learn_scrying";



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
		res.add("I have met Ilisa in Semos Temple.");
		final String questState = player.getQuest(QUEST_SLOT);
		if (questState.equals("done")) {
			res.add("Ilisa taught me how to use orbs. I must be careful as they could send me somewhere dangerous.");
		}
		return res;
	}

	private void step1() {
		final SpeakerNPC npc = npcs.get("Ilisa");

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.QUEST_OFFERED,
			"Some orbs have special properties. I can teach you how to #use an orb, like the one on this table.", null);

		npc.add(ConversationStates.ATTENDING,
			ConversationPhrases.QUEST_MESSAGES,
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"I can remind you how to #use orbs.", null);

		// player interested in orb
		npc.add(ConversationStates.QUEST_OFFERED,
			"use",
			new LevelGreaterThanCondition(10),
			ConversationStates.QUESTION_1,
			"Just right click on the orb and select Use. Did you get any message?",
			null);

		// player interested in orb but level < 10
		npc.add(ConversationStates.QUEST_OFFERED,
			"use",
			new NotCondition(new LevelGreaterThanCondition(10)),
			ConversationStates.ATTENDING,
			"Oh oh, I just noticed you are still new here. Perhaps you better come back when you have more experience. Until then if you need any #help just ask!",
			null);

		// player wants reminder on Use
		npc.add(
			ConversationStates.ATTENDING,
			"use",
			null,
			ConversationStates.ATTENDING,
			"Just right click on part of the orb, and select Use.",
			null);

		// player got message from orb
		npc.add(ConversationStates.QUESTION_1,
			ConversationPhrases.YES_MESSAGES, null,
			ConversationStates.ATTENDING,
			"You're a natural! Now that you have learned to use that orb, it will teleport you to a place of magical significance. So don't use it unless you will be able to find your way back!",
			new MultipleActions(new IncreaseXPAction(50), new SetQuestAction(QUEST_SLOT, "done")));

		// player didn't get message, try again
		npc.add(ConversationStates.QUESTION_1, ConversationPhrases.NO_MESSAGES,
			null, ConversationStates.QUESTION_1,
			"Well, you would need to stand next to it. Move closer, do you get a message now?", null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"Learn About Orbs",
				"Ilisa will teach about using orbs.",
				false);
		step1();

	}

	@Override
	public String getName() {
		return "LearnAboutOrbs";
	}

	@Override
	public int getMinLevel() {
		return 11;
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_CITY;
	}

	@Override
	public String getNPCName() {
		return "Ilisa";
	}
}
