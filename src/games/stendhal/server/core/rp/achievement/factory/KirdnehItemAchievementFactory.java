package games.stendhal.server.core.rp.achievement.factory;

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;

import java.util.Collection;
import java.util.LinkedList;

public class KirdnehItemAchievementFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.QUEST_KIRDNEH_ITEM;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		LinkedList<Achievement> achievements = new LinkedList<Achievement>();
		achievements.add(createAchievement("quest.special.weekly_item.0005", "Archaeologist", "Finish weekly item quest 5 times",
				Achievement.HARD_BASE_SCORE, true, new QuestStateGreaterThanCondition("weekly_item", 2, 4)));
		achievements.add(createAchievement("quest.special.weekly_item.0050", "Senior Archaeologist", "Finish weekly item quest 50 times",
				Achievement.HARD_BASE_SCORE, true, new QuestStateGreaterThanCondition("weekly_item", 2, 49)));
		achievements.add(createAchievement("quest.special.weekly_item.0100", "Master Archaeologist", "Finish weekly item quest 100 times",
				Achievement.HARD_BASE_SCORE, true, new QuestStateGreaterThanCondition("weekly_item", 2, 99)));
		return achievements;
	}

}
