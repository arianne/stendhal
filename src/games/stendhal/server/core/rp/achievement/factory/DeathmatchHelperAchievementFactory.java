/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;


/**
 * Achievements for players who help others with deathmatch.
 */
public class DeathmatchHelperAchievementFactory extends AbstractAchievementFactory {

	private static final Logger logger = Logger.getLogger(DeathmatchHelperAchievementFactory.class);

	public static final String HELPER_SLOT = "deathmatch_helper";

	public static final String ID_25 = "deathmatch.helper.0025";
	public static final String ID_50 = "deathmatch.helper.0050";
	public static final String ID_100 = "deathmatch.helper.0100";


	@Override
	protected Category getCategory() {
		return Category.DEATHMATCH_HELPER;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final List<Achievement> achievements = new LinkedList<Achievement>();

		achievements.add(createAchievement(
				ID_25, "Deathmatch Helper", "Aid other players in 25 rounds of deathmatch",
				Achievement.EASY_BASE_SCORE, true,
				new HasHelpedNumberOfTimes(25)));

		achievements.add(createAchievement(
				ID_50, "Deathmatch Companion", "Aid other players in 50 rounds of deathmatch",
				Achievement.EASY_BASE_SCORE, true,
				new HasHelpedNumberOfTimes(50)));

		achievements.add(createAchievement(
				ID_100, "Deathmatch Convoy", "Aid other players in 100 rounds of deathmatch",
				Achievement.MEDIUM_BASE_SCORE, false,
				new HasHelpedNumberOfTimes(100)));

		return achievements;
	}


	/**
	 * Class to check if a player has helped in deathmatch a specified number of times.
	 */
	private class HasHelpedNumberOfTimes implements ChatCondition {

		private int requiredCount;


		private HasHelpedNumberOfTimes(final int count) {
			this.requiredCount = count;
		}

		@Override
		public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
			int count = 0;
			if (player.hasQuest(HELPER_SLOT)) {
				try {
					count = Integer.parseInt(player.getQuest(HELPER_SLOT, 0));
				} catch (final NumberFormatException e) {
					logger.error("Deathmatch helper quest slot value not an integer.");
					e.printStackTrace();
				}
			}

			return count >= requiredCount;
		}
	};
}
