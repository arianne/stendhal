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

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.OrCondition;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.player.Player;


public class PrepareStepFinal extends TutorialStep {

	private static final Logger logger = Logger.getLogger(PrepareStepFinal.class);

	public void init(final String pname) {
		final ChatCondition onFinalStep = new QuestInStateCondition(SLOT, 0, ST_FINAL);
		final ChatCondition questDone = new QuestInStateCondition(SLOT, 0, "done");

		final SpeakerNPC tutor = ActiveTutors.get().get(pname);

		tutor.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			onFinalStep,
			ConversationStates.ATTENDING,
			"I think we are all #done. There is nothing more I can teach you now.",
			null);

		tutor.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			questDone,
			ConversationStates.QUESTION_1,
			"We are all done here. Just tell me if you want to #leave.",
			null);

		tutor.add(ConversationStates.ATTENDING,
			ConversationPhrases.FINISH_MESSAGES,
			onFinalStep,
			ConversationStates.QUESTION_1,
			"Good work! You are now ready to enter the world of Faiumoni. Just let me"
				+ " know when you want to #leave, and I will send you on your new"
				+ " adventure.",
			new ChatAction() {
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					player.setQuest(SLOT, "done");
					player.setQuest(SLOT, 1, Long.toString(System.currentTimeMillis()));

					// TODO:
					// - create GameEvent
				}
			});

		tutor.add(ConversationStates.QUESTION_1,
			"leave",
			new OrCondition(
				onFinalStep,
				questDone),
			ConversationStates.IDLE,
			"Good luck in your future endeavors!",
			new ChatAction(){
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					// TODO: play a sound & post area message about NPC casting a spell
					//       to teleport player
					onCompleted(player);
				}
			});
	}

	private void onCompleted(final Player player) {
		SingletonRepository.getTurnNotifier().notifyInTurns(10, new TurnListener() {
			@Override
			public void onTurnReached(final int currentTurn) {
				final StendhalRPZone guardhouse = SingletonRepository.getRPWorld()
					.getZone("int_semos_guard_house");
				if (guardhouse == null) {
					logger.error("could not get guardhouse zone to teleport player");
					return;
				}

				guardhouse.placeObjectAtEntryPoint(player);

				if (player.getZone().getName().equals(player.getName() + "_" + SLOT)) {
					logger.error("failed to teleport player out of tutorial zone");
					return;
				}

				dismantleIsland(player);
			}
		});
	}
}
