/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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

import java.util.Collection;
import java.util.LinkedList;

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.PlayerVisitedZonesInRegionCondition;


/**
 * Factory for underground zone achievements
 *
 * @author madmetzger
 */
public class UndergroundZoneAchievementFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.UNDERGROUND_ZONE;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		// all below ground achievements
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		achievements.add(createAchievement(
			"zone.underground.semos", "Canary",
			"Visit all underground zones in the Semos region",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerVisitedZonesInRegionCondition("semos", Boolean.TRUE, Boolean.FALSE)));

		achievements.add(createAchievement(
			"zone.underground.nalwor", "Fear not Drows nor Hell",
			"Visit all underground zones in the Nalwor region",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerVisitedZonesInRegionCondition("nalwor", Boolean.TRUE, Boolean.FALSE)));

		achievements.add(createAchievement(
			"zone.underground.athor", "Labyrinth Solver",
			"Visit all underground zones in the Athor region",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerVisitedZonesInRegionCondition("athor", Boolean.TRUE, Boolean.FALSE)));

		achievements.add(createAchievement(
			"zone.underground.amazon", "Human Mole",
			"Visit all underground zones in the Amazon region",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerVisitedZonesInRegionCondition("amazon", Boolean.TRUE, Boolean.FALSE)));

		achievements.add(createAchievement(
			"zone.underground.ados", "Deep Dweller",
			"Visit all underground zones in the Ados region",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerVisitedZonesInRegionCondition("ados", Boolean.TRUE, Boolean.FALSE)));

		achievements.add(createAchievement(
			"zone.underground.deniran", "Spelunker",
			"Visit all underground zones in the Deniran region",
			Achievement.HARD_BASE_SCORE, true,
			new PlayerVisitedZonesInRegionCondition("deniran", true, false)));

		return achievements;
	}
}
