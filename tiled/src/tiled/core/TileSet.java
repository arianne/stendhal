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

package tiled.core;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import javax.imageio.ImageIO;

import tiled.util.Util;

/**
 * <p>
 * TileSet handles operations on tiles as a set, or group. It has several
 * advanced internal functions aimed at reducing unnecessary data replication. A
 * 'tile' is represented internally as three distinct pieces of data. The first
 * and most important is a tiled.core.Tile object, and these are held in a
 * java.util.Vector.
 * </p>
 * 
 * <p>
 * Tile objects contain an id that can be used to look up the second piece of
 * data, the tile image hash. The tile image hash is a unique CRC32 checksum. A
 * checksum is generated for each image that is added to the set. A
 * java.util.Hashtable keeps the key-value pair of id and checksum. A second
 * java.util.Hashtable (the imageCache) maintains a key-value pair with the
 * checksum as key and the actual java.awt.Image as value.
 * </p>
 * 
 * <p>
 * When a new image is added, a checksum is created and checked against the
 * checksums in the cache. If the checksum does not already exist, the image is
 * given an id, and is added to the cache. In this way, tile images are never
 * duplicated, and multiple tiles may reference the image by id.
 * </p>
 * 
 * <p>
 * The TileSet also handles 'cutting' tile images from a tileset image, and can
 * optionally create Tile objects that reference the images.
 * </p>
 */
public class TileSet implements Iterable<Tile> {
	private String base;
	private List<Tile> tiles;
	private java.util.Map<String, Image[]> images;
	private java.util.Map<String, Image> imageCache;
	private int firstGid;
	/** standard height of a tile. */
	private int standardHeight;
	/** standard width of a tile. */
	private int standardWidth;
	/** the preferred number of tiles to show on one row. */
	private int preferredTilesPerRow;
	private String externalSource;
	private String tilebmpFile;
	private String name;
	private Map map;
	private Color transparentColor;

	public TileSet() {
		tiles = new ArrayList<Tile>();
		images = new HashMap<String, Image[]>();
		imageCache = new HashMap<String, Image>();
		preferredTilesPerRow = 0;
	}

	/**
	 * Creates a tileset from a tile bitmap file. This is a tile-cutter.
	 * 
	 * @param imgFilename
	 *            the filename of the image to be used
	 * @param tileWidth
	 *            the tile width
	 * @param tileHeight
	 *            the tile height
	 * @param spacing
	 *            the amount of spacing between the tiles
	 * @param createTiles
	 * @throws Exception
	 * @see TileSet#importTileBitmap(BufferedImage,int,int,int,boolean)
	 */
	public void importTileBitmap(String imgFilename, int tileWidth, int tileHeight, int spacing, boolean createTiles)
			throws Exception {
		File imgFile = null;
		try {
			imgFile = new File(imgFilename);
			tilebmpFile = imgFile.getCanonicalPath();
		} catch (IOException e) {
			tilebmpFile = imgFilename;
		}

		importTileBitmap(ImageIO.read(imgFile.toURL()), tileWidth, tileHeight, spacing, createTiles);
	}

	/**
	 * Creates a tileset from a buffered image. This is a linear cutter that
	 * goes left to right, top to bottom when cutting. It can optionally create
	 * tiled.core.Tile objects that reference the images as it is cutting them.
	 * 
	 * @param tilebmp
	 *            the image to be used
	 * @param tileWidth
	 *            the tile width
	 * @param tileHeight
	 *            the tile height
	 * @param spacing
	 *            the amount of spacing between the tiles
	 * @param createTiles
	 *            set to <code>true</code> to have the function create Tiles
	 * @throws Exception
	 */
	public void importTileBitmap(BufferedImage tilebmp, int tileWidth, int tileHeight, int spacing, boolean createTiles)
			throws Exception {

		if (tilebmp == null) {
			throw new Exception("Failed to load " + tilebmpFile);
		}

		int iw = tilebmp.getWidth();
		int ih = tilebmp.getHeight();

		// calculate tiles per row
		preferredTilesPerRow = iw / tileWidth;

		BufferedImage tilesetImage = new BufferedImage(iw, ih, BufferedImage.TYPE_INT_ARGB);
		Graphics2D tg = tilesetImage.createGraphics();
		// FIXME: although faster, the following doesn't seem to handle alpha on
		// some platforms...
		// GraphicsConfiguration config =
		// GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		// Image tilesetImage = config.createCompatibleImage(tileWidth,
		// tileHeight);
		// Graphics tg = tilesetImage.getGraphics();

		tg.drawImage(tilebmp, 0, 0, iw, ih, 0, 0, iw, ih, null);

		if (iw > 0 && ih > 0) {
			for (int y = 0; y <= ih - tileHeight; y += tileHeight + spacing) {
				for (int x = 0; x <= iw - tileWidth; x += tileWidth + spacing) {
					BufferedImage tile = tilesetImage.getSubimage(x, y, tileWidth, tileHeight);

					int newId = addImage(tile);
					if (createTiles) {
						Tile newTile = new Tile();
						newTile.setImage(newId);
						addNewTile(newTile);
					}
				}
			}
		}
	}

