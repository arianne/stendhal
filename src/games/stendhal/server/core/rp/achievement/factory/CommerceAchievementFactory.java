/***************************************************************************
 *                    Copyright © 2003-2024 - Arianne                      *
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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rp.HOFScore;
import games.stendhal.server.core.rp.achievement.Achievement;
import games.stendhal.server.core.rp.achievement.Category;
import games.stendhal.server.core.rp.achievement.condition.BoughtNumberOfCondition;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.HasEarnedTotalMoneyCondition;
import games.stendhal.server.entity.npc.condition.HasSpentMoneyCondition;
import games.stendhal.server.entity.player.Player;


/**
 * Factory for buying & selling items.
 */
public class CommerceAchievementFactory extends AbstractAchievementFactory {

	private static final Logger logger = Logger.getLogger(CommerceAchievementFactory.class);

	public static final String ID_HAPPY_HOUR = "buy.drink.alcohol";
	public static final String ID_SELL_20K = "commerce.sell.20k";
	public static final String ID_BUY_ALL = "commerce.buy.all";

	// NPCs involved in "Community Supporter"
	public static final Map<String, Integer> TRADE_ALL_AMOUNTS = new HashMap<String, Integer>() {{
		put("Adena", 500);
		put("Akutagawa", 1000);
		put("Aldrin", 2000);
		put("Barbarus", 400); // 1 pick
		put("Carmen", 2000);
		put("Coralia", 500);
		put("D J Smith", 4000);
		put("Dale", 500);
		put("Diehelm Brui", 1000);
		put("Dr. Feelgood", 8000);
		put("Erodel Bmud", 20000);
		put("Fleur", 1000);
		put("Haizen", 10000);
		put("Hazel", 16000);
		put("Ilisa", 4000);
		put("Jenny", 1000);
		put("Jimbo", 2000);
		put("Jynath", 16000);
		put("Karl", 50);
		put("Kendra Mattori", 16000);
		put("Laura", 2000);
		put("Lorithien", 10000);
		put("Margaret", 1000);
		put("Mayor Chalmers", 10000);
		put("Mia", 2000);
		put("Mirielle", 20000);
		put("Mrotho", 2500);
		put("Nishiya", 60); // 2 sheep
		put("Old Mother Helena", 2500);
		put("Orchiwald", 9000);
		put("Ouchit", 400);
		put("Philomena", 200);
		put("Ruarhi", 2000);
		put("Sam", 600);
		put("Sara Beth", 2500);
		put("Sarzina", 17000);
		put("Sue", 1000);
		put("Trillium", 2500);
		put("Wanda", 20000);
		put("Wrvil", 300);
		put("Wrviliza", 200);
		put("Xhiphin Zohos", 12000);
		put("Xin Blanca", 190); // 1 of each item
		put("Xoderos", 570); // 1 of each item
	}};


	@Override
	protected Category getCategory() {
		return Category.COMMERCE;
	}

	@Override
	public Collection<Achievement> createAchievements() {
		final LinkedList<Achievement> achievements = new LinkedList<Achievement>();

		achievements.add(createAchievement(
			ID_HAPPY_HOUR, "It's Happy Hour Somewhere",
			"Purchase 100 bottles of beer & 100 glasses of wine",
			HOFScore.EASY, true,
			new BoughtNumberOfCondition(100, Arrays.asList("beer", "wine"))));

		achievements.add(createAchievement(
			ID_SELL_20K, "Traveling Peddler",
			"Make 20000 money in sales to NPCs",
			HOFScore.EASY, true,
			new HasEarnedTotalMoneyCondition(20000)));

		achievements.add(createAchievement(
			ID_BUY_ALL, "Community Supporter",
			"Spend money around the world",
			HOFScore.MEDIUM, true,
			new HasSpentMoneyCondition(TRADE_ALL_AMOUNTS)));


		// add responses to sellers so player has an idea of how much they need to spend
		SingletonRepository.getCachedActionManager().register(new Runnable() {
			@Override
			public void run() {
				logger.debug("Registering seller responses for Community Supporter achievement ...");
				String csSellers = "";

				final NPCList npcs = NPCList.get();
				for (final String name: TRADE_ALL_AMOUNTS.keySet()) {
					final SpeakerNPC seller = npcs.get(name);
					if (seller != null) {
						seller.add(
							ConversationStates.ATTENDING,
							Arrays.asList("patron", "patronage"),
							null,
							ConversationStates.ATTENDING,
							null,
							new RespondToPurchaseAmountInquiry(TRADE_ALL_AMOUNTS.get(name)));

						// add some info to "help" response
						final String sHelp = seller.getReply("help");
						if (sHelp != null && !sHelp.equals("")) {
							seller.addHelp("Also, you can ask me about #patronage.");
						} else {
							seller.addHelp("You can ask me about #patronage.");
						}

						// logger output
						if (csSellers.length() > 0) {
							csSellers = csSellers + ", ";
						}
						csSellers = csSellers + name;

						seller.addKnownChatOptions("patronage");
					} else {
						logger.warn("Cannot set up NPC " + name
							+ " for \"Community Supporter\" achievement");
					}
				}

				logger.debug("Community Supporter sellers: " + csSellers);
			}
		});

		return achievements;
	}


	private static class RespondToPurchaseAmountInquiry implements ChatAction {
		private int req_purchase = 0;

		protected RespondToPurchaseAmountInquiry(final int amount) {
			req_purchase = amount;
		}

		@Override
		public void fire(final Player player, final Sentence sentence, final EventRaiser seller) {
			int spent = 0;
			final String sellerName = seller.getName();
			if (player.has("npc_purchases", sellerName)) {
				spent = player.getInt("npc_purchases", sellerName);
			}

			if (spent == 0) {
				seller.say("I don't recognize you. Have you purchased from me before?");
			} else if (spent >= req_purchase) {
				seller.say("Thank you for supporting me! Adventurers like you keep this world afloat.");
			} else {
				final double per = (Double.valueOf(spent) / req_purchase) * 100;

				if (per < 25) {
					seller.say("You aren't much of a frequenter here.");
				} else if (per < 50) {
					seller.say("I see you have been coming around once in a while.");
				} else if (per < 75) {
					seller.say("You're getting to be a regular around here, aren't you?");
				} else {
					seller.say("Of course I remember you. How could I forget my favorite customer?");
				}
			}
		}
	}
}
