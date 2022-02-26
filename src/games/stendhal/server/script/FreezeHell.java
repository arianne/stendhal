/***************************************************************************
 *                 (C) Copyright 2003-2022 - Faiumoni e.V.                 *
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

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.NotificationType;
import games.stendhal.common.tiled.LayerDefinition;
import games.stendhal.common.tiled.StendhalMapStructure;
import games.stendhal.server.core.config.zone.TMXLoader;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.ZoneAttributes;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;

/**
 * Freezes the hell. The script should be run <em>before</em>
 * the day, not during it. For testing it allows optionally specifying the time
 * and duration.
 * <p>
 * First argument is the freezing time (either date time in ISO format, or time in
 * HH:MM[:SS] format. Time before the current moment will be interpreted as the
 * corresponding time of next day. The default is start of April 1, either the
 * current year, or the next year if run after that moment.
 * <p>
 * Second argument is the freezing duration in HH:MM[:SS] format. Default is one
 * day.
 */
public class FreezeHell extends ScriptImpl {
	private static final Logger LOGGER = Logger.getLogger(FreezeHell.class);

	private static final LocalDateTime DEFAULT_FREEZING_TIME = Year.now().atMonth(Month.APRIL).atDay(1).atStartOfDay();
	private static final Duration DEFAULT_FREEZING_DURATION = Duration.ofDays(1);

	@Override
	public void execute(final Player admin, final List<String> args) {
		LocalDateTime startTime;
		LocalDateTime now = LocalDateTime.now();
		startTime = determineStartTime(admin, args, now);
		int waitSec = (int) Duration.between(now, startTime).getSeconds();

		Duration freezingDuration = determineDuration(admin, args);

		String message = "Scheduling freezing hell in " + waitSec
				+ " seconds at " + startTime + ". Freeze for " + freezingDuration + ".";
		admin.sendPrivateText(message);
		LOGGER.info(message);
		SingletonRepository.getTurnNotifier().notifyInSeconds(waitSec, currentTurn -> freezeOrThaw(true, freezingDuration));
	}

	private Duration determineDuration(final Player admin, final List<String> args) {
		if (args.size() > 1) {
			try {
				LocalTime time = LocalTime.parse(args.get(1));
				return Duration.between(LocalTime.MIN, time);
			} catch (DateTimeParseException e) {
				admin.sendPrivateText(e.getMessage());
				throw e;
			}
		}

		return DEFAULT_FREEZING_DURATION;
	}

	private LocalDateTime determineStartTime(final Player admin, final List<String> args,
			LocalDateTime now) {
		LocalDateTime startTime;
		if (!args.isEmpty()) {
			startTime = parseStartTime(admin, args, now);
		} else {
			startTime = DEFAULT_FREEZING_TIME;
		}
		if (startTime.isBefore(now)) {
			startTime = startTime.plusYears(1);
		}
		return startTime;
	}

	private LocalDateTime parseStartTime(Player admin, final List<String> args,
			LocalDateTime now) {
		LocalDateTime startTime;
		try {
			LocalTime time = LocalTime.parse(args.get(0));
			startTime = LocalDate.now().atTime(time);
			if (startTime.isBefore(now)) {
				// Testing - delay by a day
				startTime = startTime.plusDays(1);
			}
		} catch (DateTimeParseException e) {
			try {
				startTime = LocalDateTime.parse(args.get(0));
				if (startTime.isBefore(now)) {
					throw new DateTimeParseException("The specified start time is in the past.", args.get(0), 0);
				}
			} catch (DateTimeParseException ex) {
				admin.sendPrivateText(ex.getMessage());
				throw ex;
			}
		}

		return startTime;
	}

	/**
	 * The actual freezing and thawing routine.
	 *
	 * @param freeze <code>true</code> if the hell freezes
	 */
	private void freezeOrThaw(boolean freeze, Duration freezingDuration) {
		String newMap;
		if (freeze) {
			newMap = "tiled/Level -2/nalwor/frozen_hell.tmx";
		} else {
			newMap = "tiled/Level -2/nalwor/hell.tmx";
		}

		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		IRPZone zn = world.getRPZone("hell");
		StendhalRPZone zone = (StendhalRPZone) zn;
		StendhalMapStructure map;
		try {
			map = TMXLoader.load(newMap);
		} catch (Exception e) {
			LOGGER.error("Failed to load map", e);
			return;
		}
		try {
			updateZone(zone, map);
			setAttributes(zone, freeze);
			zone.notifyOnlinePlayers();
			String msg;
			if (freeze) {
				msg = "Grim Reaper shouts: Why is it suddenly cold here?";
				int seconds = (int) freezingDuration.getSeconds();
				LOGGER.info("Hell just froze, thawing in " + seconds + " seconds.");
				// Schedule thawing too
				SingletonRepository.getTurnNotifier().notifyInSeconds(seconds, currentTurn -> freezeOrThaw(false, Duration.ZERO));
			} else {
				msg = "Grim Reaper shouts: Phew, it's comfortably warm again.";
				LOGGER.info("Hell is back to normal");
			}
			SingletonRepository.getRuleProcessor().tellAllPlayers(NotificationType.PRIVMSG, msg);
		} catch (IOException e) {
			LOGGER.error("Failed to update map", e);
		}
	}

	/**
	 * Change zone attributes depending on the freezing state.
	 *
	 * @param zone hell zone
	 * @param freeze freezing state. <code>true</code> if the hell freezes
	 */
	private void setAttributes(StendhalRPZone zone, boolean freeze) {
		ZoneAttributes attr = zone.getAttributes();
		if (freeze) {
			attr.remove("color_method");
			attr.put("blend_method", "truecolor");
		} else {
			attr.remove("blend_method");
			attr.put("color_method", "softlight");
		}
	}

	/**
	 * Update zone from StendhalMapStructure.
	 *
	 * @param zone zone to be updated
	 * @param map new map data
	 * @throws IOException When encoding the layer data fails
	 */
	private void updateZone(StendhalRPZone zone, StendhalMapStructure map) throws IOException {
		// FIXME: Add some safety checking. As of now the script allows
		// replacing any zone with any tmx, whether the size or collisions match.
		String name = zone.getName();
		zone.addTilesets(name + ".tilesets", map.getTilesets());
		zone.addLayer(name + ".0_floor", map.getLayer("0_floor"));
		zone.addLayer(name + ".1_terrain", map.getLayer("1_terrain"));
		zone.addLayer(name + ".2_object", map.getLayer("2_object"));

		// Roof layers are optional
		loadOptionalLayer(zone, map, "3_roof");
		loadOptionalLayer(zone, map, "4_roof_add");
		// Effect layers are optional too
		loadOptionalLayer(zone, map, "blend_ground");
		loadOptionalLayer(zone, map, "blend_roof");

		zone.addCollisionLayer(name + ".collision", map.getLayer("collision"));
		zone.addProtectionLayer(name + ".protection", map.getLayer("protection"));
	}

	/**
	 * Load an optional layer, if present, to a zone.
	 *
	 * @param zone zone to be updated
	 * @param zonedata new map data
	 * @param layerName name of the layer
	 * @throws IOException When encoding the layer data fails
	 */
	private void loadOptionalLayer(StendhalRPZone zone,
			StendhalMapStructure zonedata, String layerName) throws IOException {
		LayerDefinition layer = zonedata.getLayer(layerName);
		if (layer != null) {
			zone.addLayer(zone.getName() + "." + layerName, layer);
		}
	}
}
