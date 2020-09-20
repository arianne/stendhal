/***************************************************************************
 *                      (C) Copyright 2020 - Stendhal                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.behaviour.adder;

import java.util.Arrays;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.CollectingGroupQuestBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;

public class CollectingGroupQuestAdder {

	public void add(SpeakerNPC npc, CollectingGroupQuestBehaviour behaviour) {
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new QuestCompletedCondition(behaviour.getQuestSlot()),
				ConversationStates.ATTENDING,
				"Thanks again for your help. We are making #progress. Hopefully we will finish in time.",
				null);
		npc.add(ConversationStates.IDLE, 
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new QuestNotCompletedCondition(behaviour.getQuestSlot()),
						new LevelLessThanCondition(5)
				),
				ConversationStates.ATTENDING,
				"I am sorry, I am very busy at the moment, trying to finish this #project in time. I would ask you for help, but you seem to be very #inexperienced.",
				null);

		npc.addReply(
				Arrays.asList("inexperienced", "inexperience", "experience"), 
				"I am very sorry, I don't have time to teach you right now. You should explore the world and gain some experience.");

		npc.addReply(Arrays.asList("status", "progress"),
				null,
				new ChatAction() {
					
					@Override
					public void fire(Player player, Sentence sentence, EventRaiser npc) {
						int percent = behaviour.getProgressPercent();
						if (percent < 10) {
							npc.say("There is still so much to do before the " + behaviour.getProjectName()  + " can start. We have hardly started.");
						} else if (percent < 50) {
							npc.say("There is still so much to do before the " + behaviour.getProjectName() + " can start. We have not even reached the half way point");
						} else if (percent < 75) {
							npc.say("There is still so much to do before the " + behaviour.getProjectName() + " can start. We have barly reached the half way point.");
						} else if (percent < 90) {
							npc.say("We are almost there. But still, there more work to be done before the " + behaviour.getProjectName() + " can start.");
						}
					}
				});

		
	}
}
