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

/**
 * builds a quest
 *
 * @author hendrik
 * @param <T> QuestTaskBuilder
 */
public class QuestBuilder<T extends QuestTaskBuilder> {

	private QuestInfoBuilder info = new QuestInfoBuilder();
	private QuestHistoryBuilder history = new QuestHistoryBuilder();
	private QuestOfferBuilder offer = new QuestOfferBuilder();
	private T task = null;
	private QuestCompleteBuilder complete = new QuestCompleteBuilder();

	/**
	 * creates a QuestBuilder
	 *
	 * @param task QuestTaskBuilder
	 */
	public QuestBuilder(T task) {
		this.task = task;
	}

	/**
	 * defines general information about this quest
	 *
	 * @return QuestInfoBuilder
	 */
	public QuestInfoBuilder info() {
		return info;
	}

	/**
	 * defines the "history" of player progress as shown in the travel log
	 *
	 * @return QuestHistoryBuilder
	 */
	public QuestHistoryBuilder history() {
		return history;
	}

	/**
	 * defines how the NPC offers the player the quest when the player says "quest"
	 *
	 * @return QuestOfferBuilder
	 */
	public QuestOfferBuilder offer() {
		return offer;
	}

	/**
	 * defines the task, which the player has to complete
	 *
	 * @return QuestTaskBuilder
	 */
	public T task() {
		return task;
	}

	/**
	 * defines how the NPC react after the player completes the quest
	 *
	 * @return QuestCompleteBuilder
	 */
	public QuestCompleteBuilder complete() {
		return complete;
	}

	/**
	 * simulates the quest
	 */
	public void simulate() {
		QuestSimulator simulator = new QuestSimulator();
		info.simulate(simulator);
		String npc = info.getQuestGiverNpc();
		offer.simulateFirst(npc, simulator);
		task.simulate(simulator);
		complete.simulate(npc, simulator);
		offer.simulateNotRepeatable(npc, simulator);
		if (info.getRepeatableAfterMinutes() > 0) {
			simulator.info("Time passed");
			simulator.info("");
			offer.simulateRepeat(npc, simulator);
		}
	}

}
