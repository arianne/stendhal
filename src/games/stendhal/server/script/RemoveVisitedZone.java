/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
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

import games.stendhal.common.KeyedSlotUtil;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * drop the specified amount of items from the player.
 *
 * @author hendrik
 */
public class RemoveVisitedZone extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		super.execute(admin, args);

		if (args.size() < 2) {
			admin.sendPrivateText("<player> 'zone_name'");
			return;
		}

		if (args.size() > 2) {
			admin.sendPrivateText("<player> 'zone_name' - and don't forget those quotes if the item name has spaces");
			return;
		}

		final Player player = SingletonRepository.getRuleProcessor().getPlayer(args.get(0));

		if (player == null) {
			admin.sendPrivateText("Player " + args.get(0) + " is not online.");
			return;
		}

		String zoneName = args.get(1).replaceAll(" ", "_");
		if (KeyedSlotUtil.getKeyedSlot(player, "!visited", zoneName) == null) {
			admin.sendPrivateText("Player has not visited that zone");
			return;
		}
		KeyedSlotUtil.setKeyedSlot(player, "!visited", zoneName, null);

		final String msg = "Admin " + admin.getName() + " removed zone " + zoneName
				+ " from player " + player.getName();

		admin.sendPrivateText(msg);

		player.sendPrivateText(msg);
		SingletonRepository.getRuleProcessor().sendMessageToSupporters("JailKeeper", msg);
		new GameEvent(admin.getName(), "adminremovevisited", player.getName(), zoneName).raise();
	}
}
