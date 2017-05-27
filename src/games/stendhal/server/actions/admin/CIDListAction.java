/***************************************************************************
 *                   (C) Copyright 2003-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.actions.admin;

import static games.stendhal.common.constants.Actions.CIDLIST;
import static games.stendhal.common.constants.Actions.TARGET;

import java.util.Map;

import games.stendhal.server.actions.CStatusAction;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

class CIDListAction extends AdministrationAction {

	public static void register() {
		CommandCenter.register(CIDLIST, new CIDListAction(), 5000);
	}

	@Override
	public void perform(final Player player, final RPAction action) {

		if (action.has(TARGET)) {

			final String inputName = action.get(TARGET);
			final Player target = SingletonRepository.getRuleProcessor().getPlayer(inputName);

			if (target == null) {
				player.sendPrivateText("Player \"" + inputName + "\" not found");
				return;
			}

			final Map<String, String> nameList = CStatusAction.nameList;
			final Map<String, String> idList = CStatusAction.idList;

			//Lets use a clean name instead of what ever the admin inputed
			String playerName = target.getName();

			if (nameList.containsKey(playerName)) {
				String tid = nameList.get(playerName);
				if (idList.containsKey(tid)) {
					String group = idList.get(tid);
					player.sendPrivateText("These players are on the same computer: " + group);
					new GameEvent(player.getName(), "cidlist", playerName, group).raise();
				}
			}

		} else {
			player.sendPrivateText("Player name required");
		}

	}

}
