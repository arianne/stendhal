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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.AbstractAdminScript;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.player.Player;


/**
 * Script for invoking spawning of creature spawn point.
 *
 * Usage: /script SpawnCreature.class <x> <y>
 */
public class SpawnCreature extends AbstractAdminScript {

	@Override
	protected void run(final Player admin, final List<String> args) {
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
		final List<String> zoneSpawns = new ArrayList<>();
		for (final CreatureRespawnPoint p: zone.getRespawnPointList()) {
			zoneSpawns.add("- " + p.getPrototypeCreature().getName() + " (" + p.getX() + "," + p.getY() + ")");
			if (p.getX() == x && p.getY() == y) {
				spawnPoint = p;
			}
		}
		Collections.sort(zoneSpawns);

		final String zoneName = zone.getName();
		if (spawnPoint == null) {
			admin.sendPrivateText(NotificationType.ERROR,
					"Spawn point not found at " + zoneName + " " + x + "," + y
					+ ". Available spawn points: \n"
					+ String.join("\n", zoneSpawns));
			return;
		}

		admin.sendPrivateText("Spawning " + spawnPoint.getPrototypeCreature().getName()
				+ " at " + zoneName + " " + x + "," + y);
		spawnPoint.spawnNow();
	}

	@Override
	protected int getMinParams() {
		return 2;
	}

	@Override
	protected int getMaxParams() {
		return 2;
	}

	@Override
	protected List<String> getParamStrings() {
		return Arrays.asList("<x> <y>");
	}
}
