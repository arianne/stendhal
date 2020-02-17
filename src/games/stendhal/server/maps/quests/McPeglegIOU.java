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
package games.stendhal.server.maps.quests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestNotCompletedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.Region;

/**
 * QUEST: McPegleg's IOU
 *
 * PARTICIPANTS: - a corpse in kanmararn - McPegleg
 *
 * NOTE: The corpse with contains the IOU is created in KanmararnSoldiers.java
 * Without it this quest cannot be started (so the player won't notice the
 * problem at all).
 *
 * STEPS: - find IOU in a corpse in kanmararn - bring it to McPegleg
 *
 * REWARD: - 250 money
 *
 * REPETITIONS: - None.
 */
public class McPeglegIOU extends AbstractQuest {

	private static final String QUEST_SLOT = "IOU";

	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}
	private void step_1() {
		// find the IOU in a corpse in kanmararn.
		// this is implemented in KanmararnSoldiers
	}

	private void step_2() {

		final SpeakerNPC npc = npcs.get("McPegleg");

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("iou", "henry", "charles", "note"),
			new QuestNotCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING, null,
			new ChatAction() {

				@Override
				public void fire(final Player player, final Sentence sentence, final EventRaiser raiser) {
					// from all notes that the player is carrying, try to
					// find the IOU note
					final List<Item> notes = player.getAllEquipped("note");
					Item iouNote = null;
					for (final Item note : notes) {
						if ("charles".equalsIgnoreCase(note.getInfoString())) {
							iouNote = note;
							break;
						}
					}
					if (iouNote != null) {
						raiser.say("Where did you get that from? Anyways, here is the money *sighs*");
						player.drop(iouNote);
						final StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem(
								"money");
						money.setQuantity(250);
						player.equipToInventoryOnly(money);
						player.setQuest(QUEST_SLOT, "done");
						raiser.setCurrentState(ConversationStates.ATTENDING);
					} else {
						raiser.say("I can't see that you got a valid IOU with my signature!");
					}
				}
			});

		npc.add(ConversationStates.ATTENDING,
			Arrays.asList("iou", "henry", "charles", "note"),
			new QuestCompletedCondition(QUEST_SLOT),
			ConversationStates.ATTENDING,
			"You already got cash for that damned IOU!", null);
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				"McPegleg IOU",
				"McPegleg doesn't always have cash on hand, and sometimes passes out IOUs instead.",
				false);

		step_1();
		step_2();
	}

	@Override
	public List<String> getHistory(final Player player) {
			final List<String> res = new ArrayList<String>();
			// only valid if player started the kanmararn soldiers quest
			if(player.isQuestCompleted("soldier_henry")) {
				res.add("Henry gave me an IOU with McPegleg's name on it.");
			}
			if (isCompleted(player)) {
				res.add("McPegleg did honour the IOU - I got 250 money!");
			}
			return res;
	}

	@Override
	public String getName() {
		return "McPeglegIOU";
	}

	@Override
	public int getMinLevel() {
		return 40;
	}
	@Override
	public String getNPCName() {
		return "McPegleg";
	}

	@Override
	public String getRegion() {
		return Region.SEMOS_DUNGEONS;
	}
}
