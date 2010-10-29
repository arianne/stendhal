package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.condition.PlayerLootedNumberOfItemsCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class ItemAchievementFactory extends AchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.ITEM;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> itemAchievements = new LinkedList<Achievement>();
		itemAchievements.add(createAchievement("item.money.100", "First pocket money", "Loot 100 money from creatures", 
												Achievement.EASY_BASE_SCORE,
												new PlayerLootedNumberOfItemsCondition(100, "money")));
		return itemAchievements;
	}

}
