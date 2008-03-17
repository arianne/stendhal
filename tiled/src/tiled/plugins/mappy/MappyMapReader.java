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
import java.util.List;
import java.util.Properties;
import java.util.Stack;
import java.util.Iterator;

import tiled.io.MapReader;
import tiled.core.*;

public class MappyMapReader implements MapReader, FileFilter {
	private List<Chunk> chunks;
	private List<BlkStr> blocks;
	private static final int BLKSTR_WIDTH = 32;
	private int twidth;
	private int theight;

	public static class BlkStr {
		public BlkStr() {
		}

		public long bg;
		public long fg0;
		public long fg1;
		public long fg2;
		public long user1;
		public long user2; // user long data
		public int user3;
		public int user4; // user short data
		public int user5;
		public int user6;
		public int user7; // user byte data
		public int bits; // collision and trigger bits
	}

	/**
	 * Loads a map from a file.
	 * 
	 * @param filename
	 *            the filename of the map file
	 */
	public Map readMap(String filename) throws Exception {
		return readMap(new FileInputStream(filename));
	}

	public Map readMap(InputStream in) throws Exception {
		Map ret = null;
		chunks = new ArrayList<Chunk>();
		blocks = new ArrayList<BlkStr>();
		byte[] hdr = new byte[4];

		in.read(hdr);
		/* long size = */Util.readLongReverse(in);
		in.read(hdr);

		try {
			Chunk chunk = new Chunk(in);
			while (chunk.isGood()) {
				chunks.add(chunk);
				/* chunk = */new Chunk(in);
			}
		} catch (IOException ioe) {
		}

		// now build a Tiled map...
		Chunk c = findChunk("MPHD");
		if (c != null) {
			ret = readMPHDChunk(c.getInputStream());
		} else {
			throw new IOException("No MPHD chunk found!");
		}

		c = findChunk("BODY");
		if (c != null) {
			readBODYChunk(ret, c.getInputStream());
		} else {
			throw new IOException("No BODY chunk found!");
		}

		return ret;
	}

	/**
	 * Loads a tileset from a file.
	 * 
	 * @param filename
	 *            the filename of the tileset file
	 */
	public TileSet readTileset(String filename) throws Exception {
		System.out.println("Tilesets aren't supported!");
		return null;
	}

	public TileSet readTileset(InputStream in) {
		System.out.println("Tilesets aren't supported!");
		return null;
	}

	/**
	 * 
	 */
	public String getFilter() throws Exception {
		return "*.fmp";
	}

	public String getPluginPackage() {
		return "Mappy input plugin";
	}

	public String getDescription() {
		return "+---------------------------------------------+\n"
				+ "|      An experimental reader for Mappy       |\n"
				+ "|                 FMAP v0.36                  |\n"
				+ "|            (c) Adam Turk 2004               |\n"
				+ "|          aturk@biggeruniverse.com           |\n"
				+ "|                                             |\n"
				+ "| Currently unsupported:                      |\n"
				+ "|  * Animated tiles                           |\n"
				+ "|  * The ATHR block                           |\n"
				+ "|  * Collision bits on BLKSTR structures      |\n"
				+ "|  * bitdepths other than 16bit               |\n"
				+ "|  * object layers                            |\n"
				+ "+---------------------------------------------+";
	}

