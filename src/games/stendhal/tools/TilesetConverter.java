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
package games.stendhal.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;
import tiled.io.TMXMapReader;
import tiled.io.TMXMapWriter;
import tiled.util.BasicTileCutter;

/**
 * A tool for converting tileset mappings.
 *
 * The new mapping is lines in format:
 * <p>
 * [oldtilesetpath]:[tilenumber]:[newtilesetpath]:[tilenumber]
 * <p>
 * These are read from the standard input.
 * <p>
 * Description of the process:
 * <p>
 * <ol>
 * <li>Loads the map
 * <li>Adds any new tilesets defined in the mapping if needed
 * <li>Converts the old tileset mappings to new
 * <li>Removes any unused tilesets from the map
 * <li>Saves the map
 * </ol>
 */
public class TilesetConverter {
	private Mapping mapping = new Mapping();
	/**
	 * For quick lookup by tileset name
	 */
	private HashMap<String, TileSet> setByName = new HashMap<String, TileSet>();

	/**
	 * Helper to make <code>namePattern</code> construction a bit more readable.
	 */
	private String sep =  Pattern.quote(File.separator);
	/**
	 * A pattern for picking the name of the tileset from the image name.
	 * The trailing "dir/image" without ".png"
	 */
	Pattern namePattern = Pattern.compile(".*" + sep + "([^" + sep + "]+"
			+ sep + "[^" + sep + "]+)\\.png$");

	/**
	 * For returning the translated tile information.
	 */
	private static class TileInfo {
		public String file;
		public int index;

		public TileInfo(String file, int index) {
			this.file = file;
			this.index = index;
		}
	}

	/**
	 * A class for keeping the tile translation information
	 */
	private static class Mapping {
		private HashMap<String, HashMap<Integer, TileInfo>> mappings = new HashMap<String, HashMap<Integer, TileInfo>>();
		private HashSet<String> newTilesets = new HashSet<String>();

		/**
		 * Add a new translation mapping.
		 *
		 * @param oldImg path to the old image file
		 * @param oldIndex index of the tile to be translated
		 * @param newImg path to the new image file
		 * @param newIndex index of the translated tile
		 */
		public void addMapping(String oldImg, int oldIndex, String newImg, int newIndex) {
			newTilesets.add(newImg);
			HashMap<Integer, TileInfo> mapping = mappings.get(oldImg);
			if (mapping == null) {
				mapping = new HashMap<Integer, TileInfo>();
				mappings.put(oldImg, mapping);
			}
			mapping.put(oldIndex, new TileInfo(newImg, newIndex));
		}

		/**
		 * Get a translated tile corresponding to an old tile.
		 *
		 * @param oldImg path to the old image file
		 * @param index index of the tile in the image
		 * @return new tile information, or <code>null</code>
		 * if the old tile should be kept
		 */
		public TileInfo getTile(String oldImg, int index) {
			TileInfo result = null;
			HashMap<Integer, TileInfo> mapping = mappings.get(oldImg);
			if (mapping != null) {
				result = mapping.get(index);
			}
			return result;
		}

		/**
		 * Get the new tilesets the translation adds to the map.
		 *
		 * @return an iterable set of image paths
		 */
		public Iterable<String> getNewSets() {
			return newTilesets;
		}
	}

