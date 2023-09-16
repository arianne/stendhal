/***************************************************************************
 *                 (C) Copyright 2022-2023 - Faiumoni e.V.                 *
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;

/**
 * defines how the NPC offers the player the quest when the player says "quest"
 *
 * @author hendrik
 */
public class QuestOfferBuilder<T extends QuestOfferBuilder<T>> {
	private String respondToFailedPreCondition = "I am sorry, I don't have a task for your right now.";
	private String respondToRequest = null;
	private String respondToUnrepeatableRequest = "Thanks for your help. I have no new task for you.";
	private String respondToRepeatedRequest = null;
	private String respondToAccept = "Thank you";
	private String respondToReject = "Ohh. Too bad";
	private String remind = "Please keep your promise";
	private double rejectionKarmaPenalty = 2.0;
	private List<String> lastRespondTo = null;
	private Map<List<String>, String> additionalReplies = new HashMap<>();

	// hide constructor
	QuestOfferBuilder() {
		super();
	}

	@SuppressWarnings("unchecked")
	public T respondToFailedPreCondition(String respondToFailedPreCondition) {
		this.respondToFailedPreCondition = respondToFailedPreCondition;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T respondToRequest(String respondToRequest) {
		this.respondToRequest = respondToRequest;
		if (this.respondToRepeatedRequest == null) {
			this.respondToRepeatedRequest = respondToRequest;
		}
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T respondToUnrepeatableRequest(String respondToUnrepeatableRequest) {
		this.respondToUnrepeatableRequest = respondToUnrepeatableRequest;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T respondToRepeatedRequest(String respondToRepeatedRequest) {
		this.respondToRepeatedRequest = respondToRepeatedRequest;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T respondToAccept(String respondToAccept) {
		this.respondToAccept = respondToAccept;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T respondToReject(String respondToReject) {
		this.respondToReject = respondToReject;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T respondTo(String... respondTo) {
		this.lastRespondTo = Arrays.asList(respondTo);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T saying(String reply) {
		additionalReplies.put(lastRespondTo, reply);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T remind(String remind) {
		this.remind = remind;
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public T rejectionKarmaPenalty(double rejectionKarmaPenalty) {
		this.rejectionKarmaPenalty = rejectionKarmaPenalty;
		return (T) this;
	}

	void simulateFirst(String npc, QuestSimulator simulator) {
		simulator.playerSays("hi");
		simulator.playerSays("quest");
		simulator.npcSays(npc, respondToRequest);
		simulator.playerSays("no");
		simulator.npcSays(npc, respondToReject);
		simulator.playerSays("bye");
		simulator.info("");

		simulator.playerSays("hi");
		simulator.playerSays("quest");
		simulator.npcSays(npc, respondToRequest);
		simulator.playerSays("yes");
		simulator.npcSays(npc, respondToAccept);
		simulator.playerSays("bye");
		simulator.info("");

		simulator.playerSays("hi");
		simulator.playerSays("quest");
		simulator.npcSays(npc, remind);
		simulator.info("");
	}

	void simulateNotRepeatable(String npc, QuestSimulator simulator) {
		simulator.playerSays("hi");
		simulator.playerSays("quest");
		simulator.npcSays(npc, respondToUnrepeatableRequest);
		simulator.playerSays("bye");
		simulator.info("");
	}

	void simulateRepeat(String npc, QuestSimulator simulator) {
		simulator.playerSays("hi");
		simulator.playerSays("quest");
		simulator.npcSays(npc, respondToRepeatedRequest);
		simulator.playerSays("bye");
		simulator.info("");
	}


	void build(SpeakerNPC npc, String questSlot,
			ChatCondition questPreCondition,
			ChatAction startQuestAction, ChatAction rejectQuestAction, ChatAction remindQuestAction,
			ChatCondition questCompletedCondition,
			int repeatableAfterMinutes) {

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new QuestNotStartedCondition(questSlot),
						questPreCondition),
				ConversationStates.QUEST_OFFERED,
				respondToRequest,
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new AndCondition(
						new QuestNotStartedCondition(questSlot),
						new NotCondition(questPreCondition)),
				ConversationStates.QUEST_OFFERED,
				respondToFailedPreCondition,
				null);

		LinkedList<String> triggers = new LinkedList<String>();
		triggers.addAll(ConversationPhrases.FINISH_MESSAGES);
		triggers.addAll(ConversationPhrases.QUEST_MESSAGES);
		npc.add(ConversationStates.ATTENDING,
				triggers,
				new AndCondition(
					new QuestActiveCondition(questSlot),
					new NotCondition(questCompletedCondition)),
				ConversationStates.ATTENDING,
				remind,
				remindQuestAction);

		if (repeatableAfterMinutes > -1) {

			npc.add(ConversationStates.ATTENDING,
					ConversationPhrases.QUEST_MESSAGES,
					new AndCondition(
						new QuestCompletedCondition(questSlot),
						new TimePassedCondition(questSlot, 1, repeatableAfterMinutes),
						questPreCondition),
					ConversationStates.QUEST_OFFERED,
					respondToRepeatedRequest,
					null);
			
			npc.add(ConversationStates.ATTENDING,
					ConversationPhrases.QUEST_MESSAGES,
					new AndCondition(
						new QuestCompletedCondition(questSlot),
						new TimePassedCondition(questSlot, 1, repeatableAfterMinutes),
						new NotCondition(questPreCondition)),
					ConversationStates.QUEST_OFFERED,
					respondToFailedPreCondition,
					null);

			npc.add(ConversationStates.ATTENDING,
					ConversationPhrases.QUEST_MESSAGES,
					new AndCondition(
							new QuestCompletedCondition(questSlot),
							new NotCondition(new TimePassedCondition(questSlot, 1, repeatableAfterMinutes))),
					ConversationStates.ATTENDING,
					respondToUnrepeatableRequest,
					null);

		} else {

			npc.add(ConversationStates.ATTENDING,
					ConversationPhrases.QUEST_MESSAGES,
					new QuestCompletedCondition(questSlot),
					ConversationStates.ATTENDING,
					respondToUnrepeatableRequest,
					null);
		}

		final List<ChatAction> start = new LinkedList<ChatAction>();
		start.add(new SetQuestAction(questSlot, 0, "start"));
		if (startQuestAction != null) {
			start.add(startQuestAction);
		}

		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				respondToAccept,
				new MultipleActions(start));

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				respondToReject,
				new MultipleActions(
						new SetQuestAndModifyKarmaAction(questSlot, 0, "rejected", -1 * rejectionKarmaPenalty),
						rejectQuestAction));

		for (Map.Entry<List<String>, String> entry : additionalReplies.entrySet()) {
			npc.add(
					ConversationStates.QUEST_OFFERED,
					entry.getKey(),
					null,
					ConversationStates.QUEST_OFFERED,
					entry.getValue(),
					null);
		}

	}

}
