/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
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

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.action.IncrementQuestAction;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;


/**
 * A quest designed to keep track of number of completions.
 */
public abstract class CompletionsTrackingQuest extends AbstractQuest {

	/**
	 * Retrieves number of times player has completed quest.
	 *
	 * @param player
	 *   Player for whom quest is being checked.
	 * @return
	 *   Number of completions.
	 */
	@Override
	public int getCompletions(final Player player) {
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
	 * Increments number of completions.
	 *
	 * @param player
	 *   Player being updated.
	 * @param complete
	 *   {@code true} to use "done" state index, {@code false} to use "active" state index.
	 */
	protected void incrementCompletions(final Player player, final boolean complete) {
		incrementCompletionsAction(complete).fire(player, null, null);
	}

	/**
	 * Increments number of completions.
	 *
	 * @param player
	 *   Player being updated.
	 */
	protected void incrementCompletions(final Player player) {
		incrementCompletions(player, true);
	}

	/**
	 * Retrieves action to execute when completions count should be incremented.
	 *
	 * NOTE: quest completions indexes should be set to use this method
	 *
	 * @param complete
	 *   {@code true} to use "done" state index, {@code false} to use "active" state index.
	 * @return
	 *   {@code IncrementQuestAction} to increment slot where completions indexes are stored.
	 */
	protected IncrementQuestAction incrementCompletionsAction(final boolean complete) {
		final Pair<Integer, Integer> indexes = questInfo.getCompletionsIndexes();
		return new IncrementQuestAction(getSlotName(), complete ? indexes.second() : indexes.first(), 1);
	}

	/**
	 * Retrieves action to execute when completions count should be incremented.
	 *
	 * NOTE: quest completions indexes should be set to use this method
	 *
	 * @return
	 *   {@code IncrementQuestAction} to increment slot where completions indexes are stored.
	 */
	protected IncrementQuestAction incrementCompletionsAction() {
		return incrementCompletionsAction(true);
	}

	/**
	 * Swaps completions value from completed index to started index.
	 *
	 * @param player
	 *   Player being updated.
	 */
	protected void swapCompletionsBeforeStart(final Player player) {
		final Pair<Integer, Integer> indexes = questInfo.getCompletionsIndexes();
		final String questSlot = getSlotName();
		final int completions = MathHelper.parseIntDefault(player.getQuest(questSlot, indexes.second()), 0);
		player.setQuest(questSlot, indexes.first(), String.valueOf(completions));
		player.setQuest(questSlot, indexes.second(), null);
	}

	/**
	 * Creates a chat action to swap completions value from completed index to started index.
	 *
	 * @return
	 *   New chat action instance.
	 */
	protected ChatAction swapCompletionsBeforeStartAction() {
		return new ChatAction() {
			@Override
			public void fire(Player player, Sentence sentence, EventRaiser npc) {
				swapCompletionsBeforeStart(player);
			}
		};
	}

	/**
	 * Swaps completions value from started index to completed index.
	 *
	 * TODO: maybe increment simultaneously so `incrementCompletions` does not need to be called
	 *
	 * @param player
	 *   Player being updated.
	 */
	protected void swapCompletionsBeforeComplete(final Player player) {
		final Pair<Integer, Integer> indexes = questInfo.getCompletionsIndexes();
		final String questSlot = getSlotName();
		final int completions = MathHelper.parseIntDefault(player.getQuest(questSlot, indexes.first()), 0);
		player.setQuest(questSlot, indexes.second(), String.valueOf(completions));
		player.setQuest(questSlot, indexes.first(), null);
	}

	/**
	 * Creates a chat action to swap completions value from started index to completed index.
	 *
	 * @return
	 *   New chat action instance.
	 */
	protected ChatAction swapCompletionsBeforeCompleteAction() {
		return new ChatAction() {
			@Override
			public void fire(Player player, Sentence sentence, EventRaiser npc) {
				swapCompletionsBeforeComplete(player);
			}
		};
	}
}
