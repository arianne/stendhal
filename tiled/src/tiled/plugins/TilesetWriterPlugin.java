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
 *  Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */
package tiled.plugins;

import java.io.InputStream;

import tiled.core.TileSet;

/**
 * The plugin writes tileset.
 * 
 * @author mtotz
 */
public interface TilesetWriterPlugin extends IOPlugin {
	/**
	 * Writes a fileset to a file.
	 * 
	 * @param filename
	 *            the filename of the tileset file
	 * @param tileSet
	 *            a {@link tiled.core.TileSet}
	 */
	void writeTileset(TileSet tileSet, String filename);

	/**
	 * Writes a fileset to a stream.
	 * 
	 * @param inputStream
	 *            the InputStream where to write the tileset to
	 * @param tileSet
	 *            a {@link tiled.core.TileSet}
	 */
	void writeTileset(TileSet tileSet, InputStream inputStream);
}
