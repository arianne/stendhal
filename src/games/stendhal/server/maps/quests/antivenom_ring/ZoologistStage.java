/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
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
public class ZoologistStage extends AVRQuestStage {

	private static final int EXTRACT_TIME = 20;

	public ZoologistStage(final String npc, final String questSlot) {
		super(npc, questSlot, questSlot + "_extract");
	}

	@Override
	protected void addDialogue() {
		prepareNPCInfo();
		prepareRequestVenom();
		prepareExtractVenom();
	}

	private void prepareNPCInfo() {
		final SpeakerNPC zoologist = npcs.get(npcName);

		// prepare helpful info
		zoologist.addJob("I am a zoologist and work full-time here at the animal sanctuary.");
		zoologist.addHelp("I specialize in #venomous animals.");
		zoologist.addQuest("There is nothing that I need right now. But maybe you could help me #milk some #snakes ones of these days.");

		// player speaks to Zoey after starting antivenom ring quest
		zoologist.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Oh! You startled me. I didn't see you there. I'm very busy, so if there is something you need please tell me quickly.",
				null);
	}

	private void prepareRequestVenom() {
		final SpeakerNPC zoologist = npcs.get(npcName);

		// player asks for venom
		zoologist.add(ConversationStates.ATTENDING,
				Arrays.asList("jameson", "antivenom", "extract", "cobra", "venom", "snake", "poison"),
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.QUESTION_1,
				"What's that, you need some venom to create an antivemon? I can extract the venom from a "
				+ "cobra's venom gland, but I will need a vial to hold it in. Do you have those items?",
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
		final SpeakerNPC zoologist = npcs.get(npcName);

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
