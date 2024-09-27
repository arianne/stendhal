/***************************************************************************
 *                    Copyright © 2003-2024 - Arianne                      *
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
import games.stendhal.server.entity.npc.condition.PlayerVisitedZonesCondition;
import games.stendhal.server.entity.npc.condition.PlayerVisitedZonesInRegionCondition;


/**
 * Factory for zone achievements
 *
 * @author madmetzger
 */
public class OutsideZoneAchievementFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.OUTSIDE_ZONE;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		// all outside zone achievements
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		achievements.add(createAchievement(
			"zone.outside.semos", "Junior Explorer",
			"Visit all outside zones in the Semos region",
			HOFScore.EASY, true,
			new PlayerVisitedZonesInRegionCondition("semos", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
			"zone.outside.ados", "Big City Explorer",
			"Visit all outside zones in the Ados region",
			HOFScore.EASY, true,
			new PlayerVisitedZonesInRegionCondition("ados", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
			"zone.outside.fado", "Far South",
			"Visit all outside zones in the Fado region",
			HOFScore.MEDIUM, true,
			new PlayerVisitedZonesInRegionCondition("fado", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
			"zone.outside.orril", "Scout",
			"Visit all outside zones in the Orril region",
			HOFScore.MEDIUM, true,
			new PlayerVisitedZonesInRegionCondition("orril", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
			"zone.outside.amazon", "Jungle Explorer",
			"Visit all outside zones in the Amazon region",
			HOFScore.HARD, true,
			new PlayerVisitedZonesInRegionCondition("amazon", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
			"zone.outside.athor", "Tourist",
			"Visit all outside zones in the Athor region",
			HOFScore.EASY, true,
			new PlayerVisitedZonesInRegionCondition("athor", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
			"zone.outside.kikareukin", "Sky Tower",
			"Visit all outside zones in the Kikareukin region",
			HOFScore.HARD, true,
			new PlayerVisitedZonesInRegionCondition("kikareukin", Boolean.TRUE, Boolean.TRUE)));

		achievements.add(createAchievement(
			"zone.outside.deniran", "Westerner",
			"Visit all outside zones in the Deniran region",
			HOFScore.EASY, true,
			new PlayerVisitedZonesInRegionCondition("deniran", true, true)));

		// special zone achievements
		achievements.add(createAchievement(
			"zone.special.bank", "Safe Deposit",
			"Visit all banks",
			HOFScore.MEDIUM, true,
			new PlayerVisitedZonesCondition(
				"int_semos_bank", "int_nalwor_bank", "int_kirdneh_bank",
				"int_fado_bank", "int_magic_bank", "int_ados_bank",
				"int_deniran_bank_blue_roof")));

		return achievements;
	}
}
