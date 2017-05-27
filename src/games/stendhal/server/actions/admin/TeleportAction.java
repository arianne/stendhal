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
import static games.stendhal.common.constants.Actions.TELEPORT;
import static games.stendhal.common.constants.Actions.X;
import static games.stendhal.common.constants.Actions.Y;
import static games.stendhal.common.constants.Actions.ZONE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.core.engine.GameEvent;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPAction;

public class TeleportAction extends AdministrationAction {


	public static void register() {
		CommandCenter.register(TELEPORT, new TeleportAction(), 400);

	}

	@Override
	public void perform(final Player player, final RPAction action) {
		if (action.has(TARGET) && action.has(ZONE) && action.has(X)
				&& action.has(Y)) {
			final String name = action.get(TARGET);
			final Player teleported = SingletonRepository.getRuleProcessor().getPlayer(name);

			if (teleported == null) {
				final String text = "Player \"" + name + "\" not found";
				player.sendPrivateText(text);
				logger.debug(text);
				return;
			}

			// validate the zone-name.
			final IRPZone.ID zoneid = new IRPZone.ID(action.get(ZONE));
			if (!SingletonRepository.getRPWorld().hasRPZone(zoneid)) {
				final String text = "Zone \"" + zoneid + "\" not found.";
				logger.debug(text);
				final String[] zoneparts = action.get(ZONE).split("_");
				List<String> zonematches = new ArrayList<String>();
				for (String zonepart : zoneparts) {
					if((zonepart.length()>2) && !zonepart.equals("int")) {
						if(zonepart.endsWith("s")) {
							zonematches.add(zonepart.substring(0,zonepart.length() -1));
						} else {
							zonematches.add(zonepart);
						}
					}
				}

				final Set<String> zoneNames = new TreeSet<String>();
				for (final IRPZone irpZone : SingletonRepository.getRPWorld()) {
					final StendhalRPZone zone = (StendhalRPZone) irpZone;
					for (String zonematch : zonematches) {
						if (zone.getName().indexOf(zonematch) != -1) {
							zoneNames.add(zone.getName());
							// just one match is enough
							break;
						}
					}
				}
				player.sendPrivateText(text + " Similar zone names: " + zoneNames);
				return;
			}

			final StendhalRPZone zone = (StendhalRPZone) SingletonRepository.getRPWorld().getRPZone(
					zoneid);
			final int x = action.getInt(X);
			final int y = action.getInt(Y);
			new GameEvent(player.getName(), TELEPORT, action.get(TARGET), zone.getName(), Integer.toString(x), Integer.toString(y)).raise();
			teleported.teleport(zone, x, y, null, player);

			SingletonRepository.getJail().grantParoleIfPlayerWasAPrisoner(teleported);
		}
	}

}
