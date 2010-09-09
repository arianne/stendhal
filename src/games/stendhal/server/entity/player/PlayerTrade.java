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

package games.stendhal.server.entity.player;

import games.stendhal.server.core.engine.SingletonRepository;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * handles player to player trade
 *
 * @author hendrik
 */
class PlayerTrade {

	/**
	 * the state of the trading.
	 */
	public enum TradeState {

		/** there is no active trade at the moment */
		NO_ACTIVE_TRADE,

		/** Offering to begin a trade */
		OFFERING_TRADE,

		/** making offers in an active trade */
		MAKING_OFFERS,

		/** i completed my offer, waiting for the other party to confirm */
		LOCKED,

		/** waiting for the other player to click deal.*/
		DEAL_WAITING_FOR_OTHER_DEAL;
	}

	private static Logger logger = Logger.getLogger(PlayerTrade.class);

	private static final List<TradeState> LOCKED_STATES = Arrays.asList(TradeState.LOCKED, TradeState.DEAL_WAITING_FOR_OTHER_DEAL);

	private String partnerName;

	private Player player;

	private TradeState tradeState;

	/**
	 * creates a new PlayerTrade object
	 *
	 * @param player the player for which this objects manages trades
	 */
	public PlayerTrade(Player player) {
		this.player = player;
	}

	/**
	 * checks if this player may offer trade to another player
	 *
	 * @param partner Player to offer a trade to
	 * @return <code>true</code>, if the trade may be offered; <code>false</code> otherwise.
	 */
	private boolean checkIfTradeMayBeOffered(Player partner) {
		if (!player.nextTo(partner)) {
			player.sendPrivateText("You are too far away to start trading with " + partner.getName());
			return false;
		}

		if (partner.getIgnore(player.getName()) != null) {
			return false;
		}

		if ((player.getAwayMessage() != null) 
			|| (partner.getTradeState() != TradeState.NO_ACTIVE_TRADE)) {
			player.sendPrivateText("Sorry, " + partner.getName() + " is busy.");
			return false;
		}

		// TODO: check grumpy
		return true;
	}

	/**
	 * checks if there is already a pending offer to start a trading session
	 *
	 * @param partner partner
	 * @return true, if there is a pending trade session
	 */
	private boolean checkPendingTradeOffer(Player partner) {
		if (partner.getTradeState() == TradeState.OFFERING_TRADE) {
			return player.getName().equals(partner.getTradePartner());
		}
		return false;
	}

	/**
	 * actually starts a trading session
	 *
	 * @param partner partner for the trade
	 */
	protected void startTrade(Player partner) {
		this.partnerName = partner.getName();
		this.tradeState = TradeState.MAKING_OFFERS;
		partner.startTrade(player);
		// TODO: tell client
	}


	/**
	 * offers the other player to start a trading session
	 * 
	 * @param partner to offer the trade to
	 */
	public void offerTrade(Player partner) {
		if (checkPendingTradeOffer(partner)) {
			startTrade(partner);
		}

		if (!checkIfTradeMayBeOffered(partner)) {
			return;
		}

		cancelTrade();
		player.sendPrivateText("You offered to trade with " + partner.getName() + ".");
		partner.sendPrivateText(player.getName() + " wants to trade with you. Right click on " + player.getName() + " and select \"Trade\" to start a trading session.");
		this.partnerName = partner.getName();
	}


	/**
	 * cancels a trade and tells the player and partner about it.
	 */
	public void cancelTrade() {
		if (tradeState == TradeState.NO_ACTIVE_TRADE) {
			return;
		}

		player.sendPrivateText("You canceled the trade");
		Player partner = SingletonRepository.getRuleProcessor().getPlayer(partnerName);
		if (partner != null) {
			partner.sendPrivateText(player.getName() + " canceled the trade with you.");
			partner.cancelTradeInternally(player.getName());
		}
		cancelTradeInternally(partnerName);
	}


	/**
	 * internally cancels a trade without telling the players
	 *
	 * @param partnerName name of partner to make sure the correct trade is canceled
	 */
	protected void cancelTradeInternally(String partnerName) {
		if ((this.partnerName == null) || this.partnerName.equals(partnerName)) {
			partnerName = null;
			tradeState = TradeState.NO_ACTIVE_TRADE;
			// TODO: move items back
			// TODO: tell client about cancel
		}
	}


	/**
	 * marks an item offer as complete. If both players have marked their item offers
	 * as complete, the trade is executed.
	 */
	public void lockItemOffer() {
		if (tradeState != TradeState.OFFERING_TRADE) {
			return;
		}

		tradeState = TradeState.LOCKED;
		Player partner = SingletonRepository.getRuleProcessor().getPlayer(partnerName);
		if (partner == null) {
			cancelTradeInternally(partnerName);
			return;
		}

		// TODO: tell both clients
	}


	public void deal() {
		if (tradeState != TradeState.LOCKED) {
			player.sendPrivateText("You must lock your offer first.");
			return;
		}

		Player partner = SingletonRepository.getRuleProcessor().getPlayer(partnerName);
		if (partner == null) {
			cancelTradeInternally(partnerName);
			return;
		}
	
		if (partner.getTradeState() == TradeState.DEAL_WAITING_FOR_OTHER_DEAL) {
			player.sendPrivateText("You traded with " + partnerName + ".");
			partner.sendPrivateText("You traded with " + player.getName() + ".");
			// TODO: transferItems();
			// TODO: tell both player about succesful trade
		} else if (partner.getTradeState() == TradeState.LOCKED) {
			player.sendPrivateText("Okay, your trade is almost complete, just waiting for " + partnerName + " to press Deal.");
			// TODO: tell client
		} else if (partner.getTradeState() == TradeState.MAKING_OFFERS) {
			player.sendPrivateText("Your partner must lock his offer first.");
		} else {
			logger.warn("Inconsitent state at \"deal\" in trade of " + player.getName() + " with partner " + partnerName);
			cancelTrade();
		}
	}


	/**
	 * removes the marking of an item offer as complete
	 */
	public void unlockItemOffer() {
		boolean myRes = unlockItemOfferInternally(partnerName);
		boolean otherRes = false;
		
		Player partner = SingletonRepository.getRuleProcessor().getPlayer(partnerName);
		if (partner != null) {
			otherRes = partner.unlockTradeItemOfferInternally(player.getName());
		}
		if (myRes || otherRes) {
			// TODO: tell both clients
		}
	}

	/**
	 * interally unlocks a trade
	 *
	 * @param partnerName name of partner (to make sure the correct trade offer is canceled)
	 * @return true, if a trade was unlocked, false if it was already unlocked
	 */
	boolean unlockItemOfferInternally(String partnerName) {
		if ((this.partnerName == null) || this.partnerName.equals(partnerName)) {
			if (LOCKED_STATES.contains(tradeState)) {
				tradeState = TradeState.OFFERING_TRADE;
				return true;
			}
		}
		return false;
	}

	/**
	 * gets the current state of trading
	 *
	 * @return TradeState
	 */
	public TradeState getTradeState() {
		return tradeState;
	}


	/**
	 * gets the name of the trading partner
	 *
	 * @return name of partner
	 */
	public String getPartnerName() {
		return partnerName;
	}

	// TODO: cancelTrade on logout
	// TODO: move items back on login (so that they don't end up unprotected on the ground on full bag during logout).
}
