/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

package games.stendhal.server.entity.npc;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.StackableItem;

import java.util.Map;

/**
 * Represents the behaviour of a NPC who is able to buy items
 * from a player.
 */
public class BuyerBehaviour extends MerchantBehaviour {

	public BuyerBehaviour(StendhalRPWorld world, Map<String, Integer> priceList) {
		super(world, priceList);
	}

	/**
	 * Gives the money for the deal to the player. If the player can't
	 * carry the money, puts it on the ground.
	 * @param player The player who sells
	 */
	protected void payPlayer(Player player) {
		StackableItem money = (StackableItem) world.getRuleManager().getEntityManager().getItem("money");
		money.setQuantity(getCharge(player));
		player.equip(money, true);
	}

	/**
	 * Transacts the deal that has been agreed on earlier via
	 * setChosenItem() and setAmount().
	 * @param seller The NPC who buys
	 * @param player The player who sells
	 * @return true iff the transaction was successful, that is when the
	 *              player has the item(s).
	 */
	public boolean transactAgreedDeal(SpeakerNPC seller, Player player) {
		if (player.drop(chosenItem, amount)) {
			payPlayer(player);
			seller.say("Thanks! Here is your money.");
			return true;
		} else {
			seller.say("Sorry! You don't have enough " + chosenItem + ".");
			return false;
		}
	}
}