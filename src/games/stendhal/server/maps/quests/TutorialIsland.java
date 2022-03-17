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

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.ZoneAttributes;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Engine;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.tutorial.ActiveTutors;
import games.stendhal.server.maps.quests.tutorial.PrepareStepChat;
import games.stendhal.server.maps.quests.tutorial.PrepareStepCombat;
import games.stendhal.server.maps.quests.tutorial.PrepareStepFinal;
import games.stendhal.server.maps.quests.tutorial.PrepareStepItems;
import games.stendhal.server.maps.quests.tutorial.PrepareStepNPCs;
import games.stendhal.server.maps.quests.tutorial.PrepareStepOffer;
import games.stendhal.server.maps.quests.tutorial.PrepareStepPets;
import games.stendhal.server.maps.quests.tutorial.PrepareStepQuests;
import games.stendhal.server.maps.quests.tutorial.PrepareStepRules;
import games.stendhal.server.maps.quests.tutorial.TutorialStep;
import marauroa.common.game.IRPZone;


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
 * - make visible in travel log
 */
public class TutorialIsland extends AbstractQuest {

	private static final Logger logger = Logger.getLogger(TutorialIsland.class);

	private final String SLOT = "tutorial_island";

	private final String tutorBasename = "tutor";
	private final String tutorTitle = "Tutor";
	// NPCs created per player
	private static ActiveTutors activeTutors;

	// the quest states
	/*
	private static final String ST_RULES = "rules";
	private static final String ST_NPCS = "npcs";
	private static final String ST_CHAT = "chat";
	private static final String ST_ITEMS = "items";
	private static final String ST_COMBAT = "combat";
	private static final String ST_QUESTS = "quests";
	private static final String ST_PETS = "pets";
	private static final String ST_FINAL = "final";
	*/


	@Override
	public void addToWorld() {
		activeTutors = ActiveTutors.get();
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
		// TODO: check if player is level 0 & never visited 0_semos_village_w

		final String pname = player.getName();

		createNPC(pname);
		prepareSteps(pname);
		assembleIsland(player);
	}

	private void createNPC(final String pname) {
		final SpeakerNPC tutor = new SpeakerNPC(tutorBasename + "_" + pname);
		tutor.setTitle(tutorTitle);
		tutor.put("cloned", tutorBasename); // hide from website

		// set NPC attributes
		tutor.setEntityClass("floattingladynpc");
		tutor.setIdleDirection(Direction.DOWN);
		tutor.setPosition(23, 8);
		tutor.put("active_idle", ""); // animate sprite when idle
		tutor.setDescription("You see a lovely young woman elegantly floating above the grass.");

		tutor.addGoodbye("Just say #hi when you are ready to continue.");

		activeTutors.put(pname, tutor);
	}

	private void assembleIsland(final Player player) {
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
		// no scroll teleport in or out
		zone.disallowIn();
		zone.disallowOut();

		final ZoneAttributes attr = new ZoneAttributes(zone);
		attr.setBaseName(SLOT);
		zone.setAttributes(attr);

		world.addRPZone(zone);

		zone.add(tutor);
		zone.placeObjectAtEntryPoint(player);

		final TurnNotifier tn = SingletonRepository.getTurnNotifier();

		tn.notifyInTurns(15, new TurnListener() {
			@Override
			public void onTurnReached(final int currentTurn) {
				final Engine en = tutor.getEngine();

				// don't interrupt if player already conversing
				if (en.getCurrentState() == ConversationStates.IDLE) {
					tutor.say("Hey! " + pname + ", come say #hi.");

					tn.notifyInTurns(10, new TurnListener() {
						@Override
						public void onTurnReached(final int currentTurn) {
							if (en.getCurrentState() == ConversationStates.IDLE) {
								tutor.say("I have something to tell you.");
							}
						}
					});
				}
			}
		});
	}

	private void prepareSteps(final String pname) {
		TutorialStep.setAttributes(SLOT, tutorBasename);

		new PrepareStepOffer().init(pname);
		new PrepareStepRules().init(pname);
		new PrepareStepNPCs().init(pname);
		new PrepareStepChat().init(pname);
		new PrepareStepItems().init(pname);
		new PrepareStepCombat().init(pname);
		new PrepareStepQuests().init(pname);
		new PrepareStepPets().init(pname);
		new PrepareStepFinal().init(pname);
	}

	public void dismantleIsland(final Player player) {
		TutorialStep.dismantleIsland(player);
	}
}
