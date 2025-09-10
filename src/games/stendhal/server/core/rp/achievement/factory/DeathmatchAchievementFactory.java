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

import games.stendhal.common.MathHelper;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.rp.HOFScore;
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

	public static final String HELPER_SLOT = "deathmatch_helper";
	public static final String SOLOER_SLOT = "deathmatch_soloer";

	public static final String ID_HELPER_25 = "deathmatch.helper.0025";
	public static final String ID_HELPER_50 = "deathmatch.helper.0050";
	public static final String ID_HELPER_100 = "deathmatch.helper.0100";
	public static final String ID_SOLO_5 = "deathmatch.solo.0005";
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
			HOFScore.HARD, true,
			new QuestStateGreaterThanCondition("deathmatch", 1, 24)));
		*/

		achievements.add(createAchievement(
			"quest.deathmatch", "Deathmatch Hero",
			"Earn 100,000 points in deathmatch",
			HOFScore.MEDIUM, true,
			new QuestStateGreaterThanCondition(
				"deathmatch_score", 0, 100000)));

		achievements.add(createAchievement(
			ID_HELPER_25, "Deathmatch Helper",
			"Aid other players in 25 rounds of deathmatch",
			HOFScore.EASY, true,
			new HasHelpedNumberOfTimes(25)));

		achievements.add(createAchievement(
			ID_HELPER_50, "Deathmatch Companion",
			"Aid other players in 50 rounds of deathmatch",
			HOFScore.EASY, true,
			new HasHelpedNumberOfTimes(50)));

		achievements.add(createAchievement(
			ID_HELPER_100, "Deathmatch Convoy",
			"Aid other players in 100 rounds of deathmatch",
			HOFScore.MEDIUM, true,
			new HasHelpedNumberOfTimes(100)));

		achievements.add(createAchievement(
			ID_SOLO_5, "Challenge Accepted",
			"Complete 5 rounds of deathmatch without help",
			HOFScore.EASY, true,
			new HasSoloedNumberOfTimes(5)));

		achievements.add(createAchievement(
			ID_HELM_MAX, "Determination",
			"Increase trophy helmet to max defense",
			HOFScore.HARD, true,
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
	 * Checks if a player has helped in deathmatch a specified number of times.
	 */
	private class HasHelpedNumberOfTimes implements ChatCondition {
		private int requiredCount;

		private HasHelpedNumberOfTimes(final int count) {
			this.requiredCount = count;
		}

		@Override
		public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
			return MathHelper.parseInt(player.getQuest(HELPER_SLOT, 0)) >= requiredCount;
		}
	};

	/**
	 * Checks if a player has competed without help in deathmatch a specified number of times.
	 */
	private class HasSoloedNumberOfTimes implements ChatCondition {
		private int requiredCount;

		private HasSoloedNumberOfTimes(final int count) {
			this.requiredCount = count;
		}

		@Override
		public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
			return MathHelper.parseInt(player.getQuest(SOLOER_SLOT, 0)) >= requiredCount;
		}
	};
}
