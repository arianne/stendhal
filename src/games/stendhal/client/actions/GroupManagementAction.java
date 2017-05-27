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
package games.stendhal.client.actions;

import games.stendhal.client.ClientSingletonRepository;
import marauroa.common.game.RPAction;

/**
 * Manages a player group.
 */
class GroupManagementAction implements SlashAction {
	private GroupMessageAction groupMessageAction;

	/**
	 * creates a new GroupAction
	 *
	 * @param groupMessageAction GroupMessageAction
	 */
	public GroupManagementAction(GroupMessageAction groupMessageAction) {
		this.groupMessageAction = groupMessageAction;
	}

	/**
	 * Execute a chat command.
	 *
	 * @param params
	 *            The formal parameters.
	 * @param remainder
	 *            Line content after parameters.
	 *
	 * @return <code>true</code> if command was handled.
	 */
	@Override
	public boolean execute(final String[] params, final String remainder) {

		final RPAction action = new RPAction();

		if (params[0].equals("message")) {
			groupMessageAction.execute(params, remainder);
			return true;
		}

		action.put("type", "group_management");
		action.put("action", params[0]);
		action.put("params", remainder);

		ClientSingletonRepository.getClientFramework().send(action);

		return true;
	}

	/**
	 * Get the maximum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMaximumParameters() {
		return 1;
	}

	/**
	 * Get the minimum number of formal parameters.
	 *
	 * @return The parameter count.
	 */
	@Override
	public int getMinimumParameters() {
		return 1;
	}
}
