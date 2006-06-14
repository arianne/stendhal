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
import java.util.Set;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * Represents the behaviour of a NPC who is able to buy items
 * from a player.
 */
public class BuyerBehaviour {
	protected StendhalRPWorld world;
	
	protected Map<String, Integer> priceList;

	protected String chosenItem;
	
	protected int amount = 0;

	public BuyerBehaviour(StendhalRPWorld world, Map<String, Integer> priceList) {
		this.priceList = priceList;
	}

	/**
	 * Returns a set of the names of all items that the NPC buys.
	 * @return the accepted items
	 */
	public Set<String> acceptedItems() {
		return priceList.keySet();
	}

	/**
	 * Checks whether the NPC buys the specified item.
	 * @param item the name of the item
	 * @return true iff the NPC buys the item
	 */
	public boolean hasItem(String item) {
		return priceList.containsKey(item);
	}

	/**
	 * Returns the price of one unit of a given item.
	 * @param item the name of the item
	 * @return the unit price
	 */
	protected int getUnitPrice(String item) {
		return priceList.get(item);
	}

	public void setAmount(String text) {
		try {
			amount = Integer.parseInt(text);
		} catch (Exception e) {
			amount = 1;
		}
	}

	protected int getCharge(Player player) {
		if (chosenItem == null) {
			return 0;
		} else {
			return amount * getUnitPrice(chosenItem);
		}
	}
	
	// TODO: create RPEntity.equip() with amount parameter.
	public void payPlayer(Player player) {
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
		world.modify(player);
	}

	public boolean onBuy(SpeakerNPC seller, Player player) {
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