	/**
	 * Sets the standard width of the tiles in this tileset. Tiles in this
	 * tileset are not recommended to have any other width.
	 * 
	 * @param width
	 *            the width in pixels to use as the standard tile width
	 */
	public void setStandardWidth(int width) {
		standardWidth = width;
	}

	/**
	 * Sets the standard height of the tiles in this tileset. This is used to
	 * calculate the drawing position of tiles with a height above the standard
	 * height.
	 * 
	 * @param s
	 *            standard height for tiles
	 */
	public void setStandardHeight(int s) {
		standardHeight = s;
		Iterator<Tile> itr = tiles.iterator();
		while (itr.hasNext()) {
			Tile t = itr.next();
			if (t != null) {
				t.setStandardHeight(standardHeight);
			}
		}
	}

	/**
	 * Sets the URI path of the external source of this tile set. By setting
	 * this, the set is implied to be external in all other operations.
	 * 
	 * @param source
	 *            a URI of the tileset image file
	 */
	public void setSource(String source) {
		externalSource = source;
	}

	/**
	 * Sets the base directory for the tileset.
	 * 
	 * @param base
	 *            a String containing the native format directory
	 */
	public void setBaseDir(String base) {
		this.base = base;
	}

	/**
	 * Sets the filename of the tileset image. Doesn't change the tileset in any
	 * other way.
	 */
	public void setTilesetImageFilename(String name) {
		tilebmpFile = name;
	}

	/**
	 * Sets the first global id used by this tileset.
	 * 
	 * @param f
	 *            first global id
	 */
	public void setFirstGid(int f) {
		firstGid = f;
	}

	/**
	 * Sets the name of this tileset.
	 * 
	 * @param name
	 *            the new name for this tileset
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the map this tileset is part of.
	 */
	public void setMap(Map map) {
		this.map = map;
	}

	/**
	 * Sets the transparent color in the tileset image.
	 */
	public void setTransparentColor(Color color) {
		transparentColor = color;
	}

	/**
	 * Adds the tile to the set, setting the id of the tile only if the current
	 * value of id is -1.
	 * 
	 * @param t
	 *            the tile to add
	 * @return int The <b>local</b> id of the tile
	 */
	public int addTile(Tile t) {
		if (t.getId() < 0) {
			t.setId(tiles.size());
		}

		if (t.getId() >= tiles.size()) {
			Vector<Tile> newList = new Vector<Tile>(tiles);
			newList.setSize(t.getId() + 1);
			tiles = newList;
		}

		tiles.set(t.getId(), t);
		t.setTileSet(this);
		t.setStandardHeight(standardHeight);
		if (standardWidth < t.getWidth()) {
			standardWidth = t.getWidth();
		}

		return t.getId();
	}

	/**
	 * This method takes a new Tile object as argument, and in addition to the
	 * functionality of <code>addTile()</code>, sets the id of the tile to
	 * -1.
	 * 
	 * @see TileSet#addTile(Tile)
	 * @param t
	 *            the new tile to add.
	 */
	public void addNewTile(Tile t) {
		t.setId(-1);
		addTile(t);
	}

