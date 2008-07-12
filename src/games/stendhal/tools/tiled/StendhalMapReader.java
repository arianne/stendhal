/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.tools.tiled;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import tiled.core.Map;
import tiled.core.TileSet;
import tiled.io.MapReader;

/**
 * experimental reader for *.stend files.
 */
@Deprecated
public class StendhalMapReader implements MapReader {

//	private PluginLogger pluginLogger;

	/**
	 * Method readMap.
	 * 
	 * @param filename
	 * @throws Exception
	 * @return null
	 */
	public Map readMap(final String filename) throws Exception {
		return null;
	}

	/**
	 * Method readTileset.
	 * 
	 * @param filename
	 * @throws Exception
	 * @return null
	 */
	public TileSet readTileset(final String filename) throws Exception {
		return null;
	}

	/**
	 * Method readMap.
	 * 
	 * @param in
	 * @throws Exception
	 * @return null
	 */
	public Map readMap(final InputStream in) throws Exception {
		return null;
	}

	/**
	 * Method readTileset.
	 * 
	 * @param in
	 * @throws Exception
	 * @return null
	 */
	public TileSet readTileset(final InputStream in) throws Exception {
		return null;
	}

	public boolean accept(final File pathname) {
		try {
			final String path = pathname.getCanonicalPath().toLowerCase();
			if (path.endsWith(".stend")) {
				return true;
			}
		} catch (final IOException e) {
//			pluginLogger.error(e);
		}
		return false;
	}

	public String getFilter() throws Exception {
		return "*.stend";
	}

	public FileFilter[] getFilters() {
		return null;
	}

	public String getName() {
		return "Stendhal reader";
	}

	public String getDescription() {
		return "+---------------------------------------------+\n"
				+ "|      An experimental reader for Stendhal    |\n"
				+ "|                                             |\n"
				+ "|      (c) Miguel Angel Blanch Lardin 2005    |\n"
				+ "|                                             |\n"
				+ "+---------------------------------------------+";
	}

	public String getPluginPackage() {
		return "Stendhal Reader/Writer Plugin";
	}

	public void setErrorStack(final Stack<String> es) {
		// not implemented
	}

//	public void setLogger(PluginLogger pluginLogger) {
//		this.pluginLogger = pluginLogger;
//	}
}
