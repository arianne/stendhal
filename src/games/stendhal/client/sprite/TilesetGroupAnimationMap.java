/*
 * @(#) src/games/stendhal/client/sprite/TilesetGroupAnimationMap.java
 *
 * $Id$
 */

package games.stendhal.client.sprite;

//
//

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import marauroa.common.Log4J;


/**
 * A group of tileset animation maps. This might normally be called
 * <code>TilesetsAnimationMap</code>, but is less likely to mix-up
 * with <code>TilesetAnimationMap</code>.
 */
public class TilesetGroupAnimationMap {
	/**
	 * The logger.
	 */
	private static final Logger logger = Log4J.getLogger(TilesetGroupAnimationMap.class);

	/**
	 * The map of tileset animation maps.
	 */
	protected Map<String, TilesetAnimationMap>	tilesets;


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
	 * Acquire a named tileset map. If it does not exists, it will be
	 * created.
	 *
	 * @param	name		The name of the tileset.
	 *
	 * @return	An tileset animation map.
	 */
	protected TilesetAnimationMap acquire(final String name) {
		TilesetAnimationMap map = tilesets.get(name);

		if(map == null) {
			map = new TilesetAnimationMap();
			tilesets.put(name, map);
		}

		return map;
	}


	/**
	 * Add a mapping of a tile index to animation frame indexes.
	 *
	 * <strong>NOTE: The array of frame indexes passed is not copied,
	 * and should not be altered after this is called.</strong>
	 *
	 * @param	name		The name of the tileset.
	 * @param	index		The tile index to map.
	 * @param	frameIndexes	The indexes of frame tiles.
	 */
	public void add(final String name, final int index, final int [] frameIndexes) {
		acquire(name).add(index, frameIndexes);
	}


	/**
	 * Add mappings of tile indexes to animation frame indexes.
	 * For each frame, a mapping will be created with the remaining
	 * indexes as it's frames (in order, starting with it's index).
	 *
	 * <strong>NOTE: The array of frame indexes passed is not copied,
	 * and should not be altered after this is called.</strong>
	 *
	 * @param	name		The name of the tileset.
	 * @param	index		The tile index to map.
	 * @param	frameIndexes	The indexes of frame tiles.
	 */
	public void add(final String name, final int [] frameIndexes) {
		acquire(name).add(frameIndexes);
	}


	/**
	 * Parse and add a configuration line.
	 *
	 * @param	line		The configuration line.
	 *
	 * @see-also	#load(InputStream)
	 */
	protected void addConfig(final String line) {
		StringTokenizer st = new StringTokenizer(line, " \t");

		/*
		 * Tileset name
		 */
		if(!st.hasMoreTokens()) {
			logger.warn("Invalid map entry: " + line);
			return;
		}

		String name = st.nextToken();

		/*
		 * Tile index
		 */
		if(!st.hasMoreTokens()) {
			logger.error("Invalid map entry: " + line);
			return;
		}

		String index = st.nextToken();

		/*
		 * Frame indexes
		 */
		if(!st.hasMoreTokens()) {
			logger.error("Invalid map entry: " + line);
			return;
		}

		String frames = st.nextToken();

		/*
		 * Split up frames indexes
		 */
		st = new StringTokenizer(frames, ":");

		int [] frameIndexes = new int[st.countTokens()];

		for(int i = 0; i < frameIndexes.length; i++) {
			try {
				frameIndexes[i] = Integer.parseInt(st.nextToken());
			} catch(NumberFormatException ex) {
				logger.error("Invalid frame #" + (i + 1) + ": " + line);
				return;
			}
		}

		/*
		 * Special '*' case for rotated frames?
		 */
		if(index.equals("*")) {
			add(name, frameIndexes);
		} else {
			try {
				add(name, Integer.parseInt(index), frameIndexes);
			} catch(NumberFormatException ex) {
				logger.error("Invalid tile index: " + line);
				return;
			}
		}
	}


	/**
	 * Clear the map.
	 */
	public void clear() {
		tilesets.clear();
	}


	/**
	 * Get a named tileset map.
	 *
	 * @param	name		The name of the tileset.
	 *
	 * @return	An tileset animation map, or <code>null</code> if one
	 *		does not exists.
	 */
	public TilesetAnimationMap get(final String name) {
		return tilesets.get(name);
	}


	/**
	 * Load tileset mappings from a file. This doesn't not first clear
	 * any existing entries.
	 * </p>
	 *
	 * <p>
	 * The file format consists of one line per entry. Blank lines and
	 * those starting with '#' (a comment) are ignored. The line format
	 * is as follows:<br>
	 * <em>tileset</em> <em>index</em> <em>frame:frame[:frame]...</em>
	 * </p>
	 *
	 * <p>
	 * Spaces may be any whitespace. The <em>index</em> may also be
	 * <code>*</code>, which indicates that an entry should be added
	 * using each frame as a mapped index.
	 *
	 * @param	in		The input stream.
	 *
	 * @throws	IOException	If an I/O error occured.
	 *
	 * @see-also	#clear()
	 */
	public void load(final InputStream in) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(in));
		String line;


		while((line = r.readLine()) != null) {
			if(line.length() == 0) {
				continue;
			}

			/*
			 * Comment? (Only works if in first column)
			 */
			if(line.charAt(0) == '#') {
				continue;
			}

			addConfig(line);
		}
	}
}
