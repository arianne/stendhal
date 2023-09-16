/***************************************************************************
 *                 (C) Copyright 2022-2023 - Faiumoni e.V.                 *
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
 * defines the "history" of player progress as shown in the travel log
 *
 * @author hendrik
 */
public class DeliverItemQuestHistoryBuilder extends QuestHistoryBuilder {

	private String whenItemWasGiven;
	private String whenToldAboutCustomer;
	private String whenInTime;
	private String whenOutOfTime;

	// hide constructor
	DeliverItemQuestHistoryBuilder() {
		super();
	}

	@Override
	public DeliverItemQuestHistoryBuilder whenNpcWasMet(String whenNpcWasMet) {
		super.whenNpcWasMet(whenNpcWasMet);
		return this;
	}

	@Override
	public DeliverItemQuestHistoryBuilder whenQuestWasRejected(String whenQuestWasRejected) {
		super.whenQuestWasRejected(whenQuestWasRejected);
		return this;
	}

	@Override
	public DeliverItemQuestHistoryBuilder whenQuestWasAccepted(String whenQuestWasAccepted) {
		super.whenQuestWasAccepted(whenQuestWasAccepted);
		return this;
	}

	@Override
	public DeliverItemQuestHistoryBuilder whenTaskWasCompleted(String whenTaskWasCompleted) {
		super.whenTaskWasCompleted(whenTaskWasCompleted);
		return this;
	}

	@Override
	public DeliverItemQuestHistoryBuilder whenQuestWasCompleted(String whenQuestWasCompleted) {
		super.whenQuestWasCompleted(whenQuestWasCompleted);
		return this;
	}

	@Override
	public DeliverItemQuestHistoryBuilder whenQuestCanBeRepeated(String whenQuestCanBeRepeated) {
		super.whenQuestCanBeRepeated(whenQuestCanBeRepeated);
		return this;
	}

	@Override
	public DeliverItemQuestHistoryBuilder whenCompletionsShown(String whenCompletionsShown) {
		super.whenCompletionsShown(whenCompletionsShown);
		return this;
	}


	public DeliverItemQuestHistoryBuilder whenItemWasGiven(String whenItemWasGiven) {
		this.whenItemWasGiven = whenItemWasGiven;
		return this;
	}

	public DeliverItemQuestHistoryBuilder whenToldAboutCustomer(String whenToldAboutCustomer) {
		this.whenToldAboutCustomer = whenToldAboutCustomer;
		return this;
	}

	public DeliverItemQuestHistoryBuilder whenInTime(String whenInTime) {
		this.whenInTime = whenInTime;
		return this;
	}

	public DeliverItemQuestHistoryBuilder whenOutOfTime(String whenOutOfTime) {
		this.whenOutOfTime = whenOutOfTime;
		return this;
	}


	public String getWhenItemWasGiven() {
		return whenItemWasGiven;
	}

	public String getWhenToldAboutCustomer() {
		return whenToldAboutCustomer;
	}

	public String getWhenInTime() {
		return whenInTime;
	}

	public String getWhenOutOfTime() {
		return whenOutOfTime;
	}

}
