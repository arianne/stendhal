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
package games.stendhal.server.events;

import games.stendhal.common.TradeState;
import games.stendhal.common.constants.Events;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.Definition.Type;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

/**
 * The state of a trade has changed
 *
 * @author hendrik
 */
public class TradeStateChangeEvent extends RPEvent {

	/**
	 * Creates the rpclass.
	 */
	public static void generateRPClass() {
		final RPClass rpclass = new RPClass(Events.TRADE_STATE_CHANGE);
		rpclass.add(DefinitionClass.ATTRIBUTE, "partner_id", Type.INT);
		rpclass.add(DefinitionClass.ATTRIBUTE, "user_trade_state", Type.STRING);
		rpclass.add(DefinitionClass.ATTRIBUTE, "partner_trade_state", Type.STRING);
	}

	/**
	 * Creates a new trade state change event.
	 *
	 * @param partnerId  id of partner
	 * @param userTradeState    my state of the trade
	 * @param partnerTradeState the state of the partner
	 */
	public TradeStateChangeEvent(int partnerId, TradeState userTradeState, TradeState partnerTradeState) {
		super(Events.TRADE_STATE_CHANGE);
		put("partner_id", partnerId);
		put("user_trade_state", userTradeState.toString());
		put("partner_trade_state", partnerTradeState.toString());
	}

}
