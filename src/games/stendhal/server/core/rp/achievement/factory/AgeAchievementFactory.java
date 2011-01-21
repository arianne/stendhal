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
package games.stendhal.server.core.rp.achievement.factory;

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
/**
 * Factory for age related achievements
 *
 * @author madmetzger
 */
public class AgeAchievementFactory extends AbstractAchievementFactory {

	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> ageAchievements = new LinkedList<Achievement>();
		// TODO: create achievements based on character creation
		return ageAchievements;
	}

	@Override
	protected Category getCategory() {
		return Category.AGE;
	}

}