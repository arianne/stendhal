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
package games.stendhal.common;

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
	DEAL_WAITING_FOR_OTHER_DEAL,

	/** a trade was accpeted and completed */
	TRADE_COMPLETED;
}
