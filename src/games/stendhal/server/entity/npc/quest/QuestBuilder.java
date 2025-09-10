/***************************************************************************
 *                 (C) Copyright 2022-2024 - Faiumoni e.V.                 *
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

import games.stendhal.server.core.rp.HOFScore;


/**
 * builds a quest
 *
 * @author hendrik
 * @param <T> QuestTaskBuilder
 */
public class QuestBuilder<T extends QuestTaskBuilder, O extends QuestOfferBuilder<O>, C extends QuestCompleteBuilder, H extends QuestHistoryBuilder> {

	private QuestInfoBuilder info = new QuestInfoBuilder();
	@SuppressWarnings("unchecked")
	protected O offer;
	private T task = null;
	protected C complete;
	protected H history = null;

	/** Score value of this quest for Hall of Fame. */
	private HOFScore baseScore = HOFScore.NONE;


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
	public H history() {
		return history;
	}

	/**
	 * defines how the NPC offers the player the quest when the player says "quest"
	 *
	 * @return QuestOfferBuilder
	 */
	public O offer() {
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
	public C complete() {
		return complete;
	}

	/**
	 * simulates the quest
	 */
	public void simulate() {
		QuestSimulator simulator = new QuestSimulator();
		setupSimulator(simulator);
		info.simulate(simulator);
		String npc = info.getQuestGiverNpc();
		offer.simulateFirst(npc, simulator);
		task.simulate(simulator);
		complete.simulate(npc, simulator);
		offer.simulateNotRepeatable(npc, simulator);
		if (info.getRepeatableAfterMinutes() > -1) {
			simulator.info("Time passed");
			simulator.info("");
			offer.simulateRepeat(npc, simulator);
		}
	}

	protected void setupSimulator(@SuppressWarnings("unused") QuestSimulator simulator) {
		// do nothing
	}

	/**
	 * Sets the completed quest score value for Hall of Fame.
	 *
	 * @param score
	 *   Hall of Fame score value.
	 */
	public void setBaseHOFScore(final HOFScore score) {
		baseScore = score;
	}

	/**
	 * Retrieves the completed quest score value for Hall of Fame.
	 *
	 * The default value is no score.
	 *
	 * @return
	 *   Hall of Fame score value.
	 */
	public HOFScore getBaseHOFScore() {
		return baseScore;
	}
}
