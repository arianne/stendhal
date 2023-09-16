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
 * defines the "history" of player progress as shown in the travel log
 *
 * @author hendrik
 */
public class QuestHistoryBuilder {
	private String whenNpcWasMet;
	private String whenQuestWasRejected;
	private String whenQuestWasAccepted;
	private String whenTaskWasCompleted;
	private String whenQuestWasCompleted;
	private String whenQuestCanBeRepeated;
	private String whenCompletionsShown;

	// hide constructor
	QuestHistoryBuilder() {
		super();
	}

	public QuestHistoryBuilder whenNpcWasMet(String whenNpcWasMet) {
		this.whenNpcWasMet = whenNpcWasMet;
		return this;
	}

	public QuestHistoryBuilder whenQuestWasRejected(String whenQuestWasRejected) {
		this.whenQuestWasRejected = whenQuestWasRejected;
		return this;
	}

	public QuestHistoryBuilder whenQuestWasAccepted(String whenQuestWasAccepted) {
		this.whenQuestWasAccepted = whenQuestWasAccepted;
		return this;
	}

	public QuestHistoryBuilder whenTaskWasCompleted(String whenTaskWasCompleted) {
		this.whenTaskWasCompleted = whenTaskWasCompleted;
		return this;
	}

	public QuestHistoryBuilder whenQuestWasCompleted(String whenQuestWasCompleted) {
		this.whenQuestWasCompleted = whenQuestWasCompleted;
		return this;
	}

	public QuestHistoryBuilder whenQuestCanBeRepeated(String whenQuestCanBeRepeated) {
		this.whenQuestCanBeRepeated = whenQuestCanBeRepeated;
		return this;
	}

	/**
	 * Will be shown in travel log when player has completed quest at
	 * least 1 time. Instances of "[count]" in the string will be
	 * replaced with the number of completions. Anything else within
	 * square brackets ("[]") will be replaced with the equivalent
	 * singular or plural noun form.
	 */
	public QuestHistoryBuilder whenCompletionsShown(String whenCompletionsShown) {
		this.whenCompletionsShown = whenCompletionsShown;
		return this;
	}

	String getWhenNpcWasMet() {
		return whenNpcWasMet;
	}

	String getWhenQuestWasRejected() {
		return whenQuestWasRejected;
	}

	String getWhenQuestWasAccepted() {
		return whenQuestWasAccepted;
	}

	String getWhenTaskWasCompleted() {
		return whenTaskWasCompleted;
	}

	String getWhenQuestWasCompleted() {
		return whenQuestWasCompleted;
	}

	String getWhenQuestCanBeRepeated() {
		return this.whenQuestCanBeRepeated;
	}

	String getWhenCompletionsShown() {
		return this.whenCompletionsShown;
	}

}
