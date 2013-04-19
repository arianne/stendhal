/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
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

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.PlayerLootedNumberOfItemsCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Factory for item related achievements.
 *
 * @author madmetzger
 */
public class ItemAchievementFactory extends AbstractAchievementFactory {

	@Override
	protected Category getCategory() {
		return Category.ITEM;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> itemAchievements = new LinkedList<Achievement>();
		itemAchievements.add(createAchievement("item.money.100", "First pocket money", "Loot 100 money from creatures",
												Achievement.EASY_BASE_SCORE, true,
												new PlayerLootedNumberOfItemsCondition(100, "money")));
		itemAchievements.add(createAchievement("item.money.1000000", "You don't need it anymore", "Loot 1000000 money from creatures",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1000000, "money")));
		itemAchievements.add(createAchievement("item.set.red", "Amazon's Menace", "Loot a complete red equipment set",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "red armor", "red helmet", "red cloak", "red legs", "red boots", "red shield")));
		itemAchievements.add(createAchievement("item.set.blue", "Feeling Blue", "Loot a complete blue equipment set",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "blue armor", "blue helmet", "blue striped cloak", "blue legs", "blue boots", "blue shield")));
		itemAchievements.add(createAchievement("item.set.elvish", "Nalwor's Bane", "Loot a complete elvish equipment set",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "elvish armor", "elvish hat", "elvish cloak", "elvish legs", "elvish boots", "elvish shield")));
		itemAchievements.add(createAchievement("item.set.shadow", "Shadow Dweller", "Loot a complete shadow equipment set",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "shadow armor", "shadow helmet", "shadow cloak", "shadow legs", "shadow boots", "shadow shield")));
		itemAchievements.add(createAchievement("item.set.chaos", "Chaotic Looter", "Loot a complete chaos equipment set",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "chaos armor", "chaos helmet", "chaos cloak", "chaos legs", "chaos boots", "chaos shield")));
		itemAchievements.add(createAchievement("item.set.golden", "Golden Boy", "Loot a complete golden equipment set",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "golden armor", "golden helmet", "golden cloak", "golden legs", "golden boots", "golden shield")));
		itemAchievements.add(createAchievement("item.set.black", "Come to the dark side", "Loot a complete black equipment set",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "black armor", "black helmet", "black cloak", "black legs", "black boots", "black shield")));
		itemAchievements.add(createAchievement("item.set.mainio", "Excellent Stuff", "Loot a complete mainio equipment set",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "mainio armor", "mainio helmet", "mainio cloak", "mainio legs", "mainio boots", "mainio shield")));
		// commented as it is not really a complete equipment set, because the helmet is missing
//		itemAchievements.add(createAchievement("item.set.xeno", "A bit xenophobic?", "Loot a complete xeno equipment set",
//				Achievement.HARD_BASE_SCORE, true,
//				new PlayerLootedNumberOfItemsCondition(1, "xeno armor", "xeno cloak", "xeno legs", "xeno boots", "xeno shield")));

		return itemAchievements;
	}

}
