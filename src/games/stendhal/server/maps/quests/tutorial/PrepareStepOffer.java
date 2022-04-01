/***************************************************************************
 *                  Copyright (C) 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.tutorial;

import java.util.Arrays;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;


public class PrepareStepOffer extends TutorialStep {

	public void init(final String pname) {
		final ChatCondition questStarted = new QuestStartedCondition(SLOT);
		final ChatCondition questNotStarted = new QuestNotStartedCondition(SLOT);

		final SpeakerNPC tutor = ActiveTutors.get().get(pname);

		tutor.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			questNotStarted,
			ConversationStates.QUEST_OFFERED,
			"You look like a fast learner. Want to learn about this world?"
				+ " If so, just say #yes.",
			null);

		tutor.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.NO_MESSAGES,
			questNotStarted,
			ConversationStates.IDLE,
			"Oh really? Well, good luck with that. If you change your mind, just say #hi.",
			null);

		tutor.add(ConversationStates.QUEST_OFFERED,
			ConversationPhrases.YES_MESSAGES,
			questNotStarted,
			ConversationStates.ATTENDING,
			"I thought so! Okay, the first thing you will need to learn is how to"
				+ " communicate. So far you are doing great! There are many helpful"
				+ " people in this #world who are always responsive to a friendly"
				+ " #hello. You will need to talk with them if you want to get"
				+ " anywhere.",
			new MultipleActions(
				new SetQuestAction(SLOT, ST_RULES),
				delayMessage(10, "If their response contains a highlighted word, like"
					+ " #this, that is an indication that they have some information"
					+ " on the subject. For example, ask me about #rules.")));

		tutor.add(ConversationStates.ATTENDING,
			ConversationPhrases.GREETING_MESSAGES,
			questStarted,
			ConversationStates.ATTENDING,
			"I'm already engaged in conversation with you. No need to greet me again.",
			null);

		tutor.add(ConversationStates.ATTENDING,
			Arrays.asList("faiumoni", "world"),
			questStarted,
			ConversationStates.ATTENDING,
			"Faiumoni is the name of our world, the world that you will soon enter.",
			null);
	}
}
