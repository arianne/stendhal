/***************************************************************************
 *                (C) Copyright 2003-2018 - Faiumoni e.V.                  *
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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.BaseEncoding;

/** Finds duplicate, empty and placeholder tiles in tiled/tileset/ */
public class DuplicateTileFinder {
	/** Tile size. */
	private static final int SIZE = 32;
	private final Multimap<FingerPrint, Tile> tilemap = LinkedHashMultimap.create();
	private MessageDigest md;
	private final PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.png");
	/** Fingerprint for empty tile. */
	private final FingerPrint emptyTile = new FingerPrint("1CEAF73DF40E531DF3BFB26B4FB7CD95FB7BFF1D");
	/** Fingerprint for the placeholder tile. */
	private final FingerPrint placeholder = new FingerPrint("1B720CBE931D798C0C3A7E94F145C56C00CF524E");

	public static void main(String[] args) {
		DuplicateTileFinder finder = new DuplicateTileFinder();
		finder.scan("tiled/tileset");
		finder.report();
	}

	/**
	 * Constructor.
	 */
	public DuplicateTileFinder() {
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Print information about empty, placeholder, and duplicate tiles.
	 */
	private void report() {
		for (Map.Entry<FingerPrint, Collection<Tile>> entry: tilemap.asMap().entrySet()) {
			Collection<Tile> tiles = entry.getValue();
			boolean empty = emptyTile.equals(entry.getKey());
			boolean pholder = placeholder.equals(entry.getKey());
			if (empty || pholder || tiles.size() > 1) {
				if (empty) {
					System.out.println("Empty tiles:");
				} else if (pholder) {
					System.out.println("Placeholder tiles:");
				} else {
					System.out.println("Set of duplicates: " + entry.getKey());
				}
				for (Tile tile : tiles) {
					System.out.println("    " + tile);
				}
			}
		}
	}

	/**
	 * Scan a file tree recursively.
	 *
	 * @param startPath starting location
	 */
	private void scan(String startPath) {
		Path path = Paths.get(startPath);
		try {
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
			    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			        throws IOException {
					checkFile(file);
			        return FileVisitResult.CONTINUE;
			    }
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process a file, if it contains a png image.
	 * @param path file path
	 * @throws IOException if reading a png file fails
	 */
	private void checkFile(Path path) throws IOException {
		if (matcher.matches(path)) {
			BufferedImage img = ImageIO.read(path.toFile());
			if (img != null) {
				int x = 0;
				int y = 0;
				do {
					md.reset();
					if (x + SIZE > img.getWidth()) {
						x = 0; y += SIZE;
					}
					if (y + SIZE > img.getHeight()) {
						break;
					}
					DataBuffer buf = img.getData(new Rectangle(x, y, SIZE, SIZE)).getDataBuffer();
					if (!(buf instanceof DataBufferByte)) {
						System.err.println("Skipping " + path.toString()
								+ " using " + buf.getClass());
						break;
					}
					DataBufferByte dataBuf = (DataBufferByte) img.getData(new Rectangle(x, y, SIZE, SIZE)).getDataBuffer();
					for (byte[] data : dataBuf.getBankData()) {
						md.update(data);
					}
					FingerPrint fp = new FingerPrint(md.digest());
					tilemap.put(fp, new Tile(path.toString(), x / SIZE + 1, y / SIZE + 1));
					x += SIZE;
				} while (true);
			}
		}
	}

	/** Object for holding tile fingerprint data. */
	private static class FingerPrint {
		private final byte[] data;

		/**
		 * Create fingerprint from data.
		 * @param data fingerprint data
		 */
		FingerPrint(byte[] data) {
			this.data = data;
		}

		/**
		 * Create fingerprint from string representation.
		 * @param data a hex string of the fingerprint
		 */
		FingerPrint(String data) {
			this.data = BaseEncoding.base16().decode(data);
		}

		@Override
		public String toString() {
			return BaseEncoding.base16().encode(data);
		}

		@Override
		public boolean equals(Object o) {
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			return Arrays.equals(((FingerPrint) o).data, data);
		}

		@Override
		public int hashCode() {
			return Arrays.hashCode(data);
		}
	}

	/**
	 * Information about a tile.
	 */
	private static class Tile {
		private final String image;
		private final int x, y;

		/**
		 * Create a tile from image name and tile coordinates.
		 * @param image image name
		 * @param x x coordinate of the tile
		 * @param y y coordinate of the tile
		 */
		Tile(String image, int x, int y) {
			this.image = image;
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return image + ": (" + x + ", " + y + ")";
		}
	}
}
