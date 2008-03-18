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

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 * The core class for our tiles.
 */
public class Tile {
	private Image internalImage, scaledImage;
	private int id = -1;
	private int stdHeight;
	private int groundHeight; // Height above/below ground
	private int tileImageId = -1;
	private int tileOrientation;
	private double myZoom = 1.0;
	private Properties properties;
	private TileSet tileset;

	public Tile() {
		properties = new Properties();
	}

	public Tile(TileSet set) {
		this();
		setTileSet(set);
	}

	public Tile(Tile t) {
		properties = (Properties) t.properties.clone();
		tileImageId = t.tileImageId;
		tileset = t.tileset;
		if (tileset != null) {
			scaledImage = getImage().getScaledInstance(-1, -1, Image.SCALE_DEFAULT);
		}
		groundHeight = getHeight();
	}

	/**
	 * Sets the id of the tile as long as it is at least 0.
	 * 
	 * @param i
	 *            The id of the tile
	 */
	public void setId(int i) {
		if (i >= 0) {
			id = i;
		}
	}

	/**
	 * Changes the image of the tile as long as it is not null.
	 * 
	 * @param i
	 *            the new image of the tile
	 */
	public void setImage(Image i) {
		if (tileset != null) {
			tileset.overlayImage("" + tileImageId, i);
		} else {
			internalImage = i;
		}
		groundHeight = getHeight();
	}

	public void setImage(int id) {
		tileImageId = id;
		groundHeight = getHeight();
	}

	public void setImageOrientation(int orientation) {
		this.tileOrientation = orientation;
	}

	public void setStandardHeight(int i) {
		stdHeight = i;
	}

	public void setTileSet(TileSet set) {
		tileset = set;
		if (internalImage != null) {
			tileImageId = set.addImage(internalImage);
			internalImage = null;
		}
		groundHeight = getHeight();
	}

	public void setProperties(Properties p) {
		properties = p;
	}

	public Properties getProperties() {
		return properties;
	}

	public int getId() {
		return id;
	}

	public int getGid() {
		if (tileset != null) {
			return id + tileset.getFirstGid();
		}
		return id;
	}

	/**
	 * Returns the {@link tiled.core.TileSet} that this tile is part of.
	 * 
	 * @return TileSet
	 */
	public TileSet getTileSet() {
		return tileset;
	}

	/**
	 * This drawing function handles drawing the tile image at the specified
	 * zoom level. It will attempt to use a cached copy, but will rescale if the
	 * requested zoom does not equal the current cache zoom.
	 * 
	 * @param g
	 *            Graphics instance to draw to
	 * @param x
	 *            x-coord to draw tile at
	 * @param y
	 *            y-coord to draw tile at
	 * @param zoom
	 *            Zoom level to draw the tile
	 */
	public void drawRaw(Graphics g, int x, int y, double zoom) {
		if (scaledImage == null || zoom != myZoom) {
			scaledImage = getScaledImage(zoom);
			myZoom = zoom;
			if (scaledImage != null) {
				MediaTracker mediaTracker = new MediaTracker(new Canvas());
				mediaTracker.addImage(scaledImage, 0);
				try {
					mediaTracker.waitForID(0);
				} catch (InterruptedException ie) {
					System.err.println(ie);
					return;
				}
				mediaTracker.removeImage(scaledImage);
				g.drawImage(scaledImage, x, y, null);
			} else {
				// TODO: Allow drawing IDs when no image data exists as a
				// config option
			}
		} else {
			g.drawImage(scaledImage, x, y, null);
		}
	}

	public void draw(Graphics g, int x, int y, double zoom) {
		// Make sure tiles longer than the standard height are drawn with
		// bottom aligned with other tiles.
		int h = (int) (getHeight() * zoom);
		int gnd_h = (int) (groundHeight * zoom);
		int std_h = (int) (stdHeight * zoom);

		if (h > std_h && std_h != 0) {
			y -= h - std_h;
		}

		// Invoke raw draw function
		drawRaw(g, x, y + (h - gnd_h), zoom);
	}

	public int getWidth() {
		if (tileset != null) {
			Dimension d = tileset.getImageDimensions("" + tileImageId, tileOrientation);
			return d.width;
		} else if (internalImage != null) {
			return internalImage.getWidth(null);
		}
		return 0;
	}

	public int getHeight() {
		if (tileset != null) {
			Dimension d = tileset.getImageDimensions("" + tileImageId, tileOrientation);
			return d.height;
		} else if (internalImage != null) {
			return internalImage.getHeight(null);
		}
		return 0;
	}

	public int getImageId() {
		return tileImageId;
	}

	public int getImageOrientation() {
		return tileOrientation;
	}

	/**
	 * Returns the tile image for this Tile.
	 * 
	 * @return Image
	 */
	public Image getImage() {
		if (tileset != null) {
			return tileset.getImageByIdAndOrientation(Integer.toString(tileImageId), tileOrientation);
		} else {
			return internalImage;
		}
	}

	/**
	 * Returns a scaled instance of the tile image.
	 */
	public Image getScaledImage(double zoom) {
		Image i = getImage();
		if (i != null) {
			if (zoom == 1.0) {
				return i;
			} else {
				return i.getScaledInstance((int) (getWidth() * zoom), (int) (getHeight() * zoom),
						BufferedImage.SCALE_SMOOTH);
			}
		}
		return null;
	}

	@Override
	public String toString() {
		String out = "";
		out += "Tile: " + id + " Image: " + tileImageId + " (" + getWidth() + "x" + getHeight() + ")";
		return out;
	}
}
