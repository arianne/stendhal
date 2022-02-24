/***************************************************************************
 *                   Copyright (C) 2003-2022 - Arianne                     *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests;

import org.apache.log4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;
import games.stendhal.common.parser.Sentence;
import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.ZoneAttributes;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;


/**
 * Creates a sandboxed map for new players to progress through
 * a tutorial scenario. Player will be teleported to island on
 * login automatically if this quest has not been completed.
 *
 * TODO:
 * - create JUnit test
 * - finish NPC interaction step
 *   - teach about buying/selling
 *   - teach about questing
 *   - teach about producing
 * - create item usage tutorial step
 *   - teach about using healing items
 *   - teach about using scrolls
 *   - teach about using tools
 *   - teach about equipping items
 * - create combat tutorial step
 *   - teach about stats
 *   - teach about weapon rate
 *   - teach about targeting
 *   - teach about ranged attacks
 *   - teach about element susceptibilities
 *   - teach about status effects & status resistant items
 * - create tool usage tutorial step
 *   - teach about fishing
 *   - teach about mining
 *   - teach about harvesting
 * - create pets tutorial step
 * - create chat commands tutorial step
 * - create achievement for finishing tutorial
 * - create admin script to reset/restart tutorial
 * - rewared karma for completing tutorial
 * - check if player is new & make sure all equipment & items are removed
 *   - can detect if player is new by checking visited zones
 * - make sure new players are teleported directly to tutorial island &
 *   not to int_semos_guard_house or int_semos_town_hall
 * - give new players weapon & armor after tutorial is complete
 * - make sure any items given to player during tutorial have infostring
 *   and are removed from player & ground when tutorial is finished
 * - handle player dieing in tutorial zone
 */
public class TutorialIsland extends AbstractQuest {

	private static final Logger logger = Logger.getLogger(TutorialIsland.class);

	private final String SLOT = "tutorial_island";

	private final String tutorBasename = "tutor";
	private final String tutorTitle = "Tutor";
	// NPCs created per player
	private Map<String, SpeakerNPC> activeTutors;


	@Override
	public void addToWorld() {
		activeTutors = new HashMap<>();
	}

	@Override
	public List<String> getHistory(final Player player) {
		return new LinkedList<>();
	}

	@Override
	public boolean isVisibleOnQuestStatus() {
		return false;
	}

	@Override
	public String getSlotName() {
		return SLOT;
	}

	@Override
	public String getName() {
		return "TutorialIsland";
	}

	@Override
	public String getNPCName() {
		return tutorTitle;
	}

	public void startTutorialForPlayer(final Player player) {
		if (!player.hasQuest(SLOT)) {
			// TODO: set this at first interaction with NPC
			// first time player has entered tutorial
			player.setQuest(SLOT, "start");
		}

		final String pname = player.getName();

		createNPC(pname);
		prepareSteps(pname);
		createIsland(player);
	}

	private void createNPC(final String pname) {
		final SpeakerNPC tutor = new SpeakerNPC(tutorBasename + "_" + pname);
		tutor.setTitle(tutorTitle);
		tutor.put("cloned", tutorBasename); // hide from website

		// set attributes
		tutor.setEntityClass("floattingladynpc");
		tutor.setIdleDirection(Direction.DOWN);
		tutor.setPosition(23, 8);
		tutor.put("flying", ""); // animate sprite when idle

		tutor.addGoodbye("Just say #hi when you are ready to continue.");

		activeTutors.put(pname, tutor);
	}

	private void createIsland(final Player player) {
		// FIXME: need to dismantle island on logout

		final String pname = player.getName();
		final SpeakerNPC tutor = activeTutors.get(pname);

		final StendhalRPWorld world = SingletonRepository.getRPWorld();

		final IRPZone templateZone = world.getRPZone(SLOT);
		if (templateZone == null) {
			logger.error("could not get zone: " + SLOT);
			return;
		}

		final StendhalRPZone zone = new StendhalRPZone(pname + "_" + SLOT,
			(StendhalRPZone) templateZone);
		zone.setEntryPoint(20, 14);
		zone.disallowIn();
		zone.disallowOut();

		final ZoneAttributes attr = new ZoneAttributes(zone);
		attr.setBaseName(SLOT);
		zone.setAttributes(attr);

		world.addRPZone(zone);

		zone.add(tutor);
		zone.placeObjectAtEntryPoint(player);

		SingletonRepository.getTurnNotifier().notifyInTurns(10, new TurnListener() {
			@Override
			public void onTurnReached(final int currentTurn) {
				// don't interrupt if player already conversing
				if (tutor.getEngine().getCurrentState() == ConversationStates.IDLE) {
					tutor.say("Hey! " + pname + ", come say #hi.");
				}
			}
		});
	}

