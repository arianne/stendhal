/***************************************************************************
 *                   Copyright © 2003-2023 - Arianne                       *
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
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.player.Player;


/**
 * Script for invoking spawning of creature spawn point.
 *
 * Usage: /script SpawnCreature.class <x> <y>
 */
public class SpawnCreature extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() < 2) {
			admin.sendPrivateText(NotificationType.ERROR,
					"Missing parameter: " + getClass().getSimpleName() + ".class <x> <y>");
			return;
		}

		int x;
		int y;
		try {
			x = Integer.parseInt(args.get(0));
		} catch (final NumberFormatException e) {
			admin.sendPrivateText(NotificationType.ERROR, "X coordinate must be a number");
			return;
		}
		try {
			y = Integer.parseInt(args.get(1));
		} catch (final NumberFormatException e) {
			admin.sendPrivateText(NotificationType.ERROR, "Y coordinate must be a number");
			return;
		}

		final StendhalRPZone zone = admin.getZone();
		if (zone == null) {
			admin.sendPrivateText(NotificationType.ERROR, "You are not in a"
					+ " suitable location for spawning creatures");
			return;
		}

		CreatureRespawnPoint spawnPoint = null;
		String zoneSpawns = "";
		for (final CreatureRespawnPoint p: zone.getRespawnPointList()) {
			if (zoneSpawns.equals("")) {
				zoneSpawns = p.getPrototypeCreature().getName() + " (" + p.getX() + "," + p.getY() + ")";
			} else {
				zoneSpawns += ", " + p.getPrototypeCreature().getName() + " (" + p.getX() + "," + p.getY() + ")";
			}

			if (p.getX() == x && p.getY() == y) {
				spawnPoint = p;
			}
		}

		final String zoneName = zone.getName();
		if (spawnPoint == null) {
					+ " " + x + "," + y + ". Available spawn points: " + zoneSpawns);
			return;
		}

		admin.sendPrivateText("Spawning " + spawnPoint.getPrototypeCreature().getName()
				+ " at " + zoneName + " " + x + "," + y);
		spawnPoint.spawnNow();
	}
}
