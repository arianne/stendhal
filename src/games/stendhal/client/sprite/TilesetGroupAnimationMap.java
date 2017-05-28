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
package games.stendhal.client.sprite;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

/**
 * A group of tileset animation maps. This might normally be called
 * <code>TilesetsAnimationMap</code>, but is less likely to mix-up with
 * <code>TilesetAnimationMap</code>.
 */
public class TilesetGroupAnimationMap {
	/**
	 * The logger.
	 */
	private static final Logger LOGGER = Logger.getLogger(TilesetGroupAnimationMap.class);

	/**
	 * The map of tileset animation maps.
	 */
	private Map<String, TilesetAnimationMap> tilesets;

	/**
	 * Create a map of tileset animation maps.
	 */
	public TilesetGroupAnimationMap() {
		tilesets = new HashMap<String, TilesetAnimationMap>();
	}

	//
	// TilesetGroupAnimationMap
	//

	/**
	 * Acquire a named tileset map. If it does not exists, it will be created.
	 *
	 * @param name
	 *            The name of the tileset.
	 *
	 * @return An tileset animation map.
	 */
	private TilesetAnimationMap acquire(final String name) {
		TilesetAnimationMap map = tilesets.get(name);

		if (map == null) {
			map = new TilesetAnimationMap();
			tilesets.put(name, map);
		}

		return map;
	}

	/**
	 * Add a mapping of a tile index to animation frame indexes.
	 *
	 * <strong>NOTE: The array of frame indexes/delays passed is not copied, and
	 * should not be altered after this is called.</strong>
	 *
	 * @param name
	 *            The name of the tileset.
	 * @param index
	 *            The tile index to map.
	 * @param frameIndexes
	 *            The indexes of frame tiles.
	 * @param frameDelays
	 *            The delays of frame tiles.
	 */
	private void add(final String name, final int index,
			final int[] frameIndexes, final int[] frameDelays) {
		acquire(name).add(index, frameIndexes, frameDelays);
	}

	/**
	 * Add mappings of tile indexes to animation frame indexes. For each frame,
	 * a mapping will be created with the remaining indexes as it's frames (in
	 * order, starting with it's index).
	 *
	 * <strong>NOTE: The array of frame indexes/delays passed is not copied, and
	 * should not be altered after this is called.</strong>
	 *
	 * @param name
	 *            The name of the tileset.
	 * @param frameIndexes
	 *            The indexes of frame tiles.
	 * @param frameDelays
	 *            The delays of frame tiles.
	 */
	private void add(final String name, final int[] frameIndexes,
			final int[] frameDelays) {
		acquire(name).add(frameIndexes, frameDelays);
	}

	/**
	 * Parse and add a configuration line.
	 *
	 * @param line
	 *            The configuration line.
	 *
	 * @see #load(InputStream)
	 */
	private void addConfig(final String line) {

		int defaultDelay;

		StringTokenizer st = new StringTokenizer(line, " \t");

		/*
		 * Tileset name
		 */
		if (!st.hasMoreTokens()) {
			LOGGER.warn("Invalid map entry: " + line);
			return;
		}

		final String name = st.nextToken();

		/*
		 * Tile index
		 */
		if (!st.hasMoreTokens()) {
			LOGGER.error("Invalid map entry: " + line);
			return;
		}

		String index = st.nextToken();
		int pos = index.indexOf('@');
		if (pos != -1) {
			final String val = index.substring(pos + 1);
			index = index.substring(0, pos);

			try {
				defaultDelay = Integer.parseInt(val);
			} catch (final NumberFormatException ex) {
				LOGGER.error("Invalid default delay: " + val);
				return;
			}
		} else {
			defaultDelay = TilesetAnimationMap.DEFAULT_DELAY;
		}

		/*
		 * Frame indexes
		 */
		if (!st.hasMoreTokens()) {
			LOGGER.error("Invalid map entry: " + line);
			return;
		}

		final String frames = st.nextToken();

		/*
		 * Split up frames indexes
		 */
		st = new StringTokenizer(frames, ":");

		final int[] frameIndexes = new int[st.countTokens()];
		final int[] frameDelays = new int[frameIndexes.length];

		for (int i = 0; i < frameIndexes.length; i++) {
			String frameIndex = st.nextToken();

			/*
			 * Custom frame duration?
			 */
			pos = frameIndex.indexOf('@');
			if (pos != -1) {
				final String val = frameIndex.substring(pos + 1);
				frameIndex = frameIndex.substring(0, pos);

				try {
					frameDelays[i] = Integer.parseInt(val);
				} catch (final NumberFormatException ex) {
					LOGGER.error("Invalid delay #" + (i + 1) + " <" + val
							+ ">: " + line);
					return;
				}
			} else {
				frameDelays[i] = defaultDelay;
			}

			/*
			 * Frame index
			 */
			try {
				frameIndexes[i] = Integer.parseInt(frameIndex);
			} catch (final NumberFormatException ex) {
				LOGGER.error("Invalid frame #" + (i + 1) + " <" + frameIndex
						+ ">: " + line);
				return;
			}
		}

		/*
		 * Special '*' case for rotated frames?
		 */
		if (index.equals("*")) {
			add(name, frameIndexes, frameDelays);
		} else {
			try {
				add(name, Integer.parseInt(index), frameIndexes, frameDelays);
			} catch (final NumberFormatException ex) {
				LOGGER.error("Invalid tile index: " + line);
				return;
			}
		}
	}

	/**
	 * Get a named tileset map.
	 *
	 * @param name
	 *            The name of the tileset.
	 *
	 * @return An tileset animation map, or <code>null</code> if one does not
	 *         exists.
	 */
	public TilesetAnimationMap get(final String name) {
		return tilesets.get(name);
	}

	/**
	 * <p>
	 * Load tileset mappings from a file. This doesn't not first clear any
	 * existing entries.
	 * </p>
	 *
	 * <p>
	 * The file format consists of one line per entry. Blank lines and those
	 * starting with '#' (a comment) are ignored. The line format is as follows:<br>
	 * <em>tileset</em> <em>index</em> <em>frame:frame[:frame]...</em>
	 * </p>
	 *
	 * <p>
	 * Spaces may be any whitespace. The <em>index</em> may also be
	 * <code>*</code>, which indicates that an entry should be added using
	 * each frame as a mapped index. The mapped index or frame index(s) maybe be
	 * appended by <code>@</code><em>delay</em>, where <em>delay</em> is
	 * a value in milliseconds of for the duration of the frame (or the default
	 * for all frames, if specified for mapped index).
	 *
	 * @param in
	 *            The input stream.
	 *
	 * @throws IOException
	 *             If an I/O error occurred.
	 */
	public void load(final InputStream in) throws IOException {
		final BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String line;

		while ((line = r.readLine()) != null) {
			if (line.length() == 0) {
				continue;
			}

			/*
			 * Comment? (Only works if in first column)
			 */
			if (line.charAt(0) == '#') {
				continue;
			}

			addConfig(line);
		}
	}
}
