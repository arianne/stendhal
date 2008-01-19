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

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Area;

import tiled.mapeditor.util.MapChangedEvent;

/**
 * A TileLayer is a specialized MapLayer, used for tracking two dimensional tile
 * data.
 */
public class TileLayer extends MapLayer {
	protected Tile[][] map;

	/**
	 * Default contructor.
	 */
	public TileLayer() {
		super();
	}

	/**
	 * Construct a TileLayer from the given width and height.
	 * 
	 * @param w
	 *            width in tiles
	 * @param h
	 *            height in tiles
	 */
	public TileLayer(int w, int h) {
		super(w, h);
	}

	/**
	 * Create a tile layer using the given bounds.
	 * 
	 * @param r
	 *            the bounds of the tile layer.
	 */
	public TileLayer(Rectangle r) {
		super(r);
	}

	/**
	 * Copy constructor. Copies all data from given TileLayer
	 * 
	 * @param ml
	 */
	public TileLayer(TileLayer ml) {
		super(ml);

		map = new Tile[bounds.height][];
		for (int y = 0; y < bounds.height; y++) {
			map[y] = new Tile[bounds.width];
			System.arraycopy(ml.map[y], 0, map[y], 0, bounds.width);
		}
	}

	/**
	 * @param m
	 *            the map this layer is part of
	 */
	TileLayer(Map m) {
		super(m);
	}

	/**
	 * @param m
	 *            the map this layer is part of
	 * @param w
	 *            width in tiles
	 * @param h
	 *            height in tiles
	 */
	public TileLayer(Map m, int w, int h) {
		super(w, h);
		setMap(m);
	}

	/**
	 * Rotates the layer by the given Euler angle.
	 * 
	 * @param angle
	 *            The Euler angle (0-360) to rotate the layer array data by.
	 * @see MapLayer#rotate(int)
	 */
	public void rotate(int angle) {
		Tile[][] trans;
		int xtrans = 0, ytrans = 0;

		if (!canEdit()) {
			return;
		}

		switch (angle) {
		case ROTATE_90:
			trans = new Tile[bounds.width][bounds.height];
			xtrans = bounds.height - 1;
			break;
		case ROTATE_180:
			trans = new Tile[bounds.height][bounds.width];
			xtrans = bounds.width - 1;
			ytrans = bounds.height - 1;
			break;
		case ROTATE_270:
			trans = new Tile[bounds.width][bounds.height];
			ytrans = bounds.width - 1;
			break;
		default:
			System.out.println("Unsupported rotation (" + angle + ")");
			return;
		}

		double ra = Math.toRadians(angle);
		int cos_angle = (int) Math.round(Math.cos(ra));
		int sin_angle = (int) Math.round(Math.sin(ra));

		for (int y = 0; y < bounds.height; y++) {
			for (int x = 0; x < bounds.width; x++) {
				int xrot = x * cos_angle - y * sin_angle;
				int yrot = x * sin_angle + y * cos_angle;
				trans[yrot + ytrans][xrot + xtrans] = getTileAt(x + bounds.x, y + bounds.y);
			}
		}

		bounds.width = trans[0].length;
		bounds.height = trans.length;
		map = trans;
		postEdit(bounds);
	}

	/**
	 * Performs a mirroring function on the layer data. Two orientations are
	 * allowed: vertical and horizontal. Example:
	 * <code>layer.mirror(MapLayer.MIRROR_VERTICAL);</code> will mirror the
	 * layer data around a horizontal axis.
	 * 
	 * @param dir
	 *            the axial orientation to mirror around
	 */
	public void mirror(int dir) {
		if (!canEdit()) {
			return;
		}

		Tile[][] mirror = new Tile[bounds.height][bounds.width];
		for (int y = 0; y < bounds.height; y++) {
			for (int x = 0; x < bounds.width; x++) {
				if (dir == MIRROR_VERTICAL) {
					mirror[y][x] = map[(bounds.height - 1) - y][x];
				} else {
					mirror[y][x] = map[y][(bounds.width - 1) - x];
				}
			}
		}
		map = mirror;
		postEdit(bounds);
	}

