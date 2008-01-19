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

/**
 * Stores coordinates for the tile.
 * 
 * @author mtotz
 */
public class StatefulTile {
	public int layer;
	public Point p;
	public Tile tile;

	public StatefulTile(Point p, int layer, Tile tile) {
		this.p = new Point(p);
		this.layer = layer;
		this.tile = tile;
	}

	public String toString() {
		return "[TileWrapper: " + p + " " + tile + "]";
	}

}