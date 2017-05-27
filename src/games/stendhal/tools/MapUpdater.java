/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2011 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

/*
 * MapConverter.java
 *
 * Created on 13. Oktober 2005, 18:24
 *
 */
package games.stendhal.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import tiled.core.Map;
import tiled.core.MapLayer;
import tiled.core.Tile;
import tiled.core.TileLayer;
import tiled.core.TileSet;
import tiled.io.TMXMapReader;
import tiled.io.TMXMapWriter;

/**
 * Fix maps by loading and saving them.
 *
 * @author mtotz, miguel
 */
public class MapUpdater extends Task {
	/** list of *.tmx files to convert. */
	private final List<FileSet> filesets = new ArrayList<FileSet>();

	/* mostly copied from dialog/TilesetManager.java, except that
	   many things refuse to compile with the usual tiled.jar,
	   so rewrote it to work with more primitive interfaces. */
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
	 * Remove unused tilesets.
	 * @param map
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
	 * Remove unused roof layers.
	 *
	 * @param map
	 */
	private void removeUnusedLayers(final Map map) {
		Iterator<MapLayer> iter = map.iterator();
		while (iter.hasNext()) {
			MapLayer layer = iter.next();
			if (layer.isEmpty()) {
				// Client merges floor layers, and removing anything there
				// prevents it doing that. Removing unused roof layers, however
				// saves drawing effort.
				if ("3_roof".equals(layer.getName())
						|| "4_roof_add".equals(layer.getName())) {
					iter.remove();
				}
			}
		}
	}

	/** Converts the map files.
	 * @param tmxFile
	 * @throws Exception */
	public void convert(final String tmxFile) throws Exception {
		final File file = new File(tmxFile);

		final String filename = file.getAbsolutePath();
		final Map map = new TMXMapReader().readMap(filename);
		removeUnusedTilesets(map);
		removeUnusedLayers(map);
		new TMXMapWriter().writeMap(map, filename);
	}

	/**
	 * Adds a set of files to copy.
	 *
	 * @param set
	 *            a set of files to copy
	 */
	public void addFileset(final FileSet set) {
		filesets.add(set);
	}

	/**
	 * ants execute method.
	 */
	@Override
	public void execute() {
		try {
			for (final FileSet fileset : filesets) {
				final DirectoryScanner ds = fileset.getDirectoryScanner(getProject());
				final String[] includedFiles = ds.getIncludedFiles();
				for (final String filename : includedFiles) {
					System.out.println(ds.getBasedir().getAbsolutePath()
							+ File.separator + filename);
					convert(ds.getBasedir().getAbsolutePath() + File.separator
							+ filename);
				}
			}
		} catch (final Exception e) {
			throw new BuildException(e);
		}
	}

	public static void main(final String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("usage: java games.stendhal.tools.MapConverter <tmx file>");
			return;
		}

		// do the job
		final MapUpdater converter = new MapUpdater();
		converter.convert(args[0]);
	}
}
