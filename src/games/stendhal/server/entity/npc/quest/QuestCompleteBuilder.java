/***************************************************************************
 *                   (C) Copyright 2022 - Faiumoni e.V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.quest;

import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;

/**
 * defines how the NPC react after the player completes the quest
 *
 * @author hendrik
 */
public class QuestCompleteBuilder {
	private String greet = "Thank you";
	private String respondToReject = null;
	private String respondToAccept = null;
	private List<ChatAction> rewardWith = new LinkedList<>();

	public QuestCompleteBuilder greet(String greet) {
		this.greet = greet;
		return this;
	}

	public QuestCompleteBuilder respondToReject(String respondToReject) {
		this.respondToReject = respondToReject;
		return this;
	}

	public QuestCompleteBuilder respondToAccept(String respondToAccept) {
		this.respondToAccept = respondToAccept;
		return this;
	}

	public QuestCompleteBuilder rewardWith(ChatAction action) {
		this.rewardWith.add(action);
		return this;
	}

	void simulate(String npc, QuestSimulator simulator) {
		simulator.playerSays("hi");
		simulator.npcSays(npc, greet);

		if (respondToReject != null || respondToAccept != null) {
			simulator.playerSays("no");
			simulator.npcSays(npc, respondToReject);
			simulator.playerSays("bye");
			simulator.info("");

			simulator.playerSays("hi");
			simulator.npcSays(npc, greet);
			simulator.playerSays("yes");
			simulator.npcSays(npc, respondToAccept);
			
		}
		simulator.info("Player was rewarded with " + this.rewardWith.toString());
		simulator.info("");
	}

	void build(SpeakerNPC npc, String questSlot, ChatCondition questCompletedCondition, ChatAction questCompleteAction) {
		if (respondToAccept != null) {
			buildWithConfirmation(npc, questSlot, questCompletedCondition, questCompleteAction);
		} else {
			buildWithoutConfirmation(npc, questSlot, questCompletedCondition, questCompleteAction);
		}
	}

	void buildWithConfirmation(SpeakerNPC npc, String questSlot, ChatCondition questCompletedCondition, ChatAction questCompleteAction) {

		// player returns while quest is still active
		npc.add(
			ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new AndCondition(
				new GreetingMatchesNameCondition(npc.getName()),
				new QuestInStateCondition(questSlot, "start"),
				questCompletedCondition),
			ConversationStates.QUEST_ITEM_BROUGHT,
			greet,
			null);

		List<ChatAction> actions = new LinkedList<ChatAction>(rewardWith);
		actions.add(questCompleteAction);

		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.YES_MESSAGES,
			// make sure the player isn't cheating by putting the armor
			// away and then saying "yes"
			questCompletedCondition,
			ConversationStates.ATTENDING,
			"Oh, I am so thankful! Here is some gold I found ... ehm ... somewhere. Now that you have proven yourself a trusted customer, you may have access to your own private banking #vault any time you like.",
			new MultipleActions(actions));

		npc.add(
			ConversationStates.QUEST_ITEM_BROUGHT,
			ConversationPhrases.NO_MESSAGES,
			null,
			ConversationStates.ATTENDING,
			respondToReject,
			null);

	}

	void buildWithoutConfirmation(SpeakerNPC npc, String questSlot, ChatCondition questCompletedCondition, ChatAction questCompleteAction) {
		List<ChatAction> actions = new LinkedList<ChatAction>(rewardWith);
		actions.add(questCompleteAction);

		npc.add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(questSlot, 0, "start"),
						questCompletedCondition),
				ConversationStates.ATTENDING, 
				greet,
				new MultipleActions(actions));
	}

}
