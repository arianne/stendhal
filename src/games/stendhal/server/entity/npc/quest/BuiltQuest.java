/***************************************************************************
 *                   (C) Copyright 2022 - Faiumoni e.V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.npc.quest;

import java.util.ArrayList;
import java.util.List;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.TimePassedCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.AbstractQuest;

/**
 * a quest created using the BuilderPattern
 *
 * @author hendrik
 */
public class BuiltQuest extends AbstractQuest {
	private QuestBuilder<?> questBuilder;
	private String questSlot = null;

	/**
	 * creates a Quest based on a builder
	 *
	 * @param questBuilder quest builder
	 */
	public BuiltQuest(QuestBuilder<?> questBuilder) {
		this.questBuilder = questBuilder;
		this.questSlot = questBuilder.info().getInternalName().toLowerCase();
	}

	@Override
	public List<String> getHistory(Player player) {
		final List<String> res = new ArrayList<String>();
		if (!player.hasQuest(questSlot)) {
			return res;
		}

		QuestHistoryBuilder history = questBuilder.history();
		res.add(history.getWhenNpcWasMet());
		final String questState = player.getQuest(questSlot, 0);
		if ("rejected".equals(questState)) {
			res.add(history.getWhenQuestWasRejected());
			return res;
		}
		res.add(history.getWhenQuestWasAccepted());
		if ("done".equals(questState) || ("start".equals(questState) && questBuilder.task().isCompleted(player, questSlot))) {
			res.add(history.getWhenTaskWasCompleted());
		}
		if ("done".equals(questState)) {
			res.add(history.getWhenQuestWasCompleted());
		}
		if (isRepeatable(player)){
			res.add(history.getWhenQuestCanBeRepeated());
		}
		return res;
	}

	@Override
	public void addToWorld() {
		fillQuestInfo(
				questBuilder.info().getName(),
				questBuilder.info().getDescription(),
				questBuilder.info().getRepeatableAfterMinutes() > 0);

		ChatAction startQuestAction = questBuilder.task().buildStartQuestAction(questSlot);
		ChatCondition questCompletedCondition = questBuilder.task().buildQuestCompletedCondition(questSlot);
		ChatAction questCompleteAction = questBuilder.task().buildQuestCompleteAction(questSlot);

		final SpeakerNPC npc = npcs.get(questBuilder.info().getQuestGiverNpc());
		questBuilder.offer().build(npc, questSlot, startQuestAction, questCompletedCondition, questBuilder.info().getRepeatableAfterMinutes());
		questBuilder.complete().build(npc, questSlot, questCompletedCondition, questCompleteAction);
	}

	@Override
	public String getName() {
		return questBuilder.info().getInternalName();
	}

	@Override
	public int getMinLevel() {
		return questBuilder.info().getMinLevel();
	}

	@Override
	public String getRegion() {
		return questBuilder.info().getRegion();
	}

	@Override
	public String getNPCName() {
		return questBuilder.info().getQuestGiverNpc();
	}

	@Override
	public String getSlotName() {
		return questSlot;
	}


	@Override
	public boolean isRepeatable(final Player player) {
		return isCompleted(player)
				&& questBuilder.info().getRepeatableAfterMinutes() > 0
				&& new TimePassedCondition(questSlot, 1, questBuilder.info().getRepeatableAfterMinutes()).fire(player,null, null);
	}
}
