/***************************************************************************
 *                 (C) Copyright 2003-2016 - Faiumoni e.V.                 *
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
import java.util.Calendar;
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
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.IRPZone;

/**
 * Freezes the hell for 1st of April. The script should be run <em>before</em>
 * the day, not during it.
 */
public class FreezeHell extends ScriptImpl {
	private static final Logger LOGGER = Logger.getLogger(FreezeHell.class);
	
	@Override
	public void execute(final Player admin, final List<String> args) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.APRIL);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 1);
		long waitTime = cal.getTimeInMillis() - System.currentTimeMillis();
		// Starting the script 1st of April is too late - the event will be
		// scheduled for the next year.
		if (waitTime < 0) {
			cal.add(Calendar.YEAR, 1);
			cal.set(Calendar.MONTH, Calendar.APRIL);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			waitTime = cal.getTimeInMillis() - System.currentTimeMillis();
		}
		// wait time in seconds
		int waitSec = (int) waitTime / 1000;
		LOGGER.info("Scheduling freezing hell in " + waitSec + " seconds.");
		SingletonRepository.getTurnNotifier().notifyInSeconds(waitSec, new TurnListener() {
			@Override
			public void onTurnReached(int currentTurn) {
				freezeOrThaw(true);
			}
		});
	}
	
	/**
	 * The actual freezing and thawing routine.
	 * 
	 * @param freeze <code>true</code> if the hell freezes
	 */
	private void freezeOrThaw(boolean freeze) {
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
				LOGGER.info("Hell just froze, thawing in " + 60 * 60 * 24 + " seconds.");
				// Schedule thawing too
				SingletonRepository.getTurnNotifier().notifyInSeconds(60 * 60 * 24, new TurnListener() {
					@Override
					public void onTurnReached(int currentTurn) {
						freezeOrThaw(false);
					}
				});
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
