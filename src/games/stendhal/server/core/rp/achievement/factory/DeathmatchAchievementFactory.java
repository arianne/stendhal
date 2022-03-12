/***************************************************************************
 *                    Copyright © 2003-2022 - Arianne                      *
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

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;
import games.stendhal.server.entity.player.Player;


/**
 * Deathmatch related achievements.
 */
public class DeathmatchAchievementFactory extends AbstractAchievementFactory {

	private static final Logger logger = Logger.getLogger(DeathmatchAchievementFactory.class);

	public static final String HELPER_SLOT = "deathmatch_helper";

	public static final String ID_HELPER_25 = "deathmatch.helper.0025";
	public static final String ID_HELPER_50 = "deathmatch.helper.0050";
	public static final String ID_HELPER_100 = "deathmatch.helper.0100";
	public static final String ID_HELM_MAX = "deathmatch.helmet.0124";


	@Override
	protected Category getCategory() {
		return Category.DEATHMATCH;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		/*
		// Ados Deathmatch
		// disabled. Currently the wrong index is being checked (it would be index 6)
		// and as per bug report:
		//     https://sourceforge.net/tracker/?func=detail&aid=3148365&group_id=1111&atid=101111
		// the count is not saved anyway
		achievements.add(createAchievement(
			"quest.special.dm.025", "Gladiator",
			"Fight 25 Deathmatches",
			Achievement.HARD_BASE_SCORE, true,
			new QuestStateGreaterThanCondition("deathmatch", 1, 24)));
		*/

		achievements.add(createAchievement(
			"quest.deathmatch", "Deathmatch Hero",
			"Earn 100,000 points in deathmatch",
			Achievement.MEDIUM_BASE_SCORE, true,
			new QuestStateGreaterThanCondition(
				"deathmatch_score", 0, 100000)));

		achievements.add(createAchievement(
			ID_HELPER_25, "Deathmatch Helper",
			"Aid other players in 25 rounds of deathmatch",
			Achievement.EASY_BASE_SCORE, true,
			new HasHelpedNumberOfTimes(25)));

		achievements.add(createAchievement(
			ID_HELPER_50, "Deathmatch Companion",
			"Aid other players in 50 rounds of deathmatch",
			Achievement.EASY_BASE_SCORE, true,
			new HasHelpedNumberOfTimes(50)));

		achievements.add(createAchievement(
			ID_HELPER_100, "Deathmatch Convoy",
			"Aid other players in 100 rounds of deathmatch",
			Achievement.MEDIUM_BASE_SCORE, false,
			new HasHelpedNumberOfTimes(100)));

		achievements.add(createAchievement(
			ID_HELM_MAX, "Determination",
			"Increase trophy helmet to max defense",
			Achievement.HARD_BASE_SCORE, true,
			new ChatCondition() {
				@Override
				public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
					for (final Item helm: player.getAllEquipped("trophy helmet")) {
						if (helm.getDefense() > 123) {
							return true;
						}
					}

					return false;
				}
			}));

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
					return false;
				}
			}

			return count >= requiredCount;
		}
	};
}
