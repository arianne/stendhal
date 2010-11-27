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

import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 * handles the management of player groups.
 *
 * @author hendrik
 */
public class GroupManagementAction implements ActionListener {
	private static Logger logger = Logger.getLogger(GroupManagementAction.class);

	/**
	 * registers the trade action
	 */
	public static void register() {
		CommandCenter.register("group_management", new GroupManagementAction());
	}

	/**
	 * processes the requested action.
	 * 
	 * @param player the caller of the action
	 * @param action the action to be performed
	 */
	public void onAction(final Player player, final RPAction action) {
		String actionStr = action.get("action");
		String params = action.get("params");
		if ((actionStr == null) || (params == null)) {
			logger.warn("missing action attribute in RPAction " + action);
			return;
		}

		if (actionStr.equals("invite")) {
			invite(player, params);
		} else if (actionStr.equals("join")) {
			join(player, params);
		} else if (actionStr.equals("part")) {
			part(player, params);
		} else if (actionStr.equals("kick")) {
			kick(player, params);
		} else {
			unknown(player, params);
		}
	}

	private void invite(Player player, String params) {
		// TODO Auto-generated method stub
		
	}

	private void join(Player player, String params) {
		// TODO Auto-generated method stub
		
	}

	private void part(Player player, String params) {
		// TODO Auto-generated method stub
		
	}

	private void kick(Player player, String params) {
		// TODO Auto-generated method stub
		
	}

	private void unknown(Player player, String params) {
		// TODO Auto-generated method stub
		
	}
}
