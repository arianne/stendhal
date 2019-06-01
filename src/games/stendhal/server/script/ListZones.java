/***************************************************************************
 *                   (C) Copyright 2018 - Arianne                          *
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
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * List all registered zone names.
 *
 * Usage:
 * 		/script ListZones.class [filter]
 *
 * TODO: Allow using regular expression?
 */
public class ListZones extends ScriptImpl {

	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() > 1) {
			admin.sendPrivateText(NotificationType.ERROR, "ERROR: Too many arguments.");
			showUsage(admin);
			return;
		}

		final StringBuilder sb = new StringBuilder();

		String filter = null;
		if (!args.isEmpty()) {
			filter = args.get(0).toLowerCase();
		}


		StendhalRPWorld world = SingletonRepository.getRPWorld();
		for (String region : world.getRegions()) {

			final Collection<StendhalRPZone> zoneList = world.getAllZonesFromRegion(region, null, null, null);

			final Set<String> addZones = new TreeSet<>();

			for (final StendhalRPZone zone : zoneList) {
				final String zoneName = zone.getName();
				if (filter == null) {
					addZones.add(zoneName);
				} else if (zoneName.toLowerCase().contains(filter)) {
					addZones.add(zoneName);
				}
			}

			if (!addZones.isEmpty()) {
				if (sb.length() > 0) {
					// add whitespace between regions
					sb.append("\n\nRegion: " + region);
				} else {
					sb.append("Region: " + region);
				}

				for (final String zoneName : addZones) {
					sb.append("\n  " + zoneName);
				}
			}
		}

		if (sb.length() > 0) {
			sb.insert(0, "Found zones:\n");

			// add empty newline at end for clarity
			sb.append("\n");

			admin.sendPrivateText(sb.toString());
		} else {
			if (filter == null) {
				admin.sendPrivateText(NotificationType.WARNING, "WARNING: No zone information found.");
			} else {
				admin.sendPrivateText("No zone names found containing the text \"" + filter + "\".");
			}
		}
	}

	/**
	 * Shows help text.
	 *
	 * @param admin Administrator invoking script
	 */
	private void showUsage(final Player admin) {
		List<String> usage = Arrays.asList(
				"\nUsage:",
				"    /script ListZones.class [filter]",
				"Args:",
				"    filter:\tOnly return zone names containing matching string.");
		admin.sendPrivateText(NotificationType.CLIENT, String.join("\n", usage));
	}
}