	public String getName() {
		return "Mappy Reader";
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

	private Chunk findChunk(String header) {
		Iterator itr = chunks.iterator();

		while (itr.hasNext()) {
			Chunk c = (Chunk) itr.next();
			if (c.equals(header)) {
				return c;
			}
		}
		return null;
	}

	private Map readMPHDChunk(InputStream in) throws IOException {
		Map ret = null;
		TileSet set = new TileSet();
		int major;
		int minor;
		major = in.read();
		minor = in.read();
		in.skip(2); // skip lsb and reserved bytes - always msb
		ret = new Map(Util.readShort(in), Util.readShort(in));
		Properties retProps = ret.getProperties();
		ret.setOrientation(Map.MDO_ORTHO); // be sure to set the orientation!
		retProps.setProperty("(s)fmap reader",
				"Don't modify properties marked (s) unless you really know what you're doing.");
		retProps.setProperty("version", "" + major + "." + minor);
		in.skip(4); // reserved
		twidth = Util.readShort(in);
		theight = Util.readShort(in);
		set.setStandardWidth(twidth);
		set.setStandardHeight(theight);
		ret.setTileWidth(twidth);
		ret.setTileHeight(theight);
		set.setName("Static tiles");
		ret.addTileset(set);
		int depth = Util.readShort(in);
		if (depth < 16) {
			throw new IOException("Tile bitdepths less than 16 are not supported!");
		}
		retProps.setProperty("(s)depth", "" + depth);
		in.skip(2);
		int numBlocks = Util.readShort(in);
		int numBlocksGfx = Util.readShort(in);
		Chunk c = findChunk("BKDT");
		if (c == null) {
			throw new IOException("No BKDT block found!");
		}
		MapLayer ml = new TileLayer(ret, ret.getWidth(), ret.getHeight());
		ml.setName("bg");
		ret.addLayer(ml);
		for (int i = 1; i < 7; i++) {
			// //TODO: I believe this should be ObjectGroup
			// ml = new ObjectGroup(ret, 0, 0);
			// ml.setName("ObjectLayer "+i);
			// ret.addLayer(ml);
		}
		ml = new TileLayer(ret, ret.getWidth(), ret.getHeight());
		ml.setName("fg 1");
		ret.addLayer(ml);
		ml = new TileLayer(ret, ret.getWidth(), ret.getHeight());
		ml.setName("fg 2");
		ret.addLayer(ml);
		ml = new TileLayer(ret, ret.getWidth(), ret.getHeight());
		ml.setName("fg 3");
		ret.addLayer(ml);

		readBKDTChunk(ret, c.getInputStream(), numBlocks);

		c = findChunk("BGFX");
		if (c != null) {
			readBGFXChunk(ret, c.getInputStream(), numBlocksGfx);
		} else {
			throw new IOException("No BGFX chunk found!");
		}

		System.out.println(ret.toString());
		return ret;
	}

	private void readBKDTChunk(Map m, InputStream in, int num) throws IOException {
		System.out.println("Reading " + num + " blocks...");
		for (int i = 0; i < num; i++) {
			blocks.add(readBLKSTR(in));
		}
	}

	/**
	 * Read a BODY chunk from a Mappy map. BODY chunks contain data for the 4
	 * main layers of the map.
	 * 
	 * @param m
	 * @param in
	 * @throws IOException
	 */
	private void readBODYChunk(Map m, InputStream in) throws IOException {
		TileSet set = (TileSet) m.getTilesets().get(0);
		TileLayer bg = (TileLayer) m.getLayer(0);
		TileLayer fg0 = (TileLayer) m.getLayer(7);
		TileLayer fg1 = (TileLayer) m.getLayer(8);
		TileLayer fg2 = (TileLayer) m.getLayer(9);

		for (int i = 0; i < m.getHeight(); i++) {
			for (int j = 0; j < m.getWidth(); j++) {
				int block = (int) ((Util.readShort(in) & 0x00FF) / BLKSTR_WIDTH);
				// System.out.print(""+block);
				BlkStr blk = (BlkStr) blocks.get(block);
				// System.out.println("bg: "+blk.bg);
				bg.setTileAt(j, i, set.getTile((int) blk.bg));
				fg0.setTileAt(j, i, set.getTile((int) blk.fg0));
				fg1.setTileAt(j, i, set.getTile((int) blk.fg1));
				fg2.setTileAt(j, i, set.getTile((int) blk.fg2));
			}
			// System.out.println();
		}
	}

	/**
	 * BGFX blocks are synonymous with {@link tiled.core.Tile}s.
	 * 
	 * @param m
	 *            The Map to add Tiles to
	 * @param in
	 * @param num
	 *            Number of Tiles to read
	 * @throws IOException
	 */
	private void readBGFXChunk(Map m, InputStream in, int num) throws IOException {
		TileSet set = (TileSet) m.getTilesets().get(0);
		set.addTile(new Tile());
		Util.readRawImage(in, twidth, theight); // skip the null-tile
		for (int i = 1; i < num; i++) {
			Tile t = new Tile();
			t.setImage(Util.readRawImage(in, twidth, theight));
			set.addTile(t);
		}
	}

	private BlkStr readBLKSTR(InputStream in) throws IOException {
		MappyMapReader.BlkStr ret = new MappyMapReader.BlkStr();
		long widthMod = (twidth * theight * 512);
		ret.bg = Util.readLongReverse(in) / widthMod;
		ret.fg0 = Util.readLongReverse(in) / widthMod;
		ret.fg1 = Util.readLongReverse(in) / widthMod;
		ret.fg2 = Util.readLongReverse(in) / widthMod;

		ret.user1 = Util.readLongReverse(in);
		ret.user2 = Util.readLongReverse(in);
		ret.user3 = Util.readShort(in);
		ret.user4 = Util.readShort(in);
		ret.user5 = in.read();
		ret.user6 = in.read();
		ret.user7 = in.read();

		ret.bits = in.read();

		return ret;
	}

	/** returns a list of available filefilters. */
	public FileFilter[] getFilters() {
		return new FileFilter[] { this };
	}
}