	/**
	 * Check whether a tileset is in use by a map.
	 *
	 * @param map the map to be checked
	 * @param tileset the tileset to be checked
	 * @return true iff the tileset is in use
	 */
	private boolean isUsedTileset(final Map map, final TileSet tileset) {
		for (final Iterator< ? > tiles = tileset.iterator(); tiles.hasNext();) {
			final Tile tile = (Tile) tiles.next();

			for (final MapLayer layer : map) {
				if ((layer instanceof TileLayer) && (((TileLayer) layer).isUsed(tile))) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Remove any tilesets in a map that are not actually in use.
	 *
	 * @param map the map to be broomed
	 */
	private void removeUnusedTilesets(final Map map) {
		for (final Iterator< ? > sets = map.getTileSets().iterator(); sets.hasNext();) {
			final TileSet tileset = (TileSet) sets.next();

			if (!isUsedTileset(map, tileset)) {
				sets.remove();
			}
		}
	}

	/**
	 * Construct a nice name for a tileset based on the image name.
	 * The substring used for the name is specified in <code>namePattern</code>
	 *
	 * @param name image path
	 * @return a human readable tileset name
	 */
	private String constructTilesetName(String name) {
		Matcher matcher = namePattern.matcher(name);

		if (matcher.find()) {
			name = matcher.group(1);
		}
		return name;
	}

	/**
	 * Add all the tilesets that the translation mapping uses to a map.
	 *
	 * @param map the map to add the tilesets to
	 * @throws IOException
	 */
	private void addNewTilesets(Map map) throws IOException {
		// First build up the mapping of old sets
		for (TileSet set : map.getTileSets()) {
			setByName.put(set.getTilebmpFile(), set);
		}

		// then add all missing new sets
		for (String name : mapping.getNewSets()) {
			if (name.equals("")) {
				continue;
			}

			if (!setByName.containsKey(name)) {
				// The tileset's not yet included. Add it to the map
				TileSet set = new TileSet();
				set.setName(constructTilesetName(name));
				BasicTileCutter cutter = new BasicTileCutter(32, 32, 0, 0);
				set.importTileBitmap(name, cutter);

				setByName.put(name, set);
				map.addTileset(set);
			}
		}
	}

	/**
	 * Find the translated tile that corresponds to a tile
	 * in the original tile mapping.
	 *
	 * @param tile The tile to be translated
	 * @return Translated tile
	 */
	Tile translateTile(Tile tile) {
		int id = tile.getId();
		TileSet set = tile.getTileSet();
		TileInfo info = mapping.getTile(set.getTilebmpFile(), id);
		if (info != null) {
			TileSet newSet = setByName.get(info.file);
			tile = newSet.getTile(info.index);
		}

		return tile;
	}

	/**
	 * Translate all the tiles of a layer.
	 *
	 * @param layer the layer to be translated
	 */
	private void translateLayer(MapLayer layer) {
		if (!(layer instanceof TileLayer)) {
			return;
		}
		TileLayer tileLayer = (TileLayer) layer;
		for (int y = 0; y < tileLayer.getHeight(); y++) {
			for (int x = 0; x < tileLayer.getWidth(); x++) {
				Tile tile = tileLayer.getTileAt(x, y);
				if (tile != null) {
					tile = translateTile(tile);
					tileLayer.setTileAt(x, y, tile);
				}
			}
		}
	}

	/**
	 * Translate all the layers of a map.
	 *
	 * @param map the map to be converted
	 */
	private void translateMap(Map map) {
		for (MapLayer layer : map) {
			translateLayer(layer);
		}
	}

	/**
	 * Converts a map file according to the tile mapping.
	 *
	 * @param tmxFile the map to be converted
	 * @throws Exception
	 */
	private void convert(final String tmxFile) throws Exception {
		final File file = new File(tmxFile);

		final String filename = file.getAbsolutePath();
		final Map map = new TMXMapReader().readMap(filename);
		addNewTilesets(map);
		translateMap(map);
		removeUnusedTilesets(map);
		new TMXMapWriter().writeMap(map, filename);
	}

	/**
	 * Load tile mapping information from the standard input.
	 *
	 * @param path The path of the <b>map</b>. Needed for proper
	 * conversion of the tileset paths.
	 * @throws IOException
	 */
	private void loadMapping(String path) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

		// needed for constructing the full path of the tilesets
		File f = new File(path);
		String dir = f.getParent();

		String line;
		while ((line = input.readLine()) != null) {
			String[] elements = line.split(":", -1);
			if (elements.length != 4) {
				System.err.println("Invalid line: '" + line + "'");
			} else {
				int newIndex = 0;
				if (!"".equals(elements[3])) {
					newIndex = Integer.parseInt(elements[3]);
				}

				/*
				 * Oh, yay. Tiled likes to translate the filenames internally
				 * to full paths.
				 * Great fun with java to compare the paths when the system
				 * allows no playing with directories whatsoever. We can't rely
				 * on the current directory being the same as that of the map.
				 * Building the full path from scratch, and hope for the best.
				 */
				String path1 = (new File(dir + File.separator + elements[0])).getCanonicalPath();
				String path2 = (new File(dir + File.separator + elements[2])).getCanonicalPath();

				mapping.addMapping(path1, Integer.parseInt(elements[1]), path2, newIndex);
			}
		}
	}

	public static void main(final String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("usage: java games.stendhal.tools.TilesetConverter <tmx file>");
			return;
		}

		final TilesetConverter converter = new TilesetConverter();
		converter.loadMapping(args[0]);

		converter.convert(args[0]);
	}
}
