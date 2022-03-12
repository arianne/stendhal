/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
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

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.PlayerLootedNumberOfItemsCondition;


/**
 * Factory for item related achievements.
 *
 * @author madmetzger
 */
public class ItemAchievementFactory extends AbstractAchievementFactory {

	public static final String ID_ROYAL = "item.set.royal";
	public static final String ID_MAGIC = "item.set.magic";


	@Override
	protected Category getCategory() {
		return Category.ITEM;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		achievements.add(createAchievement(
			"item.money.100", "First Pocket Money",
			"Loot 100 money",
			Achievement.EASY_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(100, "money")));

		achievements.add(createAchievement(
			"item.money.10000", "Goldshower",
			"Loot 10,000 money",
			Achievement.EASY_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(10000, "money")));

		achievements.add(createAchievement(
			"item.money.100000", "Moving up in the World",
			"Loot 100,000 money",
			Achievement.EASY_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(100000, "money")));

		achievements.add(createAchievement(
			"item.money.1000000", "You Don't Need it Anymore",
			"Loot 1,000,000 money",
			Achievement.HARD_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1000000, "money")));

		achievements.add(createAchievement(
			"item.set.red", "Amazon's Menace",
			"Loot a complete red equipment set",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1,
				"red armor", "red helmet", "red cloak",
				"red legs", "red boots", "red shield")));

		achievements.add(createAchievement(
			"item.set.blue", "Feeling Blue",
			"Loot a complete blue equipment set",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1,
				"blue armor", "blue helmet", "blue striped cloak",
				"blue legs", "blue boots", "blue shield")));

		achievements.add(createAchievement(
			"item.set.elvish", "Nalwor's Bane",
			"Loot a complete elvish equipment set",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1,
				"elvish armor", "elvish hat", "elvish cloak",
				"elvish legs", "elvish boots", "elvish shield")));

		achievements.add(createAchievement(
			"item.set.shadow", "Shadow Dweller",
			"Loot a complete shadow equipment set",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1,
				"shadow armor", "shadow helmet", "shadow cloak",
				"shadow legs", "shadow boots", "shadow shield")));

		achievements.add(createAchievement(
			"item.set.chaos", "Chaotic Looter",
			"Loot a complete chaos equipment set",
			Achievement.HARD_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1,
				"chaos armor", "chaos helmet", "chaos cloak",
				"chaos legs", "chaos boots", "chaos shield")));

		achievements.add(createAchievement(
			"item.set.golden", "Golden Boy",
			"Loot a complete golden equipment set",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1,
				"golden armor", "golden helmet", "golden cloak",
				"golden legs", "golden boots", "golden shield")));

		achievements.add(createAchievement(
			"item.set.black", "Come to the Dark Side",
			"Loot a complete black equipment set",
			Achievement.HARD_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1,
				"black armor", "black helmet", "black cloak",
				"black legs", "black boots", "black shield")));

		achievements.add(createAchievement(
			"item.set.mainio", "Excellent Stuff",
			"Loot a complete mainio equipment set",
			Achievement.HARD_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1,
				"mainio armor", "mainio helmet", "mainio cloak",
				"mainio legs", "mainio boots", "mainio shield")));

		achievements.add(createAchievement(
			"item.set.xeno", "A Bit Xeno?",
			"Loot a complete xeno equipment set",
			Achievement.HARD_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1,
				"xeno armor", "xeno helmet", "xeno cloak",
				"xeno legs", "xeno boots", "xeno shield")));

		achievements.add(createAchievement(
			"item.cloak.dragon", "Dragon Slayer",
			"Loot all dragon cloaks",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1,
				"black dragon cloak", "blue dragon cloak", "bone dragon cloak",
				"green dragon cloak", "red dragon cloak")));

		achievements.add(createAchievement(
			"item.cheese.2000", "Cheese Wiz",
			"Loot 2,000 cheese",
			Achievement.EASY_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(2000, "cheese")));

		achievements.add(createAchievement(
			"item.ham.2500", "Ham Hocks",
			"Loot 2,500 ham",
			Achievement.EASY_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(2500, "ham")));

		achievements.add(createAchievement(
			ID_ROYAL, "Royally Endowed",
			"Loot a complete royal equipment set",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1,
				"royal armor", "royal helmet", "royal cloak",
				"royal legs", "royal boots", "royal shield")));

		achievements.add(createAchievement(
			ID_MAGIC, "Magic Supplies",
			"Loot a complete magic equipment set",
			Achievement.MEDIUM_BASE_SCORE, true,
			new PlayerLootedNumberOfItemsCondition(1,
				"magic plate armor", "magic chain helmet", "magic cloak",
				"magic plate legs", "magic plate boots", "magic plate shield")));

		return achievements;
	}
}
