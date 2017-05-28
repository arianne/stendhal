/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.script;

import java.util.List;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.server.game.container.PlayerEntry;
import marauroa.server.game.container.PlayerEntryContainer;

/**
 * Teleports all players to a specified destination
 *
 * @author hendrik
 */
public class TeleportAllPlayers extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		StendhalRPZone targetZone = (StendhalRPZone) SingletonRepository.getRPWorld().getRPZone(args.get(0));
		if (targetZone == null) {
			admin.sendPrivateText(NotificationType.ERROR, "No such zone.");
			return;
		}

		int x = Integer.parseInt(args.get(1));
		int y = Integer.parseInt(args.get(2));

		for (PlayerEntry entry : PlayerEntryContainer.getContainer()) {
			Player player = (Player) entry.object;
			player.teleport(targetZone, x, y, null, player);
		}
	}

}
