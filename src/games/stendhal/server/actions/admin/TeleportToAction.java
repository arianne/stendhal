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

import static games.stendhal.common.constants.Actions.TARGET;
import static games.stendhal.common.constants.Actions.TELEPORTTO;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

public class TeleportToAction extends AdministrationAction {



	public static void register() {
		CommandCenter.register(TELEPORTTO, new TeleportToAction(), 300);
	}

	@Override
	public void perform(final Player player, final RPAction action) {
		if (action.has(TARGET)) {
			final String name = action.get(TARGET);
			RPEntity teleported = SingletonRepository.getRuleProcessor().getPlayer(name);

			if (teleported == null) {
				teleported = SingletonRepository.getNPCList().get(name);
				if (teleported == null) {

					final String text = "Player \"" + name + "\" not found";
					player.sendPrivateText(text);
					logger.debug(text);
					return;
				}
			}

			final StendhalRPZone zone = teleported.getZone();
			final int x = teleported.getX();
			final int y = teleported.getY();

			player.teleport(zone, x, y, null, player);
			new GameEvent(player.getName(), TELEPORTTO, action.get(TARGET), zone.getName(), Integer.toString(x), Integer.toString(y)).raise();
		}
	}

}
