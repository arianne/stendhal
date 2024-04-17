/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
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

import org.apache.log4j.Logger;

import games.stendhal.common.MathHelper;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;

/**
 * Abstract class for quests. This is a default implementation of IQuest.
 *
 * @author hendrik
 */
public abstract class AbstractQuest implements IQuest {

	private static final List<String> EMPTY_LIST = new ArrayList<String>();

	protected QuestInfo questInfo = new QuestInfo();

	@Override
	public QuestInfo getQuestInfo(Player player) {
		return questInfo;
	}

	@Override
	public void updatePlayer(Player player) {
		// do nothing, but may be overridden by children
	}

	/**
	 * fill fields of questInfo object with info about this quest
	 * @param name - name of the quest
	 * @param description - short description of this quest in a neutral tense (not first person)
	 * @param repeatable - is quest repeatable or not
	 * @param completionsOpenIndex
	 *   Slot index where completions count is stored while quest is in open state.
	 * @param completionsCompleteIndex
	 *   Slot index where completions count is stored while quest is in complete state.
	 */
	public void fillQuestInfo(final String name, final String description, boolean repeatable,
			final Integer completionsOpenIndex, final Integer completionsCompleteIndex) {
		questInfo.setName(name);
		questInfo.setDescription(description);
		questInfo.setRepeatable(repeatable);
		questInfo.setSuggestedMinLevel(this.getMinLevel());
		questInfo.setCompletionsIndexes(completionsOpenIndex, completionsCompleteIndex);
	}

	/**
	 * fill fields of questInfo object with info about this quest
	 * @param name - name of the quest
	 * @param description - short description of this quest in a neutral tense (not first person)
	 * @param repeatable - is quest repeatable or not
	 * @param completionsIndex
	 *   Slot index where completions count is stored while quest in open or complete state.
	 */
	public void fillQuestInfo(final String name, final String description, boolean repeatable,
			final int completionsIndex) {
		fillQuestInfo(name, description, repeatable, completionsIndex, completionsIndex);
	}

	/**
	 * fill fields of questInfo object with info about this quest
	 * @param name - name of the quest
	 * @param description - short description of this quest in a neutral tense (not first person)
	 * @param repeatable - is quest repeatable or not
	 * @param completionsIndexes
	 *   Pair of indexes where first value represents slot index for open state and second represents
	 *   index for complete state.
	 */
	public void fillQuestInfo(final String name, final String description, boolean repeatable,
			final Pair<Integer, Integer> completionsIndex) {
		fillQuestInfo(name, description, repeatable, completionsIndex.first(), completionsIndex.second());
	}

	/**
	 * fill fields of questInfo object with info about this quest
	 * @param name - name of the quest
	 * @param description - short description of this quest in a neutral tense (not first person)
	 * @param repeatable - is quest repeatable or not
	 */
	public void fillQuestInfo(final String name, final String description, boolean repeatable) {
		fillQuestInfo(name, description, repeatable, null, null);
	}

	/** NPCList. */
	protected NPCList npcs = SingletonRepository.getNPCList();

	/**
	 * The slot-name in !quests.
	 * @return the slot's name
	 *
	 */
	@Override
	public abstract String getSlotName();

	@Override
	public abstract void addToWorld();

	/**
	 * removes a quest from the world.
	 *
	 * @return true, if the quest could be removed; false otherwise.
	 */
	@Override
	public boolean removeFromWorld() {
		// sub classes can implement this method but should not call super if they do
		return false;
	}

	@Override
	public List<String> getHint(final Player player) {
		return EMPTY_LIST;
	}

	// Determines if the player should be given a hint to start the quest.
	// Not a hard condition about the quest itself. (use level check ChatConditions for that)
	@Override
	public int getMinLevel() {
		return 0;
	}

	@Override
	public List<String> getFormattedHistory(final Player player) {
		return getHistory(player);
	}

	@Override
	public boolean isCompleted(final Player player) {
		return player.hasQuest(getSlotName())
				&& player.isQuestCompleted(getSlotName());
	}

	@Override
	public int getCompletedCount(final Player player) {
		final String questSlot = getSlotName();
		final boolean completed = isCompleted(player);
		if (player.hasQuest(questSlot)) {
			final String[] state = player.getQuest(questSlot).split(";");
			final Pair<Integer, Integer> completionsIndexes = questInfo.getCompletionsIndexes();
			Integer stateIndex = null;
			if (completed) {
				stateIndex = completionsIndexes.second();
			} else {
				stateIndex = completionsIndexes.first();
			}
			if (stateIndex != null && state.length > stateIndex && !"".equals(state[stateIndex])) {
				return MathHelper.parseIntDefault(state[stateIndex], completed ? 1 : 0);
			}
		}
		// default is to return 1 if quest is in complete state and 0 otherwise
		return completed ? 1 : 0;
	}

	/**
	 * Can be used to determine at what level the player most recently completed the quest.
	 *
	 * Returns 0 unless overridden in quest code.
	 *
	 * @param index
	 *   Slot index where player level is stored.
	 * @param player
	 *   Player in question.
	 * @return
	 *   Player's level at time of completion or -1 if never completed.
	 */
	protected int getLevelAtLastCompletion(final int index, final Player player) {
		final String questSlot = getSlotName();
		if (!player.hasQuest(questSlot)) {
			return -1;
		}
		final String stateValue = player.getQuest(questSlot, index);
		try {
			return Integer.parseInt(stateValue);
		} catch (final NumberFormatException e) {
			Logger.getLogger(AbstractQuest.class).debug("State of quest " + questSlot
					+ " not integer value at index " + index + " (\"" + stateValue + "\") for player "
					+ player.getName());
		}
		// if quest was completed assume at least level 0
		// NOTE: should we assume player's current level?
		return 0;
	}

	@Override
	public boolean isRepeatable(final Player player) {
		// TODO implement
		return false;
	}

	@Override
	public boolean isStarted(final Player player) {
		return player.hasQuest(getSlotName());
	}

	@Override
	public abstract String getName();

	/**
	 * Denotes whether quest details should be included in travel log.
	 *
	 * Override to create custom checks.
	 *
	 * @return
	 *   `true` if details should be included.
	 */
	@Override
	public boolean isVisibleOnQuestStatus() {
		return true;
	}

	/**
	 * Denotes whether quest details should be included in travel log.
	 *
	 * Override to create custom checks.
	 *
	 * @param player
	 *   Player for whom details are requested.
	 * @return
	 *   `true` if details should be included.
	 */
	@Override
	public boolean isVisibleOnQuestStatus(final Player player) {
		return isVisibleOnQuestStatus();
	}

	/**
	 * Returns the region where the quest adventure takes place (or begins), or null for global quests
	 *
	 * @return region, or null for global quests
	 */
	@Override
	public String getRegion() {
		return null;
	}

	/**
	 * Returns the starter NPC for the quest, or null if there is none
	 *
	 * @return NPC name, or null for quests with no starter NPC
	 */
	@Override
	public String getNPCName() {
		return null;
	}
}
