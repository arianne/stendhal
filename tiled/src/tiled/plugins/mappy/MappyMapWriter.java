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

package tiled.plugins.mappy;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import tiled.io.MapWriter;
import tiled.core.Map;
import tiled.core.TileSet;

public class MappyMapWriter implements MapWriter, FileFilter {
	private List<Chunk> chunks;

	public MappyMapWriter() {
		chunks = new ArrayList<Chunk>();
	}

	/**
	 * Loads a map from a file.
	 * 
	 * @param filename
	 *            the filename of the map file
	 */
	public void writeMap(Map map, String filename) throws Exception {
		writeMap(map, new FileOutputStream(filename));
	}

	/**
	 * Loads a tileset from a file.
	 * 
	 * @param filename
	 *            the filename of the tileset file
	 */
	public void writeTileset(TileSet set, String filename) throws Exception {
		System.out.println("Asked to write " + filename);
	}

	public void writeMap(Map map, OutputStream in) throws Exception {
		in.write("FORM".getBytes());
		// TODO: write the size of the file minus this header
		in.write("FMAP".getBytes());
		createMPHDChunk(map);

		// TODO: write all the chunks
	}

	public void writeTileset(TileSet set, OutputStream out) throws Exception {
		System.out.println("Tilesets are not supported!");
	}

	/**
	 * 
	 */
	public String getFilter() throws Exception {
		return "*.map";
	}

	public String getDescription() {
		return "+---------------------------------------------+\n"
				+ "|    A sloppy writer for Mappy FMAP (v0.36)   |\n"
				+ "|             (c) Adam Turk 2004              |\n"
				+ "|          aturk@biggeruniverse.com           |\n"
				+ "+---------------------------------------------+";
	}

	public String getPluginPackage() {
		return "Mappy output plugin";
	}

	public String getName() {
		return "Mappy Writer";
	}

	public boolean accept(File pathname) {
		try {
			String path = pathname.getCanonicalPath().toLowerCase();
			if (path.endsWith(".fmp")) {
				return true;
			}
		} catch (IOException e) {
		}
		return false;
	}

	public void setErrorStack(Stack es) {
		// TODO: implement setErrorStack
	}

	private void createMPHDChunk(Map m) throws IOException {
		Chunk c = new Chunk("MPHD");
		OutputStream out = c.getOutputStream();
		String ver = m.getProperties().getProperty("version");
		if (ver == null || ver.length() < 3) {
			ver = "0.3"; // default the value
		}
		TileSet set = (TileSet) m.getTilesets().get(0);

		// FIXME
		// out.write(Integer.parseInt(ver.substring(0,ver.indexOf('.')-1)));
		// out.write(Integer.parseInt(ver.substring(ver.indexOf('.')+1)));
		out.write(0);
		out.write(3);
		out.write(1);
		out.write(0); // LSB, reserved
		Util.writeShort(m.getWidth(), out);
		Util.writeShort(m.getHeight(), out);
		out.write(0);
		out.write(0);
		out.write(0);
		out.write(0); // reserved
		Util.writeShort(m.getTileWidth(), out);
		Util.writeShort(m.getTileHeight(), out);
		Util.writeShort(16, out); // tile bitdepth
		Util.writeShort(32, out); // blkstr bytewidth
		Util.writeShort(findAllBlocks(m).size(), out);
		Util.writeShort(set.getMaxTileId(), out);

		chunks.add(c);
	}

	// private void createBKDTChunk(Map m) {
	// Chunk c = new Chunk("BKDT");
	// LinkedList blocks = findAllBlocks(m);
	// Iterator itr = blocks.iterator();
	// while(itr.hasNext()) {
	// MappyMapReader.BlkStr b = (BlkStr) itr.next();
	// // TODO: write the block
	// }
	// chunks.add(c);
	// }

	private LinkedList findAllBlocks(Map m) {
		// TODO: this
		return null;
	}

	/** returns a list of available filefilters. */
	public FileFilter[] getFilters() {
		return new FileFilter[] { this };
	}
}
