/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.semos.tavern.market;

import java.math.BigDecimal;
import java.util.List;

import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.item.Money;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.trade.Market;
/**
 * helper class for handling adding and prolonging offers
 * @author madmetzger
 */
public class TradingUtility {

	private static final double FEE_BONUS_CONSTANT = 10;
	private static final double TRADING_FEE_PERCENTAGE = 0.01;
	/** Ratio by which player killers should pay more */
	private static final double TRADING_FEE_PLAYER_KILLER_PENALTY = 0.5;
	static final int MAX_NUMBER_OFF_OFFERS = 3;

	/**
	 * substracts the trading fee from the player depending on the given price
	 *
	 * @param player
	 * @param price
	 * @return true iff player dropped the amount of money
	 */
	public static boolean substractTradingFee(Player player, int price) {
		BigDecimal fee = calculateFee(player, price);
		return player.drop("money", fee.intValue());
	}

	/**
	 * checks if a player can afford the trading fee depending on price
	 *
	 * @param player
	 * @param price
	 * @return true iff player has enough money
	 */
	public static boolean canPlayerAffordTradingFee(Player player, int price) {
		BigDecimal fee = calculateFee(player, price);
		List<Item> allEquipped = player.getAllEquipped("money");
		int ownedMoney = 0;
		for(Item item : allEquipped) {
			Money m = (Money) item;
			ownedMoney += m.getQuantity();
		}
		return fee.intValue() <= ownedMoney;
	}

	/**
	 * calculates the trading fee a player has to pay when selling for a certain price
	 * @param player
	 * @param price
	 * @return the trading fee
	 */
	public static BigDecimal calculateFee(Player player, int price) {
		BigDecimal fee  = BigDecimal.valueOf(price);
		fee = fee.multiply(BigDecimal.valueOf(TRADING_FEE_PERCENTAGE));
		if(player.isBadBoy()) {
			fee = fee.multiply(BigDecimal.valueOf(1 + TRADING_FEE_PLAYER_KILLER_PENALTY));
		}

		BigDecimal feeBonus = BigDecimal.ONE;
		feeBonus = BigDecimal.valueOf(Math.exp(-player.getTradescore()/FEE_BONUS_CONSTANT));
		fee = fee.multiply(feeBonus);

		return fee.max(BigDecimal.ONE);
	}

	/**
	 * checks if a player has not already placed the max number of offers
	 *
	 * @param player
	 * @return true if the player is within the limit, false otherwise
	 */
	public static boolean isPlayerWithinOfferLimit(Player player) {
		return countOffers(player)< MAX_NUMBER_OFF_OFFERS;
	}

	private static int countOffers(Player player) {
		Market shopFromZone = TradeCenterZoneConfigurator.getShopFromZone(player.getZone());
		if(shopFromZone != null) {
			int numberOfOffers = shopFromZone.countOffersOfPlayer(player);
			return numberOfOffers;
		}
		return 0;
	}
}
