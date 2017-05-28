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
package games.stendhal.server.actions;

import org.apache.log4j.Logger;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.EntityHelper;
import marauroa.common.game.RPAction;

/**
 * handles trade related actions.
 *
 * @author hendrik
 */
public class TradeAction implements ActionListener {
	private static Logger logger = Logger.getLogger(TradeAction.class);

	/**
	 * registers the trade action
	 */
	public static void register() {
		CommandCenter.register("trade", new TradeAction());
	}

	/**
	 * processes the requested action.
	 *
	 * @param player the caller of the action
	 * @param action the action to be performed
	 */
	@Override
	public void onAction(final Player player, final RPAction action) {
		rewriteCommandLine(action);
		String actionStr = action.get("action");
		if (actionStr == null) {
			logger.warn("missing action attribute in RPAction " + action);
			return;
		}

		if (actionStr.equals("offer_trade")) {
			Entity entity = EntityHelper.entityFromTargetName(action.get("target"), player);
			if ((entity == null) || (!(entity instanceof Player))) {
				return;
			}
			player.offerTrade((Player) entity);
		} else if (actionStr.equals("lock")) {
			player.lockTrade();
		} else if (actionStr.equals("unlock")) {
			player.unlockTradeItemOffer();
		} else if (actionStr.equals("deal")) {
			player.dealTrade();
		} else if (actionStr.equals("cancel")) {
			player.cancelTrade();
		}
	}

	/**
	 * rewrite the action in case it was entered on the command line
	 *
	 * @param action RPAction
	 */
	private void rewriteCommandLine(RPAction action) {
		if (action.has("args")) {
			action.put("action", action.get("target"));
			action.put("target", action.get("args"));
		}
	}


}
