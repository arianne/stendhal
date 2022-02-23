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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;


/**
 * Creates a sandboxed map for new players to progress through
 * a tutorial scenario. Player will be teleported to island on
 * login automatically if this quest has not been completed.
 */
public class TutorialIsland extends AbstractQuest {

	private final String SLOT = "tutorial_island";

	private final String tutorBasename = "tutor";
	private final String tutorTitle = "Tutor";
	// NPCs created per player
	private Map<String, SpeakerNPC> activeTutors;
	// name index for the npc to be created
	private int tutorIndex = 1;


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
		return "Tutorial Island";
	}

	@Override
	public String getNPCName() {
		return tutorTitle;
	}

	private void setTutorDialog(final SpeakerNPC tutor) {
		// TODO:
	}

	public static void startTutorialForPlayer(final Player player) {
		/*
		player.setQuest(SLOT, "start");

		onCreateNPC(player.getName());
		onCreateIsland(player);
		*/
	}

	private void onCreateNPC(final String pname) {
		final SpeakerNPC tutor = new SpeakerNPC(tutorBasename + tutorIndex);
		tutor.setTitle(tutorTitle);
		tutor.put("cloned", tutorBasename); // hide from website

		// TODO:
		// - set NPC attributes

		setTutorDialog(tutor);
	}

	private void onCreateIsland(final Player player) {
		// TODO:
		// - create temporary zone for sandboxed tutorial
		// - add tutor NPC to island
		// - teleport player to island
		// - make island no teleport in/out
	}

	private void onCompleted(final Player player) {
		// TODO:
		// - remove NPC from zone/world & delete
		// - teleport player to guardhouse in Semos Village
		// - create GameEvent

		player.setQuest(SLOT, "done");

		activeTutors.remove(player.getName());
		if (activeTutors.size() == 0) {
			// reset index
			tutorIndex = 1;
		}
	}
}
