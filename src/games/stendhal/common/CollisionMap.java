/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.common;



import java.awt.geom.Rectangle2D;
import java.util.BitSet;

import games.stendhal.common.tiled.LayerDefinition;


/**
 * A map containing collision information.
 */
public class CollisionMap {

	private final int width;
	private final int height;
	private final BitSet[] colls;

	/**
	 * Creates a new empty collision map.
	 *
	 * @param width
	 *   Node width of new map.
	 * @param height.
	 *   Node height of new map.
	 */
	public CollisionMap(final int width, final int height) {
		this.width = width;
		this.height = height;
		colls = new BitSet[width];
		for (int i = 0; i < width; i++) {
			colls[i] = new BitSet();
		}

	}

	/**
	 * Creates a new collision map using layers definition.
	 *
	 * @param layer
	 *   Definition used to create map.
	 */
	public CollisionMap(final LayerDefinition layer) {
		this(layer.getWidth(), layer.getHeight());
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					if (layer.getTileAt(x, y) != 0) {
						set(x, y);
					}
				}
			}
	}

	/**
	 * Retrieves width of collision map.
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Retrieves height of collision map.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Checks if node is a collision tile.
	 *
	 * @param i
	 *   Node X coordinate.
	 * @param j
	 *   Node Y coordinate.
	 * @return
	 *   {@code true} if node has collision.
	 */
	public boolean get(final int i, final int j) {
		return colls[i].get(j);
	}

	/**
	 * Sets a collision node.
	 *
	 * @param i
	 *   Node X coordinate.
	 * @param j
	 *   Node Y coordinate.
	 */
	public void set(final int i, final int j) {
		colls[i].set(j);
	}

	/**
	 * Sets collision for a rectangle area.
	 *
	 * @param shape
	 *   Area to be set.
	 */
	public void set(final Rectangle2D shape) {
		int y = (int) shape.getY();
		for (int x = (int) shape.getX(); x < shape.getX() + shape.getWidth(); x++) {
			colls[x].set(y, (int) (y + shape.getHeight()));
		}
	}

	/**
	 * Removes collision from a node.
	 *
	 * @param i
	 *   Node X coordinate.
	 * @param k
	 *   Node Y coordinate.
	 */
	public void unset(final int i, final int k) {
		colls[i].clear(k);
	}

	/**
	 * Removes all collision from the map.
	 */
	public void clear() {
		for (int i = 0; i < this.width; i++) {
			colls[i].clear();
		}
	}

	/**
	 * Checks for collision in a rectangle area.
	 *
	 * @param x
	 *   Beginning node X coordinate.
	 * @param y
	 *   Beginning node Y coordinate.
	 * @param width
	 *   Width of area to be checked.
	 * @param height
	 *   Height of area to be checked.
	 * @return
	 *   {@code true} if collision is found in area.
	 */
	public boolean collides(final int x, final int y, final int width, final int height) {
		if (x < 0 || x - 1 + width >= this.width) {
			return true;
		}

		if (y < 0 || y - 1 + height >= this.height) {
			return true;
		}

		final BitSet result = new BitSet();
		for (int i = x; i < x + width; i++) {
			result.or(colls[i]);
		}

		return !result.get(y, y + height).isEmpty();
	}

	/**
	 * Creates a new collision map.
	 *
	 * @param layer
	 *     Layer definition.
	 * @return
	 *     Resulting collision map.
	 */
	public static CollisionMap create(final LayerDefinition layer) {
		CollisionMap collissionMap = new CollisionMap(layer.getWidth(), layer.getHeight());
		for (int x = 0; x < layer.getWidth(); x++) {
			for (int y = 0; y < layer.getHeight(); y++) {
				if (layer.getTileAt(x, y) != 0) {
					collissionMap.set(x, y);
				}
			}
		}
		return collissionMap;
	}
}
