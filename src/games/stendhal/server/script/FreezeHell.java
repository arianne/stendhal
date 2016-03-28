/***************************************************************************
 *                 (C) Copyright 2003-2015 - Faiumoni e.V.                 *
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

import games.stendhal.common.tiled.LayerDefinition;
import games.stendhal.common.tiled.StendhalMapStructure;
import games.stendhal.server.core.config.zone.TMXLoader;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.engine.ZoneAttributes;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

import java.io.IOException;
import java.util.List;

import marauroa.common.game.IRPZone;

/**
 * Freezes or thaws the hell.
 */
public class FreezeHell extends ScriptImpl {
	String newColor;
	@Override
	public void execute(final Player admin, final List<String> args) {
		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		
		boolean freeze = true;
		boolean fail = false;
		newColor = null;
		if (args.size() >= 1) {
			switch (args.get(0)) {
			case "freeze":
				freeze = true;
				break;
			case "thaw":
				freeze = false;
				break;
			default:
				fail = true;
			}
			if (args.size() == 2)
				newColor = args.get(1);
		} else {
			fail = true;
		}
		if (fail) {
			sandbox.privateText(admin, "Usage: /script FreezeHell [freeze|thaw]");
			return;
		}
		
		String newMap;
		if (freeze) {
			newMap = "tiled/Level -2/nalwor/frozen_hell.tmx";
		} else {
			newMap = "tiled/Level -2/nalwor/hell.tmx";
		}

		IRPZone zn = world.getRPZone("hell");
		StendhalRPZone zone = (StendhalRPZone) zn;
		StendhalMapStructure map;
		try {
			map = TMXLoader.load(newMap);
		} catch (Exception e) {
			sandbox.privateText(admin, "Failed to load map: " + e);
			return;
		}
		try {
			setAttributes(zone, freeze);
			updateZone(zone, map);
			zone.notifyOnlinePlayers();
		} catch (IOException e) {
			sandbox.privateText(admin, "Failed to update map: " + e);
		}
	}
	
	private void setAttributes(StendhalRPZone zone, boolean freeze) {
		ZoneAttributes attr = zone.getAttributes();
		// attr.put("color_method", "soflight");
		//attr.remove("color");
		if (freeze) {
			System.err.println(newColor);
			/*
			if (newColor != null)
				attr.put("color", newColor);
			else
				attr.put("color", "0088aa");
				*/
			attr.remove("color_method");
			attr.put("blend_method", "truecolor");
		} else {
			//attr.put("color", "882200");
			attr.remove("blend_method");
			attr.put("color_method", "soflight");
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
