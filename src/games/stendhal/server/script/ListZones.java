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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
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

		// get regions sorted by name
		final Map<String, Set<StendhalRPZone>> regionMap = SingletonRepository.getRPWorld().getRegionMap()
				.entrySet().stream().sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (o1, o2) -> o1, LinkedHashMap::new));

		for (final Entry<String, Set<StendhalRPZone>> region : regionMap.entrySet()) {

			// sort zone names
			final List<StendhalRPZone> zoneList = region.getValue().stream().collect(Collectors.toList());
			Collections.sort(zoneList, (o1, o2) -> o1.getName().compareTo(o2.getName()));

			final List<String> addZones = new LinkedList<>();

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
					sb.append("\n\nRegion: " + region.getKey());
				} else {
					sb.append("Region: " + region.getKey());
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

			admin.sendPrivateText(sb.toString(), true);
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
