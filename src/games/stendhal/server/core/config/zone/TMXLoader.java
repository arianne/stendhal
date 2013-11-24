/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.config.zone;

import games.stendhal.common.tiled.LayerDefinition;
import games.stendhal.common.tiled.StendhalMapStructure;
import games.stendhal.common.tiled.TileSetDefinition;

import java.util.List;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.ObjectGroup;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;
import tiled.io.TMXMapReader;

/**
 * Loads a TMX file to server so it can understand: a) The objects layer b) The
 * collision layer c) The protection layer. d) All the layers that are sent to
 * client e) The tileset data that is also transfered to client f) A preview of
 * the zone for the minimap.
 * 
 * Client would get the layers plus the tileset info.
 * 
 * @author miguel
 * 
 */
public class TMXLoader {


	public StendhalMapStructure readMap(final String filename) throws Exception {
		
		TMXMapReader reader = new TMXMapReader();
		Map map = reader.readMap(filename);

		StendhalMapStructure stendhalMap = new StendhalMapStructure(map.getWidth(), map.getHeight());

		for (TileSet tileSet : map.getTileSets()) {
			String source = tileSet.getTilebmpFile();
			source = "../../" + source.substring(source.indexOf("tileset"));
			stendhalMap.addTileset(new TileSetDefinition(tileSet.getName(), source, tileSet.getFirstTile().getId()));
		}

		for (MapLayer mapLayer : map) {
			if (mapLayer instanceof TileLayer) {
				TileLayer tileLayer = ((TileLayer) mapLayer);
				
				final LayerDefinition layer = new LayerDefinition(mapLayer.getWidth(), mapLayer.getHeight());
				layer.setName(tileLayer.getName());
				
				for (int y = 0; y < mapLayer.getHeight(); y++) {
					for (int x = 0; x < mapLayer.getWidth(); x++) {
						Tile tile = tileLayer.getTileAt(x, y);
						if (tile != null) {
							layer.set(x, y, tile.getId());
						}
					}
				}
				stendhalMap.addLayer(layer);
			} else if (mapLayer instanceof ObjectGroup) {
				// TODO: process ObjectGroup
			}
		}
		return stendhalMap;
	}

	public static void main(final String[] args) throws Exception {
		System.out.println("Test: loading map");

		StendhalMapStructure map = null;
		/*
		 * long start=System.currentTimeMillis(); for(int i=0;i<90;i++) {
		 * map=new TMXLoader().readMap("tiled/interiors/abstract/afterlife.tmx");
		 * map=new TMXLoader().readMap("tiled/Level 0/ados/city_n.tmx");
		 * map=new TMXLoader().readMap("tiled/Level 0/semos/city.tmx");
		 * map=new TMXLoader().readMap("tiled/Level 0/nalwor/city.tmx");
		 * map=new TMXLoader().readMap("tiled/Level 0/orril/castle.tmx");
		 * }
		 * 
		 * System.out.println("Time ellapsed (ms): " + (System.currentTimeMillis()-start)); /
		 */
		map = new TMXLoaderTileBased().readMap("tiled/Level 0/semos/village_w.tmx");
		map.build();
		System.out.printf("MAP W: %d H:%d\n", map.getWidth(), map.getHeight());
		final List<TileSetDefinition> tilesets = map.getTilesets();
		for (final TileSetDefinition set : tilesets) {
			System.out.printf("TILESET firstGID: '%d' name: '%s'\n",
					set.getFirstGid(), set.getSource());
		}

		final List<LayerDefinition> layers = map.getLayers();
		for (final LayerDefinition layer : layers) {
			System.out.printf("LAYER name: %s\n", layer.getName());
			final int w = layer.getWidth();
			final int h = layer.getHeight();

			for (int y = 0; y < h; y++) {
				for (int x = 0; x < w; x++) {
					final int gid = layer.getTileAt(x, y);
					if (x == w - 1) {
						System.out.print(gid);
					} else {
						System.out.print(gid + ",");
					}
					
				}
				System.out.println();
			}
		}

	}

	public static StendhalMapStructure load(final String filename) throws Exception {
		return new TMXLoaderTileBased().readMap(filename);
	}
}
