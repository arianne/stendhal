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
package tiled.view;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import tiled.core.Map;
import tiled.core.Tile;
import tiled.core.TileGroup;
import tiled.core.TileLayer;
import tiled.mapeditor.util.MapChangeListener;
import tiled.mapeditor.util.MapChangedEvent;
import tiled.util.TiledConfiguration;

/**
 * Base class for all views.
 * 
 * @author mtotz
 */
public abstract class MapView implements MapChangeListener {

	/** default zoom level for the minimap. */
	protected static final double DEFAULT_MINIMAP_ZOOM = 0.0625;

	/** index of default zoom (100%) in zoom level array. */
	private static final int DEFAULT_ZOOM_LEVEL = 5;

	/** valid zoom levels. */
	protected static double[] zoomLevels = { 0.0625, 0.125, 0.25, 0.5, 0.75, 1.0, 1.5, 2.0, 3.0, 4.0 };

	/** the zoom-level index. */
	private int zoomLevel = DEFAULT_ZOOM_LEVEL;

	/** the map. */
	protected Map map;

	/** current zoom. */
	protected double zoom = 1.0;

	/** the cached minimap image. */
	protected BufferedImage minimapImage;

	/** the background color. */
	private Color backgroundColor;

	/** padding between the tiles. */
	protected int padding;

	/**
	 * dirty region of the minimap (will be redrawn the next time it is
	 * displayed).
	 */
	protected Rectangle dirtyMinimap;

	public MapView() {
		// get the background color
		try {
			TiledConfiguration conf = TiledConfiguration.getInstance();
			String colorString = conf.getValue("tiled.background.color");
			backgroundColor = Color.decode(colorString);
		} catch (NumberFormatException e) {
			backgroundColor = new Color(64, 64, 64);
		}
	}

	/** sets the map. */
	public void setMap(Map map) {
		if (this.map != null) {
			this.map.removeMapChangeListener(this);
		}

		this.map = map;
		map.addMapChangeListener(MapChangedEvent.Type.TILES, this);
	}

	/** size of the view (in pixel) .*/
	public abstract Dimension getSize();

	/**
	 * Draws the whole map to the graphic canvas.
	 * 
	 * @param g
	 *            the graphic to draw to
	 */
	public void draw(Graphics g) {
		Rectangle clip = g.getClipBounds();
		if (clip == null) {
			return;
		}

		// clear the background
		g.setColor(backgroundColor);
		g.fillRect(clip.x, clip.y, clip.width, clip.height);

		// draw the map
		if (map != null) {
			// the rectangle should be a little bigger than the screen
			Rectangle rect = new Rectangle(clip);
			rect.width += map.getTileWidth();
			rect.height += map.getTileHeight();

			Rectangle tileRect = screenToTileRect(rect);
			draw(g, tileRect);
		}
	}

	/**
	 * converts the screen position to tile position.
	 * 
	 * @param tileCoords
	 *            tile coords
	 * @return screen coords
	 */
	public Rectangle screenToTileRect(Rectangle tileCoords) {
		Point upperPoint = screenToTileCoords(new Point(tileCoords.x, tileCoords.y));
		Point lowerPoint = screenToTileCoords(new Point(tileCoords.x + tileCoords.width, tileCoords.y
				+ tileCoords.height));

		return new Rectangle(upperPoint.x, upperPoint.y, lowerPoint.x - upperPoint.x, lowerPoint.y - upperPoint.y);
	}

	/**
	 * converts the tile position to screen position.
	 * 
	 * @param screenCoords
	 *            screen coords
	 * @return tile coords
	 */
	public Rectangle tileToScreenRect(Rectangle screenCoords) {
		Point upperPoint = tileToScreenCoords(new Point(screenCoords.x, screenCoords.y));
		Point lowerPoint = tileToScreenCoords(new Point(screenCoords.x + screenCoords.width, screenCoords.y
				+ screenCoords.height));

		return new Rectangle(upperPoint.x, upperPoint.y, lowerPoint.x - upperPoint.x, lowerPoint.y - upperPoint.y);
	}

	/**
	 * Returns a minimap image. Its the responsibility of the MapView to update
	 * the image on changes.
	 */
	public Image getMinimap() {
		if (map != null) {
			if (minimapImage == null) {
				// lazy minimap image creation
				minimapImage = prepareMinimapImage();
				// update the image
				Rectangle all = new Rectangle(0, 0, map.getWidth(), map.getHeight());
				Graphics g = minimapImage.createGraphics();
				g.setColor(Color.BLACK);
				g.fillRect(0, 0, minimapImage.getWidth(), minimapImage.getHeight());
				g.dispose();
				updateMinimapImage(all);
			}

			if (dirtyMinimap != null) {
				Rectangle r = dirtyMinimap;
				dirtyMinimap = null;
				updateMinimapImage(r);
			}
		}

		return minimapImage;
	}

	/** Sets the layer opacity in the graphics context g. */
	protected void setLayerOpacity(Graphics g, TileLayer layer) {
		float opacity = layer.getOpacity();

		if (layer.isVisible() && opacity > 0.0f) {
			if (opacity < 1.0f) {
				((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, opacity));
			} else {
				((Graphics2D) g).setComposite(AlphaComposite.SrcOver);
			}
		}
	}

