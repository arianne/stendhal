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

package games.stendhal.server.entity.npc.behaviour.impl;

import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

/**
 * Represents the behaviour of a NPC who is able to buy items from a player.
 */
public class BuyerBehaviour extends MerchantBehaviour {

	public BuyerBehaviour(Map<String, Integer> priceList) {
		super(priceList);
	}

	/**
	 * Gives the money for the deal to the player. If the player can't carry the
	 * money, puts it on the ground.
	 * 
	 * @param player
	 *            The player who sells
	 */
	protected void payPlayer(Player player) {
		StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem(
				"money");
		money.setQuantity(getCharge(player));
		player.equip(money, true);
	}

	/**
	 * Transacts the deal that has been agreed on earlier via setChosenItem()
	 * and setAmount().
	 * 
	 * @param seller
	 *            The NPC who buys
	 * @param player
	 *            The player who sells
	 * @return true iff the transaction was successful, that is when the player
	 *         has the item(s).
	 */
	@Override
	public boolean transactAgreedDeal(SpeakerNPC seller, Player player) {
		if (player.drop(chosenItemName, getAmount())) {
			payPlayer(player);
			seller.say("Thanks! Here is your money.");
			return true;
		} else {
			seller.say("Sorry! You don't have "
					+ (getAmount() == 1 ? "any" : "that many") + " "
					+ Grammar.plnoun(getAmount(), getChosenItemName()) + ".");
			return false;
		}
	}
}
