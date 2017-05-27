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
package games.stendhal.client.events;

import org.apache.log4j.Logger;

import games.stendhal.client.GameObjects;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.RPEntity;
import games.stendhal.client.gui.trade.TradingController;
import games.stendhal.common.TradeState;
import marauroa.common.game.RPObject;

/**
 * adjust the player to player trade state
 *
 * @author hendrik
 */
class TradeStateChangeEvent extends Event<RPEntity> {
	private static Logger logger = Logger.getLogger(TradeStateChangeEvent.class);

	/**
	 * executes the event
	 */
	@Override
	public void execute() {
		try {
			IEntity partner = findPartner();
			TradeState myState = TradeState.valueOf(event.get("user_trade_state"));
			TradeState partnerState = TradeState.valueOf(event.get("partner_trade_state"));

			TradingController.get().setState(entity, partner, myState, partnerState);
		} catch (RuntimeException e) {
			logger.error("Failed to process trade state change. Event: " + event, e);
		}
	}

	/**
	 * Find the trading partner.
	 *
	 * @return trading partner
	 */
	private IEntity findPartner() {
		int partnerId = event.getInt("partner_id");
		final RPObject.ID partnerEntityID = new RPObject.ID(partnerId, entity.getRPObject().get("zoneid"));
		return GameObjects.getInstance().get(partnerEntityID);
	}
}