	/**
	 * Removes a tile from this tileset. Does not invalidate other tile indices.
	 * Removal is simply setting the reference at the specified index to <b>null</b>
	 * 
	 * @param i
	 *            the index to remove
	 */
	public void removeTile(int i) {
		tiles.set(i, null);
	}

	/**
	 * Returns the amount of tiles in this tileset.
	 * 
	 * @return the amount of tiles in this tileset
	 */
	public int size() {
		int total = 0;
		for (int i = 0; i < tiles.size(); i++) {
			if (tiles.get(i) != null) {
				total++;
			}
		}
		return total;
	}

	/**
	 * Returns the maximum tile id.
	 * 
	 * @return the maximum tile id, or -1 when there are no tiles
	 */
	public int getMaxTileId() {
		return tiles.size() - 1;
	}

	/**
	 * Returns an iterator over the tiles in this tileset.
	 * 
	 * @return an iterator over the tiles in this tileset.
	 */
	public Iterator<Tile> iterator() {
		return new TileIterator(tiles);
	}

	/**
	 * Returns the standard width of tiles in this tileset. All tiles in a
	 * tileset should be the same width.
	 * 
	 * @return the standard width as previously set with a call to
	 *         TileSet#setStandardWidth
	 */
	public int getStandardWidth() {
		return standardWidth;
	}

	/**
	 * Returns the standard height of tiles in this tileset. Not all tiles in a
	 * tileset are required to have the same height.
	 * 
	 * @return the standard height as previously set with a call to
	 *         TileSet#setStandardHeight
	 */
	public int getStandardHeight() {
		return standardHeight;
	}

	/**
	 * Iterates through the set an retrieves the largest height value.
	 * 
	 * @return the maximum hieght of any tile
	 */
	public int getTileHeightMax() {
		int maxHeight = 0;
		for (Tile tile : this) {
			if (tile.getHeight() > maxHeight) {
				maxHeight = tile.getHeight();
			}
		}
		return maxHeight;
	}

	/**
	 * Iterates through the set an retrieves the largest height value.
	 * 
	 * @return the maximum hieght of any tile
	 */
	public int getTileWidthMax() {
		int maxWidth = 0;
		for (Tile tile : this) {
			if (tile.getWidth() > maxWidth) {
				maxWidth = tile.getHeight();
			}
		}
		return maxWidth;
	}

	/**
	 * Gets the tile with <b>local</b> id <code>i</code>.
	 * 
	 * @param i
	 *            local id of tile
	 * @return A tile with local id <code>i</code> or <code>null</code> if
	 *         no tile exists with that id
	 */
	public Tile getTile(int i) {
		if (i < tiles.size()) {
			return tiles.get(i);
		}
		return null;
	}

	/**
	 * Returns the first non-null tile in the set.
	 * 
	 * @return The first tile in this tileset, or <code>null</code> if none
	 *         exists.
	 */
	public Tile getFirstTile() {
		Iterator<Tile> itr = iterator();
		if (itr.hasNext()) {
			return itr.next();
		}
		return null;
	}

	/**
	 * Returns the source of this tileset.
	 * 
	 * @return a filename if tileset is external or <code>null</code> if
	 *         tileset is internal.
	 */
	public String getSource() {
		return externalSource;
	}

	/**
	 * Returns the base directory for the tileset.
	 * 
	 * @return a directory in native format as given in the tileset file or tag
	 */
	public String getBaseDir() {
		return base;
	}

	/**
	 * Returns the filename of the tile bitmap.
	 * 
	 * @return the filename of the tile bitmap, or <code>null</code> if this
	 *         tileset doesn't reference a tile bitmap
	 */
	public String getTilebmpFile() {
		return tilebmpFile;
	}

	/**
	 * Returns the first global id connected to this tileset.
	 * 
	 * @return first global id
	 */
	public int getFirstGid() {
		return firstGid;
	}

	/**
	 * @return the name of this tileset.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the map this tileset is part of.
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * Returns the transparent color of the tileset image, or <code>null</code>
	 * if none is set.
	 */
	public Color getTransparentColor() {
		return transparentColor;
	}

	/**
	 * @return the name of the tileset, and the total tiles
	 */
	public String toString() {
		return getName() + " [" + size() + "]";
	}