	/** returns the minimap zoom level. */
	public double getMinimapScale() {
		return DEFAULT_MINIMAP_ZOOM;
	}

	/** returns the current zoom level. */
	public double getScale() {
		return zoom;
	}

	/** sets the zoom level. */
	public void setScale(double scale) {
		this.zoom = scale;
	}

	/** sets the zoom level. */
	private void setZoomLevel(int zoomLevel) {
		if (zoomLevel >= 0 && zoomLevel < zoomLevels.length) {
			this.zoomLevel = zoomLevel;
			this.zoom = zoomLevels[zoomLevel];
		}
	}

	/** zooms in one unit. */
	public boolean zoomIn() {
		setZoomLevel(zoomLevel + 1);
		return zoomLevel < zoomLevels.length - 1;
	}

	/** zooms out one unit. */
	public boolean zoomOut() {
		setZoomLevel(zoomLevel - 1);
		return zoomLevel > 0;
	}

	/** restores default zoom. */
	public void zoomNormalize() {
		setZoomLevel(DEFAULT_ZOOM_LEVEL);
	}

	/**
	 * returns the properties at x,y.
	 * 
	 * @param tileLayer
	 *            a list of all tile layers
	 * @param x
	 *            x-pos
	 * @param y
	 *            y-pos
	 * @return the properties (may be empty)
	 */
	protected Properties getPropertiesAt(List<TileLayer> tileLayer, int x, int y) {
		Properties props = new Properties();

		for (TileLayer layer : tileLayer) {
			Tile tile = layer.getTileAt(x, y);
			if (tile != null) {
				props.putAll(tile.getProperties());
			}
		}
		return props;
	}

	/**
	 * Sets the padding of the tiles in pixels.
	 * 
	 * @param padding
	 *            the padding size in pixels
	 */
	public void setPadding(int padding) {
		this.padding = padding;
	}

	/**
	 * Returns the padding of the tiles in pixels.
	 * 
	 * @return the padding size in pixels
	 */
	public int getPadding() {
		return padding;
	}

	/**
	 * mapchange events updates the minimap.
	 */
	public void mapChanged(MapChangedEvent e) {
		Rectangle r = e.getModifiedRegion();
		if (r == null) {
			return;
		}
		// update the dirty region
		if (dirtyMinimap == null) {
			dirtyMinimap = r;
		} else {
			dirtyMinimap = dirtyMinimap.union(r);
		}
	}

	/**
	 * Prepares the minimap. The view can use the <i>DEFAULT_MINIMAP_ZOOM</i>.
	 * The map is already set and non-null when this method is called. The view
	 * must return the image of the minimap (and not set the field in this
	 * baseclass directly).
	 */
	protected abstract BufferedImage prepareMinimapImage();

	/**
	 * Updates the minimap image. This method should always be called when there
	 * is a change in the map. <br>
	 * <b>Note: </b>The modified region is in tile coordinate space
	 * 
	 * @param modifiedRegion
	 *            the region which was changed and therefor needs to be redrawn.
	 */
	public abstract void updateMinimapImage(Rectangle modifiedRegion);

	/**
	 * (re)draws a portion of the map. Note that clipArea is in Tile coordinate
	 * space, not pixel space. The destination is always the upper left
	 * corner(0,0) of g. The MapView should check the clipping area of g too, to
	 * avoid unneccessary drawing operation.
	 * 
	 * @param g
	 *            the graphic to draw to
	 * @param clipArea
	 *            the are to draw in tile coordinates
	 */
	public abstract void draw(Graphics g, Rectangle clipArea);

	/**
	 * converts the screen position to tile position.
	 * 
	 * @param tileCoords
	 *            tile coords
	 * @return screen coords
	 */
	public abstract Point screenToTileCoords(Point screenCoords);

	/**
	 * converts the tile position to screen position.
	 * 
	 * @param screenCoords
	 *            screen coords
	 * @return tile coords
	 */
	public abstract Point tileToScreenCoords(Point tileCoords);

	/**
	 * Retuns list of tiles that lies in the given rectangle. Note that the
	 * rectangle is in pixel coordinate space.
	 * 
	 * @param rect
	 *            the rectangle (in pixel coordinate space)
	 * @param layer
	 *            the layer
	 * @return list of tiles in the rectangle
	 */
	public abstract List<Point> getSelectedTiles(Rectangle rect, int layer);

	/**
	 * draws a frame around the tile.
	 * 
	 * @param g
	 *            graphics context
	 * @param tile
	 *            the tile
	 */
	public void drawTileHighlight(Graphics g, Point tile) {
		drawTilesHighlight(g, Arrays.asList(new Point[] { tile }));
	}

	/**
	 * draws a frame around the tile.
	 * 
	 * @param g
	 *            graphics context
	 * @param tile
	 *            the tile
	 */
	public abstract void drawTilesHighlight(Graphics g, List<Point> tiles);

	/**
	 * Draws the tilegroup to a BufferedImage.
	 * 
	 * @param tileGroup
	 *            the tilegroup
	 */
	public abstract BufferedImage drawTileGroup(TileGroup tileGroup);

}
