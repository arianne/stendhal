/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for Stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.plugins;

import java.io.InputStream;

import tiled.core.Map;

/**
 * The plugin reads a map file.
 * 
 * @author mtotz
 */
public interface MapReaderPlugin extends IOPlugin {
	/**
	 * Loads a map from a file.
	 * 
	 * @param filename
	 *            the filename of the map file
	 * @return a {@link tiled.core.Map}
	 */
	Map readMap(String filename);

	/**
	 * Loads a map from a stream.
	 * 
	 * @param inputStream
	 *            the InputStream from where to load the map
	 * @return a {@link tiled.core.Map}
	 */
	Map readMap(InputStream inputStream);

}
