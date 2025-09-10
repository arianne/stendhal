/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.impl.AbstractAdminScript;
import games.stendhal.server.entity.mapstuff.spawner.CloverSpawner;
import games.stendhal.server.util.TimeUtil;


/**
 * Locates or re-spawns four-leaf clover.
 */
public class ManageClover extends AbstractAdminScript {

	@Override
	protected void run(List<String> args) {
		final String command = args.get(0).toLowerCase(Locale.ENGLISH);
		final CloverSpawner spawner = CloverSpawner.get();
		// re-spawn is always set to midnight
		final String timeToRespawn = TimeUtil.timeUntil(TimeUtil.secondsToMidnight());
		if ("status".equals(command)) {
			String zoneDesc = "not found";
			final StendhalRPZone zone = spawner.getZone();
			if (zone != null) {
				zoneDesc = zone.getName() + " at " + spawner.getX() + "," + spawner.getY();
			}
			final boolean available = spawner.hasPickableClover();
			admin.sendPrivateText("Location: " + zoneDesc + "\nClover available: "
						+ (available ? "yes" : "no") + "\nRespawns in " + timeToRespawn);
		} else if ("respawn".equals(command)) {
			spawner.onTurnReached(0);
			final StendhalRPZone zone = spawner.getZone();
			if (spawner.hasPickableClover() && zone != null) {
				admin.sendPrivateText("Spawned clover on " + zone.getName() + " at " + spawner.getX() + ","
						+ spawner.getY() + "\nRespawns in " + timeToRespawn);
			} else {
				admin.sendPrivateText(NotificationType.ERROR,
						"Failed to spawn clover, will retry in 15 minutes");
			}
		} else {
			// show unmodified command
			admin.sendPrivateText(NotificationType.ERROR, "Unknown command \"" + args.get(0) + "\"");
		}
	}

	@Override
	public int getMinParams() {
		return 1;
	}

	@Override
	public int getMaxParams() {
		return 1;
	}

	@Override
	protected List<String> getParamDetails() {
		return Arrays.asList(
			"status: Retrieves spawner location and clover availability.",
			"respawn: Forces clover to respawn immediately in random location."
		);
	}
}
