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
package games.stendhal.server.core.rp.achievement;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;
/**
 * An Achievement a player can reach while playing the game.
 * Achievements are given for example for doing a certain number of quests or killing a number of special creatures
 *
 * @author madmetzger
 */
public class Achievement {

	/** base score for easy achievements */
	public static final int EASY_BASE_SCORE = 1;

	/** base score for achievements of medium difficulty */
	public static final int MEDIUM_BASE_SCORE = 2;

	/** base score for difficult achievements */
	public static final int HARD_BASE_SCORE = 5;

	private final String identifier;

	private final String title;

	private final Category category;

	private final String description;

	private final int baseScore;

	/** is this achievement visible? */
	private final boolean active;

	private final ChatCondition condition;



	/**
	 * create a new achievement
	 *
	 * @param identifier
	 * @param title
	 * @param category
	 * @param description
	 * @param baseScore
	 * @param active
	 * @param condition
	 */
	public Achievement(String identifier, String title, Category category, String description, int baseScore, boolean active, ChatCondition condition) {
		this.identifier = identifier;
		this.title = title;
		this.category = category;
		this.condition = condition;
		this.description = description;
		this.baseScore = baseScore;
		this.active = active;
	}

	/**
	 * @return the category of this achievement
	 */
	public Category getCategory() {
		return category;
	}

	/**
	 * @return the identifying string
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the title a player gets awarded for this achievement
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the description of what to do to get this achievement
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the base score for this achievement
	 */
	public int getBaseScore() {
		return this.baseScore;
	}

	/**
	 * @return true if the achievement is visible, false otherwise
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * Check if a player has fulfilled this achievement
	 * @param p the player to check
	 * @return true iff this achievement's condition evaluates to true
	 */
	public boolean isFulfilled(Player p) {
		return condition.fire(p, null, null);
	}

	@Override
	public String toString() {
		return "Achievement<id: "+identifier+", title: "+title+">";
	}

}
