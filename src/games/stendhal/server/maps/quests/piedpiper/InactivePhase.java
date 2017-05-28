/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.piedpiper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;


public class InactivePhase extends TPPQuest {

	private final int minPhaseChangeTime;
	private final int maxPhaseChangeTime;

	private void addConversations(final SpeakerNPC mainNPC) {
		TPP_Phase myphase = INACTIVE;

		// Player asking about rats
		mainNPC.add(
				ConversationStates.ATTENDING,
				Arrays.asList("rats", "rats!"),
				new TPPQuestInPhaseCondition(myphase),
				ConversationStates.ATTENDING,
				"Ados isn't being invaded by rats right now. You can still "+
				  "get a #reward for the last time you helped. You can ask for #details "+
				  "if you want.",
				null);

		// Player asking about details
		mainNPC.add(
				ConversationStates.ATTENDING,
				"details",
				new TPPQuestInPhaseCondition(myphase),
				ConversationStates.ATTENDING,
				null,
				new DetailsKillingsAction());

		// Player asked about reward
		mainNPC.add(
				ConversationStates.ATTENDING,
				"reward",
				new TPPQuestInPhaseCondition(myphase),
				ConversationStates.ATTENDING,
				null,
				new RewardPlayerAction());
	}

	/**
	 * constructor
	 * @param timings
	 */
	public InactivePhase(Map<String, Integer> timings) {
		super(timings);
		minPhaseChangeTime=timings.get(INACTIVE_TIME_MIN);
		maxPhaseChangeTime=timings.get(INACTIVE_TIME_MAX);
		addConversations(TPPQuestHelperFunctions.getMainNPC());
	}


	@Override
	public int getMinTimeOut() {
		return minPhaseChangeTime;
	}

	@Override
	public int getMaxTimeOut() {
		return maxPhaseChangeTime;
	}

	@Override
	public void phaseToDefaultPhase(List<String> comments) {
		// not used
	}

	@Override
	public void prepare() {

	}


	@Override
	public TPP_Phase getPhase() {
		return TPP_Phase.TPP_INACTIVE;
	}

}
