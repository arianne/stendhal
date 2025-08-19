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
package games.stendhal.server.core.rp.achievement.factory;

import java.util.Collection;
import java.util.LinkedList;

import games.stendhal.server.core.rp.HOFScore;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.PlayerVisitedZonesInRegionCondition;


/**
 * Factory for interior zone achievements
 *
 * @author kymara
 */
public class InteriorZoneAchievementFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.INTERIOR_ZONE;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		achievements.add(createAchievement(
			"zone.interior.semos", "Home Maker",
			"Visit all interior zones in the Semos region",
			HOFScore.MEDIUM, true,
			new PlayerVisitedZonesInRegionCondition("semos", Boolean.FALSE, Boolean.FALSE)));

		achievements.add(createAchievement(
			"zone.interior.nalwor", "Elf Visitor",
			"Visit all interior zones in the Nalwor region",
			HOFScore.MEDIUM, true,
			new PlayerVisitedZonesInRegionCondition("nalwor", Boolean.FALSE, Boolean.FALSE)));

		achievements.add(createAchievement(
			"zone.interior.ados", "Up Town Guy",
			"Visit all accessible interior zones in the Ados region",
			HOFScore.MEDIUM, true,
			new PlayerVisitedZonesInRegionCondition("ados", Boolean.FALSE, Boolean.FALSE)));

		achievements.add(createAchievement(
			"zone.interior.wofolcity", "Kobold City",
			"Visit all interior zones in Wo'fol",
			HOFScore.MEDIUM, true,
			new PlayerVisitedZonesInRegionCondition("wofol city", Boolean.FALSE, Boolean.FALSE)));

		achievements.add(createAchievement(
			"zone.interior.magiccity", "Magic City",
			"Visit all interior zones in the underground Magic city",
			HOFScore.MEDIUM, true,
			new PlayerVisitedZonesInRegionCondition("magic city", Boolean.FALSE, Boolean.FALSE)));

		achievements.add(createAchievement(
			"zone.interior.deniran", "Country Recluse",
			"Visit all interior zones in the Deniran region",
			HOFScore.EASY, false,
			new PlayerVisitedZonesInRegionCondition("deniran", false, false)));

		return achievements;
	}
}
