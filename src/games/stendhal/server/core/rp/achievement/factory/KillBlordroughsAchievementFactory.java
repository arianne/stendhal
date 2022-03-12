/***************************************************************************
 *                   (C) Copyright 2003-2018 - Stendhal                    *
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

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.quests.KillBlordroughs;


/**
 * Factory for KillBlordroughsAchievement
 */
public class KillBlordroughsAchievementFactory extends AbstractAchievementFactory {

	public static final int COUNT_LACKEY = 5;
	public static final String ID_LACKEY = "quest.special.kill_blordroughs.0005";

	public static final int COUNT_SOLDIER = 25;
	public static final String ID_SOLDIER = "quest.special.kill_blordroughs.0025";

	public static final int COUNT_DOMINATOR = 50;
	public static final String ID_DOMINATOR = "quest.special.kill_blordroughs.0050";

	public static final int COUNT_DICTATOR = 100;
	public static final String ID_DICTATOR = "quest.special.kill_blordroughs.0100";


	@Override
	protected Category getCategory() {
		return Category.QUEST_KILL_BLORDROUGHS;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		achievements.add(createAchievement(
			ID_LACKEY, "Imperialist Lackey",
			"Finish Kill Blordroughs quest 5 times",
			Achievement.MEDIUM_BASE_SCORE, true,
			new CompletedCountCondition(COUNT_LACKEY)));

		achievements.add(createAchievement(
			ID_SOLDIER, "Imperialist Soldier",
			"Finish Kill Blordroughs quest 25 times",
			Achievement.HARD_BASE_SCORE, true,
			new CompletedCountCondition(COUNT_SOLDIER)));

		achievements.add(createAchievement(
			ID_DOMINATOR, "Imperialist Dominator",
			"Finish Kill Blordroughs quest 50 times",
			Achievement.HARD_BASE_SCORE, true,
			new CompletedCountCondition(COUNT_DOMINATOR)));

		achievements.add(createAchievement(
			ID_DICTATOR, "Imperialist Dictator",
			"Finish Kill Blordroughs quest 100 times",
			Achievement.HARD_BASE_SCORE, true,
			new CompletedCountCondition(COUNT_DICTATOR)));

		return achievements;
	}


	/**
	 * Condition to check if Kill Blordroughs quest has been completed a specific number of times.
	 */
	private class CompletedCountCondition implements ChatCondition {

		private final int requiredCount;


		public CompletedCountCondition(final int count) {
			requiredCount = count;
		}

		@Override
		public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
			return KillBlordroughs.getInstance().getCompletedCount(player) >= requiredCount;
		}
	}
}
