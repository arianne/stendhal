package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class QuestAchievementFactory extends AchievementFactory {
	
	/**
	 * creates a collection of all available quest achievements
	 * 
	 * @return all available quest achievements in a collection
	 */
	public Collection<Achievement> createAchievements() {
		List<Achievement> questAchievements = new LinkedList<Achievement>();
		//daily monster quest achievements
		questAchievements.add(new Achievement("quest.special.dmq.10", "Semos' Protector",
												getCategory(),  "Finish daily monster quest 10 times", Achievement.EASY_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily", 2, 9)));
		questAchievements.add(new Achievement("quest.special.dmq.50", "Semos' Guardian",
												getCategory(),  "Finish daily monster quest 50 times", Achievement.EASY_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily", 2, 49)));
		questAchievements.add(new Achievement("quest.special.dmq.100", "Semos' Hero",
												getCategory(),  "Finish daily monster quest 100 times", Achievement.MEDIUM_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily", 2, 99)));
		questAchievements.add(new Achievement("quest.special.dmq.250", "Semos' Champion",
												getCategory(),  "Finish daily monster quest 250 times", Achievement.MEDIUM_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily", 2, 249)));
		questAchievements.add(new Achievement("quest.special.dmq.500", "Semos' Vanquisher",
												getCategory(),  "Finish daily monster quest 500 times", Achievement.HARD_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily", 2, 499)));
		//daily item quest achievements
		questAchievements.add(new Achievement("quest.special.diq.10", "Ados' Supporter",
												getCategory(),  "Finish daily item quest 10 times", Achievement.EASY_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily_item", 2, 9)));
		questAchievements.add(new Achievement("quest.special.diq.50", "Ados' Provider",
												getCategory(),  "Finish daily item quest 50 times", Achievement.EASY_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily_item", 2, 49)));
		questAchievements.add(new Achievement("quest.special.diq.100", "Ados' Supplier",
												getCategory(),  "Finish daily item quest 100 times", Achievement.MEDIUM_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily_item", 2, 99)));
		questAchievements.add(new Achievement("quest.special.diq.250", "Ados' Stockpiler",
												getCategory(),  "Finish daily item quest 250 times", Achievement.MEDIUM_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily_item", 2, 249)));
		questAchievements.add(new Achievement("quest.special.diq.50", "Ados' Hoarder",
												getCategory(),  "Finish daily item quest 50 times", Achievement.HARD_BASE_SCORE,
												new QuestStateGreaterThanCondition("daily_item", 2, 499)));
		//weekly item quest achievement
		questAchievements.add(new Achievement("quest.special.wiq.5", "Archaeologist",
												getCategory(),  "Finish weekly item quest 5 times", Achievement.HARD_BASE_SCORE,
												new QuestStateGreaterThanCondition("weekly_item", 2, 4)));
		return questAchievements;
	}
	
	@Override
	protected Category getCategory() {
		return Category.QUEST;
	}

}
