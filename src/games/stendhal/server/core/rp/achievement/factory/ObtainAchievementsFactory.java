package games.stendhal.server.core.rp.achievement.factory;

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.PlayerHasObtainedNumberOfItemsFromWellGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * factory for item related achievements.
 *
 * @author madmetzger
 */
public class ObtainAchievementsFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.OBTAIN;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> achievements = new LinkedList<Achievement>();
		achievements.add(createAchievement("obtain.wish", "A wish came true", "Get an item from the wishing well",
				Achievement.EASY_BASE_SCORE, new PlayerHasObtainedNumberOfItemsFromWellGreaterThanCondition(0)));
		//daily item quest achievements
		achievements.add(createAchievement("quest.special.daily_item.0010", "Ados' Supporter", "Finish daily item quest 10 times", 
												Achievement.EASY_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 9)));
		achievements.add(createAchievement("quest.special.daily_item.0050", "Ados' Provider", "Finish daily item quest 50 times", 
												Achievement.EASY_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 49)));
		achievements.add(createAchievement("quest.special.daily_item.0100", "Ados' Supplier", "Finish daily item quest 100 times",
												Achievement.MEDIUM_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 99)));
		achievements.add(createAchievement("quest.special.daily_item.0250", "Ados' Stockpiler", "Finish daily item quest 250 times", 
												Achievement.MEDIUM_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 249)));
		achievements.add(createAchievement("quest.special.daily_item.0500", "Ados' Hoarder", "Finish daily item quest 500 times", 
												Achievement.HARD_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 499)));
		//weekly item quest achievement
		achievements.add(createAchievement("quest.special.weekly_item.0005", "Archaeologist", "Finish weekly item quest 5 times", 
												Achievement.HARD_BASE_SCORE, new QuestStateGreaterThanCondition("weekly_item", 2, 4)));
		//ultimate collector quest achievement
		achievements.add(createAchievement("quest.special.collector", "Ultimate Collector", "Finish ultimate collector quest", 
												Achievement.HARD_BASE_SCORE, new QuestCompletedCondition("ultimate_collector")));
		return achievements;
	}

}