	// TILE IMAGE CODE

	/**
	 * Provides a CRC32 checksum of the given image.
	 * 
	 * @param i
	 *            a preloaded Image object
	 * @return a String containing the checksum value
	 */
	private String checksumImage(Image i) {
		PixelGrabber pg = new PixelGrabber(i, 0, 0, -1, -1, false);
		Checksum sum = new CRC32();

		try {
			pg.grabPixels();

			try {
				int psize = pg.getColorModel().getPixelSize();
				ByteArrayInputStream bais = null;

				// handle different pixel sizes
				if (psize >= 15) {
					bais = new ByteArrayInputStream(Util.convertIntegersToBytes((int[]) pg.getPixels()));
				} else {
					bais = new ByteArrayInputStream((byte[]) pg.getPixels());
				}
				byte[] bytes = new byte[1024];
				int len = 0;

				while ((len = bais.read(bytes)) >= 0) {
					sum.update(bytes, 0, len);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return Long.toHexString(sum.getValue());
	}

	/**
	 * Returns the number of images in the set.
	 * 
	 * @return the number of images in the set
	 */
	public int getTotalImages() {
		return images.size();
	}

	/**
	 * @return an Enumeration of the image ids
	 */
	public Set<String> getImageIds() {
		return images.keySet();
	}

	/**
	 * This function uses the CRC32 checksums to find the cached version of the
	 * image supplied.
	 * 
	 * @param i
	 *            an Image object
	 * @return returns the id of the given image, or -1 if the image is not in
	 *         the set
	 */
	public int getIdByImage(Image i) {
		Iterator<String> itr = imageCache.keySet().iterator();
		int searchId = -1;
		if (i != null) {
			while (itr.hasNext()) {
				Image img = imageCache.get(itr.next());
				searchId++;
				if (img.equals(i)) {
					break;
				}
			}
		}
		return searchId;
	}

	/**
	 * @param key
	 *            a key identifying the image to get
	 * @return the imagine identified by the key, or <code>null</code> when
	 *         there is no such image
	 */
	public Image getImageById(Object key) {
		return getImageByIdAndOrientation(key, 0);
	}

	public Image getImageByIdAndOrientation(Object key, int orientation) {
		Image[] imgs = images.get(key);
		if (imgs == null) {
			return null;
		}
		if (imgs[orientation] == null) {
			imgs[orientation] = internImage(generateImageWithOrientation(imgs[0], orientation));
		}
		return imgs[orientation];
	}

	/*
	 * The following function is almost certainly incorrect, and at the very
	 * least contains a potential memory leak (because the old image is never
	 * removed from 'imageCache'). It was that way when I found it, and I don't
	 * have the patience to fix it right now. - Rainer Deyke
	 */
	public void overlayImage(String key, Image i) {
		Image[] imgs = new Image[8];
		imgs[0] = internImage(i);
		images.put(key, imgs);
	}

	/**
	 * Returns the dimensions of an image as specified by the id
	 * <code>key</code>.
	 * 
	 * @param key
	 * @return dimensions of image with referenced by given key
	 */
	public Dimension getImageDimensions(Object key, int orientation) {
		Image i = getImageByIdAndOrientation(key, orientation);
		if (i != null) {
			return new Dimension(i.getWidth(null), i.getHeight(null));
		} else {
			return new Dimension(0, 0);
		}
	}

	/**
	 * Attempt to retrieve an image matching the given image from the image
	 * cache.
	 * 
	 * @param image
	 *            the image to match
	 * @return a matching image from the cache if it exists, <code>null</code>
	 *         otherwise
	 */
	public Image queryImage(Image image) {
		String hash = checksumImage(image);
		return imageCache.get(hash);
	}

	/*
	 * Note: The following function only works for images in default
	 * orientation.
	 */

	/**
	 * Find the id fo the given image in the image cache.
	 * 
	 * @param image
	 *            the java.awt.Image to find the id for.
	 * @return an java.lang.Object that represents the id of the image
	 */
	public Object queryImageId(Image image) {
		String hash = checksumImage(image);
		image = imageCache.get(hash);
		if (image != null) {
			Iterator itr = images.keySet().iterator();
			while (itr.hasNext()) {
				Object key = itr.next();
				Image[] imgs = images.get(key);
				if (imgs[0] == image) {
					return key;
				}
			}
		}
		return Integer.toString(-1);
	}

	/**
	 * Adds the specified image to the image cache. If the image already exists
	 * in the cache, returns the id of the existing image. If it does not exist,
	 * this function adds the image and returns the new id.
	 * 
	 * @param image
	 *            the java.awt.Image to add to the image cache
	 * @return the id as an <code>int</code> of the image in the cache
	 */
	public int addImage(Image image) {
		int t = Integer.parseInt((String) queryImageId(image));
		if (t  >= 0) {
			return t;
		} else {
			// image ids should be unique.
			t = images.size();
			addImage(image, Integer.toString(t));
			return t;
		}
	}

	public int addImage(Image image, String key) {
		if (key == null) {
			return addImage(image);
		} else {
			Image[] imgs = new Image[8];
			imgs[0] = internImage(image);
			images.put(key, imgs);
			return Integer.parseInt(key);
		}
	}

	public void removeImage(String id) {
		// This operation is siginificantly slower now that 'images' stores
		// images directly instead of storing hashes. Fortunately this
		// operation is also extremely rare.
		Image[] img = images.get(id);
		images.remove(id);
		for (String key : images.keySet()) {
			for (int i = 0; i < img.length; ++i) {
				if (imageCache.get(key) == img[i]) {
					imageCache.remove(key);
				}
			}
		}
	}

	public static Image generateImageWithOrientation(Image src, int orientation) {
		if (orientation == 0) {
			return src;
		} else {
			int w = src.getWidth(null), h = src.getHeight(null);
			int[] old_pixels = new int[w * h];
			PixelGrabber p = new PixelGrabber(src, 0, 0, w, h, old_pixels, 0, w);
			try {
				p.grabPixels();
			} catch (InterruptedException e) {
			}
			int[] new_pixels = new int[w * h];
			int dest_w = ((orientation & 4) != 0) ? h : w;
			int dest_h = ((orientation & 4) != 0) ? w : h;
			for (int dest_y = 0; dest_y < dest_h; ++dest_y) {
				for (int dest_x = 0; dest_x < dest_w; ++dest_x) {
					int src_x = dest_x, src_y = dest_y;
					if ((orientation & 4) != 0) {
						src_y = dest_w - dest_x - 1;
						src_x = dest_y;
					}
					if ((orientation & 1) != 0) {
						src_x = w - src_x - 1;
					}
					if ((orientation & 2) != 0) {
						src_y = h - src_y - 1;
					}
					new_pixels[dest_x + dest_y * dest_w] = old_pixels[src_x + src_y * w];
				}
			}
			old_pixels = null;
			BufferedImage new_img = new BufferedImage(dest_w, dest_h, BufferedImage.TYPE_INT_ARGB);
			new_img.setRGB(0, 0, dest_w, dest_h, new_pixels, 0, dest_w);
			return new_img;
		}
	}

	public boolean usesSharedImages() {
		// TODO: Currently only uses shared sets...
		return true;
	}

	/**
	 * Checks whether each image has a one to one relationship with the tiles.
	 * 
	 * @return <code>true</code> if each image is associated with one and only
	 *         one tile, <code>false</code> otherwise.
	 */
	public boolean isOneForOne() {
		Iterator<String> keys = images.keySet().iterator();

		while (keys.hasNext()) {
			int key = Integer.parseInt(keys.next());
			int relations = 0;
			Iterator<Tile> itr = iterator();

			while (itr.hasNext()) {
				Tile t = itr.next();
				if (t.getImageId() == key) {
					relations++;
				}
			}
			if (relations != 1) {
				return false;
			}
		}

		return true;
	}

	private Image internImage(Image img) {
		String s = checksumImage(img);
		Image img2 = imageCache.get(s);
		if (img2 != null) {
			return img2;
		}
		imageCache.put(s, img);
		return img;
	}

	/**
	 * returns the preferred number of tiles to show on one row. This is to help
	 * the display routines to not mess the original tileset image.
	 */
	public int getPreferredTilesPerRow() {
		return preferredTilesPerRow;
	}
}
