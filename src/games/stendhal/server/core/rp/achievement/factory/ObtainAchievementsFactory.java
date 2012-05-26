package games.stendhal.server.core.rp.achievement.factory;

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.PlayerHasHarvestedNumberOfItemsCondition;
import games.stendhal.server.entity.npc.condition.PlayerGotNumberOfItemsFromWellCondition;
import games.stendhal.server.entity.npc.condition.QuestCompletedCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * factory for obtaining items related achievements.
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
		final List<Achievement> achievements = new LinkedList<Achievement>();
		achievements.add(createAchievement("obtain.wish", "A wish came true", "Get an item from the wishing well",
				Achievement.EASY_BASE_SCORE, true, new PlayerGotNumberOfItemsFromWellCondition(0)));
		achievements.add(createAchievement("obtain.harvest.vegetable", "Farmer", "Harvest 3 of all vegetables that grow in Faiumoni.",
				Achievement.EASY_BASE_SCORE, true, new PlayerHasHarvestedNumberOfItemsCondition(3, "carrot",
						"salad", "broccoli", "cauliflower", "leek", "onion", "courgette", "spinach", "collard", "garlic", "artichoke")));
		//ultimate collector quest achievement
		achievements.add(createAchievement("quest.special.collector", "Ultimate Collector", "Finish ultimate collector quest",
				Achievement.HARD_BASE_SCORE, true, new QuestCompletedCondition("ultimate_collector")));
		return achievements;
	}

}
