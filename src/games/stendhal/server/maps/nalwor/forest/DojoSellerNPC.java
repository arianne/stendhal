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
package games.stendhal.server.maps.nalwor.forest;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.BreakableItem;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.sign.ShopSign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.player.Player;


/**
 * An NPC that sells special swords for training.
 */
public class DojoSellerNPC implements ZoneConfigurator {

	private static StendhalRPZone dojoZone;

	private final String sellerName = "Akutagawa";
	private SpeakerNPC seller;

	private static final int swordPrice = 5600;

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		dojoZone = zone;

		initNPC();
		initShop();
		initRepairShop();
		initDialogue();
	}

	private void initNPC() {
		seller = new SpeakerNPC(sellerName);
		seller.setEntityClass("samurainpc");
		seller.setIdleDirection(Direction.LEFT);
		seller.setPosition(37, 80);

		dojoZone.add(seller);
	}

	private void initShop() {
		final Map<String, Integer> pricesSell = new LinkedHashMap<String, Integer>() {{
			put("training sword", swordPrice);
			put("shuriken", 80);
			put("fire shuriken", 105);
		}};

		final ShopList shops = ShopList.get();
		for (final String itemName: pricesSell.keySet()) {
			shops.add("dojosell", itemName, pricesSell.get(itemName));
		}

		final String rejectedMessage = "Only members of the assassin guild can trade here.";

		// can only purchase if carrying assassins id
		final SellerBehaviour sellerBehaviour = new SellerBehaviour(pricesSell) {
			@Override
			public ChatCondition getTransactionCondition() {
				return new PlayerHasItemWithHimCondition("assassins id");
			}

			@Override
			public ChatAction getRejectedTransactionAction() {
				return new SayTextAction(rejectedMessage);
			}
		};
		new SellerAdder().addSeller(seller, sellerBehaviour, false);

		final ShopSign shopSign = new ShopSign("dojosell", "Assassins' Dojo Shop", sellerName + " sells the following items", true) {
			/**
			 * Can only view sign if carrying assassins id.
			 */
			@Override
			public boolean onUsed(final RPEntity user) {
				if (user.isEquipped("assassins id")) {
					return super.onUsed(user);
				} else {
					seller.say(rejectedMessage);
				}

				return true;
			}
		};
		shopSign.setEntityClass("blackboard");
		shopSign.setPosition(37, 81);

		dojoZone.add(shopSign);
	}

	/**
	 * If players bring their worn training swords they can get them repaired for half the
	 * price of buying a new one.
	 */
	private void initRepairShop() {
		final List<String> repairPhrases = Arrays.asList("repair", "fix");

		final ChatCondition needsRepairCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				return getUsedSwordsCount(player) > 0;
			}
		};

		final ChatCondition canAffordRepairsCondition = new ChatCondition() {
			@Override
			public boolean fire(final Player player, final Sentence sentence, final Entity npc) {
				return player.isEquipped("money", getRepairPrice(getUsedSwordsCount(player)));
			}
		};

		final ChatAction sayRepairPriceAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final int usedSwords = getUsedSwordsCount(player);
				final boolean multiple = usedSwords > 1;

				final StringBuilder sb = new StringBuilder("You have " + Integer.toString(usedSwords) + " used training sword");
				if (multiple) {
					sb.append("s");
				}
				sb.append(". I can repair ");
				if (multiple) {
					sb.append("them all");
				} else {
					sb.append("it");
				}
				sb.append(" for " + Integer.toString(getRepairPrice(usedSwords)) + " money. Would you like me to do so?");

				npc.say(sb.toString());
			}
		};

		final ChatAction repairAction = new ChatAction() {
			@Override
			public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				final int swordsCount = getUsedSwordsCount(player);
				player.drop("money", getRepairPrice(swordsCount));

				for (final Item sword: player.getAllEquipped("training sword")) {
					final BreakableItem breakable = (BreakableItem) sword;
					if (breakable.isUsed()) {
						breakable.repair();
					}
				}

				if (swordsCount > 1) {
					npc.say("Done! Your training swords are as good as new.");
				} else {
					npc.say("Done! Your training sword is as good as new.");
				}
			}
		};


		seller.add(ConversationStates.ATTENDING,
				repairPhrases,
				new NotCondition(new PlayerHasItemWithHimCondition("assassins id")),
				ConversationStates.ATTENDING,
				"Only members of the assassins guild can have their #'training swords' repaired.",
				null);

		seller.add(ConversationStates.ATTENDING,
				repairPhrases,
				new AndCondition(
						new PlayerHasItemWithHimCondition("assassins id"),
						new NotCondition(needsRepairCondition)),
				ConversationStates.ATTENDING,
				"You don't have any #'training swords' that need repaired.",
				null);

		seller.add(ConversationStates.ATTENDING,
				repairPhrases,
				new AndCondition(
						new PlayerHasItemWithHimCondition("assassins id"),
						needsRepairCondition),
				ConversationStates.QUESTION_1,
				null,
				sayRepairPriceAction);

		seller.add(ConversationStates.QUESTION_1,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				"Okay. Let me know if you need anything else.",
				null);

		seller.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new NotCondition(needsRepairCondition),
				ConversationStates.ATTENDING,
				"Did you drop your sword?",
				null);

		seller.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						needsRepairCondition,
						new NotCondition(canAffordRepairsCondition)),
				ConversationStates.ATTENDING,
				"I'm sorry, you don't have enough money.",
				null);

		seller.add(ConversationStates.QUESTION_1,
				ConversationPhrases.YES_MESSAGES,
				new AndCondition(
						needsRepairCondition,
						canAffordRepairsCondition),
				ConversationStates.ATTENDING,
				null,
				repairAction);
	}

	private void initDialogue() {
		seller.addGreeting("If you're looking for training equipment, you have come to the right place.");
		seller.addGoodbye();
		seller.addOffer("See my blackboard for what I sell. I can also #repair any used #'training swords' that you have.");
		seller.addJob("I run the assassins' dojo shop where we sell equipment and do #repairs on #'training swords'.");
		seller.addQuest("I don't have any task for you to do. I only #fix and sell equipment.");
		seller.addHelp("If you want to train in the dojo, I recommend that you buy a #'training sword'.");
		seller.addReply("training sword", "My training swords are light and easy to swing. And just because"
				+ " they are made out of wood, doesn't mean that it won't hurt if you get whacked with one.");
	}

	private int getUsedSwordsCount(final Player player) {
		int count = 0;
		for (final Item sword: player.getAllEquipped("training sword")) {
			if (((BreakableItem) sword).isUsed()) {
				count++;
			}
		}

		return count;
	}

	private int getRepairPrice(final int count) {
		return count * (swordPrice / 2);
	}
}
