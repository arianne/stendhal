/***************************************************************************
 *                   (C) Copyright 2019 - Arianne                          *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.antivenom_ring;

import java.util.Arrays;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.EquipItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.action.SayTimeRemainingAction;
import games.stendhal.server.entity.npc.action.SetQuestAndModifyKarmaAction;
import games.stendhal.server.entity.npc.action.SetQuestToTimeStampAction;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;

/**
 * Step where player asks NPC to extract some cobra venom to take to apothecary.
 *
 * Required items:
 * - vial
 * - venom gland
 *
 * Reward:
 * - cobra venom
 * - +5 karma
 */
public class ZoologistStage extends AVRStage {
	private final SpeakerNPC zoologist;
	private final String subquestName;

	private static final int EXTRACT_TIME = 20;

	public ZoologistStage(final String npcName, final String questName) {
		super(questName);

		zoologist = SingletonRepository.getNPCList().get(npcName);
		subquestName = questName + "_extract";
	}

	@Override
	public void addToWorld() {
		prepareNPCInfo();
		prepareResponses();
		prepareRequestVenom();
		prepareExtractVenom();
	}

	private void prepareNPCInfo() {
		// prepare helpful info
		final String jobInfo = "I am a zoologist and work full-time here at the animal sanctuary. I specialize in #venomous animals.";
		zoologist.addJob(jobInfo);
		zoologist.addHelp(jobInfo);
		zoologist.addOffer(jobInfo);
		zoologist.addReply("venomous", "I can use my equipment to #extract the poisons from venomous animals.");
		zoologist.addQuest("There is nothing that I need right now. But maybe you could help me #milk some #snakes ones of these days.");

		// player speaks to Zoey after starting antivenom ring quest
		zoologist.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Oh! You startled me. I didn't see you there. I'm very busy, so if there is something you need please tell me quickly.",
				null);
	}

	private void prepareResponses() {
		final String replyVial = "Hmmm... I don't have any here. But maybe you could find one in a laboratory somewhere.";
		final String replyGland = "I would need the gland of a snake large enough to extract a decent amount.";

		zoologist.add(ConversationStates.ATTENDING,
				"vial",
				null,
				ConversationStates.ATTENDING,
				replyVial,
				null);

		zoologist.add(ConversationStates.QUESTION_1,
				"vial",
				null,
				ConversationStates.QUESTION_1,
				replyVial + " So, do you have the items?",
				null);

		zoologist.add(ConversationStates.ATTENDING,
				Arrays.asList("gland", "venom gland"),
				null,
				ConversationStates.ATTENDING,
				replyGland,
				null);

		zoologist.add(ConversationStates.QUESTION_1,
				Arrays.asList("gland", "venom gland"),
				null,
				ConversationStates.QUESTION_1,
				replyGland + " So, do you have the items?",
				null);
	}

	private void prepareRequestVenom() {
		// player asks for venom
		zoologist.add(ConversationStates.ATTENDING,
				Arrays.asList(
						"jameson", "apothecary", "antivenom", "extract", "cobra", "venom", "snake",
						"snakes", "poison", "milk"),
				new QuestActiveCondition(questName),
				ConversationStates.QUESTION_1,
				"What's that, you need some venom to create an antivemon? I can extract the venom from a "
				+ "cobra's #'venom gland', but I will need a #vial to hold it in. Do you have those items?",
				null);

		zoologist.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Oh? Okay then. Come back when you do",
				null);

		// player requests venom but doesn't have required items
		zoologist.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(new AndCondition(
						new PlayerHasItemWithHimCondition("vial"),
						new PlayerHasItemWithHimCondition("venom gland"))),
				ConversationStates.IDLE,
				"Oh? Then where are they?",
				null);

		// player requests venom and has required items
		zoologist.add(
				ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						new PlayerHasItemWithHimCondition("vial"),
						new PlayerHasItemWithHimCondition("venom gland")
				),
				ConversationStates.IDLE,
				"Okay, I will have your venom ready in about " + Integer.toString(EXTRACT_TIME) + " minutes.",
				new MultipleActions(
						new DropItemAction("vial"),
						new DropItemAction("venom gland"),
						new SetQuestToTimeStampAction(subquestName)
				)
		);

		// TODO: extract again in case player loses venom
	}

	private void prepareExtractVenom() {
		// player returns too soon
		zoologist.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new QuestActiveCondition(subquestName),
						new NotCondition(new TimePassedCondition(subquestName, EXTRACT_TIME))
				),
				ConversationStates.IDLE,
				null,
				new SayTimeRemainingAction(subquestName, EXTRACT_TIME, "The venom is not ready yet. Please come back in ")
		);

		// player returns after enough time has passed
		zoologist.add(
				ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				new AndCondition(
						new QuestActiveCondition(subquestName),
						new TimePassedCondition(subquestName, EXTRACT_TIME)
				),
				ConversationStates.IDLE,
				null,
				new MultipleActions(
						new SayTextAction("Your cobra venom is ready."),
						new EquipItemAction("cobra venom", 1, true),
						new SetQuestAndModifyKarmaAction(subquestName, "done", 5)
				)
		);
	}
}
