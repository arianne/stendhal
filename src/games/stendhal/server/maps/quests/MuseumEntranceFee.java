/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
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

import java.util.ArrayList;
import java.util.List;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.DropItemAction;
import games.stendhal.server.entity.npc.action.MultipleActions;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.condition.QuestActiveCondition;
import games.stendhal.server.entity.npc.condition.QuestNotStartedCondition;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Museum Entrance Fee
 *
 * PARTICIPANTS:
 * <ul>
 * <li> Iker</li>
 * </ul>
 *
 * STEPS:
 * <ul>
 * <li> Talk to Iker and pay the fee</li>
 * </ul>
 *
 * REWARD:
 * <ul>
 * <li> Admittance to the museum</li>
 * </ul>
 *
 * REPETITIONS:
 * <ul>
 * <li> At any time</li>
 * </ul>
 *
 * @author kribbel
 */

public class MuseumEntranceFee extends AbstractQuest {

	private static final String QUEST_SLOT = "museum_entrance_fee";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	private void step_1() {

		final SpeakerNPC npc = npcs.get("Iker");

		npc.add(ConversationStates.ATTENDING,
				"visit",
				new QuestNotStartedCondition(QUEST_SLOT),
				ConversationStates.QUEST_OFFERED,
				"You will be granted access after you paid the entrance fee of 10 money. Would you like to pay it now?",
				null);

		// Player asks to visit while already paid
		npc.add(ConversationStates.ATTENDING,
				"visit",
				new QuestActiveCondition(QUEST_SLOT),
				ConversationStates.ATTENDING,
				"You already paid the entrance fee. Please step in and enjoy your visit.",
				null);

		// Player wants to visit, but has not enough money
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(new PlayerHasItemWithHimCondition("money", 10)),
				ConversationStates.ATTENDING,
				"I'm sorry, I'm not allowed to offer you a discount. You have to give me 10 money.",
				null);

		// Player wants to visit, and has enough money. Entering the portal will set quest slot to null and so end (and delete) it. Look at deniran.xml
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.YES_MESSAGES,
				new PlayerHasItemWithHimCondition("money", 10),
				ConversationStates.ATTENDING,
				"Thank you, please step in and enjoy your visit.",
				new MultipleActions(new DropItemAction("money", 10),new SetQuestAction(QUEST_SLOT, "start")));

		// Player doesn't want to visit
		npc.add(ConversationStates.QUEST_OFFERED,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Shame! You are missing the experience of your lifetime.",
				null);
	}

	@Override
	public void addToWorld() {
		step_1();
/*		fillQuestInfo(
				"Museum Entrance Fee", null,
				//"Iker, a Deniran boy.",
				true);
*/
	}

	@Override
	public boolean isVisibleOnQuestStatus() {
		return false;
	}

	@Override
	public List<String> getHistory(final Player player) {
		return new ArrayList<String>();
	}

	@Override
	public String getName() {
		return "MuseumEntranceFee";
	}

	@Override
	public boolean isRepeatable(final Player player) {
		return true;
	}

	@Override
	public String getNPCName() {
		return "Iker";
	}
}
