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
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

import java.util.Map;

/**
 * Represents the behaviour of a NPC who is able to buy items from a player.
 */
public class BuyerBehaviour extends MerchantBehaviour {

	public BuyerBehaviour(final Map<String, Integer> priceList) {
		super(priceList);
	}

	/**
	 * Gives the money for the deal to the player. If the player can't carry the
	 * money, puts it on the ground.
	 *
	 * @param player
	 *            The player who sells
	 */
	protected void payPlayer(final Player player) {
		final StackableItem money = (StackableItem) SingletonRepository.getEntityManager().getItem(
				"money");
		money.setQuantity(getCharge(player));
		player.equipOrPutOnGround(money);
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
	public boolean transactAgreedDeal(final EventRaiser seller, final Player player) {
		if (player.drop(chosenItemName, getAmount())) {
			payPlayer(player);
			seller.say("Thanks! Here is your money.");
			return true;
		} else {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Sorry! You don't have ");
			if (getAmount() == 1) {
				stringBuilder.append("any");
				} else {
				stringBuilder.append("that many");
			}
			
			stringBuilder.append(" ");
			stringBuilder.append(Grammar.plnoun(getAmount(), getChosenItemName()));
			stringBuilder.append(".");
			seller.say(stringBuilder.toString());
			return false;
		}
	}
}