	/**
	 * Checks to see if the given Tile is used anywhere in the layer.
	 * 
	 * @param t
	 *            a Tile object to check for
	 * @return <code>true</code> if the Tile is used at least once,
	 *         <code>false</code> otherwise.
	 */
	public boolean isUsed(Tile t) {
		for (int y = 0; y < bounds.height; y++) {
			for (int x = 0; x < bounds.width; x++) {
				if (map[y][x] == t) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Sets the bounds (in tiles) to the specified Rectangle. <b>Caution:</b>
	 * this causes a reallocation of the data array, and all previous data is
	 * lost.
	 * 
	 * @param bounds
	 * @see MapLayer#setBounds
	 */
	public void setBounds(Rectangle bounds) {
		super.setBounds(bounds);
		map = new Tile[bounds.height][bounds.width];
	}

	/**
	 * Creates a diff of the two layers, <code>ml</code> is considered the
	 * significant difference.
	 * 
	 * @param ml
	 * @return A new MapLayer that represents the difference between this layer,
	 *         and the argument, or <b>null</b> if no difference exists.
	 */
	public MapLayer createDiff(MapLayer ml) {
		if (ml == null || !(ml instanceof TileLayer)) {
			return null;
		}

		Rectangle r = null;
		TileLayer other = (TileLayer) ml;

		for (int y = bounds.y; y < bounds.height + bounds.y; y++) {
			for (int x = bounds.x; x < bounds.width + bounds.x; x++) {
				if (other.getTileAt(x, y) != getTileAt(x, y)) {
					if (r != null) {
						r.add(x, y);
					} else {
						r = new Rectangle(x, y, 0, 0);
					}
				}
			}
		}

		if (r != null) {
			MapLayer diff = new TileLayer(new Rectangle(r.x, r.y, r.width + 1, r.height + 1));
			diff.copyFrom(ml);
			diff.setName(name);
			return diff;
		} else {
			return new TileLayer();
		}
	}

	/**
	 * Removes any occurences of the given tile from this map layer. If layer is
	 * locked, an exception is thrown.
	 * 
	 * @param tile
	 *            the Tile to be removed
	 * @throws Exception
	 */
	public void removeTile(Tile tile) throws Exception {
		if (isLocked()) {
			throw new Exception("Attempted to remove tile when this layer is locked.");
		}

		// keep track the changes
		Rectangle dirty = new Rectangle();

		for (int y = 0; y < bounds.height; y++) {
			for (int x = 0; x < bounds.width; x++) {
				if (map[y][x] == tile) {
					setTileAt(x + bounds.x, y + bounds.y, myMap.getNullTile());

					Rectangle r = new Rectangle(x + bounds.x, y + bounds.y);
					if (dirty == null) {
						dirty = r;
					} else {
						dirty = dirty.union(r);
					}
				}
			}
		}

		// notify the map of changes
		if (dirty != null) {
			postEdit(dirty);
		}
	}

	/**
	 * Sets the tile at the specified position. Does nothing if (tx, ty) falls
	 * outside of this layer.
	 * 
	 * @param tx
	 *            x position of tile
	 * @param ty
	 *            y position of tile
	 * @param ti
	 *            the tile object to place
	 */
	public void setTileAt(int tx, int ty, Tile ti) {
		if (canEdit() && contains(tx, ty)) {
			map[ty - bounds.y][tx - bounds.x] = ti;
			// TODO: find a better way. Not very good to notify every single
			// tile change
			postEdit(new Rectangle(tx, ty, 1, 1));
		}
	}

	/**
	 * Returns the tile at the specified position.
	 * 
	 * @param tx
	 *            Tile-space x coordinate
	 * @param ty
	 *            Tile-space y coordinate
	 * @return tile at position (tx, ty) or <code>null</code> when (tx, ty) is
	 *         outside this layer
	 */
	public Tile getTileAt(int tx, int ty) {
		int x = tx - bounds.x;
		int y = ty - bounds.y;
		if (x < 0 || y < 0 || x >= bounds.width || y >= bounds.height) {
			return null;
		}
		return map[y][x];
	}

	/**
	 * Returns the first occurance (using top down, left to right search) of the
	 * given tile.
	 * 
	 * @param t
	 *            the {@link tiled.core.Tile} to look for
	 * @return A java.awt.Point instance of the first instance of t, or
	 *         <code>null</code> if it is not found
	 */
	public Point locationOf(Tile t) {
		for (int y = bounds.y; y < bounds.height + bounds.y; y++) {
			for (int x = bounds.x; x < bounds.width + bounds.x; x++) {
				if (getTileAt(x, y) == t) {
					return new Point(x, y);
				}
			}
		}
		return null;
	}

	/**
	 * Replaces all occurances of the Tile <code>find</code> with the Tile.
	 * <code>replace</code> in the entire layer
	 * 
	 * @param find
	 *            the tile to replace
	 * @param replace
	 *            the replacement tile
	 */
	public void replaceTile(Tile find, Tile replace) {
		if (!canEdit()) {
			return;
		}

		// keep track the changes
		Rectangle dirty = new Rectangle();

		for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
			for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
				if (getTileAt(x, y) == find) {
					setTileAt(x, y, replace);
					Rectangle r = new Rectangle(x + bounds.x, y + bounds.y);
					if (dirty == null) {
						dirty = r;
					} else {
						dirty = dirty.union(r);
					}
				}
			}
		}
		// notify the map of changes
		if (dirty != null) {
			postEdit(dirty);
		}

	}

	/**
	 * Merges the tile data of this layer with the specified layer. The calling
	 * layer is considered the significant layer, and will overwrite the data of
	 * the argument layer. At cells where the calling layer has no data, the
	 * argument layer data is preserved.
	 * 
	 * @param other
	 *            the insignificant layer to merge with
	 */
	public void mergeOnto(MapLayer other) {
		if (!other.canEdit() || !(other instanceof TileLayer)) {
			return;
		}

		for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
			for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
				Tile tile = getTileAt(x, y);
				if (tile != myMap.getNullTile()) {
					((TileLayer) other).setTileAt(x, y, tile);
				}
			}
		}
	}

	/**
	 * Copy data from another layer onto this layer. Unlike mergeOnto,
	 * copyFrom() copies the empty cells as well.
	 * 
	 * @see tiled.core.MapLayer#copyFrom
	 * @param other
	 */
	public void copyFrom(MapLayer other) {
		if (!canEdit() || !(other instanceof TileLayer)) {
			return;
		}

		TileLayer otherLayer = (TileLayer) other;

		Rectangle realBounds = bounds.intersection(other.getBounds());

		for (int y = realBounds.y; y < realBounds.y + realBounds.height; y++) {
			for (int x = realBounds.x; x < realBounds.x + realBounds.width; x++) {
				setTileAt(x, y, otherLayer.getTileAt(x, y));
			}
		}
	}

	/**
	 * Like copyFrom, but will only copy the area specified.
	 * 
	 * @see tiled.core.TileLayer#copyFrom(MapLayer)
	 * @param other
	 * @param mask
	 */
	public void maskedCopyFrom(MapLayer other, Area mask) {
		if (!canEdit() || !(other instanceof TileLayer)) {
			return;
		}

		Rectangle boundBox = mask.getBounds();

		for (int y = boundBox.y; y < boundBox.y + boundBox.height; y++) {
			for (int x = boundBox.x; x < boundBox.x + boundBox.width; x++) {
				if (mask.contains(x, y)) {
					setTileAt(x, y, ((TileLayer) other).getTileAt(x, y));
				}
			}
		}
	}

	/**
	 * Unlike mergeOnto, copyTo includes the null tile when merging.
	 * 
	 * @see tiled.core.MapLayer#copyFrom
	 * @see tiled.core.MapLayer#mergeOnto
	 * @param other
	 *            the layer to copy this layer to
	 */
	public void copyTo(MapLayer other) {
		if (!other.canEdit() || !(other instanceof TileLayer)) {
			return;
		}

		for (int y = bounds.y; y < bounds.y + bounds.height; y++) {
			for (int x = bounds.x; x < bounds.x + bounds.width; x++) {
				((TileLayer) other).setTileAt(x, y, getTileAt(x, y));
			}
		}
	}

	/**
	 * Creates a copy of this layer.
	 * 
	 * @see java.lang.Object#clone
	 * @return a clone of this layer, as complete as possible
	 * @exception CloneNotSupportedException
	 */
	public Object clone() throws CloneNotSupportedException {
		TileLayer clone = (TileLayer) super.clone();

		// Clone the layer data
		clone.map = new Tile[map.length][];
		for (int i = 0; i < map.length; i++) {
			clone.map[i] = new Tile[map[i].length];
			System.arraycopy(map[i], 0, clone.map[i], 0, map[i].length);
		}

		return clone;
	}

	/**
	 * @see MultilayerPlane#resize
	 * @param width
	 *            the new width of the layer
	 * @param height
	 *            the new height of the layer
	 * @param dx
	 *            the shift in x direction
	 * @param dy
	 *            the shift in y direction
	 */
	public void resize(int width, int height, int dx, int dy) {
		if (!canEdit()) {
			return;
		}

		Tile[][] newMap = new Tile[height][width];

		int maxX = Math.min(width, bounds.width + dx);
		int maxY = Math.min(height, bounds.height + dy);

		for (int x = Math.max(0, dx); x < maxX; x++) {
			for (int y = Math.max(0, dy); y < maxY; y++) {
				newMap[y][x] = getTileAt(x - dx, y - dy);
			}
		}

		map = newMap;
		bounds.width = width;
		bounds.height = height;
	}

	/** copies the layer. */
	public MapLayer getLayerCopy(Rectangle bounds) {
		Rectangle realBounds = bounds.union(bounds);
		TileLayer other = new TileLayer();
		other.setBounds(realBounds);
		copyParameter(other);

		for (int x = realBounds.x; x < realBounds.x + realBounds.width; x++) {
			for (int y = realBounds.y; y < realBounds.y + realBounds.height; y++) {
				other.setTileAt(x, y, getTileAt(x, y));
			}
		}

		return other;
	}

	/**
	 * posts a edit event to the map.
	 * 
	 * @param bounds
	 *            the changed area
	 */
	private void postEdit(Rectangle bounds) {
		if (myMap != null) {
			myMap.fireMapChanged(new MapChangedEvent(myMap, MapChangedEvent.Type.TILES, new Rectangle(bounds), this));
		}
	}
}
