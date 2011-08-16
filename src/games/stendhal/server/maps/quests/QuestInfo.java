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

import java.util.HashMap;
import java.util.Map;

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

}
