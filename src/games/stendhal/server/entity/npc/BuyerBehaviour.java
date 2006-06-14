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
import games.stendhal.server.entity.item.Money;

import java.util.Iterator;
import java.util.Map;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Represents the behaviour of a NPC who is able to buy items
 * from a player.
 */
public class BuyerBehaviour extends MerchantBehaviour {
	protected StendhalRPWorld world;
	
	public BuyerBehaviour(StendhalRPWorld world, Map<String, Integer> priceList) {
		super(world, priceList);
	}

	// TODO: create RPEntity.equip() with amount parameter.
	protected void payPlayer(Player player) {
		boolean found = false;
		Iterator<RPSlot> it = player.slotsIterator();
		// First try to stack the money on existing money
		while (it.hasNext() && !found) {
			RPSlot slot = it.next();
			for (RPObject object: slot) {
				if (object instanceof Money) {
					((Money) object).add(getCharge(player));
					found = true;
					break;
				}
			}
		}
		if (!found) {
			// The player has no money. Put the money into an empty slot.  
			RPSlot slot = player.getSlot("bag");
			Money money = new Money(getCharge(player));
			slot.assignValidID(money);
			slot.add(money);
		}
		// TODO: if the player can't equip the money, he probably gets nothing.
		// Put money on ground in this case. Better: create RPEntity.equip()
		// with amount parameter and boolean parameter to put stuff on ground. 
		world.modify(player);
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