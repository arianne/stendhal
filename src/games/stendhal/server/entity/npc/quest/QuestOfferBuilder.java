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
import games.stendhal.server.entity.npc.condition.GreetingMatchesNameCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;

/**
 * defines how the NPC offers the player the quest when the player says "quest"
 *
 * @author hendrik
 */
public class QuestOfferBuilder {
	private String respondToRequest = null;
	private String respondToRepeatedRequest = "Thanks for your help. I have no new task for you.";
	private String respondToAccept = "Thank you";
	private String respondToReject = "Ohh. Too bad";
	private String remind = "Please keep your promise";
	private List<String> lastRespondTo = null;
	private Map<List<String>, String> additionalReplies = new HashMap<>();

	
	public QuestOfferBuilder respondToRequest(String respondToRequest) {
		this.respondToRequest = respondToRequest;
		return this;
	}

	public QuestOfferBuilder respondToRepeatedRequest(String respondToRepeatedRequest) {
		this.respondToRepeatedRequest = respondToRepeatedRequest;
		return this;
	}

	public QuestOfferBuilder respondToAccept(String respondToAccept) {
		this.respondToAccept = respondToAccept;
		return this;
	}

	public QuestOfferBuilder respondToReject(String respondToReject) {
		this.respondToReject = respondToReject;
		return this;
	}

	public QuestOfferBuilder respondTo(String... respondTo) {
		this.lastRespondTo = Arrays.asList(respondTo);
		return this;
	}

	public QuestOfferBuilder saying(String reply) {
		additionalReplies.put(lastRespondTo, reply);
		return this;
	}

	public QuestOfferBuilder remind(String remind) {
		this.remind = remind;
		return this;
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
		simulator.npcSays(npc, remind);
		simulator.info("");
	}

	void simulateRepeat(String npc, QuestSimulator simulator) {
		simulator.playerSays("hi");
		simulator.playerSays("quest");
		simulator.npcSays(npc, respondToRepeatedRequest);
	}

	void build(SpeakerNPC npc, String questSlot, ChatAction startQuestAction, ChatCondition questCompletedCondition) {
		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestNotStartedCondition(questSlot),
				ConversationStates.QUEST_OFFERED,
				respondToRequest,
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.QUEST_MESSAGES,
				new QuestActiveCondition(questSlot),
				ConversationStates.ATTENDING,
				respondToRepeatedRequest,
				null);

		final List<ChatAction> start = new LinkedList<ChatAction>();
		start.add(new SetQuestAction(questSlot, 0, "start"));
		start.add(startQuestAction);

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
				new SetQuestAndModifyKarmaAction(questSlot, "rejected", -2.0));

		for (Map.Entry<List<String>, String> entry : additionalReplies.entrySet()) {
			npc.add(
					ConversationStates.QUEST_OFFERED,
					entry.getKey(),
					null,
					ConversationStates.QUEST_OFFERED,
					entry.getValue(),
					null);
		}

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new GreetingMatchesNameCondition(npc.getName()),
						new QuestInStateCondition(questSlot, 0, "start"),
						new NotCondition(questCompletedCondition)),
				ConversationStates.ATTENDING,
				remind,
				null);
	}

}
