/***************************************************************************
 *                 (C) Copyright 2023-2024 - Faiumoni e.V.                 *
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
import java.util.Map;

import games.stendhal.common.grammar.Grammar;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.Entity;
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
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;

public class DeliverItemQuestOfferBuilder extends QuestOfferBuilder<DeliverItemQuestOfferBuilder> {

	protected String respondIfUnableToWearUniform = "I am sorry, I don't have a task for your right now.";
	protected String respondIfLastQuestFailed;
	protected String respondIfInventoryIsFull;
	protected String respondToAnotherIfLostItem;
	protected String respondToAnotherIfNotLostItem = "You are still carrying the [item].";
	protected String respondToAnotherIfNoMoney = "I need to make a profit. I can't give you a bunch"
			+ " of free [items]. Come back when you have [charge] money.";


	// hide constructor
	DeliverItemQuestOfferBuilder() {
		super();
	}

	public DeliverItemQuestOfferBuilder respondIfUnableToWearUniform(String respondIfUnableToWearUniform) {
		this.respondIfUnableToWearUniform = respondIfUnableToWearUniform;
		return this;
	}


	public DeliverItemQuestOfferBuilder respondIfLastQuestFailed(String respondIfLastQuestFailed) {
		this.respondIfLastQuestFailed = respondIfLastQuestFailed;
		return this;
	}

	public DeliverItemQuestOfferBuilder respondIfInventoryIsFull(String respondIfInventoryIsFull) {
		this.respondIfInventoryIsFull = respondIfInventoryIsFull;
		return this;
	}

	/**
	 * Player lost item and wants another.
	 */
	public DeliverItemQuestOfferBuilder respondToAnotherIfLostItem(String respondToAnotherIfLostItem) {
		this.respondToAnotherIfLostItem = respondToAnotherIfLostItem;
		return this;
	}

	/**
	 * Player didn't lose item and wants another.
	 */
	public DeliverItemQuestOfferBuilder respondToAnotherIfNotLostItem(String respondToAnotherIfNotLostItem) {
		this.respondToAnotherIfNotLostItem = respondToAnotherIfNotLostItem;
		return this;
	}

	/**
	 * Player lost item but doesn't have enough money for replacement.
	 */
	public DeliverItemQuestOfferBuilder respondToAnotherIfNoMoney(String respondToAnotherIfNoMoney) {
		this.respondToAnotherIfNoMoney = respondToAnotherIfNoMoney;
		return this;
	}

	@Override
	public void build(SpeakerNPC npc, String questSlot, QuestTaskBuilder task, ChatCondition questCompletedCondition, int repeatableAfterMinutes) {

		ChatCondition questPreCondition = task.buildQuestPreCondition(questSlot);
		ChatAction startQuestAction = ((DeliverItemTask) task).buildStartQuestAction(questSlot, respondToAccept, respondIfInventoryIsFull);
		ChatAction rejectQuestAction = task.buildRejectQuestAction(questSlot);
		ChatAction remindQuestAction = ((DeliverItemTask) task).buildRemindQuestAction(questSlot, remind, respondIfLastQuestFailed);

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
				respondIfUnableToWearUniform,
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
				null,
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
					respondIfUnableToWearUniform,
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

		ChatAction startAction = new SetQuestAction(questSlot, 0, "start");
		if (startQuestAction != null) {
			startAction = startQuestAction;
		}

		npc.add(
				ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				startAction);

		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES, null,
				ConversationStates.ATTENDING,
				respondToReject,
				new MultipleActions(
						new SetQuestAndModifyKarmaAction(questSlot, 0, "rejected", -1 * rejectionKarmaPenalty),
						rejectQuestAction));

		if (respondToAnotherIfLostItem != null) {
			final String itemName = ((DeliverItemTask) task).getItemName();
			final int chargeForLostItem = ((DeliverItemTask) task).getChargeForLostItem();
			final ChatCondition playerIsChargedCondition = new ChatCondition() {
				@Override
				public boolean fire(Player player, Sentence sentence, Entity npc) {
					return chargeForLostItem > 0;
				}
			};

			// player carrying item so cannot restart
			npc.add(
					ConversationStates.ATTENDING,
					ConversationPhrases.ABORT_MESSAGES,
					new AndCondition(
							new QuestActiveCondition(questSlot),
							new PlayerHasItemWithHimCondition(itemName)
					),
					ConversationStates.ATTENDING,
					respondToAnotherIfNotLostItem.replace("[item]", itemName),
					null);

			// player has lost item and isn't charged to restart
			npc.add(
					ConversationStates.ATTENDING,
					ConversationPhrases.ABORT_MESSAGES,
					new AndCondition(
							new QuestActiveCondition(questSlot),
							new NotCondition(new PlayerHasItemWithHimCondition(itemName)),
							new NotCondition(playerIsChargedCondition)),
					ConversationStates.ATTENDING,
					null,
					startAction);

			// player has lost item and is charged to restart
			npc.add(
					ConversationStates.ATTENDING,
					ConversationPhrases.ABORT_MESSAGES,
					new AndCondition(
							new QuestActiveCondition(questSlot),
							new NotCondition(new PlayerHasItemWithHimCondition(itemName)),
							playerIsChargedCondition
					),
					ConversationStates.RESTART_OFFERED,
					respondToAnotherIfLostItem
							.replace("[item]", itemName)
							.replace("[charge]", String.valueOf(chargeForLostItem)),
					null);

			// player doesn't wan't to pay to restart
			npc.add(
					ConversationStates.RESTART_OFFERED,
					ConversationPhrases.NO_MESSAGES,
					null,
					ConversationStates.ATTENDING,
					respondToReject,
					null);

			// player wants to pay to restart but doesn't have enough money
			npc.add(
					ConversationStates.RESTART_OFFERED,
					ConversationPhrases.YES_MESSAGES,
					new NotCondition(new PlayerHasItemWithHimCondition("money", chargeForLostItem)),
					ConversationStates.ATTENDING,
					respondToAnotherIfNoMoney
							.replace("[items]", Grammar.plural(itemName))
							.replace("[item]", itemName)
							.replace("[charge]", String.valueOf(chargeForLostItem)),
					null);

			// player wants to pay to restart
			npc.add(
					ConversationStates.RESTART_OFFERED,
					ConversationPhrases.YES_MESSAGES,
					new PlayerHasItemWithHimCondition("money", chargeForLostItem),
					ConversationStates.ATTENDING,
					null,
					startAction);
		}

		for (Map.Entry<List<String>, String> entry : additionalReplies.entrySet()) {
			npc.add(
					ConversationStates.QUEST_OFFERED,
					entry.getKey(),
					null,
					ConversationStates.QUEST_OFFERED,
					entry.getValue(),
					null);
		}

		((DeliverItemTask) task).prepareBaker();
	}

	@Override
	void simulateFirst(String npc, QuestSimulator simulator) {
		simulator.info("Player is in a non-human form.");
		simulator.info("");
		simulator.playerSays("hi");
		simulator.playerSays("quest");
		simulator.npcSays(npc, respondIfUnableToWearUniform);
		simulator.playerSays("bye");
		simulator.info("");
		simulator.info("Player becomes a human.");
		simulator.info("");

		super.simulateFirst(npc, simulator);

		simulator.info("Time passes.");
		simulator.info("");

		simulator.playerSays("hi");
		simulator.playerSays("quest");
		simulator.npcSays(npc, respondIfLastQuestFailed);
		simulator.playerSays("yes");
		simulator.npcSays(npc, respondToAccept);
		simulator.playerSays("bye");
		simulator.info("");
	}

}
