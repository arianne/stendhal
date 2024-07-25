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

import java.util.HashMap;
import java.util.Map;

import games.stendhal.server.core.rp.HOFScore;
import marauroa.common.Pair;

/**
 * Static info about quests, filled with fillQuestInfo in each quest
 *
 * @author hendrik
 */
public class QuestInfo {

	private String name = "";

	private boolean repeatable = false;

	private String description = "";

	private String descriptionGM = "";

	private Map<String, String> history = new HashMap<String, String>();

	private Map<String, String> hints = new HashMap<String, String>();

	private int suggestedMinLevel;

	/** Quest slot indexes where completions count is stored. */
	private final Pair<Integer, Integer> completionsIndexes;

	/** Score value of this quest for Hall of Fame. */
	private HOFScore baseScore = HOFScore.NONE;


	public QuestInfo() {
		completionsIndexes = new Pair<>(null, null);
	}

	public int getSuggestedMinLevel() {
		return suggestedMinLevel;
	}

	public void setSuggestedMinLevel(int suggestedMinLevel) {
		this.suggestedMinLevel = suggestedMinLevel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getDescriptionGM() {
		return descriptionGM;
	}

	public void setDescriptionGM(final String descriptionGM) {
		this.descriptionGM = descriptionGM;
	}

	public Map<String, String> getHints() {
		return hints;
	}

	public void setHints(final Map<String, String> hints) {
		this.hints = hints;
	}

	public Map<String, String> getHistory() {
		return history;
	}

	public void setHistory(final Map<String, String> history) {
		this.history = history;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public boolean getRepeatable() {
		return repeatable;
	}

	public void setRepeatable(final boolean repeatable) {
		this.repeatable = repeatable;
	}

	/**
	 * Sets quest slot indexes to check for completions count.
	 *
	 * @param openIndex
	 *   Index where count is stored while quest is in open state.
	 * @param completeIndex
	 *   Index where count is stored while quest is in complete state.
	 */
	public void setCompletionsIndexes(final Integer openIndex, final Integer completeIndex) {
		completionsIndexes.setFirst(openIndex);
		completionsIndexes.setSecond(completeIndex);
	}

	/**
	 * Sets quest slot indexes to check for completions count.
	 *
	 * @param index
	 *   Index where count is stored while quest in open or complete state.
	 */
	public void setCompletionsIndexes(final Integer index) {
		setCompletionsIndexes(index, index);
	}

	/**
	 * Sets quest slot indexes to check for completions count.
	 *
	 * @param completionsIndexes
	 *   Pair where `first` attribute represents slot index for open state and `second` attribute
	 *   represents index for complete state.
	 */
	public void setCompletionsIndexes(final Pair<Integer, Integer> completionsIndexes) {
		this.completionsIndexes.setFirst(completionsIndexes.first());
		this.completionsIndexes.setSecond(completionsIndexes.second());
	}

	/**
	 * Retrieves quest slot indexes where completions count are stored.
	 *
	 * @return
	 *   A `marauroa.common.Pair` where `first` attribute represents slot index while quest is in
	 *   open state and `second` represents slot index while quest is in complete state.
	 */
	public Pair<Integer, Integer> getCompletionsIndexes() {
		return completionsIndexes;
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
