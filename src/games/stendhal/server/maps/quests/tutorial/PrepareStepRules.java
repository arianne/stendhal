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
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestStartedCondition;


public class PrepareStepRules extends TutorialStep {

	public void init(final String pname) {
		final ChatCondition onRulesStep = new QuestInStateCondition(SLOT, 0, ST_RULES);

		final SpeakerNPC tutor = ActiveTutors.get().get(pname);

		tutor.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			onRulesStep,
			ConversationStates.ATTENDING,
			"If you are ready for the next step, just tell me #next. Or I can go over"
				+ " the #rules again.",
			null);

		tutor.add(ConversationStates.ATTENDING,
			Arrays.asList("rule", "rules"),
			new QuestStartedCondition(SLOT),
			ConversationStates.ATTENDING,
			"The rules of #Faiumoni are simple. First, always be polite in conversation."
				+ " Don't just walk away from someone without saying #bye. Secondly,"
				+ " don't cheat. We don't like it when we have to remove others from"
				+ " the community. It makes for a much smaller world with less people"
				+ " to talk to. But don't doubt for a second that we would rather keep"
				+ " it clean and fair.",
			new MultipleActions(
				delayMessage(10, "Never share your account password with another player,"
					+ " not even with a #'game master'. Your account is sure to get banned."),
				delayMessage(20, "For more information on our rules, you can read a more"
					+ " complete guide here: #https://stendhalgame.org/player-guide/rules.html"),
				delayMessage(30, "If you you are clear on the rules, tell me #next if you"
					+ " feel you are ready to move on.")));

		// TODO: change this to move on to NPCs step
		tutor.add(ConversationStates.ATTENDING,
			"next",
			onRulesStep,
			ConversationStates.ATTENDING,
			"Unfortunately, that is as far as this tutorial goes for now. So until I can come"
				+ " up with more stuff to teach, our session here will have to be #done.",
			new SetQuestAction(SLOT, 0, ST_FINAL));
	}
}
