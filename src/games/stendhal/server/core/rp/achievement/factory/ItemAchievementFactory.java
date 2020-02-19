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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.entity.npc.condition.PlayerLootedNumberOfItemsCondition;

/**
 * Factory for item related achievements.
 *
 * @author madmetzger
 */
public class ItemAchievementFactory extends AbstractAchievementFactory {

	public static final String ID_MONEY100 = "item.money.100";
	public static final String ID_MONEY10000 = "item.money.10000";
	public static final String ID_MONEY1000000 = "item.money.1000000";
	public static final String ID_RED = "item.set.red";
	public static final String ID_BLUE = "item.set.blue";
	public static final String ID_ELVISH = "item.set.elvish";
	public static final String ID_SHADOW = "item.set.shadow";
	public static final String ID_CHAOS = "item.set.chaos";
	public static final String ID_GOLDEN = "item.set.golden";
	public static final String ID_BLACK = "item.set.black";
	public static final String ID_MAINIO = "item.set.mainio";
	public static final String ID_XENO = "item.set.xeno";
	public static final String ID_CLOAK_DRAGON = "item.cloak.dragon";
	public static final String ID_CHEESE = "item.cheese.2000";
	public static final String ID_HAM = "item.ham.2500";

	public static List<String> getIDs() {
		final List<String> idList = Arrays.asList(
				ID_MONEY100, ID_MONEY10000, ID_MONEY1000000, ID_RED, ID_BLUE,
				ID_ELVISH, ID_SHADOW, ID_CHAOS, ID_GOLDEN, ID_BLACK, ID_MAINIO,
				ID_XENO, ID_CLOAK_DRAGON, ID_CHEESE, ID_HAM
		);

		return idList;
	}

	@Override
	protected Category getCategory() {
		return Category.ITEM;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> itemAchievements = new LinkedList<Achievement>();

		itemAchievements.add(createAchievement(ID_MONEY100, "First Pocket Money", "Loot 100 money from creatures",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(100, "money")));

		itemAchievements.add(createAchievement(ID_MONEY10000, "Goldshower", "Loot 10,000 money",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(10000, "money")));

		itemAchievements.add(createAchievement(ID_MONEY1000000, "You Don't Need it Anymore", "Loot 1000000 money from creatures",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1000000, "money")));

		itemAchievements.add(createAchievement(ID_RED, "Amazon's Menace", "Loot a complete red equipment set",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "red armor", "red helmet", "red cloak", "red legs", "red boots",
						"red shield")));

		itemAchievements.add(createAchievement(ID_BLUE, "Feeling Blue", "Loot a complete blue equipment set",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "blue armor", "blue helmet", "blue striped cloak", "blue legs",
						"blue boots", "blue shield")));

		itemAchievements.add(createAchievement(ID_ELVISH, "Nalwor's Bane", "Loot a complete elvish equipment set",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "elvish armor", "elvish hat", "elvish cloak", "elvish legs",
						"elvish boots", "elvish shield")));

		itemAchievements.add(createAchievement(ID_SHADOW, "Shadow Dweller", "Loot a complete shadow equipment set",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "shadow armor", "shadow helmet", "shadow cloak", "shadow legs",
						"shadow boots", "shadow shield")));

		itemAchievements.add(createAchievement(ID_CHAOS, "Chaotic Looter", "Loot a complete chaos equipment set",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "chaos armor", "chaos helmet", "chaos cloak", "chaos legs",
						"chaos boots", "chaos shield")));

		itemAchievements.add(createAchievement(ID_GOLDEN, "Golden Boy", "Loot a complete golden equipment set",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "golden armor", "golden helmet", "golden cloak", "golden legs",
						"golden boots", "golden shield")));

		itemAchievements.add(createAchievement(ID_BLACK, "Come to the Dark Side", "Loot a complete black equipment set",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "black armor", "black helmet", "black cloak", "black legs",
						"black boots", "black shield")));

		itemAchievements.add(createAchievement(ID_MAINIO, "Excellent Stuff", "Loot a complete mainio equipment set",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "mainio armor", "mainio helmet", "mainio cloak", "mainio legs",
						"mainio boots", "mainio shield")));

		itemAchievements.add(createAchievement(ID_XENO, "A Bit Xenophobic?", "Loot a complete xeno equipment set",
				Achievement.HARD_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "xeno armor", "xeno helmet", "xeno cloak", "xeno legs",
						"xeno boots", "xeno shield")));

		itemAchievements.add(createAchievement(ID_CLOAK_DRAGON, "Dragon Slayer", "Loot all dragon cloaks",
				Achievement.MEDIUM_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(1, "black dragon cloak", "blue dragon cloak", "bone dragon cloak",
						"green dragon cloak", "red dragon cloak")));

		itemAchievements.add(createAchievement(ID_CHEESE, "Cheese Wiz", "Loot 2,000 cheese",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(2000, "cheese")));

		itemAchievements.add(createAchievement(ID_HAM, "Ham Hocks", "Loot 2,500 ham",
				Achievement.EASY_BASE_SCORE, true,
				new PlayerLootedNumberOfItemsCondition(2500, "ham")));

		return itemAchievements;
	}
}