	public void dismantleIsland(final Player player) {
		final String pname = player.getName();
		if (activeTutors.containsKey(pname)) {
			activeTutors.remove(pname);
		}

		// we get the NPC from the main list in case player already completed
		// tutorial
		final SpeakerNPC tutor = npcs.get(tutorBasename + "_" + pname);
		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		final StendhalRPZone zone = world.getZone(pname + "_" + SLOT);

		final String tname = tutorBasename + "_" + pname;
		final String zname = pname + "_" + SLOT;

		if (zone != null) {
			if (tutor != null) {
				zone.remove(tutor);
				world.remove(tutor.getID());
			}
			world.removeZone(zone);
		}

		if (npcs.get(tname) != null) {
			logger.warn("failed to remove NPC: " + tname);
		}
		if (world.getZone(zname) != null) {
			logger.warn("failed to remove zone: " + zname);
		}
	}

	private void onCompleted(final Player player) {
		player.setQuest(SLOT, "done");
		player.setQuest(SLOT, 1, Long.toString(System.currentTimeMillis()));

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

		// TODO:
		// - create GameEvent
	}

	private void prepareSteps(final String pname) {
		final SpeakerNPC tutor = activeTutors.get(pname);

		// TODO:
		// - add transition to set quest state to start, in case it was not
		//   already set or, just use first transition to set state

		prepareNPCsStep(tutor);
		prepareFinalStep(tutor);
	}

	private void prepareNPCsStep(final SpeakerNPC tutor) {
		tutor.add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES,
			new QuestInStateCondition(SLOT, 0, "start"),
			ConversationStates.ATTENDING,
			"You look like a fast learner. There are many helpful people in the #world."
				+ " Don't be afraid to talk to them."
				+ " If their reply contains any highlighted words, like #this,"
				+ " that is an indication that they will respond to that word."
				+ " When you are done talking, just say #bye."
				+ " Now, why don't you ask me about some #rules.",
			null);

		tutor.add(ConversationStates.ATTENDING,
			"rules",
			null,
			ConversationStates.ATTENDING,
			"The rules of #Faiumoni are simple.... and I'll get to that later.",
			null);

		tutor.add(ConversationStates.ATTENDING,
			Arrays.asList("faiumoni", "world"),
			new QuestInStateCondition(SLOT, 0, "start"),
			ConversationStates.ATTENDING,
			"Faiumoni is the name of our world, the world that you will soon enter,"
				+ " if you are able to pass a few #tests.",
			null);

		tutor.add(ConversationStates.ATTENDING,
			Arrays.asList("faiumoni", "world"),
			new QuestInStateCondition(SLOT, 0, "start"),
			ConversationStates.ATTENDING,
			"Faiumoni is the name of our world, the world that you will soon enter.",
			null);

		tutor.add(ConversationStates.ATTENDING,
			Arrays.asList("test", "tests"),
			new QuestInStateCondition(SLOT, 0, "start"),
			ConversationStates.ATTENDING,
			"Unfortunately, that is as far as this tutorial goes for now.",
			null);
	}

	private void prepareFinalStep(final SpeakerNPC tutor) {
		tutor.add(ConversationStates.ATTENDING,
			ConversationPhrases.FINISH_MESSAGES,
			new QuestInStateCondition(SLOT, 0, "start"), // TODO: this will be different after adding more steps
			ConversationStates.IDLE,
			"Good work! You are now ready to enter the world of Faiumoni.",
			new ChatAction(){
				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					onCompleted(player);
				}
			});
	}
}
