package games.stendhal.server.core.events.achievements;

import games.stendhal.server.entity.npc.condition.PlayerLootedNumberOfItemsCondition;
import games.stendhal.server.entity.npc.condition.PlayerProducedNumberOfItemsCondition;

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
		itemAchievements.add(createAchievement("item.money.1000000", "You don't need it anymore", "Loot 1000000 money from creatures", 
				Achievement.HARD_BASE_SCORE,
				new PlayerLootedNumberOfItemsCondition(1000000, "money")));
		itemAchievements.add(createAchievement("item.set.shadow", "Shadow Dweller", "Loot a complete shadow equipment set", 
				Achievement.MEDIUM_BASE_SCORE,
				new PlayerLootedNumberOfItemsCondition(1, "shadow armor", "shadow helmet", "shadow cloak", "shadow legs", "shadow boots", "shadow shield")));
		itemAchievements.add(createAchievement("item.set.chaos", "Chaotic Looter", "Loot a complete chaos equipment set", 
				Achievement.HARD_BASE_SCORE,
				new PlayerLootedNumberOfItemsCondition(1, "chaos armor", "chaos helmet", "chaos cloak", "chaos legs", "chaos boots", "chaos shield")));
		itemAchievements.add(createAchievement("item.set.golden", "Golden Boy", "Loot a complete golden equipment set", 
				Achievement.MEDIUM_BASE_SCORE,
				new PlayerLootedNumberOfItemsCondition(1, "golden armor", "golden helmet", "golden cloak", "golden legs", "golden boots", "golden shield")));
		itemAchievements.add(createAchievement("item.set.black", "Come to the dark side", "Loot a complete black equipment set", 
				Achievement.HARD_BASE_SCORE,
				new PlayerLootedNumberOfItemsCondition(1, "black armor", "black helmet", "black cloak", "black legs", "black boots", "black shield")));
		itemAchievements.add(createAchievement("item.produce.flour", "Jenny's Assistant", "Produce 1000 flour", 
				Achievement.EASY_BASE_SCORE,
				new PlayerProducedNumberOfItemsCondition(1000, "flour")));
		return itemAchievements;
	}

}
