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

import java.io.OutputStream;

import tiled.core.Map;

/**
 * The plugin writes a map file.
 * 
 * @author mtotz
 */
public interface MapWriterPlugin extends IOPlugin {
	/**
	 * Writes a map to a file.
	 * 
	 * @param filename
	 *            the filename of the map file
	 * @param map
	 *            a{@link tiled.core.Map}
	 */
	void writeMap(Map map, String filename);

	/**
	 * Writes a map to a stream.
	 * 
	 * @param outputStream
	 *            the OutputStream where to write the map to
	 * @param map
	 *            a{@link tiled.core.Map}
	 */
	void writeMap(Map map, OutputStream outputStream);

}
