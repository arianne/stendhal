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

import java.util.LinkedHashMap;
import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.mapstuff.sign.ShopSign;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.action.SayTextAction;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.npc.condition.PlayerHasItemWithHimCondition;
import games.stendhal.server.maps.nalwor.forest.AssassinRepairerAdder.AssassinRepairer;


/**
 * An NPC that sells special swords for training.
 */
public class DojoSellerNPC implements ZoneConfigurator {

	private static StendhalRPZone dojoZone;

	private final String sellerName = "Akutagawa";
	private AssassinRepairer seller;

	private AssassinRepairerAdder repairerAdder;

	private static final Map<String, Integer> repairableSellPrices = new LinkedHashMap<String, Integer>() {{
		put("training sword", 2100);
	}};

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		dojoZone = zone;

		initNPC();
		initShop();
		initRepairShop();
		initDialogue();
	}

	private void initNPC() {
		repairerAdder = new AssassinRepairerAdder();

		seller = repairerAdder.new AssassinRepairer(sellerName, repairableSellPrices);
		seller.setEntityClass("samurai2npc");
		seller.setIdleDirection(Direction.LEFT);
		seller.setPosition(37, 80);

		dojoZone.add(seller);
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

	private void initShop() {
		final Map<String, Integer> pricesSell = new LinkedHashMap<>();
		for (final String itemName: repairableSellPrices.keySet()) {
			pricesSell.put(itemName, repairableSellPrices.get(itemName));
		}
		pricesSell.put("shuriken", 80);
		pricesSell.put("fire shuriken", 105);

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
		shopSign.setPosition(36, 81);

		dojoZone.add(shopSign);
	}

	/**
	 * If players bring their worn training swords they can get them repaired for half the
	 * price of buying a new one.
	 */
	private void initRepairShop() {
		final Map<String, Integer> repairPrices = new LinkedHashMap<>();
		for (final String itemName: repairableSellPrices.keySet()) {
			repairPrices.put(itemName, repairableSellPrices.get(itemName) / 2);
		}

		repairerAdder.add(seller, repairPrices);
	}
}
