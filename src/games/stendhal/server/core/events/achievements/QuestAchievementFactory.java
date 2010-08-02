package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;
import games.stendhal.server.entity.npc.condition.QuestStateGreaterThanCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
/**
 * Factory for quest achievements
 *  
 * @author madmetzger
 */
public class QuestAchievementFactory extends AchievementFactory {
	
	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> questAchievements = new LinkedList<Achievement>();
		//daily monster quest achievements
		questAchievements.add(createAchievement("quest.special.dmq.10", "Semos' Protector", "Finish daily monster quest 10 times",
												Achievement.EASY_BASE_SCORE, new QuestStateGreaterThanCondition("daily", 2, 9)));
		questAchievements.add(createAchievement("quest.special.dmq.50", "Semos' Guardian", "Finish daily monster quest 50 times", 
												Achievement.EASY_BASE_SCORE, new QuestStateGreaterThanCondition("daily", 2, 49)));
		questAchievements.add(createAchievement("quest.special.dmq.100", "Semos' Hero", "Finish daily monster quest 100 times", 
												Achievement.MEDIUM_BASE_SCORE, new QuestStateGreaterThanCondition("daily", 2, 99)));
		questAchievements.add(createAchievement("quest.special.dmq.250", "Semos' Champion", "Finish daily monster quest 250 times", 
												Achievement.MEDIUM_BASE_SCORE, new QuestStateGreaterThanCondition("daily", 2, 249)));
		questAchievements.add(createAchievement("quest.special.dmq.500", "Semos' Vanquisher", "Finish daily monster quest 500 times", 
												Achievement.HARD_BASE_SCORE, new QuestStateGreaterThanCondition("daily", 2, 499)));
		//daily item quest achievements
		questAchievements.add(createAchievement("quest.special.diq.10", "Ados' Supporter", "Finish daily item quest 10 times", 
												Achievement.EASY_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 9)));
		questAchievements.add(createAchievement("quest.special.diq.50", "Ados' Provider", "Finish daily item quest 50 times", 
												Achievement.EASY_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 49)));
		questAchievements.add(createAchievement("quest.special.diq.100", "Ados' Supplier", "Finish daily item quest 100 times",
												Achievement.MEDIUM_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 99)));
		questAchievements.add(createAchievement("quest.special.diq.250", "Ados' Stockpiler", "Finish daily item quest 250 times", 
												Achievement.MEDIUM_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 249)));
		questAchievements.add(createAchievement("quest.special.diq.500", "Ados' Hoarder", "Finish daily item quest 500 times", 
												Achievement.HARD_BASE_SCORE, new QuestStateGreaterThanCondition("daily_item", 2, 499)));
		//weekly item quest achievement
		questAchievements.add(createAchievement("quest.special.wiq.5", "Archaeologist", "Finish weekly item quest 5 times", 
												Achievement.HARD_BASE_SCORE, new QuestStateGreaterThanCondition("weekly_item", 2, 4)));
		//elf princess quest achievement
		questAchievements.add(createAchievement("quest.special.rhosyd.25", "Faiumoni's Casanova", "Finish elf princess quest 25 times", 
												Achievement.MEDIUM_BASE_SCORE, new QuestStateGreaterThanCondition("elf_princess", 2, 24)));
		//ultimate collector quest achievement
		questAchievements.add(createAchievement("quest.special.collector", "Ultimate Collector", "Finish ultimate collector quest", 
												Achievement.HARD_BASE_SCORE, new QuestCompletedCondition("ultimate_collector")));
		return questAchievements;
	}

	@Override
	protected Category getCategory() {
		return Category.QUEST;
	}

}
