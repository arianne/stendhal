package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ExperienceAchievementFactory extends AchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.EXPERIENCE;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> xpAchievements = new LinkedList<Achievement>();
		xpAchievements.add(createAchievement("xp.lvl.10", "Greenhorn", "Reach level 10", Achievement.EASY_BASE_SCORE,
												new LevelGreaterThanCondition(9)));
		xpAchievements.add(createAchievement("xp.lvl.50", "Novice", "Reach level 50", Achievement.EASY_BASE_SCORE,
												new LevelGreaterThanCondition(49)));
		xpAchievements.add(createAchievement("xp.lvl.100", "Apprentice", "Reach level 100", Achievement.EASY_BASE_SCORE, 
												new LevelGreaterThanCondition(99)));
		xpAchievements.add(createAchievement("xp.lvl.200", "Adventurer", "Reach level 200", Achievement.MEDIUM_BASE_SCORE, 
												new LevelGreaterThanCondition(199)));
		xpAchievements.add(createAchievement("xp.lvl.300", "Experienced Adventurer", "Reach level 100", Achievement.MEDIUM_BASE_SCORE, 
												new LevelGreaterThanCondition(299)));
		xpAchievements.add(createAchievement("xp.lvl.400", "Master Adventurer", "Reach level 400", Achievement.MEDIUM_BASE_SCORE, 
												new LevelGreaterThanCondition(399)));
		xpAchievements.add(createAchievement("xp.lvl.500", "Stendhal Master", "Reach level 500", Achievement.HARD_BASE_SCORE, 
												new LevelGreaterThanCondition(499)));
		xpAchievements.add(createAchievement("xp.lvl.597", "Stendhal High Master", "Reach level 597", Achievement.HARD_BASE_SCORE, 
												new LevelGreaterThanCondition(596)));
		return xpAchievements;
	}

}
