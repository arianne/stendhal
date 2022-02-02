/***************************************************************************
 *                     Copyright © 2020 - Arianne                          *
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.core.rp.achievement.condition.BoughtNumberOfCondition;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.npc.behaviour.journal.MerchantsRegister;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;


/**
 * Factory for buying & selling items.
 */
public class CommerceAchievementFactory extends AbstractAchievementFactory {

	private static final MerchantsRegister MR = MerchantsRegister.get();

	public static final String[] ITEMS_HAPPY_HOUR = {"beer", "wine"};
	public static final String ID_HAPPY_HOUR = "buy.drink.alcohol";
	public static final int COUNT_HAPPY_HOUR = 100;

	// TODO: this is a WIP
	private final Map<String, Integer> TRADE_ALL_AMOUNTS = new HashMap<String, Integer>() {{
		put("Margaret", 500);
		put("Ilisa", 500);
	}};


	@Override
	protected Category getCategory() {
		return Category.COMMERCE;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final List<Achievement> achievements = new LinkedList<Achievement>();

		achievements.add(createAchievement(
			ID_HAPPY_HOUR, "It's Happy Hour Somewhere", "Purchase 100 bottles of beer & 100 glasses of wine",
			Achievement.EASY_BASE_SCORE, true,
			new BoughtNumberOfCondition(COUNT_HAPPY_HOUR, ITEMS_HAPPY_HOUR)));

		// TODO: WIP
		achievements.add(createAchievement(
			"commerce.buy.all", "Community Supporter", "Spend X amound of money around the world.",
			Achievement.EASY_BASE_SCORE, false,
			new ChatCondition() {
				@Override
				public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
					boolean conditionsMet = true;

					for (final String seller: MR.getSellersNames()) {
						if (!player.has("npc_purchases", seller)) {
							conditionsMet = false;
							break;
						} else {
							final int amount = Integer.parseInt(player.get("npc_purchases", seller));
							if (TRADE_ALL_AMOUNTS.containsKey(seller)) {
								if (amount < TRADE_ALL_AMOUNTS.get(seller)) {
									conditionsMet = false;
									break;
								}
							} else if (amount < 1000) {
								conditionsMet = false;
								break;
							}
						}
					}

					return conditionsMet;
				}
			}));

		return achievements;
	}
}
