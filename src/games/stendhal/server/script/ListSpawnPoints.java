/***************************************************************************
 *                     Copyright © 2023 - Arianne                          *
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
import java.util.LinkedList;
import java.util.List;

import games.stendhal.common.NotificationType;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.AbstractAdminScript;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;
import games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint;
import games.stendhal.server.entity.player.Player;


/**
 * Script to list spawn points located in a zone.
 */
public class ListSpawnPoints extends AbstractAdminScript {

	private List<String> params = Arrays.asList("-zone", "-type");

	private int count;


	@Override
	protected void run(final Player admin, final List<String> args) {
		final int argc = args.size();
		String zonename = null;
		String spawntype = null;
		for (int idx = 0; idx < argc; idx++) {
			final String key = args.get(idx);
			if ("-help".equals(key)) {
				showUsage(admin);
				return;
			}
			if (!params.contains(key)) {
				admin.sendPrivateText(NotificationType.ERROR, "Unknown parameter: " + key);
				return;
			}
			String value = null;
			if (argc > idx + 1) {
				idx++;
				value = args.get(idx);
			}
			if (value == null) {
				admin.sendPrivateText(NotificationType.ERROR, "Missing value for parameter: " + key);
				return;
			}

			if ("-zone".equals(key)) {
				zonename = value;
			} else if ("-type".equals(key)) {
				spawntype = value;
			}
		}

		StendhalRPZone zone;
		if (zonename == null) {
			zone = admin.getZone();
		} else {
			zone = SingletonRepository.getRPWorld().getZone(zonename);
		}
		if (zone == null) {
			admin.sendPrivateText(NotificationType.ERROR, "Unknown zone" + (zonename == null ? "." : ": " + zonename));
			return;
		}
		zonename = zone.getName();

		count = 0;
		boolean unknowntype = true;
		final List<String> spawns = new LinkedList<>();
		if (spawntype == null || "creature".equals(spawntype)) {
			unknowntype = false;
			spawns.addAll(getCreatureSpawns(zone));
		}
		if (spawntype == null || "grower".equals(spawntype)) {
			unknowntype = false;
			spawns.addAll(getGrowerSpawns(zone));
		}
		if (count == 0) {
			if (unknowntype) {
				admin.sendPrivateText(NotificationType.ERROR, "Unknown spawn type: " + spawntype);
			} else {
				admin.sendPrivateText("No" + (spawntype == null ? "" : " " + spawntype) + " spawn points found in " + zonename + ".");
			}
			return;
		}
		spawns.add(0, count + (spawntype == null ? "" : " " + spawntype) + " spawn points found in " + zonename + ":");
		admin.sendPrivateText(String.join("\n", spawns));
	}

	private List<String> getCreatureSpawns(final StendhalRPZone zone) {
		final List<String> spawns = new ArrayList<>();
		for (final CreatureRespawnPoint p: zone.getRespawnPointList()) {
			spawns.add("- " + p.getPrototypeCreature().getName() + " (" + p.getX() + "," + p.getY() + ")");
			count++;
		}
		Collections.sort(spawns);
		if (spawns.size() > 0) {
			spawns.add(0, "Creature spawn points:");
		}
		return spawns;
	}

	private List<String> getGrowerSpawns(final StendhalRPZone zone) {
		final List<String> spawns = new ArrayList<>();
		for (final PassiveEntityRespawnPoint p: zone.getPlantGrowers()) {
			spawns.add("- " + p.getItemName() + " (" + p.getX() + "," + p.getY() + ")");
			count++;
		}
		Collections.sort(spawns);
		if (spawns.size() > 0) {
			spawns.add(0, "Grower spawn points:");
		}
		return spawns;
	}

	@Override
	protected int getMaxParams() {
		return 4;
	}

	@Override
	protected List<String> getParamStrings() {
		return Arrays.asList("[-zone <zone>] [-type <type>]", "-help");
	}

	@Override
	protected List<String> getParamDetails() {
		return Arrays.asList("zone: Zone name.", "type: One of \"creature\" or \"grower\".");
	}
}
