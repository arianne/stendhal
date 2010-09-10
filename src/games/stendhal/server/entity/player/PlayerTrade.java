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

import games.stendhal.common.TradeState;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.events.TradeStateChangeEvent;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * handles player to player trade
 *
 * @author hendrik
 */
class PlayerTrade {


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
		this.tradeState = TradeState.NO_ACTIVE_TRADE;
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
	}


	/**
	 * offers the other player to start a trading session
	 * 
	 * @param partner to offer the trade to
	 */
	public void offerTrade(Player partner) {
		if (player.getName().equals(partner.getName())) {
			player.sendPrivateText("Sorry, you cannot trade with yourself.");
			return;
		}

		if (checkPendingTradeOffer(partner)) {
			startTrade(partner);
			partner.startTrade(player);
			tellClients();
			return;
		}

		if (!checkIfTradeMayBeOffered(partner)) {
			return;
		}

		cancelTrade();
		player.sendPrivateText("You offered to trade with " + partner.getName() + ".");
		partner.sendPrivateText(player.getName() + " wants to trade with you. Right click on " + player.getName() + " and select \"Trade\" to start a trading session.");
		this.partnerName = partner.getName();
		this.tradeState = TradeState.OFFERING_TRADE;
	}


	/**
	 * cancels a trade because 
	 */
	protected void cancelTradeBecauseOfLogout() {
		if (tradeState == TradeState.NO_ACTIVE_TRADE) {
			return;
		}
		Player partner = SingletonRepository.getRuleProcessor().getPlayer(partnerName);
		if (partner != null) {
			partner.sendPrivateText(player.getName() + " disappeared, cancelling the trade with you.");
			partner.cancelTradeInternally(player.getName());
		}
		partnerName = null;
		// Do not move own items back. This is done on login because the
		// bag might be full and items might end up on the ground.
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
		tellClients();
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
		}
	}


	/**
	 * marks an item offer as complete. If both players have marked their item offers
	 * as complete, the trade is executed.
	 */
	public void lockItemOffer() {
		if (tradeState != TradeState.MAKING_OFFERS) {
			return;
		}

		tradeState = TradeState.LOCKED;
		Player partner = SingletonRepository.getRuleProcessor().getPlayer(partnerName);
		if (partner == null) {
			cancelTradeInternally(partnerName);
		}
		tellClients();
	}


	public void deal() {
		if (tradeState != TradeState.LOCKED) {
			player.sendPrivateText("You must lock your offer first.");
			return;
		}

		Player partner = SingletonRepository.getRuleProcessor().getPlayer(partnerName);
		if (partner == null) {
			cancelTradeInternally(partnerName);
			tellClients();
			return;
		}

		if (partner.getTradeState() == TradeState.DEAL_WAITING_FOR_OTHER_DEAL) {
			player.sendPrivateText("You traded with " + partnerName + ".");
			partner.sendPrivateText("You traded with " + player.getName() + ".");
			// TODO: transferItems();
			cancelTradeInternally(partnerName);
			partner.cancelTradeInternally(player.getName());
		} else if (partner.getTradeState() == TradeState.LOCKED) {
			player.sendPrivateText("Okay, your trade is almost complete, just waiting for " + partnerName + " to press Deal.");
			tradeState = TradeState.DEAL_WAITING_FOR_OTHER_DEAL;
		} else if (partner.getTradeState() == TradeState.MAKING_OFFERS) {
			player.sendPrivateText("Your partner must lock his offer first.");
		} else {
			logger.warn("Inconsitent state at \"deal\" in trade of " + player.getName() + " with partner " + partnerName);
			cancelTrade();
		}

		tellClients();
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
			tellClients();
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


	/**
	 * inform both clients about the current state of trading.
	 */
	private void tellClients() {
		Player partner = SingletonRepository.getRuleProcessor().getPlayer(partnerName);
		if (partner == null) {
			player.addEvent(new TradeStateChangeEvent(-1, tradeState, TradeState.NO_ACTIVE_TRADE));
		} else {
			player.addEvent(new TradeStateChangeEvent(partner.getInt("id"), tradeState, partner.getTradeState()));
			partner.addEvent(new TradeStateChangeEvent(player.getInt("id"), partner.getTradeState(), tradeState));
		}
	}



	// TODO: cancelTrade on logout
	// TODO: cancelTrade on zone change of one of the partners
	// TODO: move items back on login (so that they don't end up unprotected on the ground on full bag during logout).
}
