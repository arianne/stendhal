package games.stendhal.server.entity.npc.quest;

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

public class DeliverItemQuestOfferBuilder extends QuestOfferBuilder<DeliverItemQuestOfferBuilder> {

	protected String respondIfUnableToWearUniform = "I am sorry, I don't have a task for your right now.";
	protected String respondIfLastQuestFailed;
	protected String respondIfInventoryIsFull;

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
				null,
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
		
		((DeliverItemTask) task).prepareBaker();

	}

}
