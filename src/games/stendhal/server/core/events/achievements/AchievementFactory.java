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
package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.ChatCondition;

import java.util.Collection;
/**
 * Factory class for achievements creation with a fixed category
 *  
 * @author madmetzger
 */
public abstract class AchievementFactory {

	/**
	 * @return the category the factory should use
	 */
	protected abstract Category getCategory();

	/**
	 * Creates a collection of achievements
	 * 
	 * @return the achievments
	 */
	public abstract Collection<Achievement> createAchievements();

	/**
	 * Creates a single achievement 
	 * @param identifier
	 * @param title
	 * @param description
	 * @param score
	 * @param condition
	 * @return the new Achievement
	 */
	protected Achievement createAchievement(String identifier, String title, String description, int score, ChatCondition condition) {
		return new Achievement(identifier, title, getCategory(),  description, score, condition);
	}

}