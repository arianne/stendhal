/***************************************************************************
 *                     Copyright © 2020-2024 - Arianne                     *
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.sign.ShopSign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.entity.npc.shop.ItemShopInventory;
import games.stendhal.server.entity.npc.shop.ShopType;
import games.stendhal.server.entity.npc.shop.ShopsList;
import games.stendhal.server.maps.nalwor.forest.AssassinRepairerAdder.AssassinRepairer;


/**
 * An NPC that sells special swords for training.
 */
public class DojoSellerNPC implements ZoneConfigurator {

	private static StendhalRPZone dojo;
	private static AssassinRepairer seller;
	private static ItemShopInventory inventory;

	private static List<String> repairables = new ArrayList<String>() {{
		add("training sword");
	}};


	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		dojo = zone;
		inventory = ShopsList.get().get("dojo", ShopType.ITEM_SELL);

		initNPC();
	}

	/**
	 * Sets up NPC.
	 */
	private void initNPC() {
		initRepairShop();
		initSellShop();
		initDialogue();

		seller.setEntityClass("samurai2npc");
		seller.setIdleDirection(Direction.LEFT);
		seller.setPosition(37, 80);
		dojo.add(seller);
	}

	/**
	 * Sets up seller/repairer NPC and repair items prices.
	 *
	 * If players bring their worn training swords they can get them repaired for half the
	 * price of buying a new one.
	 */
	private void initRepairShop() {
		Map<String, Integer> repairableSellPrices = new LinkedHashMap<>();
		final Map<String, Integer> repairPrices = new LinkedHashMap<>();
		for (String itemName: repairables) {
			if (inventory.containsKey(itemName)) {
				int price = inventory.getPrice(itemName);
				repairableSellPrices.put(itemName, price);
				// repairing is half the cost of buying new
				repairPrices.put(itemName, price / 2);
			}
		}
		// initialize NPC repairer
		AssassinRepairerAdder repairerAdder = new AssassinRepairerAdder();
		seller = repairerAdder.new AssassinRepairer("Akutagawa", repairableSellPrices);
		repairerAdder.add(seller, repairPrices);
	}

	/**
	 * Sets up item shop and shop sign.
	 */
	private void initSellShop() {
		final String rejectedMessage = "Only members of the assassin guild can trade here.";

		// can only purchase if carrying assassins id
		final SellerBehaviour sellerBehaviour = new SellerBehaviour(inventory) {
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

		final ShopSign shopSign = new ShopSign("dojo", "Assassins' Dojo Shop", seller.getName()
				+ " sells the following items", true) {
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
		shopSign.setPosition(36, 81);
		dojo.add(shopSign);
	}

	/**
	 * Sets up NPC's dialogue.
	 */
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
}
